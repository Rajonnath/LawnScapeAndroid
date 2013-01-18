/*FILENAME: homeScreen.java
 * DESCRIPTION:creates home screen by showing an image, tapping screen proceeds to Main Activity screen.
 */
//package for application development
package sprinklerheads.irrigation.cadsystem;

//required packages
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

//homeScreen class extending activity
public class homeScreen extends Activity
{
	//overriding onCreate and onCreteOptionsMenu
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
       //call to oncreate of super class
    	super.onCreate(savedInstanceState);
    	//linking with respective xml
        setContentView(R.layout.activity_home_screen);
        //accessing view
        View view = (View)findViewById(R.id.myView);
        //adding touchListener to view
        view.setOnTouchListener(screenTouch);
    }

    //no menu is added here
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
    //implementing onTouch for touch listener
    public OnTouchListener screenTouch = new OnTouchListener()
    {
    	//onTouch method
    	public boolean onTouch(View v,MotionEvent e)
    	{
    	
    		//if screen is touched go to next screen
    		int action=e.getAction();
    		if(action==e.ACTION_DOWN || action==e.ACTION_MOVE)
    		{
    			//starting MainActivity class
    			Intent regular = new Intent(homeScreen.this, MainActivity.class);
    			startActivity(regular);
    		}
    		return true;
    	}//end onTouch
    };//end onTouchListener
}//end of activity class