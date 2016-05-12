package test;

import eric.camera.canon.CanonCamera
;

public class GetCanonImages {

	public static void main(String[] args) {


	CanonCamera canonCamera = new CanonCamera(true);
	//canonCamera.setShutterSpeed("1/100");
	//canonCamera.setAperture("4");
	//canonCamera.setExposureCompensation("-0.3");
	//canonCamera.setISO("1600");
	//canonCamera.setMeteringMode("Partial");
	//canonCamera.setWhiteBalance("Tungsten");
	
	//Acquire and image
	canonCamera.image();
	canonCamera.waitFor();
	
	//Acquired an image sequence of 3 frames separated by 4 seconds
	canonCamera.imageSequence(3, 4);
	canonCamera.waitFor();
	
	canonCamera.destroy();
	
    }
}