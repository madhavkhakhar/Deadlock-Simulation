
public class DeadlockAvoidance {
	int processCount;int resCount;
	int mprocessCount=0;
	int[][] resNeeded;int[] available;
	Boolean[] check;
	
	public DeadlockAvoidance(int pCount, int resCount,int[][] resNeeded, int[] available){
		this.processCount = pCount;
		this.mprocessCount=pCount;
		this.resCount= resCount;
		this.resNeeded= resNeeded;
		this.available = available;
		check = new Boolean[pCount];
	}
	
	public Boolean checkSafeS(){
		int count=0;
		  for(int i = 0 ; i < processCount ; i ++ ){
			  check[i]=true;
			  count=0;
			  for(int j=0;j<resCount;j++){
				  if(resNeeded[i][j]>available[j]){
					  check[i]=false;
					  break;
				  }
				  else if(resNeeded[i][j]==0)
					  count++;
			  }
			  if(count==resCount)
				  check[i]=false;
		  }
		  int index=-1;
		  for(int i=0;i<processCount;i++){
			  if(check[i]==true)
				  index=i;
		  }
		  if(index==-1){
			  if(mprocessCount>0)	return false;
			  return true;
		  }
		  for(int j=0;j<resCount;j++){
			  available[j]+=resNeeded[index][j];
			  resNeeded[index][j]=0;
		  }
		  mprocessCount--;
		  return checkSafeS();
	}
	
}
