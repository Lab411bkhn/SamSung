package hwr.database;

import hwr.signal.Convert;
import java.util.ArrayList;

import android.database.Cursor;

public class Table {
	public static String value;
	public static ArrayList<Pattern> list = new ArrayList<Pattern>();
	public static void update(String _value, DBAdapter database)
	{
		value = _value;
		list.clear();
		String tar = "TARGET_" + value;
		Cursor mCursor = database.getTarget(tar);
		if (mCursor.moveToFirst())
    	{
			while (!mCursor.isAfterLast()) {
				Pattern p = new Pattern(mCursor.getString(0), database.getNum(_value, mCursor.getString(0)), Convert.string2Array(mCursor.getString(1)));
				list.add(p);
                mCursor.moveToNext();
            }
    	}
	}
}
