package test;

import com.pi4j.component.adafruithat.AdafruitDcMotor;
import com.pi4j.component.adafruithat.AdafruitMotorHat;
import com.pi4j.component.adafruithat.AdafruitServo;
import com.pi4j.component.adafruithat.AdafruitServoHat;
import com.pi4j.component.adafruithat.AdafruitStepperMotor;
import com.pi4j.component.adafruithat.StepperMode;

public class ServoDcMotorStepperMotorExample {
	static AdafruitServoHat servoHat;
	static AdafruitMotorHat motorHat;
	
	
	public static void main(String[] args) {
		
		final int servoHATAddress = 0X40;
		final int motorHATAddress = 0X60;
		
		servoHat = new AdafruitServoHat(servoHATAddress);
		motorHat = new AdafruitMotorHat(motorHATAddress);
		
		AdafruitServo   servo1  = servoHat.getServo("S01");
		AdafruitDcMotor motor1  = motorHat.getDcMotor("M1");
		AdafruitDcMotor motor2  = motorHat.getDcMotor("M2");
		AdafruitStepperMotor stepper = motorHat.getStepperMotor("SM2");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 
		    	System.out.println("Turn off all servos and motors");
		    	servoHat.stopAll();
		    	motorHat.stopAll();
		    }
		 });
		
		stepper.setMode(StepperMode.SINGLE_PHASE);
		
		for (int i=0; i<10; i++) {
			servo1.setPosition( 1.0f * (float)(i%2));
			motor1.speed((float) (i-5) / (float) 5.0);
			motor2.speed((float) (i-5) / (float) 5.0);
			
			long steps = 200;
			if (i%2 == 1) steps = -steps;
			stepper.step(steps); 
		}

	}

}
