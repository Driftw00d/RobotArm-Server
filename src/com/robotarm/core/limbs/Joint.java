package com.robotarm.core.limbs;

import com.robotarm.core.arduino.MotorDriverHandler;
import com.leapmotion.leap.Vector;

public abstract class Joint {

    protected Vector position;
    protected Vector lastPosition;
    protected Vector targetPosition;
    protected float  moveSpeed;
    protected int    axies;
    protected Type   type;
    protected MotorDriverHandler[] drivers;

    public enum Type {
        SHOULDER, ELBOW, WRIST
    }
    public enum Axis {
        X, Y, Z
    }

    public Joint (int axies, Type type) {
        this.position       = new Vector();
        this.lastPosition   = new Vector();
        this.targetPosition = new Vector();
        this.type           = type;
        this.axies          = axies;
        this.moveSpeed      = 5;
    }

    public float  updatePosition (Axis axis, float newPos) {
        float posDiff;
        switch (axis) {
            case X:
                int currentX = (int)position.getX();
                posDiff = currentX - newPos;
                this.position.setX(currentX + newPos);
                break;
            case Y:
                posDiff = position.getY() - newPos;
                this.position.setY(newPos);
                break;
            case Z:
                posDiff = position.getZ() - newPos;
                this.position.setZ(newPos);
                break;
            default:
                posDiff = -1;
                break;
        }
        return posDiff;
    }

    // These functions should be overridden/implemented in a child class
    public void moveX (float posX, float speed) {  }
    public void moveY (float posY, float speed) {  }
    public void moveZ (float posZ, float speed) {  }


    public Type   type           () { return type; }
    public int    axies          () { return axies; }
    public float  moveSpeed      () { return moveSpeed; }
    public Vector position       () { return position; }
    public Vector lastPosition   () { return lastPosition; }
    public Vector targetPosition () { return targetPosition; }
    public MotorDriverHandler[] drivers () { return drivers; }
    public MotorDriverHandler driver (int index) { return drivers[index]; }

    public void setPosition  (Vector pos) { this.position = pos; }
    public void setLastPos   (Vector pos) { this.lastPosition = pos; }
    public void setTargetPos (Vector pos) { this.targetPosition = pos; }
    public void setMoveSpeed (float speed) { this.moveSpeed = speed; }

}
