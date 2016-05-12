package test;
import eric.speak.Say;
/*
 * Test all methods in the Say class
 */
public class SayWords {

	public static void main(String[] args) {
		Say say = new Say(); 
	
			say.words("Hello from Raspbeery Pi"); 
			say.waitfor();
			
			say.words("Did you hear what I said?");
			say.waitfor();
			
			say.date();
			say.waitfor();
			
			say.time();
			say.waitfor();
		
			say.destroy();			
		
	}

}
