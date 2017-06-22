package com.robotarm.core.executable;

import com.robotarm.core.arduino.Command;
import com.robotarm.core.attachable.Tool;
import com.robotarm.core.limbs.Joint;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface Task {

//    int totalTasks = 0;
//    Type type = Type.GENERIC_TASK;

    public enum Type { GENERIC_TASK, MOVEMENT_TASK, TOOL_TASK, SENSOR_TASK }

    HashMap getTaskInfo (Task task); // This could be a dictionary?
    public Type    getType       ();
    public Command getCurrentCmd ();
    public Command getNextCmd    ();
    public Tool    getTool       ();
    public boolean isValid       ();
    public BlockingQueue<Command> getCommands ();

}
