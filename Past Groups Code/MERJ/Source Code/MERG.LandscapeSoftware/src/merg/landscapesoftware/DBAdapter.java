// DBAdapter.java - a class to access sqlite database
package merg.landscapesoftware;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBAdapter 
{

	public static final String TABLE_NAME = "testTB";
	public static final String DATABASE_NAME = "testBubbler";	// Database name
	public static final int DATABASE_VERSION = 2;	// Database version
	
	// database table names
	public static final String ROTATOR_TABLE = "Rotators";
	public static final String ADJUSTABLE_TABLE = "Adjustable";
	public static final String FIXED_TABLE = "Fixed";
	public static final String SPECIALTY_TABLE = "Specialty";
	public static final String BUBBLER_TABLE = "Bubbler";
	
	// constant column names
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String MODEL = "model";
	public static final String COLOR = "color";
	public static final String PRICE = "price";
	public static final String RADIUS = "radius";
	public static final String FLOW = "flow";
	public static final String ARC = "arc";
	
	
	private SQLiteDatabase Database;	// sqlite database object
	private DatabaseHelper databaseHelper;	// database helper class
	
	// Database table strings
	public static final String DATABASE_TB1 = "CREATE TABLE "
			+ ROTATOR_TABLE
	        + "(" + ID + " integer primary key autoincrement,"
	        + NAME + " TEXT not null,"
	        + MODEL + " TEXT not null,"
	        + FLOW + " TEXT not null,"
	        + PRICE + " TEXT not null,"
	        + COLOR + " TEXT not null,"
	        + ARC + " TEXT not null,"
	        + RADIUS + " TEXT);";
	
	public static final String DATABASE_TB2 = "CREATE TABLE "
			+ ADJUSTABLE_TABLE
	        + "(" + ID + " integer primary key autoincrement,"
	        + NAME + " TEXT not null,"
	        + MODEL + " TEXT not null,"
	        + FLOW + " TEXT not null,"
	        + PRICE + " TEXT not null,"
	        + COLOR + " TEXT not null,"
	        + ARC + " TEXT not null,"
	        + RADIUS + " TEXT);";
	
	public static final String DATABASE_TB3 = "CREATE TABLE "
			+ FIXED_TABLE
	        + "(" + ID + " integer primary key autoincrement,"
	        + NAME + " TEXT not null,"
	        + MODEL + " TEXT not null,"
	        + FLOW + " TEXT not null,"
	        + PRICE + " TEXT not null,"
	        + COLOR + " TEXT not null,"
	        + ARC + " TEXT not null,"
	        + RADIUS + " TEXT);";
	
	public static final String DATABASE_TB4 = "CREATE TABLE "
			+ SPECIALTY_TABLE
	        + "(" + ID + " integer primary key autoincrement,"
	        + NAME + " TEXT not null,"
	        + MODEL + " TEXT not null,"
	        + FLOW + " TEXT not null,"
	        + PRICE + " TEXT not null,"
	        + COLOR + " TEXT not null,"
	        + ARC + " TEXT not null,"
	        + RADIUS + " TEXT);";
	
	public static final String DATABASE_TB5 = "CREATE TABLE "
			+ BUBBLER_TABLE
	        + "(" + ID + " integer primary key autoincrement,"
	        + NAME + " TEXT not null,"
	        + MODEL + " TEXT not null,"
	        + FLOW + " TEXT not null,"
	        + PRICE + " TEXT not null,"
	        + COLOR + " TEXT not null,"
	        + ARC + " TEXT not null,"
	        + RADIUS + " TEXT);";
	
	// DBAdapter constructor
	public DBAdapter (Context context)
	{
		//context.deleteDatabase(DATABASE_NAME);
		databaseHelper = new DatabaseHelper(context);
	}
	
	// opens database
	public void open () throws SQLException
	{
		Database = databaseHelper.getWritableDatabase();
	}
	
	// closes database if open
	public void close()
	{
		if(Database != null)
			Database.close();
	}
	
	// inserts new sprinkler with values into database
	public void insertSprinkler(String name, String model, String flow, String price,
			String color, String arc, String radius) 
   {
		String Table;	// selects correct table for new sprinkler entry
		   if(name == "ROTATOR")
			   Table = ROTATOR_TABLE;
		   else if(name == "ADJUSTABLE")
			   Table = ADJUSTABLE_TABLE;
		   else if(name == "FIXED")
			   Table = FIXED_TABLE;
		   else if(name == "SPECIALTY")
			   Table = SPECIALTY_TABLE;
		   else
			   Table = BUBBLER_TABLE;
		   // puts values with correct columns
      ContentValues newSprinkler = new ContentValues();
      newSprinkler.put(NAME, name);
      newSprinkler.put(MODEL, model);
      newSprinkler.put(FLOW, flow); 
      newSprinkler.put(PRICE, price);
      newSprinkler.put(COLOR, color);
      newSprinkler.put(ARC, arc);
      newSprinkler.put(RADIUS, radius);

      open();
      Database.insert(Table, null, newSprinkler);
      close();
   }
	
	// updates sprinkler already in database
	public void updateSprinkler(long id, String name, String model, String flow, String price,
			String color, String arc, String radius) 
   {
		String Table;	// selects correct table for updated sprinkler entry
		   if(name == "ROTATOR")
			   Table = ROTATOR_TABLE;
		   else if(name == "ADJUSTABLE")
			   Table = ADJUSTABLE_TABLE;
		   else if(name == "FIXED")
			   Table = FIXED_TABLE;
		   else if(name == "SPECIALTY")
			   Table = SPECIALTY_TABLE;
		   else
			   Table = BUBBLER_TABLE;
		   // puts values with correct columns
      ContentValues editSprinkler = new ContentValues();
      //editSprinkler.put(NAME, name);
      editSprinkler.put(MODEL, model);
      editSprinkler.put(FLOW, flow); 
      editSprinkler.put(PRICE, price);
      editSprinkler.put(COLOR, color);
      editSprinkler.put(ARC, arc);
      editSprinkler.put(RADIUS, radius);

      open();
      Database.update(Table, editSprinkler, "_id=" + id, null);
      close();
   }
	
		// returns cursor with all sprinklers in selected table with info for columns 
	   // columns and orders them based on radius
	   public Cursor getSprinklers(String table) 
	   {
		   String Table;	// selects correct table
		   if(table == "ROTATOR")
			   Table = ROTATOR_TABLE;
		   else if(table == "ADJUSTABLE")
			   Table = ADJUSTABLE_TABLE;
		   else if(table == "FIXED")
			   Table = FIXED_TABLE;
		   else if(table == "SPECIALTY")
			   Table = SPECIALTY_TABLE;
		   else
			   Table = BUBBLER_TABLE;
		   
	      return Database.query(
	    		  Table, new String[] {"_id", MODEL, FLOW, COLOR, ARC, RADIUS, PRICE}, 
	    		  null, null, null, null, RADIUS);
	   } 
	   
	   // returns a cursor with single sprinkler using id from selected Table
	   public Cursor getASprinkler(long id,String table) 
	   {
		   String Table;	// selects correct table
		   if(table == "ROTATOR")
			   Table = ROTATOR_TABLE;
		   else if(table == "ADJUSTABLE")
			   Table = ADJUSTABLE_TABLE;
		   else if(table == "FIXED")
			   Table = FIXED_TABLE;
		   else if(table == "SPECIALTY")
			   Table = SPECIALTY_TABLE;
		   else
			   Table = BUBBLER_TABLE;
	      return Database.query(
	    		  Table, null, "_id=" + id, null, null, null, null);
	   } 

	// deletes sprinkler using id from selected Table
	   public void deleteSprinkler(long id, String table) 
	   {
		   String Table;// selects correct table for new sprinkler entry
		   if(table == "ROTATOR")
			   Table = ROTATOR_TABLE;
		   else if(table == "ADJUSTABLE")
			   Table = ADJUSTABLE_TABLE;
		   else if(table == "FIXED")
			   Table = FIXED_TABLE;
		   else if(table == "SPECIALTY")
			   Table = SPECIALTY_TABLE;
		   else
			   Table = BUBBLER_TABLE;
	      open();
	      Database.delete(Table, "_id=" + id, null);
	      close();
	   } 
	
	   // DatabaseHelper class
	private class DatabaseHelper extends SQLiteOpenHelper
	{
		// constructor
		public DatabaseHelper (Context context)
	      {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
		// creates database tables
	      @Override
	      public void onCreate(SQLiteDatabase db) 
	      {     
	         db.execSQL(DATABASE_TB1);
	         db.execSQL(DATABASE_TB2);
	         db.execSQL(DATABASE_TB3);
	         db.execSQL(DATABASE_TB4);
	         db.execSQL(DATABASE_TB5);
	      }
	      // drops the current tables when database upgrades
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	          int newVersion) 
	      {
	    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB1);
	    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB2);
	    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB3);
	    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB4);
	    	  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TB5);
	    	  onCreate(db);
	      }
	}
	
}
