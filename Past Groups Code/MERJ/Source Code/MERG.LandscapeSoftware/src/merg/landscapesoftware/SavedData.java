package merg.landscapesoftware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// this class is used to package all saved data as a single object 
//to be written to file

public class SavedData implements Serializable
{
	
	private static final long serialVersionUID = 1075283302708123031L; //sreialization version id

	public ArrayList<LineInfo> lineinfo; // list containing information about linew
	HashMap<Integer, sprinklerInfo> sMap; //sprinkler map containing sprinkler map
	 
	 int scale ;
	 
	 public SavedData(HashMap<Integer, Line> lineMap, HashMap<Integer, sprinklerInfo> sprinklerMap)
	 {
		 LineInfo info = new LineInfo();
		 lineinfo = info.getLineInfo(lineMap);
	        scale = 100;
	        sMap = sprinklerMap;
	        
	 }
	 

}
