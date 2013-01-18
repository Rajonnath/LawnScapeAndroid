package merg.landscapesoftware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

//this is use to save serialized object as a file into device memory 
//and to open saved object
public class FileService 
{

	 private Context context;
	 String filename; //file name
	 SavedData saveData; //saved object
	 String[] filelist; // list of saved files
	 EditText input; // textview where user will type file name

	    public FileService(Context context) 
	    {
	        this.context = context;
	       
	    }
	    
	   
	   //save alert dialog 
	    public void saveDialog(SavedData savedata)
	    {
	    	
	    	AlertDialog.Builder alert = new AlertDialog.Builder(context);

	    	saveData = savedata;
	    	input = new EditText(context);
	    	 
	    	alert.setTitle("SaveAs");
	    	alert.setMessage("Please Enter File Name");

	    	// Set an EditText view to get user input 
	    	
	    	alert.setView(input);

	    	alert.setPositiveButton("Save", positiveNameAlertLstn);

	    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	  public void onClick(DialogInterface dialog, int whichButton) {
	    	    // Canceled.
	    	  }
	    	});

	    	alert.show();
	    	 
	    }
	    
	    //onclick listener for alert dialog positive button
	    private DialogInterface.OnClickListener positiveNameAlertLstn = new DialogInterface.OnClickListener()
	    {
	    	

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String value = input.getText().toString();
				filename = value;
				
				filename = filename +".merg"; // add merge extension to the file name
				
				try {
					save(filename, saveData);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	    };
	    
	    
	    void getFileName()
	    {
	    	saveDialog(saveData);
	    	
	    }
	    String FileName()
	    {
	    	
	    	return filename;
	    }

	    //save function to write file to device memory
	    public void save(String filename, SavedData savedata) throws Exception
	    {
	        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE); //open file output stream
	        ObjectOutputStream os = new ObjectOutputStream(fos); 
	        os.writeObject(savedata); // write object to file
	        
	        os.close(); // close output stream
	    }
	    
	    //function to open saved object
	    public SavedData load(String fname) throws StreamCorruptedException, IOException, ClassNotFoundException
	    {
	    	FileInputStream fis = context.openFileInput(fname); //open file input stream
	    	ObjectInputStream is = new ObjectInputStream(fis); //create instance of object input stream
	    	SavedData savedata = (SavedData) is.readObject(); //read saved data
	    	is.close(); // close input stream
	    	
	    	
	    	
	    	return savedata; // return saved data
	    }
	    
	    
	    
	    
	 // inner class, generic extension filter
	    //use to filter file base on extension
		public class GenericExtFilter implements FilenameFilter 
		{
	 
			private String ext;
	 
			public GenericExtFilter(String ext) {
				this.ext = ext;
			}
	 
			public boolean accept(File dir, String name) {
				return (name.endsWith(ext));
			}
		}
	    
		//get list of saved files
		public  String[] getFile() 
		{
			 
			
			String ext = ".merg";
			
			 File dir = context.getFilesDir();
			
			GenericExtFilter filter = new GenericExtFilter(ext); // filter saved files based on .merg extension
			
	 
			
			 filelist = dir.list(filter);
			
			return filelist;
			
		}
		
}
