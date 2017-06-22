package com.robotarm.tests;

import com.robotarm.RobotArm;
import com.robotarm.core.arduino.ArmProtocol;
import com.robotarm.core.limbs.Shoulder;
import com.robotarm.util.Helper;

/**
 * Created by higgsy789 on 02/04/2017.
 */
public class ShoulderTest extends Test {

    private static Helper h = Helper.getInstance();

    public static void main(String[] args) {

        ArmProtocol arduino = ArmProtocol.getInstance();
        arduino.addListeners();

        Shoulder shoulder = new Shoulder();

        //shoulder.moveXTo(360, 10);

        //sleep(2000);

        //shoulder.moveXTo(-360, 20);

        shoulder.moveY(100, 20 );

        h.sleep(2000);

    }

}
