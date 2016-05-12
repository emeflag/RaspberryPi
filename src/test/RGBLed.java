package test;

//import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.platform.PlatformAlreadyAssignedException;

public class RGBLed {

	public static void main(String[] args) throws InterruptedException, PlatformAlreadyAssignedException {
		
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalOutput ledRed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		final GpioPinDigitalOutput ledGrn = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
		final GpioPinDigitalOutput ledBlu = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
		
		final GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

		int wait = 50;
		
		//set all to high to turn off LEDs
		ledRed.high();
		ledGrn.high();
		ledBlu.high();
		
		//Blink RGB LEDs using low and high methods separated by a sleep
		System.out.println("--------------------------------------");
		System.out.println("Test led.high() and led.low() methods");
		System.out.println("--------------------------------------");
		//Blink RED Led
		for (int i=0; i<5; i++)  {			
			//Set low (ground) led is turned on.	
			ledRed.low();
			Thread.sleep(wait);
				
			//Set high (3V) led is turned of because of equal voltage.	
			ledRed.high();
			Thread.sleep(wait);			
		}
		
		//Blink GREEN Led
		for (int i=0; i<5; i++)  {			
			//Set low (ground) led is turned on.	
			ledGrn.low();
			Thread.sleep(wait);
				
			//Set high (3V) led is turned of because of equal voltage.	
			ledGrn.high();
			Thread.sleep(wait);			
		}

		//Blink BLUE Led
		for (int i=0; i<5; i++)  {			
			//Set low (ground) led is turned on.	
			ledBlu.low();
			Thread.sleep(wait);
				
			//Set high (3V) led is turned of because of equal voltage.	
			ledBlu.high();
			Thread.sleep(wait);			
		}
		
		//Blink RGB LEDs using the blink method
		System.out.println("-------------------------------------------");
		System.out.println("Test led.blink(rateRate,exlapseTime) method");
		System.out.println("-------------------------------------------");
		//Lets try the blink method
		ledRed.blink(50,1000);
		Thread.sleep(1050);
		ledRed.high();
		
		ledGrn.blink(50,1000);
		Thread.sleep(1050);
		ledGrn.high();
		
		ledBlu.blink(50,1000);		
		Thread.sleep(1050);
		
		ledRed.high();
		ledGrn.high();
		ledBlu.high();
		
		//Turn on/off the Red LED based on button control		
		System.out.println("----------------------------------------------");
		System.out.println("Turn Red LEN using on/off using button control");
		System.out.println("----------------------------------------------");
		
		boolean wasLow = true;
		int cnt = 0;
		
		while (cnt < 5) {
			if (button.isHigh() && wasLow) {
				wasLow = false;
				ledRed.low();
				cnt++;
			}
			else if (button.isLow()){
				wasLow = true;
				ledRed.high();
			}			
			Thread.sleep(50);
		}

		ledRed.high();
		ledGrn.high();
		ledBlu.high();

		//Turn on/off the Red LED based on button control
		//Push once turn on and leave on
		//Push again and turn off and leave off0
		System.out.println("--------------------------------------------------");
		System.out.println("Turn on Red Led using on/off using button control");
		System.out.println("Push once turn on and leave untill pushed again");
		System.out.println("--------------------------------------------------");
		
		boolean isOn = false;
		wasLow = true;
		cnt = 0;
		while (cnt < 5) {
			if (button.isHigh() && wasLow) {
				wasLow = false;			
				if (isOn) {
					ledRed.high();
					isOn = false;
				}
				else {
					ledRed.low();
					isOn = true;
					cnt++;
				}
			}
			else if (button.isLow()){
				wasLow = true;
			}			
			Thread.sleep(50);
		}

		ledRed.high();
		ledGrn.high();
		ledBlu.high();
		
		
		System.out.println("--------------------------------------------------");
		System.out.println("Try Event Listener ");
		System.out.println("Alternate turning on red and green leds");
		System.out.println("--------------------------------------------------");
				
		//Now we're going to create an event listener for the button
        // create and register gpio pin listener
        button.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if(event.getState().isHigh()){
                      ledRed.low();
                      ledGrn.high();
                      ledBlu.high();
                    }
                    else{
                      ledGrn.low();
                      ledRed.high();
                      ledBlu.high();
                    }
                }
            });
		
        System.out.println("Outside of event listener and sleeping for 30 seconds");
		Thread.sleep(30000);
		System.out.println("Done sleeping");
		
		 // Turn off all the LEDS
		ledRed.high();
		ledGrn.high();
		ledBlu.high();
		
		//Remove the Listener 
		button.removeAllListeners();
		
		System.out.println("--------------------------------------------------");
		System.out.println("Try another Event Listener ");
		System.out.println("Alternate turning on Red, Green, Blue LEDs");
		System.out.println("--------------------------------------------------");
        button.addListener(new GpioPinListenerDigital() {
        	int cnt    = 0;
        	int modCnt = 0;
        	
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // when button is pressed, speed up the blink rate on LED #2
                if(event.getState().isHigh()){
                	modCnt = cnt % 3;
                	cnt++;
                	if (modCnt == 0) {
                	   ledRed.low();
                	   ledGrn.high();
                	   ledBlu.high();
                	}
                	else if (modCnt == 1) {
                	   ledRed.high();
                	   ledGrn.low();
                	   ledBlu.high();
                	}
                	else {
                		ledRed.high();
                  		ledGrn.high();
                  		ledBlu.low();                	   
                	}
                }

            }
        });
		
        System.out.println("Outside of event listener and sleeping for 30 seconds");
		Thread.sleep(30000);
		System.out.println("Done sleeping");
		
		 // Turn off all the LEDS
		ledRed.high();
		ledGrn.high();
		ledBlu.high();
		gpio.shutdown();
	}

}
