package com.robotarm.tests;

import com.robotarm.core.arduino.Command;
import com.robotarm.core.arduino.Commands;
import com.robotarm.core.executable.MovementTask;
import com.robotarm.core.limbs.Shoulder;
import com.robotarm.util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class MovementTaskTest {

    static Helper h = Helper.getInstance();
    static Random r = new Random();

    public static void main(String[] args) {
        runTest();
    }

    public static boolean runTest () {

        Shoulder shoulder = new Shoulder();

        Command cmd0 = new Command(Command.Type.MOVE_SHOULDER_Y, Commands.MOVE_SHOULDER_Y.cmd());
        Command cmd1 = new Command(Command.Type.MOVE_SHOULDER_X, Commands.MOVE_SHOULDER_X.cmd());
        Command cmd2 = new Command(Command.Type.MOVE_ELBOW_Y, Commands.MOVE_ELBOW_Y.cmd());
        Command cmd3 = new Command(Command.Type.MOVE_WRIST_X, Commands.MOVE_WRIST_X.cmd());

        List<Command> cmds = new ArrayList<Command>();
        cmds.add(cmd0);
        cmds.add(cmd1);
        cmds.add(cmd2);
        cmds.add(cmd3);

        for (Command cmd : cmds) {
            cmd.setParams(getRandParams(r.nextInt(2) + 1, 360));
        }

        MovementTask mv = new MovementTask(cmds);

        h.println(String.valueOf(mv.getCommands().remainingCapacity()));
        h.println(mv.getCurrentCmd().cmd());
        h.println("");
        h.println(mv.getCurrentCmd().cmdWithParams());
        h.println("");
        h.println(mv.getCurrentCmd().cmdWithId());

        //MovementTask mv2 = new MovementTask(cmds);

        //shoulder.doTask(mv);
        //shoulder.doTask(mv2);

        h.sleep(1000);
        return false;
    }

    public static float[] getRandParams (int numParams, int bound) {
        float[] params = new float[numParams];
        for ( int i = 0; i < params.length; i++ ) {
            params[i] = r.nextInt(bound);
        }
        return params;
    }

}
