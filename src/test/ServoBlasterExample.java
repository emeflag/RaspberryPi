/**
 * ServoBlasterExample tests the Pi4J interface to the ServoBlaster driver created by Richard Ghirst 
 * 
 * Note: ServoBlaster must first be started with servod command
 * currently located here: ~/PiBits/ServoBlaster/user/servod
 * 
 * ServoBlaster is a driver to a software PWM implementation designed to smoothly operate a servo
 * ServoBlaster: https://github.com/richardghirst/PiBits/tree/master/ServoBlaster
 * 
 * The Pi4J interface is the "RPIServoBlasterProvider" implementation.
 * 
 * In this example:
 *  1) The GPIO_01 pin is used to command the servo
 *  2) A ServoProvider is created from the RPIServoBlasterProvider implementation.
 *  3) A ServoDriver is created with the designated pin and ServoProvider.
 *  4) The first loop goes through the 50ms to 250ms range of servo positions
 *  5) The second loop moves the servo between its extreme positions.
 */
package test;

import java.io.IOException;
import com.pi4j.component.servo.ServoDriver;
import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.RPIServoBlasterProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;

public class ServoBlasterExample {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		//Declare a ServoProvider using the RPIServoBlasterProvider implementation.
		ServoProvider servoProvider = new RPIServoBlasterProvider();
		
		//Declare a pin associated with GPIO_01 pin.
		Pin pin = CommandArgumentParser.getPin(
                RaspiPin.class,    // pin provider class to obtain pin instance from
                RaspiPin.GPIO_01,  // default pin if no pin argument found
                args);  
		
		
		//Create a servo driver with the ServoProvider and associated pin.
		ServoDriver servo =  servoProvider.getServoDriver(pin);
		
		//Loop 1 - loop through the incremental servo positions.
		for (int l=0; l<5; l++) {
			//minimum servo position at 50ms (maximum clockwise position)
			//maximum servo position at 250ms (maximum counterclockwise position)
			//minimum increment is 10ms for ServoBlaster
			//wait 100ms between each movement
			for (int iw=50; iw<=250; iw=iw+10) {
				servo.setServoPulseWidth(iw);
				Thread.sleep(100);
			}
			
			for (int iw=250; iw>=50; iw=iw-10) {
				servo.setServoPulseWidth(iw);
				Thread.sleep(100);
			}
		}
		
		//Loop 2 - loop moves from  maximum counterclockwise to maximum clockwise positions
		//Apparently the reported Pulse Resolution (100) appears to be incorrect as the
		//pulse resolution for the ServoBlaster is 10ms.
		for (int l=0; l<5; l++) {			
			servo.setServoPulseWidth(250);
			Thread.sleep(1000);
			System.out.println("PulseWidth: "+String.valueOf(servo.getServoPulseWidth()));
			System.out.println("PulseResoution: "+String.valueOf(servo.getServoPulseResolution()));
			
			servo.setServoPulseWidth(50);
			System.out.println("PulseWidth: "+String.valueOf(servo.getServoPulseWidth()));
			System.out.println("PulseResoution: "+String.valueOf(servo.getServoPulseResolution()));
			Thread.sleep(1000);
		}
		

	}

}
