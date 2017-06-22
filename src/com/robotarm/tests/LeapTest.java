package com.robotarm.tests;

import java.io.IOException;

import com.leapmotion.leap.Controller;

public class LeapTest {
	
	public static void main (String[] args) {
		// Create a sample listener and controller
        LeapControllerTest listener = new LeapControllerTest();
        Controller ctrl = new Controller();

        // Have the sample listener receive events from the controller
        ctrl.addListener(listener);
        
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        
        try { System.in.read(); } 
        catch (IOException e) { e.printStackTrace(); }

        // Remove the sample listener when done
        ctrl.removeListener(listener);
	}
}
