/**
 * The CanonCamera class acquires images and sets operating modes for a Canon camera. 
 * 
 * It can capture a single image or a sequence of images separated in time.
 * 
 * Let the Raspberry Pi do the image acquisition sequences.
 * 
 * It can set the following camera settings:
 *   Shutter Speed (Manual and TV modes only)
 *   Aperture (Manual and AV modes only)
 *   Exposure Compensation (AV and TV modes only)
 *   ISO (all camera modes)
 *   Metering Mode
 *   White Balance
 *   
 *   Acquired images are always stored on the Canon SD card, currently there is no mechanism
 *   to automatically download images to pi although it could easily be coded
 *    
 *   When taking a sequence of images be sure to allow sufficient time interval between 
 *   each observation.
 *   
 *   The class interfaces with "ghpoto2" command using the RunTime() Class to execute
 *   camera commands.
 *  
 */
package eric.camera.canon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import eric.utility.CheckExecutable;

public class CanonCamera {
	/**
	 * The auto exposure returns values: "P", "TV", "AV", "Manual", "Bulb"
	 */
	public  String   autoExposureMode; 

	private Process p;
	private boolean verbose = false;
	private String program = "/usr/bin/gphoto2";
		
	/**
	 * Get auto exposure mode setting, verify the "gphoto2" program exists, and
	 * set verbose flag.
	 * @param verbose Verbose flag (true,false)
	 */
	public CanonCamera(boolean verbose) {
		this.verbose = verbose;
		//make sure "espeak" is properly installed

  		new CheckExecutable(program).ifNotExecutableKill();
		getAutoExposureMode();
	}
	
	/**
	 * Get auto exposure mode settings and verify "gphoto2" program exists.
	 */
	public CanonCamera() {
		verbose = false;
		
		new CheckExecutable(program).ifNotExecutableKill();
		getAutoExposureMode();
	}

	/**
	 * Take an image observation on the Canon camera. 
	 * Be sure to use the CanonCamera.waitFor() method to wait for image command
	 * to complete
	 */
	public  void image() {	
		
		String command = program + " --set-config capturetarget=1 --capture-image";

		if (verbose) System.out.println(command);
		
		try {
			//Acquire an image 
			p = Runtime.getRuntime().exec(command);						
		} 
		catch (IOException  e) {
			System.out.println("Error - Something wrong with CanonCamera.image() method");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Take a set of image observations separated by a time interval.
	 * Use the CanonCamera.waitFor() method to wait for end of image command.
	 * 
	 * @param numberFrames Number of image frames to acquire
	 * @param timeInterval Time in seconds between observations. Be sure to leave sufficient time 
	 * for long exposures.
	 */
	public  void imageSequence(int numberFrames, int timeInterval) {		
		String command = program + " --set-config capturetarget=1 --capture-image";
		
		command = command + " --frames=" + String.valueOf(numberFrames);
		command = command + " --interval=" + String.valueOf(timeInterval);
		
		if (verbose) System.out.println(command);
		
		try {
			//Acquire an image sequences
			p = Runtime.getRuntime().exec(command);					
		} 
		catch (IOException  e) {
			System.out.println("Error - Something wrong with CanonCamera.image() method");
			e.printStackTrace();
		}		
		
	}
	
	/**
	 * Wait for the image or image sequence to complete before continuing.
	 */
	public void waitFor() {
		try {
			p.waitFor();
		} 
		catch (InterruptedException e) {
			System.out.println("Error - Something wrong waiting for end of CanonCamera RunProcess() class.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Kill the RunTime connection.
	 */
	public void destroy() {
		p.destroy();
	}
	
	/**
	 * Set the shutter speed for the Canon camera. The shutter speed can only be set for auto
	 * exposure modes "TV" and "Manual".
	 * @param shutterSpeed Options: "0", "25", "20", "15", "13", "10", "8", "6", "5", "4", "3.2", 
	 * "2.5", "2", "1.6", "1.3", "1", "0.8", "0.6", "0.5", "0.4", "0.3", "1/4", "1/5", "1/6", 
	 * "1/8", "1/10", "1/13", "1/15", "1/20", "1/25", "1/30", "1/40", "1/50", "1/60", "1/80", 
	 * "1/100", "1/125", "1/160", "1/200", "1/250", "1/320", "1/400", "1/500", "1/640", 
	 * "1/800", "1/1000", "1/1250", "1/1600", "1/2000", "1/2500", "1/3200", "1/4000", 
	 * "1/5000", "1/6400", "1/8000"
	 */
	public void setShutterSpeed(String shutterSpeed) {
		String[] options = new String[] {
				"0", "25", "20", "15", "13", "10", "8", "6", "5", "4", "3.2", "2.5", "2", 
				"1.6", "1.3", "1", "0.8", "0.6", "0.5", "0.4", "0.3", "1/4", "1/5", "1/6", 
				"1/8", "1/10", "1/13", "1/15", "1/20", "1/25", "1/30", "1/40", "1/50", "1/60", 
				"1/80", "1/100", "1/125", "1/160", "1/200", "1/250", "1/320", "1/400", "1/500", 
				"1/640", "1/800", "1/1000", "1/1250", "1/1600", "1/2000", "1/2500", "1/3200",
				"1/4000", "1/5000", "1/6400", "1/8000"};
		if (autoExposureMode.contains("AV")) {
			System.out.println("Error - Can not set Shutter Speed for \"AV\" Auto Exposure Mode");
			System.exit(-1);
		}
		checkOptions("ShutterSpeed", shutterSpeed, options);
		setParameter("shutterspeed",shutterSpeed);
		
	}
	
	/**
	 * Set the aperture for the Canon camera. The aperture can be set for "Manual" and "TV" auto
	 * exposure modes.
	 * @param aperture Options: "1.8", "2", "2.2", "2.5", "2.8", "3.2", "3.5", "4", "4.5", "5", "5.6", "6.3", 
	 * "7.1", "8", "9", "10", "11", "13", "14", "16", "18", "20", "22"
	 */
	public void setAperture(String aperture) {
		String[] options = new String[] { 
				"1.8", "2", "2.2", "2.5", "2.8", "3.2", "3.5", "4", "4.5", "5", "5.6", "6.3", 
				"7.1", "8", "9", "10", "11", "13", "14", "16", "18", "20", "22"};
		if (autoExposureMode.contains("TV")) {
			System.out.println("Error - Can not set aperture for \"TV\" Auto Exposure Mode");
			System.exit(-1);
		}
		checkOptions("Aperture", aperture, options);
		setParameter("aperture", aperture);		

	}
	
	/**
	 * Set the exposure compensation for the Canon camera. The exposure compensation can 
	 * be set for the "TV" and "AV" auto exposure modes.
	 * @param exposureCompensation  Options: "-5", "-4.6", "-4.3", "-4", "-3.6", "-3.3", "-3", "-2.6", "-2.3", "-2" ,"-1.6",
	 * "-1.3", "-1.0", "-0.6", "-0.3", "0", "0.3", "0.6", "1.0", "1.3", "1.6", "2", "2.3", "2.6", "3", "3.3", "3.6",
	 * "4", "4.3", "4.6", "5"
	 */
	public void setExposureCompensation(String exposureCompensation) {
		String[] options = new String[] {
		"-5", "-4.6", "-4.3", "-4", "-3.6", "-3.3", "-3", "-2.6", "-2.3", "-2", "-1.6", 
		"-1.3", "-1.0", "-0.6", "-0.3", "0", "0.3", "0.6", "1.0", "1.3", "1.6", "2", "2.3", 
		"2.6", "3", "3.3", "3.6", "4", "4.3", "4.6", "5"};
		if (!autoExposureMode.contains("TV") && !autoExposureMode.contains("AV")) {
			System.out.println("Error - Can not set Exposure Compensation only for \"TV\" or \"AV\"");
			System.exit(-1);
		}
		checkOptions("Aperture", exposureCompensation, options);
		setParameter("exposurecompensation", exposureCompensation);		
	}
	
	/**
	 * Set the ISO for the Canon camera.
	 * @param iso Options: "100", "200", "400", "800", "1600", "3200", "6400"
	 */
	public void setISO(String iso) {
		String[] options = new String[] { 
				"100", "200", "400", "800", "1600", "3200", "6400"};
		checkOptions("ISO", iso, options);
		setParameter("iso", iso);		
	}
	
	/**
	 * Set the Metering Mode for the Canon camera
	 * @param meteringMode  Options: "Evaluative", "Partial", "Spot"
	 */
	public void setMeteringMode(String meteringMode) {
		String[] options = new String[] {
				"Evaluative", "Partial", "Spot"};		
		checkOptions("MeteringMode", meteringMode, options);
		setParameter("meteringmode", meteringMode);			
	}
	
	/**
	 * Set the white balance value for the Canon camera
	 * @param whiteBalance Options: "Auto", "Daylight", "Shadow", "Cloudy", "Tungsten", "Fluorescent", "Flash", "Manual"
	 */
	public void setWhiteBalance(String whiteBalance) {
		String[] options = new String[] {
				"Auto", "Daylight", "Shadow", "Cloudy", "Tungsten",  "Fluorescent",  "Flash",  
				"Manual"};
		checkOptions("WhiteBalance", whiteBalance, options);
		setParameter("whitebalance", whiteBalance);			
	}
		
	/**
	 * Check to see if the value matches with an entry in the options table.
	 * Terminate program if value is not a valid option.
	 * @param parameter Configuration parameter name
	 * @param value  Value to test
	 * @param option Array of containing valid options for value
	 */
	private void checkOptions(String parameter, String value, String[] options) {
		boolean match;
		match = false;
		
		//does the value match any of the options?
		for (int i=0; i<options.length && match == false; i++) if (value == options[i]) match = true;		
		
		if (!match) {
			System.out.println("Error - For the parameter: \"" + parameter + "\"");
			System.out.println("The value: \"" + value + "\" does not match the options available:");
			for(String s: options) System.out.println("--option: \"" + s + "\"");
			System.exit(-1);
		}
	}
	
	/**
	 * Set the configuration parameter for the camera
	 * @param name  Configuration parameter name in canon camera
	 * @param value Value of configuration parameter
	 */
	private void setParameter(String name, String value) {
		String command = program + " --set-config "+ name + "=" + value;
		if (verbose) System.out.println(command);
		
				
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} 
		catch (IOException | InterruptedException e) {
			System.out.println("Error - something wrong with setParameter class");
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the Auto Exposure Mode value of the canon
	 */
	private void getAutoExposureMode() {
		String s; //Input lines
		String[] options = new String[] {"TV", "AV", "P","Manual", "Bulb"};
		boolean valid;
		String command = program + " --get-config autoexposuremode";
		
		if (verbose) System.out.println(command);
		
		s = null;
		try {
			//Obtain the autoexosuremode value
			p = Runtime.getRuntime().exec(command);
			
			//Read the output stream from this command
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdInput.readLine()) != null) {
				if (s.indexOf("Current:") >= 0) autoExposureMode = s.substring(s.indexOf(" ")+1);
			}
			if (verbose) System.out.println("Auto Exposure Mode for Canon: \""+autoExposureMode+"\"");			
			p.waitFor();
			
		} 
		catch (IOException | InterruptedException e) {
			System.out.println("Error - Something wrong with GetAutoExposure class");
			e.printStackTrace();
		}
		
		if (autoExposureMode == null) {
			System.out.println("Error - Something wrong with the connection to the Canon camera.");
			System.out.println("Possible causes: camera turned off or not connected to Raspberry Pi");
			System.out.println("May need to clear connection with the command:");
			System.out.println("sudo killall gvfs-gphoto2-volume-monitor");
			System.exit(-1);
		}
		
		if (autoExposureMode.contains("Unknown value 0013")) {
			System.out.println("Error - CanonCamera class does not support auto exposure mode \"CA\"");
			System.exit(-1);
		}
		
		valid = false;
		for (String opt: options) if (autoExposureMode.contains(opt)) valid = true;
			
		if (!valid) {
			System.out.println("Error - Auto Exposure Mode value: \""+autoExposureMode+"\"");
			System.out.println("Must be P, TV, AV, Manual, or Bulb");
			System.exit(-1);		
		}
	}

}
