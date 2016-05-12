/**
 * The CheckExecutable class tests to verify that a program exists 
 * on the computers system. Compatible for Linux or MacOS systems.
 * Uses the "which" command to test if command  was returned;
 */
package eric.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckExecutable {
	private String programName;  //Program name to test if it exists
	private Process p;
	private boolean existFlag;
	
	/**
	 * Constructor method
	 * @param programName Test to see if programName can be found and is executable. 
	 * The programName can be prepended with a path specification.
	 */
	public CheckExecutable(String programName) {
		
		this.programName = programName;
		
		try {
			//execute the which command
			p = Runtime.getRuntime().exec("which "+ this.programName);

			//get a handle for standard input
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			//set flag to test existence of the program
			//if the input is not null then we have a winner
			existFlag = stdInput.readLine() != null;
			
			//make sure process is gone
			p.destroy();
		} 
		catch (IOException e) {
			System.out.println("Error - Something work with the CheckExecutable Class");
			System.out.println("The Runtime class failed.");
			e.printStackTrace();
		}		
	
	}
	
	/**
	 * Does programName exist and is it executable? Returns 'true' or 'false'.
	 * 
	 * @return Returns true or false if program exists.
	 */
	public boolean isExecutable() {
		return existFlag;
	}
	
	/**
	 * Terminate if the programName does not exist or is not executable.
	 */
	public void ifNotExecutableKill() {
		if (!existFlag) {
			System.out.println("Error - The program: " + programName + " does not exist ");
			System.out.println("Terminate processing");
			System.exit(1);
		}
	}
		
}
