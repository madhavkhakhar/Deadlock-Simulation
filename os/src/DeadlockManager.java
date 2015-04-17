import java.util.Vector ;

public class DeadlockManager
{

  private static Vector resources ;
  private static Vector processes ;

  public static void setResources( Vector newResources )
  {
    resources = newResources ;
  }

  public static void setProcesses( Vector newProcesses )
  {
    processes = newProcesses ;
  }

  public static boolean grantable( int id , Resource resource )
  {
    return available( id , resource ) ;
  }

  public static boolean available( int id , Resource resource )
  {
    return ( resource.getCurrentAvailable() > 0 ) ;
  }

  public static void allocate( int id , Resource resource )
  {
    resource.setCurrentAvailable( resource.getCurrentAvailable() - 1 ) ;
    Process p = (Process)processes.elementAt(id);
    p.addAllocatedResource( resource ) ;
  }

  public static void deallocate( int id , Resource resource )
  {
    resource.setCurrentAvailable( resource.getCurrentAvailable() + 1 ) ;
    Process p = (Process)processes.elementAt(id);
    p.removeAllocatedResource( resource ) ;
  }

  public static void deadlocked()
  {
	  //System.out.println("Deadlock created");
  }

}