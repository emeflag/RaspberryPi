/**
 * ServoSoftPwm demonstrates use of the wiringPi "software PWM" 
 * 
 * In this example we're using softPWM to operate a 3003 Futaba servo.
 * 
 *  We're using the lowest level access to the wiringPi native routines written in C
 *  by Gordon Henderson (http://wiringpi.com)
 *  
 *  The wiringPi native routines are located in the "com.pi4j.wiringpi.Gpio" package
 *  
 * Setup:
 * 
 * The servo is powered with a 2.4 amp 5V DC provided by a powered USB hub.
 * 
 * GPIO pin 1 (Wiring Pi numbering convention) controls the servo
 * 
 * Wiring: 
 * 
 * A 1K OHM resistor is placed between the GPIO pin and the servo control wire.
 * 
 * For ground be sure the raspberry pi ground pin, USB ground, and servo ground are
 * conjoined. Setup does not work unless all are properly grounded.
 * 
 * 
 * Results:
 * 
 * For a pwmRange of 100 (cycles/second?) the following values were derived by
 * experimentation.
 * 
 * value 1  = maximum rotation angle in clockwise direction
 * value 23 = maximum rotation angle in counterclockwise direction
 * value 11 = approximate mid-point of servo movement. 
 * value  0 = no movement, servo is disabled sitting in its current position
 * 
 */
package test;

import java.util.Scanner;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

public class ServoSoftPwm {

	public static void main(String[] args) {
		
		// initialize wiringPi
        Gpio.wiringPiSetup();
        
        int pin = 1; //GPIO pin 1 commands the servo
        int pwmRange = 100;	//Range of values for the softPWM software
        
        int initialValue = 0; //servo is disabled 
        SoftPwm.softPwmCreate(pin, initialValue, pwmRange); //create the softPwm
        
        Scanner input = new Scanner(System.in); //Read the values to give to softPwm
        
        boolean done = false;
        while (!done) {
        	System.out.print("Enter value:");
        	String sValue = input.nextLine();  //Get the PWM value
        	
        	if(sValue.length() <= 0) {
        		done = true; //If empty string, we're done
        	}
        	else {
        		int value = Integer.parseInt(sValue);
        		SoftPwm.softPwmWrite(pin, value);  //Set the PWM value.      		
        	}
        	
        }
        SoftPwm.softPwmStop(pin); //Stop softPwm
        input.close(); //close out the reader
	}
}
