/**
 * LedPwm demonstrates use of the PiJ4 interface to the hardware PWM.
 * 
 * In this example the brightness of an LED is adjusted based on the hardware PWM.
 * 
 * I do not understand how the hardware PWM is commanded nor how it works inspite
 * of reading various on-line documents on the topic.
 * 
 * Here is what I have observed:
 * 
 * For PWM_MODE_MS Mode
 * 
 * setting a clock = 0 appears to turn of the LED (which is really setting pin high)
 * 
 * setting clock=1, range=1024: causes LED to continue to blink at about 5-10HZ
 * as it diminishes in brightness as it loops through the pwm values 
 * 
 * setting clock=1, range=100: causes LED to diminish in value with pwm value
 * no blinking of any kind occurs
 * 
 * setting clock=1, range=512: causes LED to continue to blink at high rate 
 * as it diminishes in brightness as it loops through the pwm values
 * 
 * setting clock=1, range=256: causes LED to continue to blink at even higher
 * rate as it diminishes in brightness as it loops through pwm values 
 * 
 * setting clock=1, range=128: causes hardly noticeable blink on LED
 * as it diminishes in brightness as it loops through pwm values
 * 
 * setting clock=1, range=10: causes LED to not blink visibly
 * as it diminishes in brightness as it loops through pwm values
 * 
 * The same behavior as above occurs when clock=4095 (maximum value) with
 * different settings of range.
 * 
 */
package test;

import java.util.Scanner;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.wiringpi.Gpio;

public class LedPwm {

	public static void main(String[] args) throws InterruptedException {
		
		//Create a GPIO Instance
		GpioController gpio =GpioFactory.getInstance();
		
		//Create a pin associated with GPIO_1 that supports PWM control
		 Pin pin = CommandArgumentParser.getPin(
	                RaspiPin.class,    // pin provider class to obtain pin instance from
	                RaspiPin.GPIO_01,  // default pin if no pin argument found
	                args);             // argument array to search in
		 
		 //Sets the pin to PWM control
		 GpioPinPwmOutput pwmLed = gpio.provisionPwmOutputPin(pin);
		 
		 //Mark space is the traditional PWM control
		 Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
		 
		 Scanner input = new Scanner(System.in);
		 
		 int irange = 0;
		 int iclock = 0;
		 int ipwm   = 0;
		 
		 boolean end = false;
		 while(!end) {
			 System.out.print("Enter Clock Value:");
			 String sclock = input.nextLine();
			 System.out.print("Enter Range Value:");
			 String srange = input.nextLine();
			 
			 if(srange.length() != 0) irange = Integer.parseInt(srange);
			 if(sclock.length() != 0) iclock = Integer.parseInt(sclock);
			 
			 Gpio.pwmSetRange(irange);
			 Gpio.pwmSetClock(iclock);
			 
			 int inc = irange/10;
			 if (inc==0) inc=1;
			 
			 for (ipwm=0; ipwm<=irange; ipwm=ipwm+inc) {
				 System.out.println("iclock, irange, ipwm: "+String.valueOf(iclock)+" "+String.valueOf(irange)+" "+String.valueOf(ipwm));
				 pwmLed.setPwm(ipwm);
				 Thread.sleep(250);
			 }
			 
			 System.out.print("Return to continue or type 'end' to finsh:");
			 String str = input.nextLine();
			 if (str.contains("end")) end = true;
		 }
		 
		 input.close();
		 
		 pwmLed.setPwm(irange);
		 pwmLed.setShutdownOptions(true);
		 gpio.shutdown();
	}

}
