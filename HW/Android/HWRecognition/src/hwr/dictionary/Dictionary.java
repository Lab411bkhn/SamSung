package hwr.dictionary;

import hwr.database.DBAdapter;
import java.util.ArrayList;

import android.database.Cursor;

public class Dictionary {
	public static ArrayList<String> dic;
	public static void loadDic(DBAdapter database)
	{
		Cursor mCursor = database.getDic();
		if (mCursor.moveToFirst())
    	{
			while (!mCursor.isAfterLast()) {
				dic.add(mCursor.getString(0));
                mCursor.moveToNext();
            }
    	}
	}
	public static boolean checkWord(String word, String input)
	{
		for (int i = 0; i < input.length(); i++) {
			if (word.contains(String.valueOf(input.charAt(i))))
				return true;
		}
		return false;
	}
	public static double accuracy(String word, String input)
	{
		if(checkWord(word, input))
		{
			double count = 0;
			for(int i=0; i<word.length();i++){
				if(input.contains(String.valueOf(word.charAt(i))))
					count ++;
			}
			return (double) count*100/(double) word.length();
		}
		else
			return 0;
	}
}
