/*
 * This class is used to import sqlite database file of sprinklers
 * into application's sqlite database for newly installed application
 * The sqlite file is stored in the asset file as part of application's
 * resources
 * 
 */


package merg.landscapesoftware;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper
{
	 
    //The Android's default system path of application database.
    private static String DB_PATH = "/data/data/merg.landscapesoftware/databases/";
 
    private static String DB_NAME = "equipment.sqlite";
 
    private SQLiteDatabase equipmentDataBase; 
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of application 
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	equipmentDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(equipmentDataBase != null)
    		    equipmentDataBase.close();
 
    	    super.close();
 
	}
 
	

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
 
        // Add public helper methods to access and get content from the database.
      
	
	public  List<Sprinkler> loadSprinkler() {
	    List<Sprinkler> sprinklerList = new ArrayList<Sprinkler>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM sprinklers";
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to filelist
	    if (cursor.moveToFirst()) {
	        do {
	            Sprinkler sprinkler = new Sprinkler();
	            sprinkler.sprinklerID = (Integer.parseInt(cursor.getString(0)));
	            sprinkler.manufactuter = (cursor.getString(1));
	            sprinkler.model = (cursor.getString(2));
	            sprinkler.sprinklerType = (cursor.getString(3));
	            sprinkler.minRaius = (Integer.parseInt(cursor.getString(4)));
	            sprinkler.maxRadius = (Integer.parseInt(cursor.getString(5)));
	            sprinkler.minAngle = (Integer.parseInt(cursor.getString(6)));
	            sprinkler.maxAngle = (Integer.parseInt(cursor.getString(7)));
	            sprinkler.price = (Integer.parseInt(cursor.getString(8)));
	            
	            
	            sprinklerList.add(sprinkler);
	        } while (cursor.moveToNext());
	    }
	 
	    return sprinklerList;
	}
 
}
