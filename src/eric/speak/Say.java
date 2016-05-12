/**
 * The Say Class speaks words, time, and date
 * 
 * The "espeak" program is used to accomplish the speaking
 * 
 * 
 */
package eric.speak;

import eric.utility.CheckExecutable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * The Say class will speak the words passed to it through a string
 * It uses the "espeak" command to perform the operation.
 */
public class Say {
	
    private Process p;
    private String month; 		//name of month
    private String weekDay;		//day of the week
    private String monthDay;	//day of the month
    private String hour;		//current hour
    private String minute;		//current day
    private String program;		//contains the name of espeak program
	
  /**
   * Check that "espeak" program exists.
   */
  	public Say() 
  	{
  		//make sure "espeak" is properly installed
  		program = "/usr/bin/espeak";
  		new CheckExecutable(program).ifNotExecutableKill();
  	}
  	
    /**
     * This method will speak the words passed to it through through the parameter list
     * @param words - Pass the words to speak 
     *  
     */
    public void words(String words) {
    	
    	try {
			p = Runtime.getRuntime().exec(new String[] {program, words});
		} catch (IOException e) {
			System.out.println("Error - Something wrong with Say.words() method.");
			e.printStackTrace();
		}		
    }
	
    /**
     * Wait for the completion of the speaking
     * 
     */
    public void waitfor() {
    	try {
			p.waitFor();
		} catch (InterruptedException e) {
			System.out.println("Error - Something wrong with Say.waitFor()");
			e.printStackTrace();
		}
    }
	
    /**
     * End the task if not already ended
     */
    public void destroy() {
    	p.destroy();
    }
    
    /**
     * Speak the current time
     */
    public void time() {
    	getDate();
    	
    	try {
			p = Runtime.getRuntime().exec(new String[] {program, "Time " + hour + " " + minute});
		} catch (IOException e) {
			System.out.println("Error - Something wrond with Say.time() method");
			e.printStackTrace();
		}
    }
    
    /**
     * Speak today's date
     * 
     */
    public void date() {
    	getDate();
    	
    	try {
			p = Runtime.getRuntime().exec(new String[] {program, "Todays date " + weekDay + " " + month + " " + monthDay});
		} catch (IOException e) {
			System.out.println("Error - Something wrong with Say.date() method");
			e.printStackTrace();
		}
    }
    
    /**
     * Internal method to extract time and date information
     */
    private void getDate() {
    	DateFormat dateFormat = new SimpleDateFormat("EEEEEEEEE/MMMMMMMMMMM/dd/HH/mm");
    	Date date = new Date();
    	
    	int i1 = dateFormat.format(date).indexOf('/');
    	int i2 = dateFormat.format(date).indexOf('/',i1+1);
    	int i3 = dateFormat.format(date).indexOf('/',i2+1);
    	int i4 = dateFormat.format(date).indexOf('/',i3+1);
    	
    	weekDay  = dateFormat.format(date).substring(0,i1);
    	month    = dateFormat.format(date).substring(i1+1,i2);
    	monthDay = dateFormat.format(date).substring(i2+1,i3);
    	hour     = dateFormat.format(date).substring(i3+1,i4);
    	minute   = dateFormat.format(date).substring(i4+1);
    	
    	//System.out.println(dateFormat.format(date));
    	//System.out.println(weekDay);
    	//System.out.println(month);
    	//System.out.println(monthDay);
    	//System.out.println(hour);
    	//System.out.println(minute);
    }

}

