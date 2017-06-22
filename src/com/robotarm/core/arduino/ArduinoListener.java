package com.robotarm.core.arduino;

import com.robotarm.RobotArm;
import com.robotarm.core.executable.Task;
import com.robotarm.util.Helper;
import org.ardulink.core.Link;
import org.ardulink.core.events.CustomEvent;
import org.ardulink.core.events.CustomListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Created by higgsy789 on 31/03/2017.
 */
public class ArduinoListener implements CustomListener {

    Helper h = Helper.getInstance();

    @Override
    public void customEventReceived(CustomEvent customEvent) {
        String message = customEvent.getMessage();

        if (message.startsWith("RAWMONITOR") && RobotArm.DEBUG) {
            message = message.substring(11);
            h.confirm(message);
        }
    }
}
