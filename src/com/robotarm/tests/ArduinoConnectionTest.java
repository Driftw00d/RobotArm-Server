package com.robotarm.tests;

import com.leapmotion.leap.Arm;
import com.robotarm.core.arduino.ArmProtocol;
import com.robotarm.core.arduino.Commands;

import java.util.Scanner;

/**
 * Created by higgsy789 on 22/03/2017.
 */
public class ArduinoConnectionTest extends Test {

    public static void main(String[] args) {

        ArmProtocol arduino = ArmProtocol.getInstance();

        if (arduino.isConnected()) {
            arduino.addListeners();

            //arduino.sendCommand(Commands.MOVE_SHOULDER_Y.cmd(180, 180));

//            try { Thread.sleep(1000); } catch (Exception e) {}
//
//            arduino.sendCommand(Commands.MOVE_ELBOW.cmd(000, 180));
//
//            try { Thread.sleep(1000); }  catch (Exception e) {}
//
//            arduino.sendCommand(Commands.MOVE_WRIST.cmd(180, 180));

            h.println("Tests complete");

        } else {
            h.err("Cant connect to the arduino");
        }

        try { Thread.sleep(5000); }
        catch (Exception e) {}

        //String a = s.nextLine();
        arduino.disconnect();
    }

}
