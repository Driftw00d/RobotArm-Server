package com.robotarm.core.arduino;

/**
 * Created by higgsy789 on 24/03/2017.
 */
public class Command {

    private static int commands = 0;
    private int commandId;
    private final String idString = "?id=";
    private final String cmd;
    private final Type type;
    private float[] params;
    private boolean isValid;
    private int stepsPerTick;

    public enum Type {
        MOVE_SHOULDER_X, MOVE_SHOULDER_Y, MOVE_ELBOW_Y, MOVE_WRIST_X, MOVE_WRIST_Y,
        READ_ACCEL, READ_GRIP_PRES, READ_ERROR,
        TOOL_GRAB, TOOL_RELEASE
    }

    public Command (Type type, String cmdString) {
        this.commandId = commands++;
        this.type = type;
        this.cmd = cmdString;
        isValid = false;
    }

    public Command (Type type, float[] params) {
        this(type);
        this.params = params;
        isValid = isValid();
    }

    public Command (Type type) {
        this.commandId = commands++;
        this.type = type;

        // Set cmd string based on type.
        // Switch stmnt for now, change to for loop below when 'getCommandString' method is implemented
        switch (type) {
            case MOVE_SHOULDER_X:
                this.cmd = Commands.MOVE_SHOULDER_X.cmd();
                break;
            case MOVE_SHOULDER_Y:
                this.cmd = Commands.MOVE_SHOULDER_Y.cmd();
                break;
            case MOVE_ELBOW_Y:
                this.cmd = Commands.MOVE_ELBOW_Y.cmd();
                break;
            case MOVE_WRIST_X:
                this.cmd = Commands.MOVE_WRIST_X.cmd();
                break;
            case MOVE_WRIST_Y:
                this.cmd = Commands.MOVE_WRIST_Y.cmd();
                break;
            default:
                this.cmd = null;
                //throw new Exception("Command not found");
                break;
        }
            // More extensible way of doing it
//        for (Type t : Type.values()) {
//            if (type == t) {
//                this.cmd = Commands.Type.cmd();
//                break;
//            }
//        }
    }

    public String cmdWithParams () {
        if (params.length > 0) {
            String cmdWithParams = cmd + '(';
            for (int i = 0; i < params.length; i++) {
                cmdWithParams += String.valueOf((int)params[i]);
                if (i != params.length - 1) {
                    cmdWithParams += ',';
                }
            }
            cmdWithParams += ')';
            return cmdWithParams;
        } else {
            return null;
        }
    }

    public String cmdWithId () {
        if (params.length > 0) {
            String cmdWithparams = cmdWithParams();
            return cmdWithparams + idString + commandId;
        } else {
            return null;
        }
    }

    public static String cmd (Type type, int[] parameters) {
        Command c = new Command(type);
        String cmdWithParams;
        if (parameters.length > 0) {
            cmdWithParams = c.cmd() + '(';
            for (int i = 0; i < parameters.length; i++) {
                cmdWithParams += String.valueOf(parameters[i]);
                if (i != parameters.length - 1) cmdWithParams += ',';
            }
            cmdWithParams += ')';
            return cmdWithParams;
        } else {
            return null;
        }
    }

    public boolean isValid () {
        return (params.length > 0 && cmd != null);
    }

    public float   param     (int index) { return params[index]; }
    public float[] params    () { return params; }
    public int     id        () { return commandId; }
    public int     tickSteps () { return stepsPerTick; }
    public Type    type      () { return type; }
    public String  cmd       () { return cmd; }

    public void setTickSteps (int stepsPerTick) { this.stepsPerTick = stepsPerTick; }
    public void    setParams (float params[]) { this.params = params; isValid = true; }

}
