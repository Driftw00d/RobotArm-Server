package com.robotarm.remoteconnection;

import com.robotarm.RobotArm;
import com.robotarm.util.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Server side connection to receive tasks and commands from the client.
 */
public class ConnectionServer implements Runnable {

    private boolean keepAlive;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private String inputLine, outputLine;

    private Helper h = Helper.getInstance();

    public ConnectionServer(int port) {
        try {
            keepAlive = true;
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            h.err("Could not listen on port " + port);
            if (RobotArm.DEBUG) e.printStackTrace();
        }
    }

    public boolean connect () {
        try {
            clientSocket = serverSocket.accept();
            return clientSocket.isConnected();
        } catch (Exception e) {
            h.err("Could not connect to client");
            if (RobotArm.DEBUG) e.printStackTrace();
            return false;
        }
    }

    public void close () {
        keepAlive = false;
        h.sleep(10);
        try {
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            h.err("Error closing server connection");
            if (RobotArm.DEBUG) e.printStackTrace();
        }
    }

    public void listen () {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            ConnectionProtocol protocol = new ConnectionProtocol();

            outputLine = protocol.processInput(in.readLine());
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = protocol.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye."))
                    break;
            }

        } catch (IOException e) {
            h.err("Connection Server failure");
            if (RobotArm.DEBUG) e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (keepAlive) {
            if (clientSocket == null) connect();
            if (clientSocket.isConnected()) {
                listen();
                h.sleep(10);
            } else {
                h.err("Not connected to client");
            }
        }
    }
}
