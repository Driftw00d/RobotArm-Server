package com.robotarm.tests;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.leapmotion.leap.*;
import com.robotarm.util.Helper;

/**
 *  This is mostly a test class
 *
 *
 */
public class LeapControllerTest extends Listener {

	public void main(String[] args) {

	}

	public void writeFramesToFile (Controller controller) {
    	int framesToWrite = 9;
    	try
    	{
    	  Path outPath = Paths.get("frames.data");
    	  OutputStream out = Files.newOutputStream(outPath);
    	  for (int f = framesToWrite; f >= 0; f--) {
    	      Frame frameToSerialize = controller.frame(f);
    	      byte[] serialized = frameToSerialize.serialize();
    	      out.write( ByteBuffer.allocate(4).putInt(serialized.length).array() );
    	      out.write(serialized);
    	  }
    	  out.flush();
    	  out.close();
    	} catch (IOException e)
    	{
    	  System.out.println("Error writing to file: " + e);
    	}
    }
    
    public Vector getHandOrientation (Hand hand) {
    	float pitch = hand.direction().pitch();
    	float yaw = hand.direction().yaw();
    	float roll = hand.palmNormal().roll();
    	System.out.printf("pitch: %s \t yaw: %s \t roll: %s\n", pitch, yaw, roll);
    	Vector params = new Vector(pitch, yaw, roll);
    	return params;
    }
    
    public void getFingerData (Hand hand) {
	   	//PointableList pointables = hand.pointables();
	   	//FingerList fingers = hand.fingers();
	   	//Finger rightFinger = hand.fingers().rightmost();
	   	Finger frontFinger = hand.fingers().frontmost();
	    System.out.printf("Tip: %s \t Vel : %s\n", 
	    					frontFinger.tipPosition(), 
	    					frontFinger.tipVelocity());

    }
    
    public void getHandData (Hand hand) {
    	Vector pos = hand.palmPosition();
       	Vector vel = hand.palmVelocity();
        Vector dir = hand.direction();
        System.out.printf("Pos: %s \t\t Vel: %s \t\t Dir: %s\n", pos, vel, dir);
        //Vector params = new Vector(pos, );
    }
    
    public void onConnect(Controller controller) {
        System.out.println("Leap Motion Connected");

		controller.enableGesture(Gesture.Type.TYPE_SWIPE);

    }
    
    public void onDisconnect (Controller controller){
        System.out.println("Leap: Disconnected");
    }
    
    public void onFocusGained (Controller controller){
        System.out.println("Leap: Focus gained");
    }
    
    public void  onFocusLost (Controller controller){
        //System.out.println("Leap: Focus lost");
    }
    
    public void  onImages (Controller controller){
        System.out.println("Leap: New images available");
    }
    
    public void onInit (Controller controller){
        //System.out.println("Leap Initialized");
    }

    public void onExit (Controller controller) {

	}
}
