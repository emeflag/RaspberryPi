package test;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class I2CTesting {

	public static void main(String[] args) throws IOException {
		
		I2CBus hatI2C;
		I2CDevice hatDevice;
		
		//final int MODE2 			=0X01;
		//final int COMMAND_OUTDRV   = 0x04;
		
		
		int I2C_BUS = I2CBus.BUS_1;
		int DEVICE_ADDR = 0X60;
		
		int[]  LedAddr = new int[]  {0X06, 0X07, 0X08, 0X09};
		byte[] LedVal  = new byte[] {0X00,0X01,0X02,0X03};
		
		hatI2C = I2CFactory.getInstance(I2C_BUS);	
		hatDevice = hatI2C.getDevice(DEVICE_ADDR);
		
		System.out.println("Test individual byte writes & byte reads");
		for (int i=0; i<4; i++) hatDevice.write(LedAddr[i],LedVal[i]);
		for (int i=0; i<4; i++) {
			int val = hatDevice.read(LedAddr[i]);
			System.out.format("Addr: 0X%02X Value: 0X%02X\n", LedAddr[i],val);			
		}
		
		System.out.println("Test buffer write & byte reads");
		hatDevice.write(0X06, LedVal,0,4);
		for (int i=0; i<4; i++) {
			int val = hatDevice.read(LedAddr[i]);
			System.out.format("Addr: 0X%02X Value: 0X%02X\n", LedAddr[i],val);			
		}
		
		
	}

}
