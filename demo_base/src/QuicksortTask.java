import java.io.Serializable;


public class QuicksortTask implements Serializable {

	private static final long serialVersionUID = 2989395239186231271L;
	private int startIndex;
	private int pivotPos;
	private int[] array;
	
	public QuicksortTask(int startIndex, int pivotPos, int[] array){
		this.startIndex = startIndex;
		this.pivotPos = pivotPos;
		this.array = array;
	}
	
	public QuicksortTask executeTask(){
		int[] result = new int[array.length];
		int pivot = array[0];
		
		int beginIndex = 0;
		int endIndex = result.length - 1;
		
		for(int i = 1; i < array.length; i++){
			int value = array[i];
			
			if(value <= pivot)
				result[beginIndex++] = value;
			else
				result[endIndex--] = value;
		}
		result[beginIndex] = pivot;
		
		return new QuicksortTask(startIndex, beginIndex, result);
	}
	
	public void printTask(){
		System.out.print("Task: [");
		for(int i = 0; i < array.length; i++)
			System.out.print(array[i] + ", ");
		
		System.out.println("EOT]");
	}
}