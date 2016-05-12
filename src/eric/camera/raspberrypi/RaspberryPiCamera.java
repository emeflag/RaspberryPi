/**
 * The RaspberryPi class acquires images using the Raspberry Pi
 * camera. It is an interface to the "raspistill" program.
 * 
 * Special note:  Be sure to call the "p.waitfor()" method to wait for 
 * the end of the image acquisition.
 * 
 * The getImaging method is used to acquire a single image or a sequence of images as commanded
 * by raspistill.
 * 
 * Various methods are used to define the image characteristics such as the width, height, 
 * image quality, etc. Not all raspistill imaging options are used in this class
 * 
 * Note that for an image sequence the image file name provide must contain the form 
 * "my_pics%04d.jpg" where "%04d" provides sequence-number formatting of the file name.
 * 
 * For more information on the raspistill program see the documentation here:
 * https://www.raspberrypi.org/documentation/raspbian/applications/camera.md
 * 
 */
package eric.camera.raspberrypi;
import java.io.File;
import java.io.IOException;
import eric.utility.CheckExecutable;

public class RaspberryPiCamera 
{
	
	//Fields used to specify raspistill command-line parameters
	//The value -1 is an indicator to not set this parameter in the raspistill command
	private int sharpness      = -1;
	private int contrast       = -1;
	private int brightness     = -1;
	private int saturation     = -1;
	private int iso            = -1;
	private int ev             = -1;   //exposure compensation
	private String exposure    = null; //exposure mode 
	private String awb         = null; //automatic white balance
	private String metering    = null; //type of metering
	private int rotation       = -1; 
	private boolean hflip      = false; //flip image horizontally
	private boolean vflip      = false; //flip image vertically	
	private boolean roiFlag    = false;
	private float[] roi        = {0.0f, 0.0f, 1.0f, 1.0f}; //roi (region of interest) specification
	private int shutter        = -1; //shutter time in microseconds
	private String drc         = null; //Dynamic range compression	
	private int width          = -1;  		
	private int height         = -1;  		
	private int quality        = -1;
	private int timeOut        = -1; //raspistill will take images for this length of time (milliseconds)
	private int timeLapse      = -1; //time between images in sequence (milliseconds) 0=next image taken immediately
	
	//other class fields
	private Process pCamera;	
	private String  command;
	private boolean verbose = false; //print information about commanding
	private String imageFile; 	//name of image file or image file sequence;
	private String program;     //name of raspistill program
	private boolean directoryFlag = false; //add directory specification to image file name
	private String  directoryTree;  //directory location to store image files
		
	/**
	 * Makes sure "raspistill" is installed
	 */
	public RaspberryPiCamera() 
	{
		verbose = false;
		//make sure "raspistill" is properly installed
		program = "/usr/bin/raspistill";
		new CheckExecutable(program).ifNotExecutableKill();
		
		//Initialize the raspistill camera parameters.
		resetDefaults();
	}

	/**
	 * Set verbose flag and make sure "raspistill" is installed
	 * @param verbose Verbose flag for printing status information.
	 */
	public RaspberryPiCamera(boolean verbose) {
		this();
		this.verbose = verbose;	
	}
	
	/**
	 * Reset default raspistill parameters.
	 */
	public void resetDefaults() {
		sharpness   = -1;
		contrast    = -1;
		brightness  = -1;
		saturation  = -1;
		iso         = -1;
		ev          = -1;
		exposure    = null;
		awb         = null;
		metering    = null;
		rotation    = -1;
		hflip       = false;
		vflip       = false;
		roiFlag     = false;
		roi[0]      = 0.0f;
		roi[1]      = 0.0f;
		roi[2]      = 0.0f;
		roi[3]      = 0.0f;
		shutter     = -1;
		drc         = null;		
		width 		= -1;
		height 		= -1;
		quality		= -1;
		timeOut     = -1;
		timeLapse   = -1;
		imageFile	= null;	
	}
	
	/**
	 * Acquire an image or imaging sequence using the raspistill program.
	 * @param imageFile Name of output image file or output image file sequence.
	 * For an image sequence the file name must include "%XXd" specification
	 * where 'XX' is a number indicating size of sequence number field. 
	 */
	public void getImaging (String imageFile) {
		this.imageFile = imageFile;
		
		//Should we prepend a directoryTree specification to the image file name?
		if (directoryFlag && this.imageFile.indexOf("/") == -1) {
			this.imageFile = directoryTree + "/" + this.imageFile;
		}
		
		command = program + " ";
		if (width       != -1) 	command = command + "--width "      + String.valueOf(width)   + " ";
		if (height      != -1) 	command = command + "--height "     + String.valueOf(height)  + " ";
		if (quality     != -1) 	command = command + "--quality "    + String.valueOf(quality) + " ";
		if (sharpness   != -1) 	command = command + "--sharpness "  + String.valueOf(sharpness) + " ";
		if (contrast    != -1) 	command = command + "--contrast "   + String.valueOf(contrast) + " ";
		if (brightness  != -1) 	command = command + "--brightness " + String.valueOf(brightness) + " ";
		if (saturation  != -1) 	command = command + "--saturation " + String.valueOf(saturation) + " ";
		if (iso         != -1) 	command = command + "--ISO "        + String.valueOf(iso) + " ";
		if (ev          != -1) 	command = command + "--ev "         + String.valueOf(ev) + " ";
		if (exposure  != null) 	command = command + "--exposure "   + exposure + " ";
		if (awb       != null) 	command = command + "--exposure "   + awb + " ";
		if (metering  != null) 	command = command + "--metering "   + metering + " ";
		if (rotation    != -1) 	command = command + "--rotation "   + String.valueOf(rotation) + " ";
		if (hflip)				command = command + "--hflip ";
		if (vflip)				command = command + "--vflip ";		
		if (roiFlag) 			command = command + "--roi "
											+String.valueOf(roi[0])+","
											+String.valueOf(roi[1])+","
											+String.valueOf(roi[2])+","
											+String.valueOf(roi[3])+" ";
		if (shutter   != -1)    command = command + "--shutter "    + String.valueOf(shutter) + " ";
		if (drc       != null)  command = command + "--drc "        + drc + " ";
		if (timeOut   != -1)    command = command + "--timeout "    + String.valueOf(timeOut) + " ";
		if (timeLapse != -1)    command = command + "--timelapse " + String.valueOf(timeLapse) + " ";

		//if image file name not available and image sequence specified the create this file name
		if (this.imageFile == null && timeOut != -1 && timeLapse != -1) {
			this.imageFile = "image%06d.jpg";
		}
		//else if image file name is not specified then create this file name.
		else if (this.imageFile == null) {
			this.imageFile = "image.jpg";
		}
		
		if (timeOut != -1 && timeLapse != -1) {
			if (imageFile.indexOf('%') == -1) {
				System.out.println("Error - file name must contain sequence designation '%XXd'");
				System.exit(-1);
			}
		}
		
		
		command = command + "--output " + this.imageFile + " ";
		
		if (verbose) System.out.println(command);
		try {
			pCamera = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			System.out.println("Error - Something wrong with RaspberryPiCamera.getImaging()");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Wait for end of image acquisition. It takes about 5 seconds
	 * to acquire an image.
	 */
	public void waitfor() {
		try {
			pCamera.waitFor();
		} catch (InterruptedException e) {
			System.out.println("Error - Something wrong with RaspberryPiCamera.waitFor() method.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Forcibly terminate the RunTime process
	 */
	public void  destroy() {
		pCamera.destroy();
	}

	/**
 	 * Path location where images are to be placed.
 	 * If the directory does not exist then one will be created.
	 * @param directoryTree Directory tree specification.
	 */
	public void directory(String directoryTree) {		
		File dir = new File(directoryTree);
		dir.mkdirs();
		
		try {
			this.directoryTree = dir.getCanonicalPath();
			directoryFlag = true;
		} 
		catch (IOException e) {
			System.out.println("Error - unable to create directory: "+directoryTree);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Set the width and height of the image to be acquired.
	 * Note: when setting width and height be sure to make the
	 * height/width values proportional to the 256x1944 dimensions
	 * of the camera in order to capture the entire scene.
	 * 
	 * @param width  Width in pixels of acquired image (1 - 2592)
	 * @param height Height in pixels of acquired image (1 - 1944)
	 */
	public void setWidthHeight(int width, int height) {
		if (width < 1 || width > 2592) {
			System.out.println("Error - image width be in the range 1 to 2592 pixels");
			System.exit(-1);
		}
		if (height < 1 || height > 1944 ) {
			System.out.println("Error - image width be in the range 1 to 1944 pixels");
			System.exit(-1);
		}
		this.width =  width;
		this.height = height;
	}
	
	/**
	 * Set the jpeg image quality parameter.  
	 * @param quality Jpeg quality value (0-100)
	 */
	public void setQuality(int quality) {
		if (quality < 0 || quality > 100){
			System.out.println("Error - image quality must be in the range 0 to 100");
			System.exit(-1);	
		}
		this.quality = quality;
	}
	
	/**
	 * Set the sharpness level of the image.
	 * @param sharpness Image Sharpness level value (-100 - 100)
	 */
	public void setSharpness(int sharpness) {
		if (sharpness < -100 || sharpness > 100){
			System.out.println("Error - image sharpness must be in the range -100 to 100");
			System.exit(-1);	
		}
		this.sharpness = sharpness;
	}
	
	/**
	 * Set the contrast level of the image
	 * @param contrast  Image contrast value (-100 to 100)
	 */
	public void setContrast(int contrast) {
		if (contrast < -100 || contrast > 100){
			System.out.println("Error - image contrast must be in the range -100 to 100");
			System.exit(-1);	
		}
		this.contrast = contrast;
	}
	
	/**
	 * Set the brightness level of the image 
	 * @param brightness Image brightness level (0 - 100)
	 */
	public void setBrightness(int brightness) {
		if (brightness < 0 || brightness > 100){
			System.out.println("Error - image brightness must be in the range -100 to 100");
			System.exit(-1);	
		}
		this.brightness = brightness;
	}

	/**
	 * Set the saturation level of the image
	 * @param saturation Image saturation value (-100 - 100)
	 */
	public void setSaturation(int saturation) {
		if (saturation < -100 || saturation > 100){
			System.out.println("Error - image saturation must be in the range -100 to 100");
			System.exit(-1);	
		}
		this.saturation = saturation;
	}
	
	/**
	 * Set the ISO level for the image 
	 * @param iso Camera ISO value (100 - 800)
	 */
	public void setISO(int iso) {
		if (iso < 100 || iso > 800){
			System.out.println("Error - image ISO must be in the range 100 to 800");
			System.exit(-1);	
		}
		this.iso = iso;
	}
	
	/**
	 * Set the Exposure Compensation (EV) for the image
	 * @param ev Image exposure compensation value  (-10 - 10)
	 */
	public void setEV(int ev) {
		if (ev < -10 || ev > 10){
			System.out.println("Error - image EV must be in the range -10 to 10");
			System.exit(-1);	
		}
		this.ev = ev;
	}
	
	/**
	 * Set exposure mode and test for valid value
	 * @param exposure Image exposure mode. Options:  "auto", "night", "nightpreview",
	 * "backlight", "spotlight", "sports", "snow", "beach", "verylong",
	 * "fixedfps", "antishake", "fireworks"
	 * 
	 */
	public void setExposureMode(String exposure) {
		
		//permitted values for the exposure mode
		String modeValues[] ={"auto", "night", "nightpreview", "backlight", "spotlight",
				"sports", "snow", "beach", "verylong", "fixedfps", "antishake", "fireworks"};
		boolean exposureOk;
		
		exposureOk = false;
		for (String s: modeValues) if (exposure == s) exposureOk = true;
				
		if (!exposureOk)
		{
			System.out.println("Error - image eposure mode permitted values:");
			for (String s: modeValues) System.out.println(s);			
			System.exit(-1);	
		}
		this.exposure = exposure;
	}	

	/**
	 * Set automatic white balance values
	 * @param awb  Automatic white balance. Options: "off", "auto", "sun", "shade:, 
	 * "tungsten", "fluorescent", "incandescent", "flash", "horizon"
	 */
	public void setAwb(String awb) {
		
		//permitted values for the exposure mode
		String awbValues[] ={"off", "auto", "sun", "shade", "tungsten", "fluorescent", 
				"incandescent", "flash", "horizon"};
		boolean awbOk;
		
		awbOk = false;
		for (String s: awbValues) if (awb == s) awbOk = true;
				
		if (!awbOk)
		{
			System.out.println("Error - image eposure mode permitted values:");
			for (String s: awbValues) System.out.println(s);			
			System.exit(-1);	
		}
		this.awb = awb;
	}
	
	/**
	 * Set the metering value for determining exposure settings
	 * @param metering Exposure's metering value. Options: "average", 
	 * "spot", "backlit", "matrix"
	 */
	public void setMetering(String metering) {
		
		//permitted values for the metering mode
		String meteringValues[] ={"average", "spot", "backlit", "matrix"};
		boolean meteringOk;
		
		meteringOk = false;
		for (String s: meteringValues) if (metering == s) meteringOk = true;
				
		if (!meteringOk)
		{
			System.out.println("Error - image eposure mode permitted values:");
			for (String s: meteringValues) System.out.println(s);			
			System.exit(-1);	
		}
		this.metering = metering;
	}
	
	/**
	 * Set the image rotation value 
	 * @param rotation Rotate image in degrees.  Permitted values: 0, 90, 180, 270
	 */
	public void setRotation(int rotation) {
		if (rotation != 0 && rotation  != 90 && rotation != 180 && rotation != 270){
			System.out.println("Error - image roation must 0, 90, 180,270");
			System.exit(-1);	
		}
		this.rotation = rotation;
	}
	
	/**
	 * Flip image horizontally
	 */
	public void setHflip() {
		this.hflip = true;
	}
	
	/**
	 * Flip image vertically
	 */
	public void setVflip() {
		this.vflip = true;
	}
	
	/**
	 * Set the roi (region of interest) values 
	 * @param roi Four values specifying region of interest
	 * {start width, start height, width, height}.
	 * Values are in the range 0.0f to 1.0f.
	 */
	public void setRoi(float[] roi) {
		roiFlag = true;
		if (roi.length != 4) {
			System.out.println("Error - must provide exactly 4 values for roi (region of interest) ");
			System.exit(-1);	
		}
		for (float x: roi) {
			if (x < 0.0 || x > 1.0) {
				System.out.println("Error - roi (region of interest) values must be in range 0.0 - 1.0");
				System.exit(-1);	
			}
		}
		
		this.roi = roi;
	}
	
	/**
	 * Set the dynamic range compression mode
	 * 
	 * @param drc Dynamic range compression. Options: "off", "low", "medium", "high"
	 */
	public void setDrc(String drc) {
		
		//permitted values for the drc mode
		String drcValues[] ={"off", "low", "medium", "high"};
		boolean drcOk;
		
		drcOk = false;
		for (String s: drcValues) if (drc == s) drcOk = true;
				
		if (!drcOk)
		{
			System.out.println("Error - image drc (dynamic range compression) mode permitted values:");
			for (String s: drcValues) System.out.println(s);			
			System.exit(-1);	
		}
		this.drc = drc;
	}
	
	/**
	 * Set the shutter time of the exposure in microseconds
	 * @param shutter Exposure duration in microseconds (1 - 6000000) 
	 */
	public void setShutter(int shutter) {
		if (shutter < 1 || shutter > 6000000 ){
			System.out.println("Error - shutter time (microseconds) must be in range 0 to 6.0 seconds");
			System.exit(-1);	
		}
		this.shutter = shutter;
	}
	
	/**
	 * Set the total time duration of an image sequence (milliseconds) 
	 * @param timeOut Total time duration of image sequence (milliseconds)
	 */
	public void setTimeOut(int timeOut) {
		if (timeOut < 1 ) {
			System.out.println("Error - time duration of image sequence (milliseconds) must be greater than 0");
			System.exit(-1);				
		}
		this.timeOut = timeOut;
	}
	
	/**
	 * Set the time between image observations (milliseconds) 
	 * @param timeLapse Time between observations in an imaging sequence (milliseconds)
	 */
	public void setTimeLapse(int timeLapse) {
		if (timeLapse < 0 ) {
			System.out.println("Error - time duration between images in sequence (milliseconds) can not be negative");
			System.exit(-1);				
		}
		this.timeLapse = timeLapse;
	}	

}
