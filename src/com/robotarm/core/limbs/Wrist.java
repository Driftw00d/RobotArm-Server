package com.robotarm.core.limbs;

import com.robotarm.core.arduino.Command;
import com.robotarm.core.arduino.Commands;
import com.robotarm.core.arduino.MotorDriverHandler;

public class Wrist extends Joint {

    public final float limbLength = 0.0f;

    private MotorDriverHandler driverX;
    private MotorDriverHandler driverY;
    private Command cmdX;
    private Command cmdY;

    public Wrist () {
        super(2, Type.WRIST);
        cmdX = Commands.MOVE_SHOULDER_X;
        cmdY = Commands.MOVE_SHOULDER_Y;
        driverX = new MotorDriverHandler(cmdX, 6, 8);
        driverY = new MotorDriverHandler(cmdY, 9, -1);
        drivers = new MotorDriverHandler[axies];
        drivers[0] = driverX;
        drivers[1] = driverY;
    }

    @Override
    public void moveX (float posX, float speed) {
        cmdX.setParams(new float[] { posX, speed });
        driverX.sendCommand(cmdX);
        updatePosition(Axis.X, posX);
    }

    @Override
    public void moveY (float posY, float speed) {
        cmdY.setParams(new float[] { posY, speed });
        driverY.sendCommand(cmdY);
        updatePosition(Axis.Y, posY);
    }
}
