package com.robotarm.tests;

import com.robotarm.remoteconnection.ConnectionServer;

/**
 *
 */
public class RemoteConnectionTest {

    public static void main(String[] args) {

        ConnectionServer server = new ConnectionServer(44444);

        while (true) server.listen();

    }
}
