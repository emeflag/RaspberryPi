/**
 * LedSoftPwm demonstrates use of the the wiringPi "software PWM" 
 * 
 * In this example we cycle through all the colors of an RGB LED.
 * 
 *  We're using the lowest level access to the wiringPi native routines written in C
 *  by Gordon Henderson (http://wiringpi.com)
 *  
 *  The wiringPi native routines are located in the "com.pi4j.wiringpi.Gpio" package
 *  
 *  This code appears to use ~0.7% of a Raspberry Pi 3 CPU.
 */
package test;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

public class LedSoftPwm {

	public static void main(String[] args) throws InterruptedException {


		// initialize wiringPi library
		Gpio.wiringPiSetup(); //Another way to call: com.pi4j.wiringpi.Gpio.wiringPiSetup();
		
        int pinRed = 1; //Controls Red LED
        int pinGrn = 4; //Controls Green LED
        int pinBlu = 5; //Controls Blue LED
        
        int inc = 50; //increments by 
        
        int pwmRange     = 100;
        int initialValue = 1; //This turns off the led
        
        //Create a soft PWM for each of the LEDs
        SoftPwm.softPwmCreate(pinRed, initialValue, pwmRange);
        SoftPwm.softPwmCreate(pinGrn, initialValue, pwmRange);
        SoftPwm.softPwmCreate(pinBlu, initialValue, pwmRange);
        
        
        //In reverse and forward order cycle through all the RGB permutations
        //of 0:50:100 ranges.
        
        //Currently only looping once
        for (int iLoop=0; iLoop<1; iLoop++) {
        	
        	//Forward direction
        	for (int iRed=0; iRed<=pwmRange; iRed=iRed+inc) {
        		SoftPwm.softPwmWrite(pinRed,iRed);
        		
        		for (int iGrn=0; iGrn<=pwmRange; iGrn=iGrn+inc) {
        			SoftPwm.softPwmWrite(pinGrn,iGrn);
        			
        			for (int iBlu=0; iBlu<=pwmRange; iBlu=iBlu+inc) {
        				SoftPwm.softPwmWrite(pinBlu,iBlu);
        				Thread.sleep(500);
        			}
        		}       		
        	}
        	
        	//Reverse direction
        	for (int iRed=pwmRange; iRed>=0; iRed=iRed-inc) {
        		SoftPwm.softPwmWrite(pinRed,iRed);
        		
        		for (int iGrn=pwmRange; iGrn>=0; iGrn=iGrn-inc) {
        			SoftPwm.softPwmWrite(pinGrn,iGrn);
        			
        			for (int iBlu=pwmRange; iBlu>=0; iBlu=iBlu-inc) {
        				SoftPwm.softPwmWrite(pinBlu,iBlu);
        				Thread.sleep(500);
        			}
        		}
        	}      
        }
        
        //Stop the software PWMs
        SoftPwm.softPwmStop(pinRed);
        SoftPwm.softPwmStop(pinGrn);
        SoftPwm.softPwmStop(pinBlu);
        
        //Set pins high to turn off the positive-cathode LED
        Gpio.digitalWrite(pinRed, true);
        Gpio.digitalWrite(pinGrn, true);
        Gpio.digitalWrite(pinBlu, true);       
	}		
}
