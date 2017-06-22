package com.robotarm.tests;

import com.robotarm.RobotArm;
import com.robotarm.core.arduino.Commands;
import com.robotarm.core.arduino.MotorDriverHandler;

/**
 * Created by higgsy789 on 02/04/2017.
 */
public class MotorDriverHandlerTest extends Test {

    public static void main(String[] args) {

        RobotArm arm = new RobotArm(true);

        MotorDriverHandler driverY = new MotorDriverHandler(Commands.MOVE_SHOULDER_Y, 9, 8);

        //driverY.sendCommand(360, 20);

    }

}
