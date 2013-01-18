/*FILENAME- ExistingActivity.java
 * DESCRIPTION -This class is used when one file is selected to load form lit of files.
 */
//package of class
package sprinklerheads.irrigation.cadsystem;

//importing required packages
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;

//ExistingActivity class extends Activity
public class ExistingActivity extends Activity 
{
	//Creatin an image view 
	public ImageView v;
    //on create method
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //reads the file based on filename and loads it as an bitmap image
        String fn="";
        v=(ImageView)findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        fn=extras.getString("fn");
        Bitmap bitmap = null;
        FileInputStream fis;
      try 
      {
    	  //decoding the fiel to bitmap
    	  fis=openFileInput(fn);
     	bitmap=BitmapFactory.decodeStream(fis);
     	fis.close();
		}
      catch (FileNotFoundException e)
      {
     // TODO Auto-generated catch block
     e.printStackTrace();
     } catch (IOException e)
     {
	
   	 // TODO Auto-generated catch block
	e.printStackTrace();
     } 
      //settin bimap to image view
      v.setImageBitmap(bitmap);   
     	  
        }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_existing, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
