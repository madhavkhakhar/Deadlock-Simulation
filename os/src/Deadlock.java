import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Deadlock
{	
  public static void main( String args[] ) throws FileNotFoundException
  {
    Kernel kernel;
    kernel = new Kernel();
    int processCount, resourceCount;
    Scanner in = new Scanner(new File("deadlock.txt"));
    processCount=Integer.parseInt(in.nextLine());
    
    kernel.initCommands(processCount);
    kernel.setProcessCount(processCount);
    
    resourceCount=Integer.parseInt(in.nextLine());
    
    kernel.setResourceCount(resourceCount);
    try
    {
      kernel.setProcessCount(processCount) ;
      kernel.setResourceCount(resourceCount) ;
    }
    catch (NumberFormatException e)
    {
      System.err.println( "Invalid number \"" + args[1] + "\" specified as process count");
      System.exit(0);
    }
    String temp = in.nextLine();
    
    String[] resInit = temp.split(" ");
     for( int i = 0 ; i <resourceCount ; i ++ ){
          kernel.setResourceInitialAvailable(i,Integer.parseInt(resInit[i]));
     }
     if(kernel.avoidance)
    	 kernel.initAvoidance(resourceCount);
    kernel.run();
  }
}
