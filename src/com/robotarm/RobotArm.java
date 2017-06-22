package com.robotarm;

import com.leapmotion.leap.Vector;
import com.robotarm.controller.LeapMotionController;
import com.robotarm.core.arduino.ArmProtocol;
import com.robotarm.core.arduino.Command;
import com.robotarm.core.attachable.Tool;
import com.robotarm.core.executable.MovementTask;
import com.robotarm.core.executable.Task;
import com.robotarm.core.limbs.Elbow;
import com.robotarm.core.limbs.Joint;
import com.robotarm.core.limbs.Shoulder;
import com.robotarm.core.limbs.Wrist;
import com.robotarm.gui.MainWindow;
import com.robotarm.remoteconnection.ConnectionServer;
import com.robotarm.util.Helper;

import java.nio.channels.NotYetConnectedException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RobotArm {

    public enum State {
        STARTING(0), WAITING(1), READY(2), LISTEN_LOCAL(3),
        LISTEN_NETWORK(4), EXECUTE_TASKS(5), SHUT_DOWN(6);
        private int value;
        State (int value) { this.value = value; }
        public int value () { return value; }
    }
    public  static final boolean DEBUG   = true;
    public  static final float   version = 0.1f;
    private boolean isPowered;
    private boolean headlessMode;
    private boolean arduinoConnected;
    private boolean controllerConnected;
    private boolean hasRemoteConnection;

    private State state;
    private ConnectionServer remoteConnection;
    private ArmProtocol arduino;
    private Shoulder shoulder;
    private Elbow    elbow;
    private Wrist    wrist;
    private Tool     tool;
    private List<Vector> armPosition;

    private List<Joint> joints;
    private BlockingQueue<Task>  taskList;

    private LeapMotionController leap;

    private Thread arduinoTaskProcessor;
    private Thread networkServer;

    private MainWindow ui;

    Helper  h = Helper.getInstance();
    Scanner s = new Scanner(System.in);

    public RobotArm (boolean headlessMode) {
        state = State.STARTING;
        try {
            this.headlessMode = headlessMode;
            this.taskList = new LinkedBlockingQueue<>(10);

            // Setup arduino
            arduino = ArmProtocol.getInstance();
            arduino.addListeners();
            arduinoConnected = arduino.isConnected();

            // Init objects
            shoulder    = new Shoulder();
            elbow       = new Elbow();
            wrist       = new Wrist();
            joints = new ArrayList<>(3);
            joints.add(shoulder);
            joints.add(elbow);
            joints.add(wrist);
            armPosition = new ArrayList<Vector>();
            armPosition.add(0, shoulder.position());
            armPosition.add(1, elbow.position());
            armPosition.add(2, wrist.position());

            // Start threads
            arduinoTaskProcessor = new Thread( arduino );
            arduinoTaskProcessor.setName("Arduino Task Processor");
            arduinoTaskProcessor.setPriority(1);
            arduinoTaskProcessor.start();

            // Detect & Setup controller
            leap = new LeapMotionController();
            controllerConnected = leap.isConnected();
            if ( ! controllerConnected) h.err("Leap not connected");
            if ( ! (controllerConnected || arduinoConnected) ) throw new NotYetConnectedException();

            // Make sure connections are established before continuing.
            h.sleep(200);
            state = State.READY;

            h.confirm("Arm initialised");
        } catch (Exception e) {
            h.err("Problem initialising arm");
            if (DEBUG) e.printStackTrace();
        }
    }

    public void runState (State _state) {
        if (_state == State.READY) {
            this.state = _state;
            h.confirm("Program running, state set: " + toString());
            // If not headless mode, start gui
            if (!headlessMode) {
                ui = new MainWindow(MainWindow.Mode.SERVER);
                ui.run();
            }
            // Check state and start appropriate mode
            while (true) {
                switch (state) {
                    case LISTEN_LOCAL:
                        this.state = State.LISTEN_LOCAL;
                        listenLocal();
                        break;

                    case LISTEN_NETWORK:
                        this.state = State.LISTEN_NETWORK;
                        listenOnNetwork();
                        break;

                    case EXECUTE_TASKS:
                        this.state = State.EXECUTE_TASKS;
                        List<Task> tasks = new ArrayList<>(100);
                        processTasks(tasks);
                        break;

                    case SHUT_DOWN:

                    default:
                        state = State.SHUT_DOWN;
                        shutdown(0);
                        break;
                }
                h.sleep(1000);
            }
        } else {
            h.err("Arm is not initialised properly, Shutting down...");
            shutdown(1);
        }
    }

    private void processTasks (List<Task> tasks) {
        // If tasks are set, do tasks.
        if (tasks.size() > 0) {
            while ( ! tasks.isEmpty() ) {
                for (Task t : tasks)
                    arduino.addTask(t);
            }
        }
    }

    private void listenOnNetwork () {
        // Detect & Setup network connection
        if (remoteConnection == null && networkServer == null) {
            remoteConnection = new ConnectionServer(44444);
            networkServer = new Thread(remoteConnection);
            networkServer.setName("Network Listener");
            networkServer.setPriority(2);
            networkServer.start();
        } else {
            h.err("Connection already established");
        }
    }

    private void listenLocal () {
        // Listen directly from the leap motion
        int deadZone = 20;
        int stepLimit = 20;
        Vector leapWrist;
        Vector leapPalm;
        MovementTask moveTask;
        Command command;

        while (state == State.LISTEN_LOCAL) {
            if (leap.isConnected() && arduino != null) {
                // if frameid > prevFrameId
                if (leap.hasFrame() && leap.hand().isValid()) {
                    leapWrist = leap.arm().wristPosition();
                    leapPalm = leap.hand().palmPosition();
                    moveTask = new MovementTask();

                    // Shoulder rotation - X axis
                    if ((shoulder.position().getX() - leapWrist.getX()) > deadZone ||
                            (shoulder.position().getX() - leapWrist.getX()) < -deadZone) {
                        command = new Command(
                                shoulder.driver(0).getCommand().type(),
                                new float[]{leapWrist.getX() % stepLimit, shoulder.moveSpeed()}
                        );
                        moveTask.addWaypoint(command);
                    }
                    // Shoulder forward/back - Z axis
                    if ((shoulder.position().getY() - leapWrist.getZ()) > deadZone ||
                            (shoulder.position().getY() - leapWrist.getZ()) < -deadZone) {
                        command = new Command(
                                shoulder.driver(1).getCommand().type(),
                                new float[]{leapWrist.getZ() % stepLimit, shoulder.moveSpeed()}
                        );
                        moveTask.addWaypoint(command);
                    }
                    // Elbow up/down - Y axis
                    if ((elbow.position().getY() - leapWrist.getY()) > deadZone ||
                            (elbow.position().getY() - leapWrist.getY()) < -deadZone) {
                        command = new Command(
                                elbow.driver(0).getCommand().type(),
                                new float[]{leapWrist.getY() % stepLimit, elbow.moveSpeed()}
                        );
                        moveTask.addWaypoint(command);
                    }
                    // Wrist rotation - X axis
                    if ((wrist.position().getX() - leapPalm.roll()) > deadZone ||
                            (wrist.position().getX() - leapPalm.roll()) < -deadZone) {
                        command = new Command(
                                wrist.driver(0).getCommand().type(),
                                new float[]{leapWrist.getZ() % stepLimit, wrist.moveSpeed()}
                        );
                        moveTask.addWaypoint(command);
                    }
//                    if (arduinoTaskProcessor.getState() == Thread.State.TIMED_WAITING)
//                        arduinoTaskProcessor.interrupt();
                    if (moveTask.isValid()) arduino.addTask(moveTask);
                }
            } else {
                h.err("Controller or arduino not connected");
                h.sleep(1000);
            }
        }
    }

    public boolean setOptions () {
        return false;
    }

    public void shutdown (int exitStatus) {
        state = State.SHUT_DOWN;
        try {
            leap.removeListeners();
            arduinoTaskProcessor.join();
            networkServer.join();
        } catch (InterruptedException e) {
            h.err("Error shutting down");
            e.printStackTrace();
        }
        h.sleep(1000);
        System.exit(exitStatus);
    }

    public boolean              isPowered             () { return isPowered; }
    public boolean              isArduinoConnected    () { return arduinoConnected; }
    public boolean              isControllerConnected () { return controllerConnected; }
    public List<Vector>         armPosition           () { return armPosition; }
    public LeapMotionController leapMotionController  () { return leap; }
    public Shoulder             shoulder              () { return shoulder; }
    public Elbow                elbow                 () { return elbow; }
    public Wrist                wrist                 () { return wrist; }
    public Tool                 tool                  () { return tool; }
    public State                state                 () { return state; }

    public void setState (State state) { this.state = state; }

}

//    /* Display Hashmap content using Iterator*/
//    Set set = hmap.entrySet();
//    Iterator iterator = set.iterator();
//      while(iterator.hasNext()) {
//              Map.Entry mentry = (Map.Entry)iterator.next();
//              System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
//              System.out.println(mentry.getValue());
//              }
