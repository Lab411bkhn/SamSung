package hwr.signal;

import hwr.database.DBAdapter;
import hwr.database.Table;
import java.util.ArrayList;

import android.database.Cursor;

public class Vector {
	public double[] target;
	public double[] features;
	public Vector()
	{
	}
	public Vector(double[] _target, double[] _features)
	{
		target = _target;
		features = _features;
	}
	public Vector(String _target, String _features)
	{
		target = Convert.string2Array(_target);
		features = Convert.string2Array(_features);
	}
	public static ArrayList<Vector> loadTrainData(DBAdapter database)
	{
		ArrayList<Vector> result = new ArrayList<Vector>();
		Cursor mCursor = database.getAllValues(Table.value);
		int counter = 0;
		if (mCursor.moveToFirst())
    	{
			while (!mCursor.isAfterLast()) {
				if(counter%20 != 0) {
					Vector vec = new Vector();
					for(int i=0; i<Table.list.size(); i++)
					{
						if(mCursor.getString(0).equals(Table.list.get(i).value))
							vec.target = Table.list.get(i).target;
					}
					double[] acc_x = Convert.string2Array(mCursor.getString(1));
					double[] acc_y = Convert.string2Array(mCursor.getString(2));
					double[] acc_z = Convert.string2Array(mCursor.getString(3));
					vec.features = ExtractFeatures.getFeatures(acc_x, acc_y, acc_z);
					result.add(vec);
				}
				counter ++;
                mCursor.moveToNext();
            }
    	}
    	return result;
	}
	public static ArrayList<Vector> loadTestData(DBAdapter database)
	{
		ArrayList<Vector> result = new ArrayList<Vector>();
		Cursor mCursor = database.getAllValues(Table.value);
		int counter = 0;
		if (mCursor.moveToFirst())
    	{
			while (!mCursor.isAfterLast()) {
				if(counter%20 == 0) {
					Vector vec = new Vector();
					for(int i=0; i<Table.list.size(); i++)
					{
						if(mCursor.getString(0).equals(Table.list.get(i).value))
							vec.target = Table.list.get(i).target;
					}
					double[] acc_x = Convert.string2Array(mCursor.getString(1));
					double[] acc_y = Convert.string2Array(mCursor.getString(2));
					double[] acc_z = Convert.string2Array(mCursor.getString(3));
					vec.features = ExtractFeatures.getFeatures(acc_x, acc_y, acc_z);
					result.add(vec);
				}
				counter ++;
                mCursor.moveToNext();
            }
    	}
    	return result;
	}
}
