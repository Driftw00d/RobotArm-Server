package com.robotarm.core.arduino;

import com.robotarm.RobotArm;
import com.robotarm.core.executable.MovementTask;
import com.robotarm.core.executable.Task;
import com.robotarm.util.Helper;

import org.ardulink.core.Link;
import org.ardulink.core.convenience.Links;
import org.ardulink.core.events.*;
import org.ardulink.core.proto.api.Protocol;
import org.ardulink.core.proto.impl.ArdulinkProtocol2;
import org.ardulink.util.URIs;

import java.awt.*;
import java.io.IOException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Communication protocol
 *      Should probably be a singleton class for a single instance of connection
 *      also a consumer thread, consuming any tasks sent to it
 */
public class ArmProtocol extends ArdulinkProtocol2 implements Runnable {

    private Helper h = Helper.getInstance();

    private static ArmProtocol instance = null;
    private Link arduino;
    private final  String name     = "ArmProtocol v0.1";
    private final  String protoHeader = "alp://cust/";
    private final  String connectionString = "ardulink://serial-jssc?";
    private final  String baudrate = "&baudrate=115200";
    private String port;
    private boolean arduinoConnected = false;
    private boolean arduinoBusy = false;
    private boolean keepAlive = false;

    private ArduinoListener customListener;
    private static BlockingQueue<Task> taskList;

    public static final Object lock = new Object();

    //private Pin.DigitalPin debug = Pin.digitalPin(13);
    //private Pin.DigitalPin xAxis = Pin.DigitalPin

    private ArmProtocol () {
        if (instance == null) {
            try {
                String os = System.getProperty("os.name");
                if (os.contains("Mac") || os.contains("Linux")) {
                    //port = "port=/dev/tty.usbmodem1411";
                    port = "port=/dev/tty.usbmodem1421";
                } else if (os.contains("Windows")) {
                    port = "COM3";
                }

                arduino = Links.getLink(URIs.newURI(connectionString + port));
                taskList = new LinkedBlockingQueue<>(10);
                arduinoConnected = arduino != null;

                if (! arduinoConnected) throw new NotYetConnectedException();
                if (RobotArm.DEBUG) h.confirm("Arduino connected, port: " + port);

            } catch (Exception e) {
                arduinoConnected = false;
                h.err("Arduino is not connected");
                if (RobotArm.DEBUG) e.printStackTrace();
            }
            instance = this;
        } else {
            h.err("Arm Protocol is a singleton class and can only have one instance");
        }
    }

    @Override
    public void run() {
        keepAlive = true;
        Task currentTask;
        BlockingQueue<Command> cmds;
        Command cmd;

        // Loop for the lifetime of the thread
        while (keepAlive) {
            if (arduino != null && !taskList.isEmpty()) {
                try {
                    currentTask = taskList.take();
                    cmds = currentTask.getCommands();
                    while (cmds.peek() != null) {
                        cmd = cmds.take();
                        if (cmd.isValid()) {
                            sendCommand(cmd);

                            if (RobotArm.DEBUG) {
                                h.print("Free queue: " + taskList.remainingCapacity());
                                h.println(" Current Cmd: " + cmd.cmdWithParams() + "\n");
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    h.err("Error executing task queue");
                    if (RobotArm.DEBUG) e.printStackTrace();
                }
            }
        }
    }

    public boolean addTask (Task task) {
        if (taskList.remainingCapacity() > 0) {
            taskList.add(task);
            return true;
        } else {
            return false;
        }
    }

    public boolean addCommand (Command command) {
        if (taskList.remainingCapacity() > 0) {
            Task t = new MovementTask(command);
            taskList.add(t);
            return true;
        } else {
            return false;
        }
    }

    private boolean sendCommand (Command cmd) {
        try {
            synchronized (lock) {
                arduino.sendCustomMessage(cmd.cmdWithParams());
            }
            return true;
        } catch (Exception e) {
            h.err("Error Sending command to arduino");
            if (RobotArm.DEBUG) e.printStackTrace();
        }
        return false;
    }

    private boolean sendKeyPress (char key) {
        try {
            arduino.sendKeyPressEvent(key, 0,0 ,0,0);
            return true;
        }
        catch (Exception e ) { if (RobotArm.DEBUG) e.printStackTrace(); }
        return false;
    }

    public void addListeners () {
        // Listen on the pins
        try {
            customListener = new ArduinoListener();
            arduino.addCustomListener(customListener);
            arduino.addRplyListener(new RplyListener() {
                @Override
                public void rplyReceived(RplyEvent e) {
                    if (RobotArm.DEBUG) {
                        if (e.isOk()) {
                            h.println("Reply OK! ");
                        } else {
                            h.err("Reply Not OK");
                            for (String param : e.getParameterNames()) {
                                h.print(param + " = ");
                                h.println(e.getParameterValue(param).toString());
                            }
                        }
                    }
                    // Event code here....
                }
            });
            arduino.addListener(new EventListener() {
                @Override
                public void stateChanged(AnalogPinValueChangedEvent event) {
                    h.println("Arduino analog pin state changed ");
                    h.println(event.getValue().toString(), event.getPin().toString());
                }

                @Override
                public void stateChanged(DigitalPinValueChangedEvent event) {
                    h.println("Arduino digital pin state changed ");
                    h.println(event.getValue().toString(), event.getPin().toString());
                }
            });
        } catch (Exception e) {
            if (RobotArm.DEBUG) {
                h.err("Exception adding listeners");
                e.printStackTrace();
            }
        }
    }

    public void removeListeners () {
        try {
            arduino.removeCustomListener(customListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connect () {
        if ( ! arduinoConnected ) {
            try {
                arduino = Links.getLink(URIs.newURI(connectionString + port));
                arduinoConnected = true;
                return true;
            } catch (Exception e) {
                if (RobotArm.DEBUG) {
                    h.err("Link not found");
                    e.printStackTrace();
                }
                return false;
            }
        }
        return true;
    }

    public void disconnect () {
        if (arduinoConnected) {
            try {
                arduino.close();
                arduinoConnected = false;
                if (RobotArm.DEBUG) h.print("Arduino Connection closed");
            } catch (Exception e) {
                if (RobotArm.DEBUG) e.printStackTrace();
            }
        }
    }

    public static ArmProtocol getInstance () {
        return (instance == null) ? new ArmProtocol() : instance;
    }

    public boolean isConnected      () { return arduinoConnected = (arduino != null); }
    public String  name             () { return name; }
    public String  connectionString () { return connectionString; }
    public String  baudrate         () { return baudrate; }
    public String  port             () { return port; }
    public BlockingQueue<Task> taskList () { return taskList; }

    public void setTaskList (BlockingQueue<Task> tasks) {
        taskList = taskList;
    }
    public void setKeepAlive (boolean keepAlive) { this.keepAlive = keepAlive; }


}
