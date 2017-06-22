package com.robotarm.remoteconnection;

import com.robotarm.util.Helper;

/**
 *
 */
public class ConnectionProtocol {

    private Helper h = Helper.getInstance();

    public enum State {
        WAITING(0), SENT_MESSAGE(1);
        private int value ;
        State ( int value ) { this . value = value ; }
        public  int val ( ) { return value; }
    }

    private State state = State.WAITING;

    public ConnectionProtocol () {

    }

    public String processInput(String input) {
        String output = null;
        h.confirm(input);
        switch (state.val()) {
            case 0:
                output = "something";
                state = State.SENT_MESSAGE;
                break;

            case 1:

                break;
        }
        return output;
    }

    public State state () { return state; }

}
