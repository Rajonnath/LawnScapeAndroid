package merg.landscapesoftware;

import android.graphics.Path;
import android.graphics.Color;

//the class to save all the information about a line
public class Line extends Path 
{
	
	
	Path path; // line path information
	int color; // line color
	int linePattern; // line style eg solid line
	float linewidth; // line width
	float curveDegree; // degree of curvature
	boolean isCurve; // curve or straight
	int lineId; // line identification
	boolean showLen; // whether the line length should be display
	int arcMultiplier; // to decide whether a line is concave or convex

	public Line()
	{
		path = new Path();
		color = Color.BLACK;
		linePattern = 0;
		linewidth = 5;
		isCurve = false;
		lineId  = 0;
		curveDegree = 36;
		showLen = false;
		arcMultiplier = 1;
		
	}
	
}
