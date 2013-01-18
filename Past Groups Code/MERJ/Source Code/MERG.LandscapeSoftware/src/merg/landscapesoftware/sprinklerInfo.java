package merg.landscapesoftware;

import java.io.Serializable;

// class use to package information of a sprinkler as a single object
public class sprinklerInfo implements Serializable
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double Radius;
		int Angle;
		int px;
		int py;
		int color;
		int startAngle;
		int ID;
		
		public sprinklerInfo()
		{
			Radius = 0;
			Angle = 0;
			px =0;
			py = 0;
			color = 1577058559; //transparent blue
			startAngle = 0;
		}
		
}