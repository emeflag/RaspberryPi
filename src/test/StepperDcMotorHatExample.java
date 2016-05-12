package test;

import com.pi4j.component.adafruithat.AdafruitDcMotor;
import com.pi4j.component.adafruithat.AdafruitMotorHat;
import com.pi4j.component.adafruithat.AdafruitStepperMotor;
import com.pi4j.component.adafruithat.StepperMode;

public class StepperDcMotorHatExample {

	public static void main(String[] args) {
		
		final int motorHATAddress = 0X60;
		AdafruitMotorHat motorHat = new AdafruitMotorHat(motorHATAddress);
		
		/*
		 * Because the Adafruit motor HAT uses PWMs that pulse independently of
		 * the Raspberry Pi the motors will keep running at its current direction
		 * and power levels if the program abnormally terminates. 
		 * A shutdown hook like the one in this example is useful to stop the 
		 * motors when the program is abnormally interrupted.
		 */		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    	System.out.println("Turn off all motors");
		    	motorHat.stopAll();		    	
		    }
		 });

		AdafruitDcMotor motorLeft  = motorHat.getDcMotor("M1");
		AdafruitDcMotor motorRight = motorHat.getDcMotor("M2");
		AdafruitStepperMotor stepper = motorHat.getStepperMotor("SM2");
		
		
		//Set Stepper Mode to SINGLE_PHASE
		stepper.setMode(StepperMode.HALF_STEP);
				
		//Set the number of motor steps per 360 degree
		//revolution for this stepper mode.
		stepper.setStepsPerRevolution(200);
		
		motorLeft.speed(1.0f);
		motorRight.speed(1.0f);
		stepper.step(2000);

		motorLeft.speed(-1.0f);
		motorRight.speed(-1.0f);
		stepper.step(-2000);
		
		motorHat.stopAll();
		
	}


}
