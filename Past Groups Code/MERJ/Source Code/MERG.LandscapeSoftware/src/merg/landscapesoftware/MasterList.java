// MasterList.java - a list of all sprinkler types in database
package merg.landscapesoftware;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MasterList extends ListActivity {

	public static final String NAMES = "master_name";
	public String Selection;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // string array with preloaded table names
        String[] SprinklerNames = new String[] { "ROTATOR", "ADJUSTABLE", "FIXED", 
        	"SPECIALTY", "BUBBLER"};

        // loads the list with target layout and populates TextViews with table names
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.sprinkler_item, 
        		R.id.sprinklerTextView, SprinklerNames));
    }

    // calls SprinklerList activity when a table name in the list is clicked, and 
    // sends the name of the table as an extra
    @Override
    protected void onListItemClick(ListView first, View v, int position, long id)
    {
    	String item = (String) getListAdapter().getItem(position);
    	Selection = item;	// get table name
    	//Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
    	Intent sprinklerList = new Intent(MasterList.this, SprinklerList.class);
    	sprinklerList.putExtra(NAMES, Selection); 	// sets extra
    	startActivity(sprinklerList);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.master_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	return super.onOptionsItemSelected(item);
    }
}
