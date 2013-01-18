/*FILENAME: MainActivity.java
 * DESCRIPTION: Displays new and exiting buttons, moves to different 
 * activity based on selection
 */
//package of the class
package sprinklerheads.irrigation.cadsystem;

//importing all required packages
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//activity class
public class MainActivity extends Activity
{
	//declaring required widgets
	Button newButton;
	Button existingButton;
	
	//overriding onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        //calling oncreate of super class 
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Button for New Project and onClickListener
        newButton = (Button) findViewById(R.id.NewProject);
        newButton.setOnClickListener(newProjectListener);
        
        // Button for the Existing Project and onClickListener
        existingButton = (Button) findViewById(R.id.ExistingProject);
        existingButton.setOnClickListener(existingProjectListener);
    }
    
    public OnClickListener newProjectListener = new OnClickListener()
    {
    	
    	/*
    	 * Overrides the on click event
    	 */
		
		public void onClick(View v) 
		{
			// Sets an intent to call the regular kaleidoscope activity class
			Intent regular = new Intent(MainActivity.this, Contact.class);
			startActivity(regular);
		} // end the onClick() method
    }; // end the regularButtonListener 
    
    public OnClickListener existingProjectListener = new OnClickListener()
    {
    	
    	/*
    	 * Overrides the on click event
    	 */
		
		public void onClick(View v) 
		{
			//shows list of existing designs
			existing();
		} // end the onClick() method
    }; // end the regularButtonListener 

   
    //no menu added
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    //to display list of saved files
    public void existing()
    {
    	//alert dialog
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	//get the list of files saved in internal storage
    	final String [] fileList=fileList();
    	//set files to dialog
 	   alert.setItems(fileList, new DialogInterface.OnClickListener()
 	   {
           //on selecting file open it in a new xml
 		   public void onClick(DialogInterface dialog, int item)
            {        	   
					Intent regular = new Intent(MainActivity.this, ExistingActivity.class);
					regular.putExtra("fn",fileList[item]);	
					startActivity(regular);
            }//end of onClick
        }); //end of setItems
 	   //adding cancel button
 	   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                dialog.cancel();
            }
        });
 	   alert.show();//display alert
    }//end of alert
}//end of class
