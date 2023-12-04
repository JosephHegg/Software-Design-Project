package TrainwreckTests.DataStructureTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHybridClass {

	public int[] myArrayInt = new int[5];
	public List<Integer> myInts = new ArrayList<Integer>();
	public Map<Integer, Integer> myIntMap = new HashMap<>();
	
	public void initializeData(){
		for(int i = 0; i < 5; i++){
			myArrayInt[i] = i;
			
			myInts.add(i);
			myIntMap.put(i, i);
		}
		
		calculateSum();
	}
	
	// should only be storing data, not using it for calculations!!!
	
	// Since hybrid, we default into saying that this is susceptible to being a PLK Violation
	
	public int calculateSum(){
		int value = 0;
		for(Integer currInt : myIntMap.keySet()){
			value += myIntMap.get(currInt);
		}
		
		return value;
	}
	
	
}
