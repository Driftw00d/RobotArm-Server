package com.robotarm.core.executable;

import com.robotarm.core.arduino.Command;
import com.robotarm.core.attachable.Tool;
import com.robotarm.core.limbs.Joint;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by higgsy789 on 24/03/2017.
 */
public class ToolTask implements Task {

//    @Override
//    public boolean doTask(Task task) {
//        return false;
//    }
//
//    @Override
//    public boolean stopTask(Task task) {
//        return false;
//    }

    @Override
    public HashMap getTaskInfo(Task task) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }


    public Command getCurrentCmd() {
        return null;
    }

    @Override
    public Command getNextCmd() {
        return null;
    }

    @Override
    public Tool getTool() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public BlockingQueue<Command> getCommands() {
        return null;
    }

}
