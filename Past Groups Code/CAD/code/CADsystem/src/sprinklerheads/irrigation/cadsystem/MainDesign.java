/*FILENAME: MainDesign.java
 * DESCRIPTION: This class is where all designs are drawn, consists menu of objects to draw on plot
 */
//package of class
package sprinklerheads.irrigation.cadsystem;
//importing required packages
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputType;

// OpenGL ES Imports
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

//MainDesign class extends Activity and implement Renderer classes
public class MainDesign extends Activity implements Renderer
{
	//GLSurface veiw to draw graphics   
	GLSurfaceView glview;
	GLGraphics glgraphics;
	//objects of Project_Screen and project classes 
	Project_Screen screen;
	Project project;
	
	//Adding menus
	private final int HOUSE = Menu.FIRST;
	private final int SIDEWALK = Menu.FIRST+1;
	private final int FLOWERBED=Menu.FIRST+2;
	private final int SAVE= Menu.FIRST+3;
	private final int WATERMAIN = Menu.FIRST+4;
	
	//tempoeray variables
	private Float sidewalkLength,sidewalkWidth,sidewalkDist;
	int i,j;
	float plen = -1.0f;
	float pwidth = -1.0f;

	//to save design as file
	String FILENAME;
	
	//oncreate method
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       //set flags to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        i=0;
      	j=0;
      	//calling constructors 
        glview = new GLSurfaceView(this);
        glview.setRenderer(this);
        glgraphics = new GLGraphics(glview);
        
        //bundle to get varibales from other classes
        Bundle extras = getIntent().getExtras();
        plen = extras.getFloat("plen");
        pwidth = extras.getFloat("pwidth");
        
        //getting values from plotdetails class
		float[] corners = new float[] {0.0f		, 0.0f,
                pwidth	, 0.0f,
                pwidth	, plen,
                0.0f		, plen				
               };
        //creating new project
		if (project == null) {
			Log.v("MainDesign", "Activity Created Project");
			project = new Project(glgraphics);
			project.setSite(corners, 4);
		}
		else
		{
		project=saved_project;
		}
        
        /***********************************************************************
    	 * adding touch to screen
    	 * 
    	 **********************************************************************/    
        
         glview.setOnTouchListener(screenTouch);
       
        setContentView(glview);
        getActionBar().setDisplayHomeAsUpEnabled(true);      
    }
	//to fix bug with orientation
		 @Override
    public Project onRetainNonConfigurationInstance() {
    	final Project p = project;

		return p;
    }
    //adding menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main_design, menu);
        //various objects to be placed on plot
        menu.add(Menu.NONE, HOUSE, Menu.NONE, "HOUSE");
        menu.add(Menu.NONE,SIDEWALK,Menu.NONE,"SIDEWALK");
        menu.add(Menu.NONE,FLOWERBED,Menu.NONE,"FLOWERBED");
        menu.add(Menu.NONE,SAVE, Menu.NONE,"SAVE");
        menu.add(Menu.NONE,WATERMAIN, Menu.NONE,"WATERMAIN");
        return true;
    }

    //on menu items selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	//each menu items calls a method respectively
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case HOUSE:
            	houseDialog();
            	break;
            case SIDEWALK:
            	sidewalkDialog();
            	break;
            case FLOWERBED:
            	flowerbedDialog();
            	break;
            case WATERMAIN:
            	watermain();
            	break;
            case SAVE:
          		savedesign();
    			break;
           
        }
        return super.onOptionsItemSelected(item);
    }
    //if house is selected
    public void houseDialog()
    { 	
    	//layout for alert dialog
    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation(LinearLayout.VERTICAL);     	
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	//title of alert dialog
    	alert.setTitle("House Measurements");
    	//adding text views and edit texts to get required details
        final TextView lengthText =new TextView(this);
        lengthText.setText("Enter House Length:");
        layout.addView(lengthText);
    	final EditText lengthValue = new EditText(this);
    	lengthValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(lengthValue);
    	final TextView widthText =new TextView(this);
        widthText.setText("Enter House Width:");
        layout.addView(widthText);
    	final EditText widthValue = new EditText(this);
    	widthValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(widthValue);
    	final TextView distLText =new TextView(this);
    	distLText.setText("Enter distance from left edge of plot");
        layout.addView(distLText);
    	final EditText distLeft = new EditText(this);
    	distLeft.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distLeft);
    	final TextView distBText =new TextView(this);
    	distBText.setText("Enter distance from bottom edge of plot");
        layout.addView(distBText);
    	final EditText distBottom = new EditText(this);
    	distBottom.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distBottom);
    	
    	//and converting to them to float on click ok 
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
            	float length, width, x, y;
            	float[] corners;
            	//convert to float and save to database
            	length = Float.parseFloat(lengthValue.getText().toString().trim());
            	width = Float.parseFloat(widthValue.getText().toString().trim());
            	x = Float.parseFloat(distLeft.getText().toString().trim());
            	y = Float.parseFloat(distBottom.getText().toString().trim());
            	
            	corners = new float[] 	{	x			, y 		,
            								x + width	, y			,
            								x + width	, y + length,
            								x			, y + length
            							};
            	
            	//aad biulding method of project class
            	project.addBuilding("House", corners, 4);
            	
            }
        });
        //cancel button
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                dialog.cancel();
            }
        });
        //  
        alert.setView(layout);
        alert.show(); //show alert
    }
    //Alert dialog to ask details of the sidewalk
    public void sidewalkDialog()
    {
    	
    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation(LinearLayout.VERTICAL);     	
    	           	
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Sidewalk Measurements");
        final TextView lengthText =new TextView(this);
        lengthText.setText("Enter Length of side walk:");
        layout.addView(lengthText);
    	final EditText lengthvalue = new EditText(this);
    	lengthvalue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(lengthvalue);
    	final TextView widthText =new TextView(this);
        widthText.setText("Enter Width of side walk:");
        layout.addView(widthText);
    	final EditText widthvalue = new EditText(this);
    	layout.addView(widthvalue);
    	widthvalue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	final TextView distText =new TextView(this);
        distText.setText("Enter distance from house");
        layout.addView(distText);
    	final EditText distValue = new EditText(this);
    	layout.addView(distValue);
    	distValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	final TextView distLText =new TextView(this);
    	distLText.setText("Enter distance from left edge of plot");
        layout.addView(distLText);
    	final EditText distLeft = new EditText(this);
    	distLeft.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distLeft);
    	final TextView distBText =new TextView(this);
    	distBText.setText("Enter distance from bottom edge of plot");
        layout.addView(distBText);
    	final EditText distBottom = new EditText(this);
    	distBottom.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distBottom);
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
            	float length, width, x, y;
            	float[] corners;
            	
            	length = Float.parseFloat(lengthvalue.getText().toString().trim());
                width = Float.parseFloat(widthvalue.getText().toString().trim());
                x = Float.parseFloat(distLeft.getText().toString().trim());
                y = Float.parseFloat(distBottom.getText().toString().trim());
                
                corners = new float[] 	{	x			, y 		,
											x + width	, y			,
											x + width	, y + length,
											x			, y + length
										};
               project.addSlab(corners, 4);
               
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                dialog.cancel();
            }
        });
        //  
        alert.setView(layout);
        alert.show(); 
        }
    //Alert dialog to ask details of the flowebed	
    public void flowerbedDialog()
    {
    	
    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation(LinearLayout.VERTICAL);     	
    	           	
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Flowerbed Measurements");
        final TextView lengthText =new TextView(this);
        lengthText.setText("Enter Length of flowerbed:");
        layout.addView(lengthText);
    	final EditText lengthvalue = new EditText(this);
    	lengthvalue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(lengthvalue);
    	final TextView widthText =new TextView(this);
        widthText.setText("Enter Width of flowerbed:");
        layout.addView(widthText);
    	final EditText widthvalue = new EditText(this);
    	widthvalue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(widthvalue);
    	final TextView distText =new TextView(this);
        distText.setText("Enter distance from house");
        layout.addView(distText);
    	final EditText distValue = new EditText(this);
    	distText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distValue);
    	
    	final TextView waterText =new TextView(this);
        waterText.setText("Enter water requirement for flowerbed");
        layout.addView(waterText);
    	final EditText waterValue = new EditText(this);
    	waterValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(waterValue);
    	
    	final TextView distLText =new TextView(this);
        distLText.setText("Enter distance from left edge of plot");
        layout.addView(distLText);
    	final EditText distLeft = new EditText(this);
    	distLeft.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distLeft);
    	
    	final TextView distBText =new TextView(this);
        distBText.setText("Enter distance from Bottom edge of plot");
        layout.addView(distBText);
    	final EditText distBottom = new EditText(this);
    	distBottom.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	layout.addView(distBottom);
    	
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {            	
            	float length, width, x, y;
            	float[] corners;
            	
            	length = Float.parseFloat(lengthvalue.getText().toString().trim());
                width = Float.parseFloat(widthvalue.getText().toString().trim());
                x = Float.parseFloat(distLeft.getText().toString().trim());
                y = Float.parseFloat(distBottom.getText().toString().trim());
                
                corners = new float[] 	{	x			, y 		,
											x + width	, y			,
											x + width	, y + length,
											x			, y + length
										};
               project.addFlowerBed(corners, 4);
               
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                dialog.cancel();
            }
        });
        //  
        alert.setView(layout);
        alert.show(); 
        }
	 
    //Alert dialog to save design as a bitmap
    	public void savedesign() 
    	{
    		//alert dialog asks for file name
    		LinearLayout layout = new LinearLayout(this);
        	layout.setOrientation(LinearLayout.VERTICAL);
    		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    		alert.setTitle("Save Design");
    		final TextView design_name =new TextView(this);
            design_name.setText("Enter name of design:");
            layout.addView(design_name);
        	final EditText design_value = new EditText(this);
        	design_value.setInputType(InputType.TYPE_CLASS_TEXT);
        	layout.addView(design_value);
        	//ok button
        	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {	//on click ok 
                 public void onClick(DialogInterface dialog, int whichButton)
                 {
                	 //the design will be stored to internal storage as a bitmap.
                	 FILENAME=design_value.getText().toString(); 
                	 final Bitmap finaldesign = Bitmap.createBitmap(1000, 500, Bitmap.Config.ARGB_8888);
                	
                	 FileOutputStream fOut = null;
					try {
						fOut = openFileOutput(FILENAME,MODE_PRIVATE);
						 finaldesign.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 	 try {
						fOut.write("testing".getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	 try {
						fOut.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                  	dialog.cancel();
               	
                 }
             });
        	              
             alert.setView(layout);
             alert.show();//display alert dialog
    	}
    	 //Alert dialog to ask details of the watermain
    	public void watermain()
    	{
    		LinearLayout layout = new LinearLayout(this);
        	layout.setOrientation(LinearLayout.VERTICAL);     	
        	           	
        	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setTitle("Water main details");
        	
        	final TextView distLText =new TextView(this);
            distLText.setText("Enter distance from left edge of plot");
            layout.addView(distLText);
        	final EditText distLeft = new EditText(this);
        	distLeft.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        	layout.addView(distLeft);
        	
        	final TextView distBText =new TextView(this);
            distBText.setText("Enter distance from Bottom Edge");
            layout.addView(distBText);
        	final EditText distBottom = new EditText(this);
        	distBottom.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        	layout.addView(distBottom);
        	
            final TextView loopSize =new TextView(this);
            loopSize.setText("Enter size of mainloop");
            layout.addView(loopSize);
        	final EditText loopValue = new EditText(this);
        	loopValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        	layout.addView(loopValue);
        	final TextView mainPSI =new TextView(this);
            mainPSI.setText("Enter water main psi");
            layout.addView(mainPSI);
        	final EditText psiValue = new EditText(this);
        	psiValue.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        	layout.addView(psiValue);
      
        	
        	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                  //convert to float and save to database
                	
                   
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int whichButton) 
                {
                    dialog.cancel();
                }
            });
            //  
            alert.setView(layout);
            alert.show(); 
    	
    	}
    	
    	/***********************************************************************
    	 * onTouch points
    	 * 
    	 **********************************************************************/
    	 public OnTouchListener screenTouch = new OnTouchListener()
    	    {
    	    	
    	    	public boolean onTouch(View v,MotionEvent e)
    	    	{
    	    	
    	    		int action=e.getAction();
    	    		//float x,y;
    	    		float []x= new float [20];
    	    		float []y = new float [20];
    	    		if(action==e.ACTION_DOWN )
    	    		{
    	    			 //store the X value when the user's finger was pressed down
    	            //x=e.getX();
    	            //y=e.getY();
    	    			  x[i++]=e.getX();
    	               y[j++]=e.getY();
    	              
    	               
    	              
    	               }
    	    		
    	    		return true;
    	    		
    	    	}
    	    };


    	/***********************************************************************
    	 * GLSurfaceView Renderer Methods
    	 **********************************************************************/
		public void onDrawFrame(GL10 gl) {
			screen.draw(project);
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {	
			Log.v("MainDesign", "Property : " + plen + ", " + pwidth);
			
			// Each Point : X, Y, Z, Alpha
			// The Z coordinate is set to zero so that it lies in the frustrum between
			// -1.0f and 1.0f.
		
			glgraphics.setGL(gl);
			
			project.setGLGraphics(glgraphics);
			
	        screen = new Project_Screen(glgraphics, pwidth + 10.0f, plen + 10.0f);
			screen.centerCamera(pwidth/2, plen/2);
			screen.updateCamera();
			
			Log.v("MainDesign", "onSurfaceCreated Done");
		}
}