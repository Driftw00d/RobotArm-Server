package com.robotarm.core.limbs;

import com.robotarm.core.arduino.Command;
import com.robotarm.core.arduino.Commands;
import com.robotarm.core.arduino.MotorDriverHandler;

public class Shoulder extends Joint {

    /*
        Inherrited vars:
        protected Vector position;
        protected Vector lastPosition;
        protected Vector targetPosition;
        protected float  moveSpeed;
        protected int    axies;
        protected Type   type;
        protected MotorDriverHandler[] drivers;
    */

    public  final float limbLength = 0.0f;
    private MotorDriverHandler driverX;
    private MotorDriverHandler driverY;
    private Command cmdX;
    private Command cmdY;

    public Shoulder () {
        super(2, Joint.Type.SHOULDER);
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

    public void moveX (float posX) {
        cmdX.setParams(new float[] { posX, moveSpeed});
        driverX.sendCommand(cmdX);
        updatePosition(Axis.X, posX);
    }

    public void moveY (float posY) {
        cmdY.setParams(new float[] { posY, moveSpeed });
        driverY.sendCommand(cmdY);
        updatePosition(Axis.X, posY);
    }

    public float getLimbLength () { return limbLength; }
}
