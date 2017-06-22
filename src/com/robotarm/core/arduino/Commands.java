package com.robotarm.core.arduino;

/**
 * Created by higgsy789 on 24/03/2017.
 * Commands defined in the Robot arm protocol
 */
public class Commands {

    // Motors
    public static Command MOVE_SHOULDER_X = new Command(Command.Type.MOVE_SHOULDER_X, "mshX");
    public static Command MOVE_SHOULDER_Y = new Command(Command.Type.MOVE_SHOULDER_Y, "mshY");
    public static Command MOVE_ELBOW_Y = new Command(Command.Type.MOVE_ELBOW_Y, "melY");
    public static Command MOVE_WRIST_X = new Command(Command.Type.MOVE_WRIST_X, "mwrX");
    public static Command MOVE_WRIST_Y = new Command(Command.Type.MOVE_WRIST_Y, "mwrY");

    // Sensors
    public static Command READ_ACCEL = new Command(Command.Type.READ_ACCEL, "racc");
    public static Command READ_GRIP_PRESS = new Command(Command.Type.READ_GRIP_PRES, "racc");
    public static Command READ_ERROR = new Command(Command.Type.READ_ERROR, "rerr");

    // Tools
    public static Command TOOL_GRAB = new Command(Command.Type.TOOL_GRAB, "grab");
    public static Command TOOL_RELEASE = new Command(Command.Type.TOOL_RELEASE, "rels");

}
