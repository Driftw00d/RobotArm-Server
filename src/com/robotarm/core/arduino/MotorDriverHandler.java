package com.robotarm.core.arduino;

import org.ardulink.core.Pin;
import org.ardulink.core.Pin.DigitalPin;

/**
 *  This is where all the calculations take place for organising how many steps to send per tick
 *  each joint will have its own instance(s) of this class
 */

public class MotorDriverHandler implements Actuator {

    private final int MOTOR_STEPS = 200; // not sure if needed as hardcoded in arduino firmware

    private DigitalPin   stepPin;
    private DigitalPin   dirPin;
    private DigitalPin[] mStepPins;
    private DigitalPin   faultPin;
    private Command      action;
    private int speed;
    private int microsteps;
    private int position; // in degrees
    private int stepsPerTick = 10;

    private ArmProtocol arduino;

    public MotorDriverHandler (Command action, int movePin) {
        this.position = 0;
        this.action = action;
        this.stepPin = Pin.digitalPin(movePin);
        this.arduino = ArmProtocol.getInstance();
    }

    public MotorDriverHandler (Command action, int movePin, int dirPin) {
        this(action, movePin);
        this.dirPin = Pin.digitalPin(dirPin);
    }

    public MotorDriverHandler (Command action, int movePin, int dirPin, int[] mStepPins) {
        this(action, movePin, dirPin);
        for ( int i = 0; i < mStepPins.length; i ++) {
            this.mStepPins[i] = Pin.digitalPin(mStepPins[mStepPins[i]]);
        }
    }

    public boolean sendCommand (float[] params) {
        Command c = new Command(action.type(), params);
        return sendCommand(c);
    }

    public boolean sendCommand (Command command) {
        if ( arduino.isConnected() ) {
            synchronized (this) {
                command.setTickSteps(stepsPerTick);
                arduino.addCommand(command);
            }
            return true;
        }
        return false;
    }

    public void setMotorSpeed (int rpm)        { this.speed = rpm; }
    public void setMicrosteps (int microsteps) { this.microsteps = microsteps; }
    public void setFaultPin   (int pin)        { faultPin = Pin.digitalPin(pin); }

    public int          getPosition   () { return position; }
    public int          getMotorSpeed () { return speed; }
    public int          getMicrosteps () { return microsteps; }
    public DigitalPin   getStepPin    () { return stepPin; }
    public DigitalPin   getDirPin     () { return dirPin; }
    public DigitalPin[] getMStepPins  () { return mStepPins; }
    public DigitalPin   getFaultPin   () { return faultPin; }
    public Command      getCommand    () { return action; }
    public int          stepsPerTick  () { return stepsPerTick; }

}
