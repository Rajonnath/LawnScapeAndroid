package merg.landscapesoftware;

//This class is used to store undo information
import merg.landscapesoftware.Settings.drawAction;

public class undo 
{
	public enum objectType
	{
		line, sprinkler
	};
	
	sprinklerInfo sInfo;
	Line line;
	drawAction action;
	objectType object;
	
	public undo()
	{
		
	}

}
