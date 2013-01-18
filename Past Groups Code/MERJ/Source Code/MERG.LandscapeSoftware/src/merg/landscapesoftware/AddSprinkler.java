// AddSprinkler.java - an activity that allows the user to add, edit and delete
// 						sprinklers from the database
// 			Reference: Deitel (2012) Android: How to Program.
//					** This file adopted code from the activity in chapter 11.	**
//					** In particular, it borrowed from the use of cursors and	**
//					** asyncronous tasks to access the database.				**
package merg.landscapesoftware;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSprinkler extends Activity 
{
   private long rowID;
   private String Name;
   private EditText modelEditText;
   private EditText flowEditText;
   private EditText priceEditText;
   private EditText colorEditText;
   private EditText arcEditText;
   private EditText radiusEditText;

   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.add_sprinkler);

      modelEditText = (EditText) findViewById(R.id.modelEditText);
      flowEditText = (EditText) findViewById(R.id.flowEditText);
      priceEditText = (EditText) findViewById(R.id.priceEditText);
      colorEditText = (EditText) findViewById(R.id.colorEditText);
      arcEditText = (EditText) findViewById(R.id.arcEditText);
      radiusEditText = (EditText) findViewById(R.id.radiusEditText);
      
      // get extras from sending activity
      Bundle extras = getIntent().getExtras();
      rowID = extras.getLong("row_id");
      Name = extras.getString("name");
     // Toast.makeText(getApplicationContext(), Name, Toast.LENGTH_SHORT).show();
      
      if (rowID != 0L) // if there is a row id, call method to populate EditTexts
    	  setEditTexts();
      
      // Set save button and its onClickListener
      Button saveSprinklerButton = (Button) findViewById(R.id.saveSprinkler);
      saveSprinklerButton.setOnClickListener(saveSprinklerButtonClicked);
      
      // Set delete button and its onClickListener
      Button deleteSprinklerButton = (Button) findViewById(R.id.deleteSprinkler);
      deleteSprinklerButton.setOnClickListener(deleteSprinklerButtonClicked);
   }

   // use AsyncTask to populate EditTexts with sprinkler's information
   public void setEditTexts()
   {
 	  AsyncTask<Long, Object, Cursor> getSprinklerTask = 
              new AsyncTask<Long, Object, Cursor>() 
      {
		 final DBAdapter dbAdapter = new DBAdapter(AddSprinkler.this);
		 final String TableName = Name;
		 // open database connection and return cursor with sprinkler information
         @Override
         protected Cursor doInBackground(Long... params) 
         {
        	dbAdapter.open();
            return dbAdapter.getASprinkler(params[0],TableName);
         }
         @Override
         protected void onPostExecute(Cursor result) 
         {
        	 // populates EditTexts with sprinkler's information by column id
            result.moveToFirst();
            modelEditText.setText(result.getString(result.getColumnIndex("model")));
            flowEditText.setText(result.getString(result.getColumnIndex("flow")));
            priceEditText.setText(result.getString(result.getColumnIndex("price")));
            colorEditText.setText(result.getString(result.getColumnIndex("color")));
            arcEditText.setText(result.getString(result.getColumnIndex("arc")));
            radiusEditText.setText(result.getString(result.getColumnIndex("radius")));
        	dbAdapter.close(); // close database connection
         }
      };
      getSprinklerTask.execute(rowID);	// executes task
   }
   
   OnClickListener saveSprinklerButtonClicked = new OnClickListener() 
   {
      public void onClick(View view) 
      {		// checks that all EditTexts are non-empty
         if (modelEditText.getText().length() != 0
        		 && flowEditText.getText().length() != 0
        		 && priceEditText.getText().length() != 0
        		 && colorEditText.getText().length() != 0
        		 && arcEditText.getText().length() != 0
        		 && radiusEditText.getText().length() != 0)
         {
            AsyncTask<Object, Object, Object> saveSprinklerTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                	 if (rowID == 0L)	// if sprinkler does not exit, insert in database
                		 insertSprinkler();
                	 else				// if sprinkler does  exit, update in database
                		 updateSprinkler();
                     return null;
                  }
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     finish();	// return to previous activity
                  }
               };
            saveSprinklerTask.execute(); // executes task
         }
         else	// displays alert dialog if any EditText is empty
         {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddSprinkler.this);
            builder.setTitle(R.string.errorTitle); 
            builder.setMessage(R.string.errorMessage);
            builder.setPositiveButton(R.string.errorOk, null); 
            builder.show();
         }
      }
   };
   
   OnClickListener deleteSprinklerButtonClicked = new OnClickListener() 
   {
      public void onClick(View view) 
      {		// displays alert dialog to confirm delete
          AlertDialog.Builder builder = new AlertDialog.Builder(AddSprinkler.this);
          builder.setTitle(R.string.confirm_delete); 
          builder.setMessage(R.string.confirm_message);
          	// positive button processes deletion in database
          builder.setPositiveButton(R.string.button_delete,
        		  new DialogInterface.OnClickListener()
          {
             public void onClick(DialogInterface dialog, int button)
             {	// task to delete sprinkler from database
            	 AsyncTask<Long, Object, Object> deleteSprinklerTask = 
            			 new AsyncTask<Long, Object, Object>()
            	 {
            		 final DBAdapter dbAdapter = new DBAdapter(AddSprinkler.this);
            		 final String TableName = Name;
            		 
            		 @Override
                     protected Object doInBackground(Long... params) 
                     {
            			 //dbAdapter.open();
	                   	 if (rowID != 0L)
	                   		 dbAdapter.deleteSprinkler(params[0],TableName);
	                     return null;
                     }
                     @Override
                     protected void onPostExecute(Object result) 
                     {
                    	 //dbAdapter.close();
                         finish();	// returns to previous activity
                     }
            	 };
            	 deleteSprinklerTask.execute(rowID);	// execute delete task
             }
          });
          // negative button dismisses dialog
          builder.setNegativeButton(R.string.button_cancel, null);
          builder.show();	// show dialog
      }
   };
   
   // inserts new sprinkler into database
   private void insertSprinkler()
   {
	   DBAdapter dbAdapter = new DBAdapter(this);
	   //dbAdapter.open();
	   dbAdapter.insertSprinkler(
    		Name,
    	    modelEditText.getText().toString(),
    	    flowEditText.getText().toString(),
    	    priceEditText.getText().toString(),
    	    colorEditText.getText().toString(), 
            arcEditText.getText().toString(),
            radiusEditText.getText().toString());
	   //dbAdapter.close();
   }
   
   // updates existing sprinkler into database
   private void updateSprinkler()
   {
	   DBAdapter dbAdapter = new DBAdapter(this);
	   //dbAdapter.open();
	   dbAdapter.updateSprinkler(
    		rowID,
		    Name,
    	    modelEditText.getText().toString(),
    	    flowEditText.getText().toString(),
    	    priceEditText.getText().toString(),
    	    colorEditText.getText().toString(), 
            arcEditText.getText().toString(),
            radiusEditText.getText().toString());
	   //dbAdapter.close();
   }
} 
