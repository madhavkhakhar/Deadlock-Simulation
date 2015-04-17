import java.util.*;
import java.lang.Thread ;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException ;


public class Kernel extends Thread 
{
  private int time = 1 ; 
  
  public static Boolean avoidance = false; //Turn on false for no avoidance and change file input in initCommands method accordingly.
  
  private String[] command;
  private int sleepTime = 1000 ;
  private int processCount=0 ;
  private int resourceCount=0 ;
  private Vector<Process> processes = new Vector<Process>() ;
  private Vector<Resource> resources = new Vector<Resource>() ;
  private boolean stepping = false ;
  private int compltedCount = 0 ;
  private int blockedCount = 0 ;
  ArrayList<String>[] cmd;
  private int[] cmdindex;
  
 private Boolean safeState=true;
 private int[] existing,processed,available;
 private int[][] resAssigned, resNeeded;
 Scanner in;
 
 
  
  public void initCommands(int processCnt){
	  cmd = (ArrayList<String>[])new ArrayList[processCnt];
	  for(int i=0;i<processCnt;i++){
		  cmd[i]=new ArrayList<>();
	  }
	 cmdindex = new int[processCnt];
	 if(!avoidance){
		 try {
			in = new Scanner(new File("deadlock.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
  }
  
  public void initAvoidance(int resCount){
	  existing = new int[resCount];
	  this.resourceCount=resCount;
	  processed = new int[resCount];
	  available = new int[resCount];
	  resAssigned = new int[processCount][resCount];
	  resNeeded = new int[processCount][resCount];
	  try {
			in = new Scanner(new File("command3.txt"));
			in.nextLine(); in.nextLine();
			String temp = in.nextLine();
		    String[] resInit = temp.split(" ");
		    for( int i = 0 ; i <resInit.length ; i ++ ){
		    	existing[i]=Integer.parseInt(resInit[i]);
		    	processed[i] = 0;
		    	available[i]=existing[i];
		    }
		    for(int i=0;i<processCount;i++){
		    	temp=in.nextLine();
		    	resInit = temp.split(" ");
		    	for(int j=0;j<resInit.length;j++){
		    		resNeeded[i][j] = Integer.parseInt(resInit[j]);
		    		resAssigned[i][j]=0;
		    	}
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }


  public void processcomplted()
  {
    compltedCount ++ ;
  }

  public void processBlocked()
  {
    blockedCount ++ ;
  }

  public void processUnblocked()
  {
    blockedCount -- ;
  }

  public boolean getStepping()
  {
    return stepping ;
  }

  public void setStepping( boolean newStepping )
  {
    stepping = newStepping ;
  }

  public int getTime( ) 
  {
    return time ;
  }

  public void setTime( int newTime )
  {
    time = newTime ;
  }

  public int getSleepTime()
  {
    return sleepTime ;
  }

  public void setSleepTime( int newSleepTime )
  {
    sleepTime = newSleepTime ;
  }

  public void setProcessCount( int newProcessCount ) 
  { 
    if ( newProcessCount > processCount )
    {
      for ( int i = processCount ; i < newProcessCount ; i ++ )
        processes.addElement( new Process(i));
    }
    else if ( newProcessCount < processCount )
    {
      processes.setSize( newProcessCount ) ;
      processes.trimToSize( ) ;
    }
    processCount = newProcessCount ;
  }

  public int getProcessCount( ) 
  {
    return processCount ;
  }

  public void setResourceCount( int newResourceCount )
  {
    if ( newResourceCount > resourceCount )
    {
      for ( int i = resourceCount ; i < newResourceCount ; i ++ )
        resources.addElement( new Resource( i ) ) ;
    }
    else if ( newResourceCount < resourceCount )
    {
      resources.setSize( newResourceCount ) ;
      resources.trimToSize( ) ;
    }
    resourceCount = newResourceCount;
  }

  public int getResourceCount( )
  {
    return resourceCount ;
  }

  public void setResourceInitialAvailable( int resource , int newInitialAvailable )
  {
    ((Resource)(resources.elementAt( resource ))).setInitialAvailable( newInitialAvailable ) ;
    ((Resource)(resources.elementAt( resource ))).reset();
  }

  public int getResourceInitialAvailable( int resource )
  {
    return ((Resource)(resources.elementAt( resource ))).getInitialAvailable() ;
  }

  public Vector getResources()
  {
    return resources ;
  }

  public Vector getProcesses()
  {
    return processes ;
  }

  public void reset()
  {
    compltedCount = 0 ;
    blockedCount = 0 ;
    // reset all the resources
    for ( int i = 0 ; i < resourceCount ; i ++ )
    {
      Resource resource = (Resource)resources.elementAt(i) ;
      resource.setCurrentAvailable( resource.getInitialAvailable() ) ;
    }

    // reset all the processes
    for ( int i = 0 ; i < processCount ; i ++ )
    {
      try
      {
        ((Process)processes.elementAt(i)).reset() ;
      }
      catch (IOException e)
      {
        // the reset should fail, and an error message should be displayed.
        System.out.println( "unable to terminate \"" +
          ((Process)processes.elementAt(i)) + "\" for input" ) ;
      }
    }
    time = 0 ;
  }
  public Boolean checkSafteState(){
	  DeadlockAvoidance a = new DeadlockAvoidance(processCount,resourceCount,resNeeded,available);
	  return a.checkSafeS();
  }
 
  
  public void stepWithAvoidance()
  {
	  setStepping(true);
	  
    for(int i = 0 ; i < processCount ; i ++ )
    {
      Process process = (Process)processes.elementAt(i);
      /**
      Perform one millisecond of work for a process.  Note that only 
      computable processes consume time.  It does not take time to 
      grant a resource,
      nor does it take time to free a resource.  If a process is blocked,
      or if it is complted, the next process is free to execute.
      */

        switch( process.getState() )
        {
        case Process.STATE_UNKNOWN:
          if ( command[i] == null)
          {
            process.state = Process.STATE_COMPLETE;
          }
          else
          {
            if ( command[i].contains( "C" ) ) 
            {
              process.timeToCompute = Integer.parseInt(command[i].replace("C", ""))-1;
              if(process.timeToCompute>0)
              process.state = Process.STATE_COMPUTABLE ;
              else{
            	  cmdindex[i]++;
            	  process.state= Process.STATE_UNKNOWN;  
              }
            }
            else if ( command[i].contains( "R" ) )
            {
              // allocate resource command.getParameter()
              // state depends on whether the resource is available
              int r = Integer.parseInt(command[i].replace("R", "")) ;
              Resource resource = (Resource)resources.elementAt(r);
              if ( DeadlockManager.grantable( i , resource ) ){
                  DeadlockManager.allocate( i , resource ) ;
                  resAssigned[i][r]++;
                  resNeeded[i][r]--;
                  available[r]--;
                  processed[r]++;
                  cmdindex[i]++;
              }
              else
              {
                // increment blocked count
                processBlocked();
                process.resourceAwaiting = resource ;
                process.state = Process.STATE_RESOURCE_WAIT ;
                if ( processCount == blockedCount )
                {
                  // deadlocked; have the deadlock manager kill a process.
                  DeadlockManager.deadlocked(); 
                }
              }
            }
            else if ( command[i].contains( "F" ) )
            {
              // free resource command.getParameter() (if it was allocated ???)
              // state depends on whether there are other commands
              int r = Integer.parseInt(command[i].replace("F", ""));
              Resource resource = (Resource)resources.elementAt(r);
              DeadlockManager.deallocate( i , resource ) ;
              resAssigned[i][r]--;
              processed[r]--;
              available[r]++;
              cmdindex[i]++;
            }
            else if ( command[i].equals( "E" ) )
            {
              process.state = Process.STATE_COMPLETE ;
              cmdindex[i]++;
              processcomplted();
            }
          }
          break ;
        case Process.STATE_COMPUTABLE:
        	if ( command[i] == null)
            {
              process.state = Process.STATE_COMPLETE;
              processcomplted();
              break;
            }
          if ( process.timeToCompute > 1 )
          {
            process.timeToCompute -- ;
          }
          else{
        	  cmdindex[i]++;
        	  process.state = Process.STATE_UNKNOWN;
          }
          break ;
        case Process.STATE_RESOURCE_WAIT:
        	if ( command[i] == null)
            {
              process.state = Process.STATE_COMPLETE;
              processcomplted();
              break;
            }
          if ( DeadlockManager.available( i , process.resourceAwaiting ) )
            if ( DeadlockManager.grantable( i , process.resourceAwaiting ) ) 
            {
              DeadlockManager.allocate( i , process.resourceAwaiting ) ;
              process.state = Process.STATE_UNKNOWN ;
              process.resourceAwaiting = null ;
              processUnblocked();
              int r = resources.indexOf(process.resourceAwaiting);
              resAssigned[i][r]++;
              resNeeded[i][r]--;
              available[r]--;
              processed[r]++;
              cmdindex[i]++;
              process.state = Process.STATE_UNKNOWN;
            }
          break ;
        case Process.STATE_COMPLETE:
          // we're already stopped, no need to do anything
          break ;
          
        }
      }
    
    printStatus();
    time ++ ;
    setStepping(false);
  }

  public void step()
  {
	  setStepping(true);
    for(int i = 0 ; i < processCount ; i ++ )
    {
      Process process = (Process)processes.elementAt(i);
      /**
      Perform one millisecond of work for a process.  Note that only 
      computable processes consume time.  It does not take time to 
      grant a resource,
      nor does it take time to free a resource.  If a process is blocked,
      or if it is complted, the next process is free to execute.
      */

        switch( process.getState() )
        {
        case Process.STATE_UNKNOWN:
          if ( command[i] == null)
          {
            process.state = Process.STATE_COMPLETE;
          }
          else
          {
            if ( command[i].contains( "C" ) ) 
            {
              process.timeToCompute = Integer.parseInt(command[i].replace("C", ""))-1;
              if(process.timeToCompute>0)
              process.state = Process.STATE_COMPUTABLE ;
              else{
            	  cmdindex[i]++;
            	  process.state= Process.STATE_UNKNOWN;  
              }
            }
            else if ( command[i].contains( "R" ) )
            {
              // allocate resource command.getParameter()
              // state depends on whether the resource is available
              int r = Integer.parseInt(command[i].replace("R", "")) ;
              Resource resource = (Resource)resources.elementAt(r);
              if ( DeadlockManager.grantable( i , resource ) ){
                  DeadlockManager.allocate( i , resource ) ;
                  cmdindex[i]++;
              }
              else
              {
                // increment blocked count
                processBlocked();
                process.resourceAwaiting = resource ;
                process.state = Process.STATE_RESOURCE_WAIT ;
                if ( processCount == blockedCount )
                {
                  // deadlocked; have the deadlock manager kill a process.
                  DeadlockManager.deadlocked(); 
                }
              }
            }
            else if ( command[i].contains( "F" ) )
            {
              // free resource command.getParameter() (if it was allocated ???)
              // state depends on whether there are other commands
              int r = Integer.parseInt(command[i].replace("F", ""));
              Resource resource = (Resource)resources.elementAt(r);
              DeadlockManager.deallocate( i , resource ) ;
              cmdindex[i]++;
            }
            else if ( command[i].equals( "E" ) )
            {
              process.state = Process.STATE_COMPLETE;
              for(int j=0;j<resourceCount;j++){
            	  resAssigned[i][j]=0;
            	  resNeeded[i][j]=0;
              }
              cmdindex[i]++;
              processcomplted();
            }
          }
          break ;
        case Process.STATE_COMPUTABLE:
        	if ( command[i] == null)
            {
              process.state = Process.STATE_COMPLETE;
              processcomplted();
              break;
            }
          if ( process.timeToCompute > 1 )
          {
            process.timeToCompute -- ;
          }
          else{
        	  cmdindex[i]++;
        	  process.state = Process.STATE_UNKNOWN;
          }
          break ;
        case Process.STATE_RESOURCE_WAIT:
        	if ( command[i] == null)
            {
              process.state = Process.STATE_COMPLETE;
              processcomplted();
              break;
            }
          if ( DeadlockManager.available( i , process.resourceAwaiting ) )
            if ( DeadlockManager.grantable( i , process.resourceAwaiting ) ) 
            {
              DeadlockManager.allocate( i , process.resourceAwaiting ) ;
              process.state = Process.STATE_UNKNOWN ;
              process.resourceAwaiting = null ;
              processUnblocked();
              cmdindex[i]++;
              process.state = Process.STATE_UNKNOWN;
            }
          break ;
        case Process.STATE_COMPLETE:
          // we're already stopped, no need to do anything
        	
          break ;
          
        }
      }
    printStatus();
    time ++ ;
    setStepping(false);
  }
  
  public void printStatus()
  {
    System.out.print( "time = " + time + " available =" ) ;
    for( int i = 0 ; i < resourceCount ; i ++ )
      {
      Resource resource = ((Resource)resources.elementAt(i)) ;
      System.out.print( " " + resource.getCurrentAvailable() ) ;
      }
    System.out.println( " blocked = " + blockedCount +", completed= "+ compltedCount) ;
    if(avoidance)
    	System.out.println("State Safe: "+checkSafteState());
    if(processCount==blockedCount)
    	System.out.println("Deadlock Created");
  }
  public void readCommands(){
	  	if(!avoidance){
	  		in.nextLine(); in.nextLine(); in.nextLine();
	  	}
		String line;
		while(in.hasNextLine()){
			line=in.nextLine();
			int temp=Integer.parseInt(line.charAt(0)+"");
			cmd[temp-1].add(line.replaceFirst(line.charAt(0)+"", ""));
			
		}
		command=new String[processCount];
  }
  
  public void updateCommands(){
	  for(int i=0;i<processCount;i++){
		  if(cmdindex[i]<cmd[i].size())
		  command[i]=cmd[i].get(cmdindex[i]);
		  else
			  command[i]=null;
	  }
  }
  
  public void run()
  {
    DeadlockManager.setProcesses(processes) ;
    DeadlockManager.setResources(resources) ;
    readCommands();
    while( true )
    {
    	updateCommands();
    	if(avoidance)
    		stepWithAvoidance();
    	else
    		step();
    	if(!stepping){
    	  updateCommands();
    	  if ( processCount == compltedCount ||
    	           processCount == blockedCount )
    		  break;
    	  step();
    	}
    }
  }
}
