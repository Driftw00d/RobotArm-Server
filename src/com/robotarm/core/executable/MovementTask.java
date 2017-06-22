package com.robotarm.core.executable;

import com.robotarm.RobotArm;
import com.robotarm.core.arduino.Command;
import com.robotarm.core.attachable.Tool;
import com.robotarm.core.limbs.Joint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by higgsy789 on 24/03/2017.
 */
public class MovementTask implements Task, Serializable {

    private static int totalTasks = 0;
    private int id;
    private BlockingQueue<Command> waypoints;
    private Command currentCmd;
    private Tool    tool; // for moving the tool only, tool actions are in ToolTask
    private Type    type;
    boolean isValid = false;

    byte completion = 0;
    long timeElapsed = 0;

    public MovementTask () {
        this.type = Type.MOVEMENT_TASK;
        this.id = totalTasks++;
        this.waypoints = new ArrayBlockingQueue<>(100);
    }

    public MovementTask (Command action) {
        this();
        this.waypoints.add(action);
    }

    public MovementTask (Command type, Tool tool) {
        this(type);
        this.tool = tool;
    }

    public MovementTask (List<Command> commands) {
        this();
        for (Command c : commands) {
            this.waypoints.add(c);
        }
        try {
            currentCmd = waypoints.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public HashMap getTaskInfo (Task task) {
        long estCompleteTime = 0;
        HashMap jobStats = new HashMap(4);

        jobStats.put("Type", currentCmd.type());
        jobStats.put("Completion", completion);
        jobStats.put("Elapsed Time", timeElapsed);
        jobStats.put("Estimated Completion time", estCompleteTime);
        return jobStats;
    }

    public Command getNextCmd () {
        try {
            return waypoints.take();
        } catch (InterruptedException e) {
            if (RobotArm.DEBUG) e.printStackTrace();
        }
        return null;
    }

    public boolean isValid() {
        isValid = false;
        if (currentCmd != null) {
            isValid = currentCmd.isValid();
        } else if ( waypoints.remainingCapacity() > 0) {
            isValid = true;
        }
        return isValid;
    }

    public int     getId         () { return id; }
    public Command getCurrentCmd () { return currentCmd; }
    public Tool    getTool       () { return tool; }
    public Type    getType       () { return type; }
    public BlockingQueue<Command> getCommands () { return waypoints; }

    public void  setTool      (Tool t) { tool = t; }
    public void  addWaypoint  (Command action) { this.waypoints.add(action); }
    public void  addWaypoints (List<Command> waypoints) { for (Command c : waypoints) this.waypoints.add(c); }

}
