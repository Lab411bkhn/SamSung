package hwr.database;

import hwr.signal.Convert;
import hwr.signal.ExtractFeatures;
import hwr.signal.Vector;

import java.io.File;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBAdapter {
	private static String FOLDER = "/Download/HANDWRITING/DATABASE/";
	
	static final String DATABASE_NAME = "HANDWRITING.sqlite";
    static final int DATABASE_VERSION = 2;
    
    static final String TABLE_ALPHABETS = "ALPHABETS";
    static final String TABLE_NUMBERS = "NUMBERS";
    static final String TABLE_SHAPES = "SHAPES";
    static final String TABLE_WORDS = "WORDS";
    static final String TABLE_DICTIONARY = "DICTIONARY";
    
    static final String COLUMN_ID = "ID";
	static final String COLUMN_VALUE = "VALUE";
	static final String COLUMN_ACCELERATION_X = "ACCELERATION_X";
	static final String COLUMN_ACCELERATION_Y = "ACCELERATION_Y";
	static final String COLUMN_ACCELERATION_Z = "ACCELERATION_Z";
	static final String COLUMN_ACCELERATION = "ACCELERATION";
	static final String COLUMN_WORD = "WORD";
	static final String COLUMN_TARGET = "TARGET";
	
	Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
	
	//**********************************************************************************
    private static class DatabaseHelper extends SQLiteOpenHelper{
    	public DatabaseHelper(Context context) {
    		super(context, Environment.getExternalStorageDirectory() + File.separator + FOLDER + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
    //**********************************************************************************
    
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        DBHelper.close();
    }
    //**********************************************************************************
    public void insertElement(String table, String value, String acceleration_x, String acceleration_y, String acceleration_z, String acceleration)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_VALUE, value);
        initialValues.put(COLUMN_ACCELERATION_X, acceleration_x);
        initialValues.put(COLUMN_ACCELERATION_Y, acceleration_y);
        initialValues.put(COLUMN_ACCELERATION_Z, acceleration_z);
        initialValues.put(COLUMN_ACCELERATION, acceleration);
        db.insert(table, null, initialValues);
    }
    public void deleteElement(String table, String value)
    {
    	Cursor mCursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + COLUMN_VALUE + " = '" + value +"'", null);
    	if(mCursor.moveToLast())
    	{
    		db.delete(table, COLUMN_ID + "=" + mCursor.getInt(0), null);
    	}
    }
    public int getNum(String table, String value)
    {
    	Cursor mCursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + COLUMN_VALUE + " = '" + value +"'", null);
    	return (int) mCursor.getCount();
    }
    public Cursor getAllValues(String table)
    {
    	return db.query(table, new String[] {COLUMN_VALUE, COLUMN_ACCELERATION_X, COLUMN_ACCELERATION_Y, COLUMN_ACCELERATION_Z}, null, null, null, null, null);
    }
    public Cursor getValues(String table, String value)
    {
    	return db.rawQuery("SELECT * FROM " + table + " WHERE " + COLUMN_VALUE + " = '" + value +"'", null);
    }
    public Cursor getTarget(String table)
    {
    	return db.query(table, new String[] {COLUMN_VALUE, COLUMN_TARGET}, null, null, null, null, null);
    }
    public Cursor getDic()
    {
    	return db.query(TABLE_DICTIONARY, new String[] {COLUMN_WORD}, null, null, null, null, null);
    }
//    public void vovan()
//    {
//    	Cursor mCursor = getValues("MATH", "/");
//    	if (mCursor.moveToFirst())
//    	{
//			while (!mCursor.isAfterLast()) {
//				insertElement("ABC", "/", mCursor.getString(2), mCursor.getString(3), mCursor.getString(4), mCursor.getString(5));
//                mCursor.moveToNext();
//            }
//    	}
//    }
    //**********************************************************************************
}
