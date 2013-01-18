// SprinklerList.java - a list of all sprinklers of a given type in database.
// 			Reference: Deitel (2012) Android: How to Program.
//					** This file adopted code from the activity in chapter 11.	**
//					** In particular, it borrowed from the use of cursors and	**
//					** asyncronous tasks to access the database.				**
package merg.landscapesoftware;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.app.Activity;

public class SprinklerList extends ListActivity 
{

   public String test;
   public static final String ROW_ID = "row_id";
   private ListView sprinklerListView;
   private CursorAdapter sprinklerAdapter; 
   
   public String nameVal;
   public String priceVal;
   public String colorVal;
   public String radiusVal;
   
   // constants for table column names
   public static final String NAME = "name";
   public static final String FLOW = "flow";
   public static final String COLOR = "color";
   public static final String PRICE = "price";
   public static final String ARC = "arc";
   public static final String RADIUS = "radius";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();	// get extras from calling activity
        test = extras.getString(MasterList.NAMES);	// set table name
        
        LayoutInflater inflater = 
        		(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sprinklerListView = getListView(); 
        View view = inflater.inflate(R.layout.header_view, 
      		  sprinklerListView, false);
        sprinklerListView.addHeaderView(view);	// places a header at top of list
        // set click listeners for each item in list
        sprinklerListView.setOnItemClickListener(viewSprinklerListener);
        // load string array with all column names to be displayed
        String[] from = new String[] { "model", "flow", "color","radius", "arc","price" };
        // load int array with id's of text boxes to correspond with table names
        int[] to = new int[] { R.id.list_model, R.id.list_flow, R.id.list_color,
        		R.id.list_radius, R.id.list_arc, R.id.list_price};
        // create simple cursor adapter to display sprinklers retrieved from database
        sprinklerAdapter = new SimpleCursorAdapter(
           SprinklerList.this, R.layout.whole_view, null, from, to);
        
        setListAdapter(sprinklerAdapter);
        //Toast.makeText(getApplicationContext(), test, Toast.LENGTH_SHORT).show();
     } 

    // onResume gets a new GetSprinklersTask each time the activity is called
     @Override
     protected void onResume() 
     {
        super.onResume(); 
         new GetSprinklersTask().execute((Object[]) null);
      } 

     @Override
     protected void onStop() 
     {
        Cursor cursor = sprinklerAdapter.getCursor();
        if (cursor != null) // if cursor is not null, close cursor
      	  cursor.close();
        sprinklerAdapter.changeCursor(null); 
        super.onStop();
     } 

     // returns a cursor with all sprinklers in selected table
     private class GetSprinklersTask extends AsyncTask<Object, Object, Cursor> 
     {
        DBAdapter dbAdapter = new DBAdapter(SprinklerList.this);

        @Override
        protected Cursor doInBackground(Object... params)
        {
      	  dbAdapter.open();	// open database connection
      	  return dbAdapter.getSprinklers(test);
        } 
        @Override
        protected void onPostExecute(Cursor result)
        {
           sprinklerAdapter.changeCursor(result); 
           dbAdapter.close();	// close database connection
        } 
     } 
     
     @Override
     public boolean onCreateOptionsMenu(Menu menu) 
     {
        getMenuInflater().inflate(R.menu.sprinkler_list, menu);
        return true;
     } 
     
     // starts the AddSprinkler activity when menu item is clicked
     @Override
     public boolean onOptionsItemSelected(MenuItem item) 
     {
        Intent addSprinkler = new Intent(SprinklerList.this, AddSprinkler.class);
        addSprinkler.putExtra(NAME, test);	// add table name to extras
        startActivity(addSprinkler);	// start activity
        return super.onOptionsItemSelected(item);
     }

     // starts the AddSprinkler activity when list item is clicked to edit/ delete
     // an existing sprinkler
     OnItemClickListener viewSprinklerListener = new OnItemClickListener() 
     {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
        {
      	  Intent addSprinkler = new Intent(SprinklerList.this, AddSprinkler.class);
      	  addSprinkler.putExtra(ROW_ID, arg3); // add sprinkler id to extras
      	  addSprinkler.putExtra(NAME, test);	// add table name to extras
      	  startActivity(addSprinkler); // start activity
        }
     }; 
  }


