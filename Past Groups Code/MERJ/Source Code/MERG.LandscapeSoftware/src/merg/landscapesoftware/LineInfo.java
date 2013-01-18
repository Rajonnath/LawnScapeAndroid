package merg.landscapesoftware;

//The class is used to extract information from a line object and then
// store the information using simple types in other for the object to 
//Serializable 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;

public class LineInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2717389251369718902L;
	
	//starting coordinates of a line
	float startX; 
	float startY;
	//end coordinate of a line
	float endX;
	float endY;
	int color;
	int linePattern;
	float linewidth;
	float curveDegree;
	boolean isCurve;
	int lineId;
	int arcMultiplier;
	
	public LineInfo()
	{
		//intialization to default values
		 startX = 0;
		 startY = 0;
		 endX = 0;
		 endY = 0 ;
		color = Color.BLACK;
		linePattern = 0;
		linewidth = 5;
		isCurve = false;
		lineId  = 0;
		curveDegree = 36;
		arcMultiplier = 1;
		
	}
	
	// return a list of line information for all the lines present in a hash map
	public ArrayList<LineInfo> getLineInfo(HashMap<Integer, Line> lineMap)
	{
		
		ArrayList<LineInfo> lineinfo = new ArrayList<LineInfo>();
		
		//loop through an hash map of line and get the information of individual line
		for (Integer key : lineMap.keySet()) 
	      {
			
			Line line = lineMap.get(key);
			LineInfo lineInfo = new LineInfo();
			
			lineInfo.color = line.color;
			lineInfo.linePattern = line.linePattern;
			lineInfo.linewidth = line.linewidth;
			lineInfo.isCurve = line.isCurve;
			lineInfo.lineId = line.lineId;
			lineInfo.curveDegree = line.curveDegree;
			lineInfo.arcMultiplier = line.arcMultiplier;
			getPathInfo(line.path);
			lineInfo.startX = startX;
			lineInfo.startY = startY;
			lineInfo.endX = endX;
			lineInfo.endY = endY;
			
			lineinfo.add(lineInfo);
			
	      }
		
		return lineinfo;
		
	}
	
	//extract information of a line path
	public void getPathInfo(Path path)
	{
		
		   float point1[] = {0f, 0f};
		   float point2[] = {0f, 0f};
		   
		   PathMeasure PM = new PathMeasure(path, false);
		   PM.getPosTan(0, point1, null);
		   PM.getPosTan(PM.getLength(), point2, null);
		   
		   startX = point1[0];
		   startY = point1[1];
		   endX = point2[0];
		   endY = point2[1];
	
	}
}
