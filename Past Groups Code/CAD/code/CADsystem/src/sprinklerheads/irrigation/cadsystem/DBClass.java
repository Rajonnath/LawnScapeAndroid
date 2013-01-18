/*FILENAME: DBClass.java
*DESCRIPTION: DBClass for creting various databse tables updating and deleting all in one class
*/
//package of class
package sprinklerheads.irrigation.cadsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBClass extends SQLiteOpenHelper{
	

	
private static final int DATABASE_VERSION = 1;
static int userid=0;
    
    private SQLiteDatabase database;
 
    // Database Name
    private static final String DATABASE_NAME = "UserInputs";
    //private static final String DATABASE_NAME1 = "Design";
    // Contacts table name
    private static final String TABLE_USERINFO = "UserInformation";
    private static final String TABLE_PROPERTYINFO = "PropertyInformation";
    private static final String TABLE_PLANTINFO = "PlantInformation";
    private static final String TABLE_SLABINFO = "SlabInformation";
    private static final String TABLE_FLOWERINFO = "FlowerInformation";
    private static final String TABLE_BUILDINGINFO = "BuildingInformation";
    private static final String TABLE_POINTINFO = "PointInformation";
    
    // Contacts Table Columns names
    //User Information Table 
    private static final String KEY_USERID = "userid";
    private static final String KEY_FNAME = "fname";
    private static final String KEY_LNAME = "lname";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "emailid";
    private static final String KEY_STREET = "street";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_ZIP= "zip";
    
    
    private static final String KEY_KEYXP= "key_xp";
    private static final String KEY_KEYYP= "key_yp";
    private static final String KEY_MAINX=  "water_main_x";
    private static final String KEY_MAINY= "water_main_y";
    private static final String KEY_MAINPSI= "water_main_psi";
    private static final String KEY_MAINSIZE= "water_main_size";
 
    private static final String KEY_KEYXPL= "key_xpl";
    private static final String KEY_KEYYPL= "key_ypl";
    private static final String KEY_WATERREQ=  "water_req";
    private static final String KEY_SIZE= "size";
    private static final String KEY_TYPE= "type";
    
    private static final String KEY_KEYXS= "key_xs";
    private static final String KEY_KEYYS= "key_ys";
    private static final String KEY_LENGTH=  "length";
    private static final String KEY_WIDTH= "width";
    
    private static final String KEY_KEYXBI= "key_xbi";
    private static final String KEY_KEYYBI= "key_ybi";
    private static final String KEY_NAME=  "name";
    
    private static final String KEY_KEYXFL= "key_xfl";
    private static final String KEY_KEYYFL= "key_yfl";
    private static final String KEY_WATERREQFL=  "waterreqfl";
   
    private static final String KEY_KEYXPO= "key_xs";
    private static final String KEY_KEYYPO= "key_ys";
    private static final String KEY_OBJECT=  "object";
    private static final String KEY_ORDER= "order";
    
    //constructor   
	public DBClass(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	//all create table queries in onCreate 
	public void onCreate(SQLiteDatabase db) {
        String CREATE_USERINFO_TABLE = "CREATE TABLE " + TABLE_USERINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_FNAME + " TEXT,"
                + KEY_LNAME + " TEXT,"+ KEY_PHONE + " TEXT,"+ KEY_EMAIL + " TEXT,"
                + KEY_STREET + " TEXT,"+ KEY_CITY + " TEXT,"+ KEY_STATE + " TEXT,"
                + KEY_ZIP + " TEXT" + ")";
	
        String CREATE_PROPERTY_TABLE = "CREATE TABLE " + TABLE_PROPERTYINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_MAINX + " TEXT,"
                + KEY_MAINY + " TEXT,"+ KEY_MAINPSI + " TEXT,"+ KEY_MAINSIZE + " TEXT,"
                + KEY_KEYXP + " TEXT,"+ KEY_KEYYP + " TEXT,"+ ")";
        
        String CREATE_PLANT_TABLE = "CREATE TABLE " + TABLE_PLANTINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_WATERREQ + " TEXT,"
                + KEY_SIZE + " TEXT,"+ KEY_TYPE + " TEXT,"
                + KEY_KEYXPL + " TEXT,"+ KEY_KEYYPL + " TEXT,"+ ")";
        
        String CREATE_SLAB_TABLE = "CREATE TABLE " + TABLE_SLABINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_LENGTH + " TEXT,"
                + KEY_WIDTH + " TEXT,"
                + KEY_KEYXS + " TEXT,"+ KEY_KEYYS + " TEXT,"+ ")";
        
        String CREATE_BUILDING_TABLE = "CREATE TABLE " + TABLE_BUILDINGINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_KEYXBI + " TEXT,"+ KEY_KEYYBI + " TEXT,"+ ")"; 
        
        String CREATE_FLOWER_TABLE = "CREATE TABLE " + TABLE_FLOWERINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_WATERREQFL + " TEXT,"
                + KEY_KEYXFL + " TEXT,"+ KEY_KEYYFL + " TEXT,"+ ")";
        
        String CREATE_POINT_TABLE = "CREATE TABLE " + TABLE_POINTINFO + "("
                + KEY_USERID + " INTEGER PRIMARY KEY," + KEY_OBJECT + " TEXT,"
                + KEY_ORDER + " TEXT,"
                + KEY_KEYXPO + " TEXT,"+ KEY_KEYYPO + " TEXT,"+ ")";
        
        //executing all create queries
        db.execSQL(CREATE_USERINFO_TABLE);
        db.execSQL(CREATE_PROPERTY_TABLE);
        db.execSQL(CREATE_SLAB_TABLE);
        db.execSQL(CREATE_PLANT_TABLE);
        db.execSQL(CREATE_BUILDING_TABLE);
        db.execSQL(CREATE_FLOWER_TABLE);
        db.execSQL(CREATE_POINT_TABLE);
	
    }
	 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERINFO);
	    onCreate(db);
    }
	
    public void open() throws SQLException
    {
    	database=getWritableDatabase();
    }
    
    public void close()
    {
    	if(database!=null)
    		database.close();
    	//close the database connection
    }
	
	
 // Adding new Building
    public void insertBuilding(String name, long keyx, long keyy)
    {
    	userid++;
    	ContentValues newBuilding=new ContentValues();
    	newBuilding.put("useridb", userid);
    	newBuilding.put("key_xbi", keyx);
    	newBuilding.put("key_ybi", keyy);
    	newBuilding.put("name", name);
    	   	
    	open();
    	database.insert("BuildingInformation", null,newBuilding);
    	close();
    	
    }
    
    public Cursor getOneBuilding(long id) 
    {
       return database.query(
          "BuildingInformation", null, "useridb=" + id, null, null, null, null);
    }
    
    public void deleteBuilding(long id) 
    {
       open(); // open the database
       database.delete("BuildingInformation", "useridb=" + id, null);
       close(); // close the database
    }
	
    public void insertSlab(long keyx, long keyy)
    {
    	userid++;
    	ContentValues newSlab=new ContentValues();
    	newSlab.put("useridsl", userid);
    	newSlab.put("key_xs", keyx);
    	newSlab.put("key_ys", keyy);
    	//newSlab.put("slab_width", bdistance);
    	   	
    	open();
    	database.insert("SlabInformation", null,newSlab);
    	close();
    	
    }
    
    public void updateSlab(long userid, long blength, long bwidth, long bdistance)
    {
    	ContentValues editSlab=new ContentValues();
    	editSlab.put("useridsl", userid);
    	editSlab.put("slab_length", blength);
    	editSlab.put("slab_width", bwidth);
    	editSlab.put("slab_width", bdistance);
    	   	
    	open();
    	database.update("SlabInformation",editSlab,"useridsl="+ userid,null);
    	close();
    }
    
    public void deleteSlab(long id) 
    {
       open(); // open the database
       database.delete("SlabInformation", "useridsl=" + id, null);
       close(); // close the database
    }
    
    public void insertProperty(long userid, long plength, long pwidth, long watermain_psi)
    {
    	ContentValues newProperty=new ContentValues();
    	newProperty.put("useridp", userid);
    	newProperty.put("plot_length", plength);
    	newProperty.put("plot_width", pwidth);
    	newProperty.put("watermain_psi", watermain_psi);
    	   	
    	open();
    	database.insert("PlotInformation", null,newProperty);
    	close();
    	
    }
    
    public void updateProperty(long userid, long plength, long pwidth, long watermain_psi)
    {
    	ContentValues editProperty=new ContentValues();
    	editProperty.put("useridp", userid);
    	editProperty.put("plot_length", plength);
    	editProperty.put("plot_width", pwidth);
    	editProperty.put("watermain_psi", watermain_psi);
    	   	
    	open();
    	database.update("PlotInformation",editProperty,"useridp="+ userid,null);
    	close();
    	
    	
    }
    
    public void deleteProperty(long id) 
    {
       open(); // open the database
       database.delete("PropertyInformation", "useridp=" + id, null);
       close(); // close the database
    }
    
   
    public void insertFlower()
    {
    	ContentValues newSlab=new ContentValues();
    	newSlab.put("useridsl", userid);
    	newSlab.put("slab_length", blength);
    	newSlab.put("slab_width", bwidth);
    	newSlab.put("slab_width", bdistance);
    	   	
    	open();
    	database.insert("SlabInformation", null,newSlab);
    	close();
           	
    }
    
    public void deleteFlower()
    {
    	
    }
    
    public void updateFlower()
    {
    	ContentValues newSlab=new ContentValues();
    	newSlab.put("useridsl", userid);
    	newSlab.put("slab_length", blength);
    	newSlab.put("slab_width", bwidth);
    	newSlab.put("slab_width", bdistance);
    	   	
    	open();
    	database.insert("SlabInformation", null,newSlab);
    	close();
    }
    
      
}
