package merg.landscapesoftware;

// MaiActivity.java
//This is the main activity class of the app.
//It is used to launch other activities and views as well
// as to coordinates events

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import merg.landscapesoftware.Settings.drawAction;
import merg.landscapesoftware.Settings.drawOption;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	
	
	public DrawingView drawingView; // drawing View
	
	String[] filelist; // filelist of saved files;
	FileService loadfile ; //instance of FileService class, use in loading saved object
	GridView gridView;
	List<Sprinkler> SprinklerList ;

	
	private Dialog currentDialog; //used to launch custom dialog
	String filename = "";
	Menu mainMenu; //main menu

	// called when this Activity is loaded
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen); // inflate the layout

		// get reference to the DrawingView
		drawingView = (DrawingView) findViewById(R.id.drawView);

		mainMenu = (Menu) findViewById(R.menu.menus);
		
	} // end method onCreate

	// when app is sent to the background, stop listening for sensor events
	@Override
	protected void onPause() {
		super.onPause();
		
	} // end method onPause

	
	// displays configuration options in menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu); // call super's method

		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menus, menu);
	    mainMenu = menu;
	    
	    mainMenu.findItem(R.id.settings).setVisible(false);
	    mainMenu.findItem(R.id.line).setVisible(false);
	    mainMenu.findItem(R.id.curve).setVisible(false);
	    mainMenu.findItem(R.id.select).setVisible(false);
	    mainMenu.findItem(R.id.undo).setVisible(false);
	    mainMenu.findItem(R.id.redo).setVisible(false);
	    mainMenu.findItem(R.id.move).setVisible(false);
	    mainMenu.findItem(R.id.rotate).setVisible(false);
	    mainMenu.findItem(R.id.delete).setVisible(false);
	    mainMenu.findItem(R.id.clear).setVisible(false);
	    mainMenu.findItem(R.id.sprinkler).setVisible(false);
	    mainMenu.findItem(R.id.symbols).setVisible(false);
	    mainMenu.findItem(R.id.pageDescription).setVisible(false);
	    mainMenu.findItem(R.id.save).setVisible(false);
	    mainMenu.findItem(R.id.saveAs).setVisible(false);
	    mainMenu.findItem(R.id.lengthDsp).setVisible(false);
	    mainMenu.findItem(R.id.saveAsImage).setVisible(false);
	    

		return true; // options menu creation was handled
	} // end onCreateOptionsMenu

	// handle choice from options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		currentDialog = new Dialog(this);
		currentDialog.setCancelable(true);
		
		// switch based on the MenuItem id
		switch (item.getItemId()) {
		case R.id.color:
			colorOption(); //set color option
			return true; // consume the menu event
		case R.id.lineWidth:
			linewidthOption(); //set line option
			return true; // consume the menu event
			
		case R.id.curveDegree:
			curveDegreeOption(); //set curve degree
			return true; // consume the menu event
			
		
		case R.id.clear:
			drawingView.clear(); // clear screen
			return true; // consume the menu event
			
		case R.id.gridLine:
			//display grid lines
			if(drawingView.settings.showGridLines == true)
			{
				drawingView.settings.showGridLines = false;
			}
			else
			{
				drawingView.settings.showGridLines = true;
			}
			
			return true; // consume the menu event
			
		case R.id.lengthDsp:
			lenDspOption(); //display length of a line
			return true; // consume the menu event
		
		case R.id.select:
			
			selectOption(); //selects an object
			return true; // consume the menu event
			
		case R.id.move:
			moveOption(); // move an object
			return true; // consume the menu event
			
		case R.id.rotate:
			
			rotateOption(); // rotate an object
			return true; // consume the menu event
			
		case R.id.delete:
			
			deleteOption(); // delete an object
			return true; // consume the menu event
			
		case R.id.curve:
			drawingView.isCurve = true; // set the setting to draw curves
			drawingView.settings.dOption = drawOption.curve;
			
			return true; // consume the menu event
		case R.id.line:
			drawingView.isCurve = false; // set the setting to draw line
			drawingView.settings.dOption = drawOption.line;// 
			
			return true;
		case R.id.undo:
			drawingView.undo(); // call undo method
			
			return true;
		case R.id.snapLines:
			drawingView.settings.setSnapLines(); // set snap to the nearest line option
			
			return true;
		case R.id.scale:
			drawingView.settings.setScale(); // set the drawing scale
			
			return true;
			
		case R.id.unit: //set nit
			// create the dialog and inflate its content			
			currentDialog.setContentView(R.layout.unit_dialog);
			currentDialog.setTitle("Unit Dialog");
			drawingView.settings.showUnitDialog(currentDialog); 
			
			return true;
			
		case R.id.redo:
			drawingView.redo(); // call the redo method
			return true;
		case R.id.draw:
			drawOption(); // select drawing activity
			    
			return true; // consume the menu event
		case R.id.save:
			
			saveOption(); // call save method to save drawings
			
			return true;
		case R.id.saveAs:
			
			saveAs(); // save with new name
			
			return true;
			
		case R.id.saveAsImage:
			
			drawingView.saveImage(); // save drawing as jpeg image
			
			return true;
			
		case R.id.open:
			
			openOption(); // call open method to open a saved drawing
			
			
			return true;
		case R.id.lineStyle:
			drawingView.settings.setLineStyle(); // change theline style
			
			return true; // consume the menu event 
		case R.id.dbm:
			dbmOption(); //call dbm method
			return true; // consume the menu event 
			
		case R.id.sprinkler:
			 sprinklerOption(); // change the setting to draw sprinkler
			 return true; // consume the menu event 
			 
			 
		case R.id.pageDescription:
			
			pageDescOption(); //dislay the page description
			return true; // consume the menu event 
			
		case R.id.menu_database:
			
			//launch database maintenance activity
			Intent masterList = new Intent(this, MasterList.class);
    		startActivity(masterList);
			
			
			return true; // consume the menu event 
			
		} // end switch

		return super.onOptionsItemSelected(item); // call super's method
	} // end method onOptionsItemSelected
	
	//Method to set color option
	public void colorOption()
	{
		// create the dialog and inflate its content			
					currentDialog.setContentView(R.layout.color_dialog);
					currentDialog.setTitle(R.string.title_color_dialog);	
					drawingView.settings.showColorDialog(currentDialog); // display color selection dialog
	}
	
	void linewidthOption() // launch custom dialog to set line width option
	{
		currentDialog.setContentView(R.layout.width_dialog);
		currentDialog.setTitle(R.string.title_line_width_dialog);
		drawingView.settings.showLineWidthDialog(currentDialog); // display line thickness dialog
	}
	
	void curveDegreeOption()
	{
		//launch custom dialog to set curve dgree option
		currentDialog.setContentView(R.layout.curvature_degree);
		currentDialog.setTitle("Set curvature degree");
		drawingView.settings.showLineCurvatureDialog(currentDialog); // display line thickness dialog
	}
	
	void lenDspOption()
	{
	
		//display drawing description box
		drawingView.settings.dAction = drawAction.draw;
		drawingView.selectedLine.color = drawingView.originalColor;
		drawingView.selectedLine.showLen = true; // clear drawingView
		drawingView.invalidate();
		 mainMenu.findItem(R.id.delete).setVisible(false);
		 mainMenu.findItem(R.id.move).setVisible(false);
		 mainMenu.findItem(R.id.rotate).setVisible(false);
		 mainMenu.findItem(R.id.select).setVisible(true);
		 mainMenu.findItem(R.id.line).setVisible(true);
		 mainMenu.findItem(R.id.curve).setVisible(true);
		 mainMenu.findItem(R.id.lengthDsp).setVisible(false);
		 mainMenu.findItem(R.id.sprinkler).setVisible(true);
		 mainMenu.findItem(R.id.symbols).setVisible(true);
		 mainMenu.findItem(R.id.undo).setVisible(true);
		 mainMenu.findItem(R.id.clear).setVisible(true);
		 mainMenu.findItem(R.id.redo).setVisible(true);	 
		 	 
	}
	
	void selectOption() // launches method that selects an object and disable and enable appropriate buttons
	{
		
		if(drawingView.settings.dAction == drawAction.select)
		{
			drawingView.settings.dAction = drawAction.draw;
			
			 mainMenu.findItem(R.id.delete).setVisible(false);
			 mainMenu.findItem(R.id.move).setVisible(false);
			 mainMenu.findItem(R.id.rotate).setVisible(false);
			// mainMenu.findItem(R.id.select).setVisible(true);
			 mainMenu.findItem(R.id.line).setVisible(true);
			 mainMenu.findItem(R.id.curve).setVisible(true);
			 mainMenu.findItem(R.id.lengthDsp).setVisible(false);
			 mainMenu.findItem(R.id.sprinkler).setVisible(true);
			 mainMenu.findItem(R.id.symbols).setVisible(true);
			 mainMenu.findItem(R.id.undo).setVisible(true);
			 mainMenu.findItem(R.id.clear).setVisible(true);
			 mainMenu.findItem(R.id.redo).setVisible(true);
			 
			 if (drawingView.selectedLine != null)
			 {
				 drawingView.selectedLine.color = drawingView.originalColor;
				 drawingView.invalidate();
			 }
		}
		
		else
		{
			
			 drawingView.settings.dAction = drawAction.select;
			 
			 mainMenu.findItem(R.id.line).setVisible(false);
			 mainMenu.findItem(R.id.curve).setVisible(false);
			 //mainMenu.findItem(R.id.select).setVisible(false);
			 mainMenu.findItem(R.id.move).setVisible(true);
			 mainMenu.findItem(R.id.rotate).setVisible(true);
			 mainMenu.findItem(R.id.delete).setVisible(true);
			 mainMenu.findItem(R.id.lengthDsp).setVisible(true);
			 mainMenu.findItem(R.id.sprinkler).setVisible(false);
			 mainMenu.findItem(R.id.symbols).setVisible(false);
			 mainMenu.findItem(R.id.undo).setVisible(false);
			 mainMenu.findItem(R.id.clear).setVisible(false);
			 mainMenu.findItem(R.id.redo).setVisible(false);
			    
		}
	}
	
	void moveOption() //launches method that moves object round screen and disable and enable appropriate buttons
	{
		drawingView.settings.dAction = drawAction.move;// 
		
		 mainMenu.findItem(R.id.delete).setVisible(false);
		 mainMenu.findItem(R.id.move).setVisible(false);
		 mainMenu.findItem(R.id.rotate).setVisible(false);
		 mainMenu.findItem(R.id.select).setVisible(true);
		 mainMenu.findItem(R.id.line).setVisible(true);
		 mainMenu.findItem(R.id.curve).setVisible(true);
		 mainMenu.findItem(R.id.lengthDsp).setVisible(false);
		 mainMenu.findItem(R.id.sprinkler).setVisible(true);
		 mainMenu.findItem(R.id.symbols).setVisible(true);
		 mainMenu.findItem(R.id.undo).setVisible(true);
		 mainMenu.findItem(R.id.clear).setVisible(true);
		 mainMenu.findItem(R.id.redo).setVisible(true);
	}
	
	
	void rotateOption() // calls the method that rotates object and disable and enable appropriate buttons
	{
		drawingView.settings.dAction = drawAction.rotate;//
		 if(drawingView.settings.dOption == drawOption.sprinkler)
		   {
			 drawingView.rotateSprinkler();
			   
		   }
		
		 mainMenu.findItem(R.id.line).setVisible(true);
		 mainMenu.findItem(R.id.curve).setVisible(true);
		 mainMenu.findItem(R.id.delete).setVisible(false);
		 mainMenu.findItem(R.id.move).setVisible(false);
		 mainMenu.findItem(R.id.rotate).setVisible(false);
		 mainMenu.findItem(R.id.select).setVisible(true);
		 mainMenu.findItem(R.id.lengthDsp).setVisible(false);
		 mainMenu.findItem(R.id.sprinkler).setVisible(true);
		 mainMenu.findItem(R.id.symbols).setVisible(true);
		 mainMenu.findItem(R.id.undo).setVisible(true);
		 mainMenu.findItem(R.id.clear).setVisible(true);
		 mainMenu.findItem(R.id.redo).setVisible(true);
	}
	
	void deleteOption() //calls method that delete object and disable and enable appropriate buttons
	{
		drawingView.settings.dAction = drawAction.delete;// 
		drawingView.deleteObject();
		
		 mainMenu.findItem(R.id.line).setVisible(true);
		 mainMenu.findItem(R.id.curve).setVisible(true);
		 mainMenu.findItem(R.id.delete).setVisible(false);
		 mainMenu.findItem(R.id.move).setVisible(false);
		 mainMenu.findItem(R.id.rotate).setVisible(false);
		 mainMenu.findItem(R.id.select).setVisible(true);
		 mainMenu.findItem(R.id.lengthDsp).setVisible(false);
		 mainMenu.findItem(R.id.sprinkler).setVisible(true);
		 mainMenu.findItem(R.id.symbols).setVisible(true);
		 mainMenu.findItem(R.id.undo).setVisible(true);
		 mainMenu.findItem(R.id.clear).setVisible(true);
		 mainMenu.findItem(R.id.redo).setVisible(true);
	}
	
	
	void drawOption() //launches the main view where user can draw on canvas 
	{
		

		setContentView(R.layout.main_drawing);
		drawingView = (DrawingView) findViewById(R.id.drawView);
		 mainMenu.findItem(R.id.line).setVisible(true);
		    mainMenu.findItem(R.id.curve).setVisible(true);
		    mainMenu.findItem(R.id.select).setVisible(true);
		    mainMenu.findItem(R.id.undo).setVisible(true);
		    mainMenu.findItem(R.id.redo).setVisible(true);
		    mainMenu.findItem(R.id.clear).setVisible(true);
		    mainMenu.findItem(R.id.settings).setVisible(true);
		    mainMenu.findItem(R.id.sprinkler).setVisible(true);
		    mainMenu.findItem(R.id.symbols).setVisible(true);
		    mainMenu.findItem(R.id.pageDescription).setVisible(true);
		    mainMenu.findItem(R.id.save).setVisible(true);
		    mainMenu.findItem(R.id.saveAs).setVisible(true);
		   // mainMenu.findItem(R.id.open).setVisible(true);
		    mainMenu.findItem(R.id.saveAsImage).setVisible(true);
		    
	}
	
	void saveOption() // save drawings
	{
		
		FileService saveFile = new FileService(this); //file service object
		//initialize SavedData class with object to be save (lineMap and sprinklerMap)
		SavedData savedata = new SavedData(drawingView.lineMap, drawingView.sprinklerMap);
		savedata.scale = drawingView.settings.scale; // save scale
		
		if(filename == "") //check if the file already has name
		{
			saveFile.saveDialog(savedata); //prompt user to enter name
			
		}
		else
		{
			try {
				saveFile.save(filename, savedata); // call save method of FileService to save the drawing
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void openOption()
	{
		//open a saved drawing
		
		    setContentView(R.layout.main_drawing); // set the view to the drawing view
		    drawingView = (DrawingView) findViewById(R.id.drawView);
		    //enable appropriate buttons
		    mainMenu.findItem(R.id.line).setVisible(true);
		    mainMenu.findItem(R.id.curve).setVisible(true);
		    mainMenu.findItem(R.id.select).setVisible(true);
		    mainMenu.findItem(R.id.undo).setVisible(true);
		    mainMenu.findItem(R.id.redo).setVisible(true);
		    mainMenu.findItem(R.id.clear).setVisible(true);
		    mainMenu.findItem(R.id.settings).setVisible(true);
		    mainMenu.findItem(R.id.sprinkler).setVisible(true);
		    mainMenu.findItem(R.id.symbols).setVisible(true);
		    mainMenu.findItem(R.id.pageDescription).setVisible(true);
		    mainMenu.findItem(R.id.save).setVisible(true);
		    mainMenu.findItem(R.id.saveAs).setVisible(true);
		   // mainMenu.findItem(R.id.open).setVisible(true);
		    mainMenu.findItem(R.id.saveAsImage).setVisible(true);
		
		
		
	    loadfile = new FileService(this); 
		filelist = loadfile.getFile(); // get filelist of saved files
		
		//prompt user to select file to open
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Please Select a File");
	    builder.setItems(filelist, new DialogInterface.OnClickListener() 
	    {
	               public void onClick(DialogInterface dialog, int which) 
	               {
	               filename = filelist[which];
	               
	               try 
	   			{
	   				
	   				SavedData data  = loadfile.load(filename); // call load method of FileSerce class
	   				
	   				drawingView.LoadData(data);
	   			} 
	   			catch (StreamCorruptedException e) {
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   			} catch (IOException e) {
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   			} catch (ClassNotFoundException e) {
	   				// TODO Auto-generated catch block
	   				e.printStackTrace();
	   			}
	           }
	    });
	    builder.show(); // show dialog
	}
	
	void dbmOption() // show table of saved sprinklers 
	{
		
		DataBaseHelper dbHelper = new DataBaseHelper(this);
		dbHelper.openDataBase();
		 List<Sprinkler> sprinklerList = new ArrayList<Sprinkler>();
		 sprinklerList = dbHelper.loadSprinkler();
		 setContentView(R.layout.sprinklerview);
		 loadTable(sprinklerList);
	}
	
	void sprinklerOption() //show custom dialog with dropdown filelist of sprinkler models to select from
	{	
		drawingView.settings.dOption = drawOption.sprinkler;
		DataBaseHelper dbHelper2 = new DataBaseHelper(this);
		dbHelper2.openDataBase();
		 List<Sprinkler> sprinklerList2 = new ArrayList<Sprinkler>();
		 sprinklerList2 = dbHelper2.loadSprinkler();
		 showSprinklerModels(sprinklerList2); // call method to display the drop down box
	}
	
	void pageDescOption()
	{
		//display page description
		
		if(drawingView.settings.displayPageDesc == false)
		{
			drawingView.settings.displayPageDesc = true;
		}
		else
		{
			drawingView.settings.displayPageDesc = false;
		}
	}
	
	
	//load table to display sprinkler filelist
	public void loadTable(List<Sprinkler> sprinklerList)
	{
	
		 // Get the TableLayout
        TableLayout tl = (TableLayout) findViewById(R.id.sprinklerTable);
        
     // Create a TableRow and give it an ID
        TableRow th = new TableRow(this);
        th.setId(100);
        th.setPadding(5, 5, 5, 5);
          

        //create table row with table header
        
        // Create a TextView 
        TextView h1 = new TextView(this);
        h1.setId(200);
        h1.setText("Manufacturer");
        h1.setTextColor(Color.WHITE);
        
        th.addView(h1);
        
        // Create a TextView 
        TextView h2 = new TextView(this);
        h2.setId(200);
        h2.setText(String.valueOf("Model"));
        h2.setTextColor(Color.WHITE);
        
        th.addView(h2);
        
        // Create a TextView 
        TextView h3 = new TextView(this);
        h3.setId(200);
        h3.setText("Sprinkler Type");
        h3.setTextColor(Color.WHITE);
        
        th.addView(h3);
        
        // Create a TextView 
        TextView h4 = new TextView(this);
        h4.setId(200);
        h4.setText("Min Radius");
        h4.setTextColor(Color.WHITE);
        
        th.addView(h4);
        
        // Create a TextView 
        TextView h5 = new TextView(this);
        h5.setId(200);
        h5.setText("Max Radius");
        h5.setTextColor(Color.WHITE);
        
        th.addView(h5);
        
        // Create a TextView 
        TextView h6 = new TextView(this);
        h6.setId(200);
        h6.setText("Min Angle");
        h6.setTextColor(Color.WHITE);
        
        th.addView(h6);
        
        // Create a TextView 
        TextView h7 = new TextView(this);
        h7.setId(200);
        h7.setText("Max Angle");
        h7.setTextColor(Color.WHITE);
        
        th.addView(h7);
        
        // Create a TextView 
        TextView h8 = new TextView(this);
        h8.setId(200);
        h8.setText("Price");
        h8.setTextColor(Color.WHITE);
        
        th.addView(h8);
        
        

        // Add the TableRows to the TableLayout
        tl.addView(th, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        // Go through each item in the array
        for (int current = 0; current < sprinklerList.size(); current++)
        {
        	//create new table row and fill it with item in the filelist
        	
            // Create a TableRow and give it an ID
            TableRow tr = new TableRow(this);
            tr.setId(100+current);
            tr.setPadding(5, 5, 5, 5);
            //tr.setLayoutParams(new LayoutParams(
                    //LayoutParams.FILL_PARENT,
                   // LayoutParams.WRAP_CONTENT));   

            // Create a TextView 
            TextView t1 = new TextView(this);
            t1.setId(200+current);
            t1.setText(String.valueOf(sprinklerList.get(current).manufactuter).trim());
            t1.setTextColor(Color.WHITE);
            
            tr.addView(t1);
            
            // Create a TextView 
            TextView t2 = new TextView(this);
            t2.setId(200+current);
            t2.setText(String.valueOf(sprinklerList.get(current).model).trim());
            t2.setTextColor(Color.WHITE);
            
            tr.addView(t2);
            
            // Create a TextView 
            TextView t3 = new TextView(this);
            t3.setId(200+current);
            t3.setText(String.valueOf(sprinklerList.get(current).sprinklerType).trim());
            t3.setTextColor(Color.WHITE);
            
            tr.addView(t3);
            
            // Create a TextView 
            TextView t4 = new TextView(this);
            t4.setId(200+current);
            t4.setText(String.valueOf(sprinklerList.get(current).minRaius).trim());
            t4.setTextColor(Color.WHITE);
            
            tr.addView(t4);
            
            // Create a TextView 
            TextView t5 = new TextView(this);
            t5.setId(200+current);
            t5.setText(String.valueOf(sprinklerList.get(current).maxRadius).trim());
            t5.setTextColor(Color.WHITE);
            
            tr.addView(t5);
            
            // Create a TextView 
            TextView t6 = new TextView(this);
            t6.setId(200+current);
            t6.setText(String.valueOf(sprinklerList.get(current).minAngle).trim());
            t6.setTextColor(Color.WHITE);
            
            tr.addView(t6);
            
            // Create a TextView 
            TextView t7 = new TextView(this);
            t7.setId(200+current);
            t7.setText(String.valueOf(sprinklerList.get(current).maxAngle).trim());
            t7.setTextColor(Color.WHITE);
            
            tr.addView(t7);
            
            // Create a TextView 
            TextView t8 = new TextView(this);
            t8.setId(200+current);
            t8.setText(String.valueOf(sprinklerList.get(current).price).trim());
            t8.setTextColor(Color.WHITE);
            
            tr.addView(t8);
            
            

            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }	
	}
	
	//display available sprinkler model to user in a drop down filelist
	public void showSprinklerModels(List<Sprinkler> sprinklerList)
	   {
		   List<String> sprinklerModel = new ArrayList<String>();
		   SprinklerList = sprinklerList;
		   
		   for (int i = 0; i < sprinklerList.size(); i++)
	       {
			   
			   sprinklerModel.add(sprinklerList.get(i).model);
	       
	       }
		   
		   final ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, sprinklerModel);
		   
		   final Spinner sp = new Spinner(this);
		  
		    sp.setAdapter(adp);
		    sp.setOnItemSelectedListener(sprinklerModelSelected);
		  
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setView(sp);
		    builder.setTitle("Please select a sprinkler model");
		    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		    {
				
				public void onClick(DialogInterface dialog, int which) {
							
					
				}
			});
		    builder.create().show();
	   }
	
	//Listener for selected sprinkler
	//gets the information of the selected sprinkler 
	private OnItemSelectedListener sprinklerModelSelected = new OnItemSelectedListener()
	{

		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int pos, long id) {
			// TODO Auto-generated method stub
			
			int MinR = SprinklerList.get(pos).minRaius;
			int MaxR = SprinklerList.get(pos).maxRadius;
			int MinA = SprinklerList.get(pos).minAngle;
			int MaxA= SprinklerList.get(pos).maxAngle;
			
			currentDialog.setContentView(R.layout.sprinklersetting);
			currentDialog.setTitle("Set Sprinkler Settings");	
			//display another dialog where user can adjust settings of selected sprinkler
			drawingView.settings.showSprinklerSettingDialog(currentDialog, MinA, MaxA, MinR, MaxR); 
			
			
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	//similar to save but
	//prompt user for file name before saving
	public void saveAs() 
	{
		
		FileService saveAs = new FileService(this);
		SavedData savedata = new SavedData(drawingView.lineMap, drawingView.sprinklerMap);
		savedata.scale = drawingView.settings.scale;
		
		
		saveAs.saveDialog(savedata);
		
			try {
				saveAs.save(filename, savedata);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	
} // end class GUIActivity

