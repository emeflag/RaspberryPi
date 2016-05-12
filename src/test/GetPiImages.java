package test;
/**
 * The TestRaspberryPiCamera class tests methods of the RaspberryPiCamera class.
 */
import eric.camera.raspberrypi.RaspberryPiCamera;

public class GetPiImages {

	public static void main(String[] args) {
		RaspberryPiCamera piCamera = new RaspberryPiCamera(true); //set verbose mode
		float[] roi = new float[4];
					
			//Take a default image
			piCamera.getImaging("/home/eric/jpg/default.jpg");	
			piCamera.waitfor();					//wait for end of image observation
			
			//scale image 512 x 512 set image quality to 100
			piCamera.resetDefaults();			//reset defaults
			piCamera.setWidthHeight(518,382);	//scale image
			piCamera.setQuality(100);			//set image compression quality to 100
			piCamera.getImaging("/home/eric/jpg/imagescale.jpg");	//file name of image
			piCamera.waitfor();					//wait for end of image
			
			//select a region of interest scale image to 256x256
			piCamera.resetDefaults();			//reset defaults
			roi[0] = 0.25f;
			roi[1] = 0.25f;
			roi[2] = 0.5f;
			roi[3] = 0.5f;
			piCamera.setRoi(roi);				//specify region of interest
			piCamera.setWidthHeight(256, 256);	//scale image size
			piCamera.getImaging("/home/eric/jpg/imageroi.jpg");	//file name of image
			piCamera.waitfor();
			
			//rotate image 90 degrees
			piCamera.resetDefaults();			//reset defaults
			piCamera.setRotation(90);
			piCamera.setWidthHeight(518,382);	//scale image
			piCamera.getImaging("/home/eric/jpg/imagerotate.jpg"); //file name of image
			piCamera.waitfor();
			
			//flip vertical and horizontal
			piCamera.resetDefaults();			//reset defaults
			piCamera.setHflip();				//flip horizontal
			piCamera.setVflip();				//flip vertical
			piCamera.setWidthHeight(518,382);	//scale image
			piCamera.getImaging("/home/eric/jpg/imageflip.jpg"); //file name of image
			piCamera.waitfor();
			
			//take and image with enhancement for sharpness, contrast, brightness, saturation
			piCamera.resetDefaults();			//reset defaults
			piCamera.setBrightness(100);
			piCamera.setContrast(100);
			piCamera.setSaturation(100);
			piCamera.setSharpness(100);
			piCamera.getImaging("/home/eric/jpg/imageenhance.jpg"); //file name of image			
			piCamera.waitfor();

			//take and image with dynamic range compression
			piCamera.resetDefaults();			//reset defaults
			piCamera.setDrc("high");
			piCamera.getImaging("/home/eric/jpg/imagedrc.jpg"); //file name of image			
			piCamera.waitfor();	
			
			//take and image with ISO 800
			piCamera.resetDefaults();			//reset defaults
			piCamera.setISO(800);
			piCamera.getImaging("/home/eric/jpg/imageiso.jpg"); //file name of image			
			piCamera.waitfor();	
									
			//take an image sequence for 10 seconds scale to 256x256
			piCamera.directory("/home/eric/jpg/sequence"); //Use this directory to store the image sequence
			piCamera.resetDefaults(); 				//reset camera defaults
			piCamera.setWidthHeight(518,382);		//scale image size to 512 x 512
			piCamera.setRoi(roi);					//specify entire image of region of interest
			piCamera.setTimeOut(10000);				//take an image sequence for 10 seconds
			piCamera.setTimeLapse(0);		  		//time between images in a sequence is 0 
			piCamera.getImaging("imgseq%02d.jpg"); 	//note sequence file name "%02d"
			piCamera.waitfor();						//wait for end of image sequence
						
	}
}
