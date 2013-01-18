/*FILENAME: PlotDetails.java
 * DESCRIPTION: Ask for plot details, saves to database and proceed to main design
 */
//package of the class
package sprinklerheads.irrigation.cadsystem;
//importing required packages
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
//Activity class
public class PlotDetails extends Activity
{
    //declaring required widgets
	public EditText plotLength;
    public EditText plotWidth;
    private Button ok;
    //temporary variables
    private String width;
    private String Hieght;
    public Float plotlengthvalue=0.0f;
    public Float plotwidthvalue=0.0f;
    
    //overriding default methods
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        //calling onCreate of super class
		super.onCreate(savedInstanceState);
		//linking to relative xml
        setContentView(R.layout.activity_plot_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //adding onclicklistener for ok button
        ok = (Button) findViewById(R.id.okButton);
        ok.setOnClickListener(okButtonListener);
    }
	//on crete options  menu
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_plot_details, menu);
        return true;
    }

    //on menu items selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //on click listener for ok button
    public OnClickListener okButtonListener = new OnClickListener()
    {	
    	//onClick ok 
    	public void onClick(View v) 
		{    	    		
    		//go to mainDesign class
    		Intent regular = new Intent(PlotDetails.this, MainDesign.class);
            //save the value entered in edit text to string
    		plotLength= (EditText)findViewById(R.id.lengthEditText);
            plotWidth= (EditText)findViewById(R.id.widthEditText);
            width=plotWidth.getText().toString();
            Hieght=plotLength.getText().toString();
            
            //checking if values are entered or not
            if(width.equals("") || Hieght.equals(""))
            {
            	String temp="Please enter:\n";
            	if(width.equals(""))
            	{
            		temp=temp.concat("Width\n");
            	}
            	if(Hieght.equals(""))
            	{
            		temp=temp.concat("Length\n");
            	}
	            Toast toast=Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_LONG);  
	            toast.show();
            }
            //else convert those string to float vlaues
            else
            {
                plotlengthvalue=Float.parseFloat(plotLength.getText().toString());
                plotwidthvalue=Float.parseFloat(plotWidth.getText().toString());
	            Log.v("PlotDetails", "Length, Width : " + plotlengthvalue + ", " + plotwidthvalue);
	            
	            //to pass values to another class
	    		regular.putExtra("plen", plotlengthvalue);
	    		regular.putExtra("pwidth", plotwidthvalue);
				//start maindesign activity
	    		startActivity(regular);
            }
		
		}// end the onClick() method
    }; // end the regularButtonListener 
}
