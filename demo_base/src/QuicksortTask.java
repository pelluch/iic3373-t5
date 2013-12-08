import java.io.Serializable;
import java.util.ArrayList;


public class QuicksortTask implements Serializable {

	private static final long serialVersionUID = 2989395239186231271L;
    public  int getAnswerCount() {
        return array.length;
    }
	private int startIndex;
	private int pivotPos;
	private int[] array;
	
	public int getStartIndex()	{ return startIndex; }
	public int getPivotPos()	{ return pivotPos; }
	public int[] getArray()		{ return array; }
	
	public QuicksortTask(int startIndex, int pivotPos, int[] array){
		this.startIndex = startIndex;
		this.pivotPos = pivotPos;
		this.array = array;
	}

    public QuicksortTask() {
        this.startIndex = 0;
        this.pivotPos = 1;
        array = new int[] { 6, 2, 9, 0, 7, 3, 8, 4, 5, 1 };
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


    public ArrayList<QuicksortTask> getNextTasks(Object[] currentResults) {
        ArrayList<QuicksortTask> next = new ArrayList<QuicksortTask>();

        // Obtained the result, we then proceed to get its result:
        int resultPivotPos = getPivotPos();
        int resultStartIndex = getStartIndex();
        int[] resultArray =  getArray();
        System.out.println("Current pivot = " + resultArray[resultPivotPos]);
        // Store the resulting pivot in the array:
        System.out.println("currentResults length = " + currentResults.length);
        currentResults[resultStartIndex + resultPivotPos] = resultArray[resultPivotPos];
        System.out.println("Current pivot = " + resultArray[resultPivotPos]);


        // Then we proceed to split the remaining array in 2 subtasks:
        if(resultPivotPos < resultArray.length - 1){
            int newLength = resultArray.length - 1 - resultPivotPos;
            int newStartIndex = resultStartIndex + resultPivotPos + 1;
            int[] newArray = new int[newLength];

            for(int originalIndex = resultPivotPos + 1, index = 0; originalIndex < resultArray.length; originalIndex++, index++)
                newArray[index] = resultArray[originalIndex];

            QuicksortTask newTask = new QuicksortTask(newStartIndex, -1, newArray);
            next.add(newTask);
        }
        if(resultPivotPos > 0){
            int newLength = resultPivotPos;
            int newStartIndex = resultStartIndex;
            int[] newArray = new int[newLength];

            for(int i = 0; i < resultPivotPos; i++)
                newArray[i] = resultArray[i];

            QuicksortTask newTask = new QuicksortTask(newStartIndex, -1, newArray);
            next.add(newTask);
        }

        return next;
    }
	public void printTask(){
		System.out.println("Task");
		System.out.println("------------------------------------------");
		System.out.println("startIndex = " + startIndex);
		System.out.println("pivotPos = " + pivotPos);
		
		System.out.print("Array: [");
		for(int i = 0; i < array.length; i++)
			System.out.print(array[i] + ", ");
		
		System.out.println("EOT]");
		System.out.println("------------------------------------------\n");
	}
}