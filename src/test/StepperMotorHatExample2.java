package test;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.pi4j.component.adafruithat.AdafruitMotorHat;
import com.pi4j.component.adafruithat.AdafruitStepperMotor;
import com.pi4j.component.adafruithat.StepperMode;



/**
 * This example program demonstrates a simple thread scheme for moving two stepper motors simultaneously.
 * @author Eric Eliason
 *
 */
public class StepperMotorHatExample2 {

	/**
	 * This nested class provides a simple example of how an Adafruit stepper motor could be
	 * encapsulated into a thread. In this example the AdafruitStepperMotor step() method 
	 * is run in the thread.
	 * 
	 * @author Eric Eliason
	 *
	 */
	static class StepThread implements Runnable {
		public Thread thread; 
		private AdafruitStepperMotor stepper;
		private long steps;
		private CountDownLatch latch; //used to wait for the completion of the thread
		
		/**
		 * Initialize the Thread 
		 * @param stepper A stepper instance
		 * @param steps Number of steps to move.
		 */
		public StepThread(AdafruitStepperMotor stepper, long steps) {
			this.stepper = stepper;
			this.steps = steps;
			latch = new CountDownLatch(1); //latch used to indicate when thread has completed it's task
			thread = new Thread(this); //create the thread but let main() start it.
		}
		
		/**
		 * The stepper motor step() method is executed in the thread.
		 */
		@Override
		public void run() {
			stepper.step(steps);
			latch.countDown();	 //latch count is now zero		
		}
		
		/**
		 * Through a latch mechanism the method will wait for completion of the thread.
		 */
		public void waitForFinished() {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) throws InterruptedException {		
		final int motorHATAddress = 0X60;
		AdafruitMotorHat motorHat = new AdafruitMotorHat(motorHATAddress);
		AdafruitStepperMotor s1;
		AdafruitStepperMotor s2;
		
		//Because we only use a thread instance one then employ a list of threads instantiations
		List<StepThread> s1Thread = new ArrayList<StepThread>();
		List<StepThread> s2Thread = new ArrayList<StepThread>();
		
		
		
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
			
		
		
		//Create an instances for the stepper motors.
		s1 = motorHat.getStepperMotor("SM1");
		s2 = motorHat.getStepperMotor("SM2");
		
		//Set Stepper Mode to SINGLE_PHASE
		s1.setMode(StepperMode.SINGLE_PHASE);
		s2.setMode(StepperMode.SINGLE_PHASE);
		
		//Set the number of motor steps per revolution for this stepper mode.
		s1.setStepsPerRevolution(200);
		s2.setStepsPerRevolution(200);
		
		//Sequence of steps for the two stepper motors.
		int[] s1StepSeq = new int[] {200,-10,  20,  500, -2000,  20, -20,  20, -20};
		int[] s2StepSeq = new int[] {400, 10, 100, 2000,    50, -50,  50, -50,  50, -50};
		
		boolean s1Proc;
		boolean s2Proc;
		boolean done = false;
		int cnt = 0;
		
		while (!done) {
			if (cnt < s1StepSeq.length) {
				s1Proc = true;
				long stepper1Steps = s1StepSeq[cnt];
				s1Thread.add(new StepThread(s1,stepper1Steps));
				s1Thread.get(cnt).thread.start();
			}
			else s1Proc = false;

			
			if (cnt < s2StepSeq.length) {
				long stepper2Steps = s2StepSeq[cnt];
				s2Thread.add(new StepThread(s2,stepper2Steps));
				s2Thread.get(cnt).thread.start();
				s2Proc = true;
			}
			else s2Proc = false;
			
			if (s1Proc) s1Thread.get(cnt).waitForFinished();
			if (s2Proc) s2Thread.get(cnt).waitForFinished();
			
			cnt++;
			if (!s1Proc && !s2Proc) done = true;
			
		}
		
		
		System.out.println("ALL DONE!");
		
		
	}
}
