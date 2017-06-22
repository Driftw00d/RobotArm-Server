package com.robotarm.core.limbs;

import com.robotarm.core.arduino.Command;
import com.robotarm.core.arduino.Commands;
import com.robotarm.core.arduino.MotorDriverHandler;

public class Elbow extends Joint {

    public final float limbLength = 0.0f;
    private MotorDriverHandler driverY;
    private Command cmdY;

    public Elbow () {
        super(1, Type.ELBOW);
        cmdY = Commands.MOVE_SHOULDER_Y;
        driverY = new MotorDriverHandler(cmdY, 0, 0);
        drivers = new MotorDriverHandler[axies];
        drivers[0] = driverY;
    }

    @Override
    public void moveY (float posY, float speed) {
        cmdY.setParams(new float[] { posY, speed });
        driverY.sendCommand(cmdY);
        updatePosition(Axis.Y, posY);
    }

}
