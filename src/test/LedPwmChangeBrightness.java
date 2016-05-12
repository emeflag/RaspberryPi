/**
 * LedPwmChangeBrightness demonstrates the use of the hardware PWM.
 * 
 * It blinks the LED my cycling through various values of pwm.
 */
package test;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;

public class LedPwmChangeBrightness {

	public static void main(String[] args) throws InterruptedException {
		//Create a GPIO Instance
		GpioController gpio =GpioFactory.getInstance();
		
		//Create a pin associated with GPIO_1 that supports PWM control
		 Pin pin = CommandArgumentParser.getPin(
	                RaspiPin.class,    // pin provider class to obtain pin instance from
	                RaspiPin.GPIO_01,  // default pin if no pin argument found
	                args);             // argument array to search in
		 
		 
		 GpioPinPwmOutput pwmLed = gpio.provisionPwmOutputPin(pin);
		 
		 //Mark space is the traditional PWM control
		 com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
		 
		 int iclock = 1023;
		 int irange = 100;
		 
		 com.pi4j.wiringpi.Gpio.pwmSetClock(iclock);
		 com.pi4j.wiringpi.Gpio.pwmSetRange(irange);
		 
		 for (int j=0; j<20; j++) {
			 
			 for(int i=0; i<=irange; i++) {
				 pwmLed.setPwm(i);
				 Thread.sleep(5);
			 }
		 
			 for(int i=irange; i>0; i--) {
				 pwmLed.setPwm(i);
				 Thread.sleep(5);
			 }
		 }
		 
		 pwmLed.setPwm(irange);
		 gpio.shutdown();
	}

}
