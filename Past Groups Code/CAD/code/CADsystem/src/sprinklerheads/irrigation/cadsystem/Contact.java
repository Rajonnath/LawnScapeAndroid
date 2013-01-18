/*FILENAME- Contact.java
 * DESCRIPTION - Asks for information of owner of plot  
 */
//package of the class
package sprinklerheads.irrigation.cadsystem;

//importing required packages
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

//Activity class
public class Contact extends Activity 
{
	//declaring required widgets
	Button Submit;
	private EditText lastName;
	private EditText firstName;
	private EditText email;	
	private EditText phoneNumber;
	private EditText city;
	private EditText state;
	private EditText street;
	private EditText zipCode;
	//temporary varibales
	private String LNV, FNV, EV, PNV, CV, STATEV, STV, ZCV;

    //overriding onCreate
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        
        // Submit Button
        Submit = (Button) findViewById(R.id.submitButton);
        Submit.setOnClickListener(submitButtonListener);
        
        //associate EditText to layout
        lastName = (EditText) findViewById(R.id.lastName);
    	firstName = (EditText) findViewById(R.id.firstName);
    	email = (EditText) findViewById(R.id.email);
    	phoneNumber = (EditText) findViewById(R.id.phoneNumber);
    	street = (EditText) findViewById(R.id.street);
    	city = (EditText) findViewById(R.id.city);
    	state = (EditText) findViewById(R.id.state);
    	zipCode = (EditText) findViewById(R.id.zipCode);
    }

    //on create options menu
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_contact, menu);
        return true;
    }
    
    private void update_db()
    {
    	//insertion into database needs to go on here.
    	//Need to decide what to do with blank answers.
    	//Just leave them blank?
    	//I do not know if blank is a null value.
   	
    }    
    public OnClickListener submitButtonListener = new OnClickListener()
    {	
    	/*
    	 * Overrides the on click event
    	 */
		public void onClick(View v) 
		{ 
			//converting edit text values  to Strings
			LNV=lastName.getText().toString();    	
	    	FNV=firstName.getText().toString();
	    	EV=email.getText().toString();
	    	PNV=phoneNumber.getText().toString();
	    	STV=street.getText().toString();
	    	CV=city.getText().toString();
	    	STATEV=state.getText().toString();
	    	ZCV=zipCode.getText().toString();
	    	
	    	//checking if no data is entered displays toast message
			if(LNV.equals("") || FNV.equals("") || PNV.equals("") || STV.equals(""))
			{
				String temp="Please enter:\n";
				if(LNV.equals(""))
				{
					temp=temp.concat("Last Name\n");
				}
				if(FNV.equals(""))
				{
					temp=temp.concat("First Name\n");
				}
				if(PNV.equals(""))
				{
					temp=temp.concat("Phone Number\n");
				}
				if(STV.equals(""))
				{
					temp=temp.concat("Street Address\n");
				}
	            Toast toast=Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_LONG);  
	            toast.show();
			}
			//else update values to database
			else
			{
				update_db();
				// Sets an intent to call the PlotDetails activity class
				Intent regular = new Intent(Contact.this, PlotDetails.class);
				startActivity(regular);
			}
		} // end the onClick() method
    }; // end the submitButtonListener 
}
