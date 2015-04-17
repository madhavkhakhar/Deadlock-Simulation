import java.io.*;
import java.util.Vector ;

public class Process
{
  public static final int STATE_UNKNOWN = 0 ;
  public static final int STATE_COMPUTABLE = 1 ;
  public static final int STATE_RESOURCE_WAIT = 2 ;
  public static final int STATE_COMPLETE = 3 ;

  protected int id ;
  
  protected int state = STATE_UNKNOWN ;
  protected int timeToCompute ;
  protected Resource resourceAwaiting = null ;
  private Vector<Resource> allocatedResources = new Vector<Resource>() ;

  public Process( int newId)
  {
    super() ;
    id = newId ;
    
  }

  public int getId()
  {
    return id ;
  }

  public int getState()
  {
	  return state;
  }

  public void setState( int newState )
  {
	this.state=newState;  
  }

  public Resource getResourceAwaiting( )
  {
    return resourceAwaiting ;
  }

  public void addAllocatedResource( Resource resource )
  {
    allocatedResources.addElement( resource ) ;
  }

  public void removeAllocatedResource( Resource resource )
  {
    allocatedResources.removeElement( resource ) ;
  }

  public void reset() throws IOException 
  {
    state = STATE_UNKNOWN ;
    timeToCompute = 0 ;
    resourceAwaiting = null ;
    allocatedResources.removeAllElements() ;
  }

}