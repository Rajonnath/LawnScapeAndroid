// DrawingView.java
// Main View for the GUIActivity app.
//This file handles all the drawing functions for this app
//Functions that help user to draw lines, curves, and place sprinklers on canvas
//It extends custom view class
package merg.landscapesoftware;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Stack;
import merg.landscapesoftware.Settings.drawAction;
import merg.landscapesoftware.Settings.drawOption;
import merg.landscapesoftware.undo.objectType;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

// the main screen that is painted
public class DrawingView extends View 
{
	// an enumeration type to use to mark different object on the screen
	public enum selectedObject
    {     
        sprinkler,
        
        line // curves are also regarded as line
    }
	

	public Settings settings; // class setting 
	
// used to determine whether user moved a finger enough to draw again   
   private static final float TOUCH_TOLERANCE = 10;

   private Bitmap bitmap; // drawing area for display or saving
   private Canvas bitmapCanvas; // used to draw on bitmap
   private Paint paintScreen; // use to draw bitmap onto screen
   
   private Paint tipPaint; // used to draw toolTip
   HashMap<Integer, Line> lineMap; // store lines that are on the screen
   private HashMap<Integer, Point> previousPointMap; // current Points
   HashMap<Integer, sprinklerInfo> sprinklerMap; // stores sprinkler on the screen
   private int screenwidth; // store screen width
   private int screenheight; // store screen height
   String distTip = ""; // tips on line draw on the screen
   Point tipPoint = new Point(600,20); //Cord to place distTip
   float previousX=0;
   float previousY = 0;
   double time1 = 0;
   int objectCount = 0;
   int sprinklerCount = 0;
   int arcMultiplier = 1; // variable to adjust curve from concave to convex or vice visa
   int originalColor;
   boolean isCurve; //differentiate between curves and straight line
   int linestyle = 1; // default line style (solid Line)
   Stack<undo> undoStack = new Stack<undo>(); // stores previous actions
   Stack<undo> redoStack = new Stack<undo>(); // stores actions that are undone
   
   int linepattern = 0;
   Line selectedLine;
   Point pointOfSelection;
   Paint sprinklerPaint;
   sprinklerInfo selectedsprinkler;
   EditText rotationAngle;
   int sprinklerColor = 1577058559;

   // DrawingView constructor initializes the DrawingView
   public DrawingView(Context context, AttributeSet attrs) 
   {
      super(context, attrs); // pass context to View's constructor

      isCurve = false;
      linestyle = 1;
      
      paintScreen = new Paint(); // used to display bitmap onto screen
      tipPaint = new Paint(); //use to draw tips
      settings = new Settings(context); //holds variable for different configuration of the app
      
      
      tipPaint.setColor(Color.GREEN);
      tipPaint.setStrokeWidth(1);
      tipPaint.setTextSize(15);
      tipPaint.setAntiAlias(true);
      tipPaint.setStyle(Paint.Style.STROKE);
      tipPaint.setStrokeCap(Paint.Cap.ROUND);
      
    
       sprinklerPaint = new Paint();
	   sprinklerPaint.setStrokeWidth(1);
	   sprinklerPaint.setStrokeCap(Paint.Cap.ROUND);
	   sprinklerPaint.setStyle(Paint.Style.FILL);
	   sprinklerPaint.setColor(sprinklerColor);      
      
      lineMap = new HashMap<Integer, Line>();
      previousPointMap = new HashMap<Integer, Point>();
      sprinklerMap = new HashMap<Integer, sprinklerInfo>();
      
      
  
      
   } // end DrawingView constructor

   // Method onSizeChanged creates BitMap and Canvas after app displays
   @Override
   public void onSizeChanged(int w, int h, int oldW, int oldH)
   {
	   //get the actual screen size of the device 
	   
	   screenheight =  h;
	   screenwidth = w;
	   
      bitmap = Bitmap.createBitmap(getWidth(), getHeight(), 
         Bitmap.Config.ARGB_8888);
      bitmapCanvas = new Canvas(bitmap);
      bitmap.eraseColor(Color.WHITE); // erase the BitMap with white
   } // end method onSizeChanged
   
   // clear the painting
   public void clear()
   {
      lineMap.clear(); // remove all lines
      sprinklerMap.clear(); // remove all sprinkler
      previousPointMap.clear(); // remove all previous points
      bitmap.eraseColor(Color.WHITE); // clear the bitmap 
      
      invalidate(); // refresh the screen
   } // end method clear
   
   // set the painted line's color

   // called each time this View is drawn
   @Override
   protected void onDraw(Canvas canvas) 
   {
      // draw the background screen
      canvas.drawBitmap(bitmap, 0, 0, paintScreen);
      bitmap.eraseColor(Color.WHITE);
      
      int color = settings.getDrawingColor();
      int width = settings.getLineWidth();

    	  
      // for each line currently being drawn
      for (Integer key : lineMap.keySet()) 
      {
    	  // customize the paintline to properties of the line being drawn
    	  Line line = lineMap.get(key);
    	  settings.paintLine.setColor(line.color);
    	  settings.paintLine.setStrokeWidth(line.linewidth);
    	  if(line.linePattern ==0)
    	  {
    		  settings.paintLine.setPathEffect(new DashPathEffect(new float[] {0,0}, 0));
    	  }
    	  else if (line.linePattern ==1)
    	  {
    		  
    		  settings.paintLine.setPathEffect(new DashPathEffect(new float[] {20,10}, 5));
    	  }
    	  else if (line.linePattern ==2)
    	  {
    		  
    		  settings.paintLine.setPathEffect(new DashPathEffect(new float[] {5,10}, 5));
    	  }
    	  else if (line.linePattern ==3)
    	  {
    		  
    		  settings.paintLine.setPathEffect(new DashPathEffect(new float[] {1,10}, 5));
    	  }
    	  
    	  if(line.showLen)
    	  {
    		  displayLength(line, canvas );
    	  }
    	 
    	  arcMultiplier = line.arcMultiplier;
         canvas.drawPath(line.path, settings.paintLine); // draw line
      }
      
      for (Integer key : sprinklerMap.keySet()) 
      {
      
    	sprinklerInfo sInfo =   sprinklerMap.get(key);
    	sprinklerPaint.setColor(sInfo.color);
    	drawSprinkler(sInfo, sInfo.px, sInfo.py, canvas);
    	
    	
      }
      
      if(settings.displayPageDesc == true)
      {
    	  drawingDetails(canvas);
      }
      
      settings.paintLine.setColor(color);
      settings.setLineWidth(width);
      
      if(settings.showGridLines)
      {
    	  drawGridLines(canvas);
    	  
      }
      
     canvas.drawText(distTip, tipPoint.x, tipPoint.y, tipPaint);
   } // end method onDraw

   // handle touch event
   @Override
   public boolean onTouchEvent(MotionEvent event) 
   {
      // get the event type and the ID of the pointer that caused the event
      int action = event.getActionMasked(); // event type 
      int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
     
      
      // determine which type of action the given MotionEvent 
      // represents, then call the corresponding handling method
      if (action == MotionEvent.ACTION_DOWN ) 
      {
         touchStarted(event.getX(actionIndex), event.getY(actionIndex), 
        		 ++objectCount);
      } // end if
      else if (action == MotionEvent.ACTION_UP ) 
      {
    	  if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line))
    	  {
    		  if (lineMap.containsKey( objectCount)) 
    	      	{
    			  Line line = lineMap.get(objectCount); // get the corresponding line
    			  
    			  snapTonearestLine(line);
    	      	}
    	  }
         touchEnded(objectCount);
         
      } // end else if
      else if (action == MotionEvent.ACTION_POINTER_UP ) 
      {
    	  
    	  touchEnded(objectCount);
    	  touchStarted(event.getX(0), event.getY(0),
    			  ++objectCount);
    	  arcMultiplier = arcMultiplier* -1;
      }
      else 
      {
    	  touchMoved(event); 
      } // end else
      
      invalidate(); // redraw
      return true; // consume the touch event
   } // end method onTouchEvent
   
 

   // called when the user touches the screen
   private void touchStarted(float x, float y, int lineID) 
   {
	   Line line; // used to store the line for the given touch id
      Point point; // used to store the last point in line

      point = new Point(); // create a new Point
      point.x = (int) x;
      point.y = (int) y;
	  
      //determine which action is being taken by user
      
      if(settings.dAction == drawAction.select)
      {
    	  selectObject(point); // select a line
    	 
      }
      else if (settings.dAction == drawAction.move) // move line from one place to another
      {
    	  if((settings.dOption == drawOption.line) ||(settings.dOption == drawOption.line))
    	  {
    		 
    		  pointOfSelection = point;
    		  Line l = new Line();
    		  l = selectedLine;
    		  undo u = new undo(); // store the action in undo stack
    		  l.color = originalColor;
    		  u.line = l;
    		  u.action = drawAction.move;
    		  u.object = objectType.line;
    		  undoStack.push(u);
    	  
    	  }
    	  else if(settings.dOption == drawOption.sprinkler)
    	  {
    		  if (sprinklerMap.containsKey( selectedsprinkler.ID)) 
    		  {
	   	
    			  sprinklerInfo sInfo = sprinklerMap.get(selectedsprinkler.ID);
    			  sInfo.color = sprinklerColor;
    			  undo u = new undo();
    			  u.sInfo = sInfo;
    			  u.action = drawAction.move;
    			  u.object = objectType.sprinkler;
    			  undoStack.push(u);
    		  }
    	  }
   	   
      }
      else if (settings.dAction == drawAction.rotate)
      {
    	  pointOfSelection = point; //get the fulcrum point of rotation
    	 
    	  
      }
      else if (settings.dAction == drawAction.delete)
      {
    	  pointOfSelection = point;
    	  
      }
      else if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line) &&(settings.dAction == drawAction.draw))
      {
    	  
    	// create a new line  and store its properties and put it on hashmap
      // if there is already a path for lineID
    	if (lineMap.containsKey( objectCount)) 
      	{
    	  line = lineMap.get(objectCount); // get the Path
    	  line.path.reset(); // reset the Path because a new touch has started
    	  point = previousPointMap.get(objectCount); // get Path's last point
      	} // end if
      	else 
      	{
    	  	line = new Line(); // create a new Line
         	line.color = settings.paintLine.getColor();
         	
         	line.isCurve = isCurve;
         	line.linewidth = settings.paintLine.getStrokeWidth();
         	line.lineId = lineID;
         	line.arcMultiplier = arcMultiplier;
         	switch(settings.linestyle)
         	{
         	
         	case solid_line:
         		line.linePattern = 0;
         		break;
         		
         	case dashed_line:
         		line.linePattern = 1;
         		break;
         		
         	case dotted:
         		line.linePattern = 2;
         		break;
         		
         	case fine_dotted:
         		line.linePattern = 3;
         		break;
         		
         	}
         	line.curveDegree = settings.curveDegree;
         	lineMap.put(lineID, line); // add the Line to Map
         	
         	previousPointMap.put(lineID, point); // add the Point to the Map
      	} // end else

      // move to the coordinates of the touch
      	line.path.moveTo(x, y);
      	point.x = (int) x;
      	point.y = (int) y;
      }
      
      
      else if ((settings.dOption == drawOption.sprinkler ) &&(settings.dAction == drawAction.draw))
      {
    	// create a new line  and store its properties and put it on hashmap
    	  
    	  sprinklerInfo sInfo = new sprinklerInfo();
    	  sInfo.Angle = settings.Angle;
    	  sInfo.Radius = settings.Radius;
    	  sInfo.px = point.x;
    	  sInfo.py = point.y;
    	  objectCount += 1;
    	  sInfo.ID = objectCount;
    	  sprinklerMap.put(objectCount, sInfo);
    	  drawSprinkler(sInfo, point.x, point.y, bitmapCanvas);
    	  
    	  invalidate();
    	  
      }
      //objectCount++;
   } // end method touchStarted

   // called when the user drags along the screen
   private void touchMoved(MotionEvent event) 
   {
	   PointF p2 = new PointF();
	   p2.x = event.getX(0);
	   p2.y = event.getY(0);
	   
	   if (settings.dAction == drawAction.move)
	   {
		   if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line))
		   {
		   
		   		moveLine(selectedLine, pointOfSelection, p2);
		   }
		   else if(settings.dOption == drawOption.sprinkler)
		   {
			   
			   Point p = new Point();
			   p.x = (int)p2.x;
			   p.y = (int)p2.y;
			   selectedsprinkler.px = p.x;
			   selectedsprinkler.py = p.y;
			   
			  
		   }
	    	  
	    }
	   else if (settings.dAction == drawAction.rotate)
	   {
		   if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line))
		   {
		   
			   rotateLine(selectedLine, pointOfSelection, p2);
		   }
		  
	   }
	   else if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line) &&(settings.dAction == drawAction.draw))
	   {
            
		// create a new line  and store its properties and put it on hashmap
         if (lineMap.containsKey(objectCount)) 
         {
            // get the new coordinates for the pointer
            float newX = event.getX(0);
            float newY = event.getY(0);
            
            
            // get the Path and previous Point associated with 
            // this pointer
            Line line = lineMap.get(objectCount);
            Point point = previousPointMap.get(objectCount);
            
            // calculate how far the user moved from the last update
            float deltaX = Math.abs(newX - point.x);
            float deltaY = Math.abs(newY - point.y);

            // if the distance is significant enough to matter
            if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) 
            {
              
            	line.path.rewind(); //rewind the path as the path changes
            	point = previousPointMap.get(objectCount);// get the starting point of the line
            	line.path.moveTo(point.x, point.y);//move starting point of the path to this point
                if(!(isCurve))
                {
                	line.path.lineTo(newX,  newY); //draw straight line
                }
                else
                {
            	
                	PointF p1= new PointF(point);
                	
                	p2.x = newX;
                	p2.y = newY;
                	DrawArc( p1, p2, line.path); // draw curve
            	
                }
            	
            	
            	//calculate the length and angle(calculated relative to horizontal) of lines drawn on the screen
                
            	double angle = calculateAngle(point.x, point.y, newX, newY) ;
            	double distance = getDistance (point.x, point.y, newX, newY);
            	angle = Math.round(angle);
            	distance = Math.round(distance);
            	//display this information as tips to user
            	distTip = "Distance = " + Double.toString(distance) + "m, " + "Angle " + Double.toString(angle) + " degree" ;
            	
            	
            } // end if
            
         } // end if
         
	   }
      
	   else if ((settings.dOption == drawOption.sprinkler) && (settings.dAction == drawAction.draw))
	      {
	    	  //update cord information of a sprinkler as you move it across screen
	    	  sprinklerInfo sInfo = sprinklerMap.get(objectCount);
	    	  sInfo.px = (int) p2.x;
	    	  sInfo.py = (int) p2.y;
	    	  
	    	  
	    	  invalidate();//refresh screen
	    	  
	      }
      
   } // end method touchMoved

   // called when the user finishes a touch
   private void touchEnded(int lineID) 
   {
	   if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line) &&(settings.dAction == drawAction.draw))
	   {
		   if (lineMap.containsKey(lineID)) 
		   {
		   	
			   Line line = lineMap.get(lineID); // get the corresponding line
			   // put the information in the undo stack
			   undo u = new undo();
			   u.line = line;
			   u.action = drawAction.draw;
			   u.object = objectType.line;
			   undoStack.push(u);
			   bitmapCanvas.drawPath(line.path, settings.paintLine); // draw to bitmapCanvas
		   }
		   
	   }
	   
	   else if((settings.dOption == drawOption.sprinkler) &&(settings.dAction == drawAction.draw))
	   {
		   
		   if (sprinklerMap.containsKey( lineID)) 
		   {
		   	
			   sprinklerInfo sInfo = sprinklerMap.get(lineID); // get the corresponding sprinkler 
			   // put the information in the undo stack
			   undo u = new undo();
			   u.sInfo = sInfo;
			   u.action = drawAction.draw;
			   u.object = objectType.sprinkler;
			   undoStack.push(u);
			   
		   }
		   
	   }
   
	   else if((settings.dAction == drawAction.move) || (settings.dAction == drawAction.rotate))
	   {
		   
		   //change back the color of object being edited to its original color
		  if( (settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line))
		   {
			   //change back the color of line being edited to its original color
			  selectedLine.color = originalColor;
			  lineMap.put(selectedLine.lineId, selectedLine);
			  
			  
			  
			  settings.dAction = drawAction.draw;
			  
		   }
		  else if(settings.dOption == drawOption.sprinkler)
		  {
			  
			  selectedsprinkler.color = sprinklerColor;
  			  
  			  sprinklerMap.put(selectedsprinkler.ID, selectedsprinkler);
  			  settings.dAction = drawAction.draw;
			  
		  }
	   }
      
     
      
   } // end method touch_ended

   // save the current image to the Gallery
   public void saveImage()
   {
      // use "GUIActivity" followed by current time as the image file name
      String fileName = "GUIActivity" + System.currentTimeMillis();

      // create a ContentValues and configure new image's data
      ContentValues values = new ContentValues();
      values.put(Images.Media.TITLE, fileName);
      values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
      values.put(Images.Media.MIME_TYPE, "image/jpg");

      // get a Uri for the location to save the file
      Uri uri = getContext().getContentResolver().insert(
         Images.Media.EXTERNAL_CONTENT_URI, values);

      try 
      {
         // get an OutputStream to uri
         OutputStream outStream = 
            getContext().getContentResolver().openOutputStream(uri);

         // copy the bitmap to the OutputStream
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

         // flush and close the OutputStream
         outStream.flush(); // empty the buffer
         outStream.close(); // close the stream

         // display a message indicating that the image was saved
         Toast message = Toast.makeText(getContext(), 
            R.string.message_saved, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            message.getYOffset() / 2);
         message.show(); // display the Toast
      } // end try
      catch (IOException ex) 
      {
         // display a message indicating that the image was saved
         Toast message = Toast.makeText(getContext(), 
            R.string.message_error_saving, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            message.getYOffset() / 2);
         message.show(); // display the Toast
      } // end catch
   } // end method saveImage
   
   //calculates angle in degree of a line relative to horizontal 
   public double calculateAngle (float x1, float y1, float x2, float y2)
   {
	   
	   double angle = Math.atan((y2-y1)/(x2-x1));
	   angle = Math.toDegrees(angle);
	   return angle;
	   
	   
   }
   
   //gets distance between two points
   public double getDistance(float x1, float y1, float x2, float y2)
   {
	  
	   double dist = Math.sqrt(((x2-x1)*(x2-x1))+ ((y2-y1)*(y2-y1)));
	   
	   return ((settings.scale*dist)/screenwidth);// return the scaled distance
	   
   }
   
 

   //draws an arc between two points
   public void DrawArc(PointF e1, PointF e2, Path path)
   {
	   
	  
	    float a1Degrees = settings.curveDegree; // degree of curvature set by user
		double a1 = Math.toRadians(a1Degrees); 
		// l1 is half the length of the line from e1 to e2
		double dx = e2.x - e1.x, dy = e2.y - e1.y;
		double l = Math.sqrt((dx * dx) + (dy * dy));
		double l1 = l / 2.0;
		
		// h is length of the line from the middle of the connecting line to the 
		//  center of the circle.
		double h = l1 / (Math.tan(a1 / 2.0));
				
		// r is the radius of the circle
		double r = l1 / (Math.sin(a1 / 2.0));
		
		// a2 is the angle at which L intersects the x axis
		double a2 = Math.atan2(dy, dx);
		
		// a3 is the angle at which H intersects the x axis
	    double a3 = (Math.PI / 2.0) - a2;
	    
	 // m is the midpoint of the line from e1 to e2
        double mX = (e1.x + e2.x) / 2.0;
        double mY = (e1.y + e2.y) / 2.0;

        // c is the the center of the circle
        double cY = mY + (arcMultiplier*(h * Math.sin(a3)));
        double cX = mX - (arcMultiplier*(h * Math.cos(a3)));
        
       
        
        // rect is the square RectF that bounds the "oval"
        RectF oval = 
            new RectF((float) (cX - r), (float) (cY - r), (float) (cX + r), (float) (cY + r));
        
        // a4 is the starting sweep angle
        double rawA4 = Math.atan2(e1.y - cY, e1.x - cX);
        float a4 = (float) Math.toDegrees(rawA4);
        
        //c(oval, a4, a1Degrees, false, paint);
        
        a1Degrees =  arcMultiplier*a1Degrees; // determines whether the curve is concave or convex
        path.arcTo(oval, a4, a1Degrees); // draw the arc
        
	   
   }
   
   //Join the line being drawn to the closet line around
   public void snapTonearestLine(Line line)
   {
	   
	   float point1[] = {0f, 0f};
	   float point2[] = {0f, 0f};
	   
	   PointF p1;
	   PointF p2;
	   
	   PathMeasure PM = new PathMeasure(line.path, false);
	   PM.getPosTan(0, point1, null);
	   PM.getPosTan(PM.getLength(), point2, null);
	   
	   float x1 = point1[0];
	   float y1 = point1[1];
	   float x2 = point2[0];
	   float y2 = point2[1];
	   
	   p1 = checkPathProximity(x1, y1); //checks whether there is a line in the proximity
	   p2 = checkPathProximity(x2, y2);
	   
	   line.path.rewind();
	   	line.path.moveTo(p1.x, p1.y);
	    
	    if(!(line.isCurve))
		 {
			 line.path.lineTo(p2.x, p2.y);
		 }
		 else
		 {
			 
           	DrawArc( p1, p2, line.path);
		 }
	   
	   
   }
  
   
   public PointF checkPathProximity(float x, float y)
   {
	
	   PointF point = new PointF();
	   point.x = x;
	   point.y = y;
	   
	   for (Integer key = 1; key<objectCount; key++) 
	   {
		   Path path;
		   if (lineMap.containsKey( key)) 
		   {
			   	path = lineMap.get(key).path;
		      
			   	PathMeasure pm = new PathMeasure(path, false);
			   	//coordinates will be here
			   	float pstart[] = {0f, 0f};
			   	float pEnd[] = {0f, 0f};
			   	float pMid[] = {0f, 0f};

			   	//get point from beginning
			   	pm.getPosTan(0, pstart, null);
			   	//get point from end
			   	pm.getPosTan(pm.getLength(), pEnd, null);
	    
			   	if((Math.abs(pstart[0]- x)<=20) && (Math.abs(pstart[1] - y) <= 20))
			   	{
			   		point.x= pstart[0];
			   		point.y = pstart[1];
			   		
			   		return point;
			   
			   	}
			   	else if((Math.abs(pEnd[0]- x)<=20) && (Math.abs(pEnd[1] - y) <= 20))
				{
			    	
			   		point.x= pEnd[0];
			   		point.y = pEnd[1];
			   		return point;
				}
			   	double ratio = (getDistance(pstart[0], pstart[1], x, y))/(getDistance(pstart[0], pstart[1], pstart[0], pEnd[1]));
				   pm.getPosTan(pm.getLength()*(float)ratio, pMid, null);
		    	 
				   if((Math.abs(pMid[0]- x)<=20) && (Math.abs(pMid[1] - y) <= 20))
				   {
					   point.x= pMid[0];
					   point.y = pMid[1];
					   return point;
					   
				   }
		   
		   }
		      
		   
	   }
	   
	   return point;
   }
   
   
   //undo previous action based on the information in the undo stack
   public void undo()
   {
	   if(!undoStack.isEmpty())
	   {
		   undo u = undoStack.pop();
	   
		   //check the type of action
		   if(u.action == drawAction.draw)
		   {
			   //check the type of object the action apllies to
			   if(u.object == objectType.line)
			   {
		   
				   lineMap.remove(u.line.lineId);
				   redoStack.push(u);
			   }
			   else if(u.object == objectType.sprinkler)
			   {
				   sprinklerMap.remove(u.sInfo.ID);
				   redoStack.push(u);
			   }
			   
		   }
		   else if(u.action == drawAction.delete)
		   {
			   
			   if(u.object == objectType.line)
			   {
				   
				   lineMap.put(u.line.lineId, u.line);
				   redoStack.push(u);
			   }
			   else if(u.object == objectType.sprinkler)
			   {
				   sprinklerMap.put(u.sInfo.ID, u.sInfo);
				   redoStack.push(u);
			   }
			   
		   }
		   else if(u.action == drawAction.move)
		   {
			   
			   if(u.object == objectType.line)
			   {
				   Line line = new Line();
				   line = lineMap.remove(u.line.lineId);
				  
				   
				   lineMap.put(u.line.lineId, u.line);
				 //  u.line = line;
				   redoStack.push(u);
			   }
			   else if(u.object == objectType.sprinkler)
			   {
				   sprinklerInfo sInfo = sprinklerMap.get(u.sInfo.ID);
				   
				   sprinklerMap.put(u.sInfo.ID, u.sInfo);
				   u.sInfo  = sInfo;
				   redoStack.push(u); // copy the action into redo stack in case we need to redo the action
			   }
			   
		   }
		   
		   invalidate();
	   }
	   
   }
   
   //redo action previously undo
   public void redo()
   {
	   
	   if(!redoStack.isEmpty())
	   {
		   undo u = redoStack.pop();
		   
		// check the type of action
		   if(u.action == drawAction.draw)
		   {
			   
			   // check the type of the object
			   if(u.object == objectType.line)
			   {
		   
				   lineMap.put(u.line.lineId, u.line);
				   
			   }
			   else if(u.object == objectType.sprinkler)
			   {
				   sprinklerMap.put(u.sInfo.ID, u.sInfo);
				  
			   }
			   
		   }
		   
		   else if(u.action == drawAction.delete)
		   {
			   
			   if(u.object == objectType.line)
			   {
				   
				   lineMap.remove(u.line.lineId);
				   
			   }
			   else if(u.object == objectType.sprinkler)
			   {
				   sprinklerMap.remove(u.sInfo.ID);
				  
			   }
			   
		   }
		   else if(u.action == drawAction.move)
		   {
			   
			   if(u.object == objectType.line)
			   {
				   Line line = lineMap.get(u.line.lineId);
				  
				   
				   lineMap.put(u.line.lineId, u.line);
				   u.line = line;
				
			   }
			   else if(u.object == objectType.sprinkler)
			   {
				   sprinklerInfo sInfo = sprinklerMap.get(u.sInfo.ID);
				   
				   sprinklerMap.put(u.sInfo.ID, u.sInfo);
				   u.sInfo  = sInfo;
				  
			   }
			   
		   }
	   
		   undoStack.push(u);
	   }  
	   
	   invalidate();
   }
   
   
   //method to select an object
   public void selectObject(Point p)
   {
	   //check the type of the object being select
	  if(settings.dOption == drawOption.curve || settings.dOption == drawOption.line)
	  {
		  
	  
		  // check there is a line along the cord touched by the user
	   for(Integer key : lineMap.keySet())  
	   {
		   Line line;
		   Path path;
		   if (lineMap.containsKey( key)) 
		   {
			   line = lineMap.get(key);
			   	path = line.path;
		      
		   PathMeasure pm = new PathMeasure(path, false);
		   //coordinates will be here
		   float pPath[] = {0f, 0f};
		   
		   float len = 0;
		   
		   while(len<= pm.getLength())
		   {
			   pm.getPosTan(len, pPath, null); // get the information of a line
			   
			   if((Math.abs(pPath[0]- p.x)<=10) && (Math.abs(pPath[1] - p.y) <= 10))
			   {
				   originalColor = line.color; // store theline color
				   line.color = Color.RED; // change the selected lie color to red
				   selectedLine = line;
				   settings.dAction = drawAction.none;
				   return;
			   }
			   
			   len+= 10;
		   }
		   
		   }
		   
	   }
	   
	  }
	  
	  else if (settings.dOption == drawOption.sprinkler)
      {
		  for (Integer key : sprinklerMap.keySet()) 
		  {
			  
			  sprinklerInfo sInfo = new sprinklerInfo();
			  sInfo = sprinklerMap.get(key);
			  double radiusInPixel = getDistantInPixel(sInfo.Radius);
			  
			  //draw a region around point where user touches on the screen
			  //and check wherther a sprinkler lies in that region
			  Region r = new Region((int)(sInfo.px - (radiusInPixel)), (int)(sInfo.py - (radiusInPixel)), (int)(sInfo.px + (radiusInPixel)), (int)(sInfo.py + (radiusInPixel)));
			  
			  if(r.contains(p.x, p.y))
			  {
				  sInfo.color = Color.RED;
				  selectedsprinkler = sInfo;
				  settings.dAction = drawAction.none;
				  return;
			  }
		  }
    	  
    	  
    	  
    	  invalidate();
    	  
      }
	  
	  
   
   }
	   // move line around on the screen
   public void moveLine(Line line, Point p, PointF p2)
   {
	 		
	   		
		   Matrix m = new Matrix();
		   m.setTranslate(p2.x - p.x, p2.y - p.y);
		   line.path.transform(m);
		   pointOfSelection.x = (int) p2.x;
		   pointOfSelection.y = (int) p2.y;
		   
	   //}
		     
	   
   }
   
   //rotate a line
   public void rotateLine(Line line, Point p, PointF p2)
   {
	   if(isLineInProximity(line, p))
	   {
		   PathMeasure pm = new PathMeasure(line.path, false);
		   //coordinates will be here
		   float pPath[] = {0f, 0f};
		   
		   pm.getPosTan(pm.getLength(), pPath, null);
		   
		   p2.x = (int) pPath[0];
		   p2.y = (int) pPath[1];
		   
		   double angle = calculateAngle (p.x, p.y, p2.x, p2.y);
		   
		   Matrix m = new Matrix();
		   m.setRotate((float) angle, p.x, p.y);
		   line.path.transform(m);
		   
		   pointOfSelection.x = (int) p2.x;
		   pointOfSelection.y = (int) p2.y;
	   }
		     
	   
   }
   
   //checks whether a point lies on a particular line
   public boolean isLineInProximity(Line line, Point point)
   {
	   
	   PathMeasure pm = new PathMeasure(line.path, false);
	   //coordinates will be here
	   float pPath[] = {0f, 0f};
	   
	   float len = 0;
	   
	   while(len<= pm.getLength())
	   {
		   pm.getPosTan(len, pPath, null);
		   
		   if((Math.abs(pPath[0]- point.x)<=10) && (Math.abs(pPath[1] - point.y) <= 10))
		   {
			   return true;
		   }
		   
		   len+= 10;
	   }
	   
	   return false;
   }
   
   //loads saved data and re draw the saved objects
   public void LoadData(SavedData savedObj) throws StreamCorruptedException, IOException, ClassNotFoundException
   {
	   
	   
	   lineMap.clear(); // empty the line map
	   sprinklerMap.clear(); // empty the sprinkler map
	   objectCount = 1;
	   
	   if (savedObj.sMap != null)
	   {
		   sprinklerMap = savedObj.sMap;
	   }
	   
	   // get and copy all the saved lines into line map
	   for(LineInfo info: savedObj.lineinfo)
	   {
		   
		   Line line = new Line();
		   line.color = info.color;
		   line.curveDegree = info.curveDegree;
		   line.linePattern = info.linePattern;
		   line.linewidth = info.linewidth;
		   line.isCurve = info.isCurve;
		   line.arcMultiplier = info.arcMultiplier;
		   line.lineId = objectCount;
		   Path path = new Path();
		   path.moveTo(info.startX, info.startY);
		   
		   if(info.isCurve)
		   {
			   	PointF p1= new PointF();
			   	PointF p2 = new PointF();
			   	p1.x = info.startX;
			   	p1.y = info.startY;
			   	p2.x = info.endX;
           		p2.y = info.endY;
           		DrawArc( p1, p2, path);
           		
		   }
		   else
		   {
			   
			   path.lineTo(info.endX,  info.endY);
		   }
		   
		   line.path = path;
		   
		   lineMap.put(line.lineId, line);
		   
		   objectCount++;
	   }
	   
	   if (sprinklerMap != null)
	   {
		   
		   // change the ids of the sprinkler, to make objectcount to be in sync
		   
		   HashMap<Integer, sprinklerInfo> tempMap;
		   tempMap = new HashMap<Integer, sprinklerInfo>();
	   
		   for (Integer key : sprinklerMap.keySet()) 
		   {
			  
			
			   sprinklerInfo sInfo =    sprinklerMap.get(key);
			   sInfo.ID = objectCount;
			   tempMap.put(objectCount, sInfo);
			   objectCount++;
		   }
		   sprinklerMap.clear();
		   for (Integer key : tempMap.keySet()) 
		   {
			   
			   sprinklerInfo sInfo =    tempMap.get(key);
			   
			   sprinklerMap.put(objectCount, sInfo);
			  
		   }
	   
	   }
	   
	   objectCount++;
	   
	   invalidate();   
   }
   
   // draw sprinkler on to the screen
   public void drawSprinkler(sprinklerInfo sInfo, int px, int py, Canvas canvas)
   {
	   
	  //paint to draw the base of sprinkler
	   Paint sprinklerBase = new Paint();
	   sprinklerBase.setStrokeWidth(1);
	   sprinklerBase.setStrokeCap(Paint.Cap.ROUND);
	   sprinklerBase.setStyle(Paint.Style.FILL);
	   sprinklerBase.setColor(Color.BLACK);

	   
	   double radiusInPixel = getDistantInPixel(sInfo.Radius); // get the radius in pixel
	   
	   
	   //rectangle that defines the region where the sprinkler lies
	   RectF oval = 
	            new RectF((px-(float)radiusInPixel),( py -(float)radiusInPixel), (px+(float)radiusInPixel), (py + (float)radiusInPixel));
	   canvas.drawArc(oval, sInfo.startAngle, sInfo.Angle, true, sprinklerPaint);
	   canvas.drawCircle(px, py, 10, sprinklerBase);
	     
	  
   }
   
   //get equivalent distant in pixel to be drawn on the screen for particualr measurement
   public double getDistantInPixel(double dist)
   {
	   
	   return screenwidth*dist/settings.scale;
   }
   
   //rotate sprinkler
   public void rotateSprinkler()
   {
	   AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

	   //get the angle of rotation in degree
	   rotationAngle = new EditText(getContext());
   		rotationAngle.setInputType(InputType.TYPE_CLASS_NUMBER);
   	 
   		alert.setTitle("Enter angle");
   		alert.setMessage("Please enter rotation angle");

   	// Set an EditText view to get user input 
   	
   		alert.setView(rotationAngle);

   		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
   			public void onClick(DialogInterface dialog, int whichButton) 
   			{
   	    
   				selectedsprinkler.startAngle = (selectedsprinkler.startAngle+ Integer.parseInt(rotationAngle.getText().toString()))%360;
   				
   				selectedsprinkler.color = sprinklerColor;
  			  
  			  sprinklerMap.put(selectedsprinkler.ID, selectedsprinkler);
  			  settings.dAction = drawAction.draw;
   			}
   		});

   		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
   			public void onClick(DialogInterface dialog, int whichButton) {
   	    // Canceled.
   			}
   		});

   		alert.show();
   	 
   
   }
   
   //print drawing description on the screen
   public void drawingDetails(Canvas c)
   {
	   
	   Paint paint = new Paint();
	   paint.setStrokeWidth(1);
	   paint.setStrokeCap(Paint.Cap.ROUND);
	   paint.setStyle(Paint.Style.STROKE);
	   paint.setColor(Color.BLACK);
	   paint.setTextSize(25);
	   
	   c.drawRect(50, 620, 1220, 680, paint);
	   c.drawLine(340, 620, 340, 1220, paint);
	  
	   c.drawLine(630, 620, 630, 1220, paint);
	   c.drawLine(920, 620, 920, 1220, paint);
	   
	   c.drawText("Company", 55, 640, paint);
	   c.drawText("Project", 345, 640, paint);
	   c.drawText("Client", 635, 640, paint);
	   c.drawText("Description", 925, 640, paint);
	  
	   
	   invalidate();
	   
   }
   
   //delete an object on the screen
   public void deleteObject()
   {
	   
	     
		   if((settings.dOption == drawOption.curve)||(settings.dOption == drawOption.line))
		   {
			   //place the information of the deleted object in undo stack 
			   lineMap.remove(selectedLine.lineId);//delete
			   selectedLine.color = originalColor;
			   undo u = new undo();
			   u.line = selectedLine;
			   u.action = drawAction.delete;
			   u.object = objectType.line;
			   undoStack.push(u);
			   
		   }
		   else if((settings.dOption == drawOption.sprinkler))
		   {
			   sprinklerMap.remove(selectedsprinkler.ID);
			   selectedsprinkler.color = sprinklerColor;
			   undo u = new undo();
			   u.sInfo = selectedsprinkler;
			   u.action = drawAction.delete;
			   u.object = objectType.sprinkler;
			   undoStack.push(u);
		   }
		   
		   settings.dAction = drawAction.draw;
		   
		   invalidate(); //repaint the screen after deleting
	  
   }
   
   //show the length of a selected line
   public void displayLength(Line line, Canvas c)
   {
	   
	   float point1[] = {0f, 0f};
	  
	   //
	   PathMeasure PM = new PathMeasure(line.path, false);
	   PM.getPosTan((PM.getLength()/2), point1, null); // get the midpoint of the line
	   
	   //get the length of the line and scale it based on scale setting and screen width
	   int len = (int)(settings.scale*PM.getLength())/screenwidth; 
	   String length = Integer.toString(len);
	   length = length +"m";
	   
	   float x1 = point1[0] + 10;
	   float y1 = point1[1] + 10;
	  
	   Paint paint = new Paint();
	   paint.setStrokeWidth(1);
	   paint.setStrokeCap(Paint.Cap.ROUND);
	   paint.setStyle(Paint.Style.STROKE);
	   paint.setColor(Color.BLACK);
	   paint.setTextSize(20);
	   
	   c.drawText(length, x1, y1, paint);
	   
   }
	   
   
   // draw grid lines
	   
   public void drawGridLines(Canvas c)
   {
	   
	   Paint paint = new Paint();
	   paint.setStrokeWidth(1);
	   paint.setStrokeCap(Paint.Cap.ROUND);
	   paint.setStyle(Paint.Style.STROKE);
	   paint.setColor(Color.BLACK);
	   paint.setTextSize(25);
	   
	   int i = 20;
	   while(i< (screenheight- 10))
	   {
		   c.drawLine(10, (screenheight- i), (screenwidth  - 10), (screenheight -i), paint);
		   i += 40;
	   }
	   
	   int j = 20;
	   while(j< (screenwidth - 10))
	   {
		   c.drawLine((j), (20), (j), (screenheight -20), paint);
		   j += 40;
	   }
	  
	  
	   
	   invalidate();
	   
   }
   
   
   
   
} // end class DrawingView

	

