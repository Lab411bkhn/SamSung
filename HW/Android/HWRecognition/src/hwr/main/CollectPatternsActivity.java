package hwr.main;

import hwr.database.DBAdapter;
import hwr.database.Table;
import hwr.signal.Acceleration;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CollectPatternsActivity extends Activity {
	RadioGroup rg_tables;
	ListView lv_patterns;
	static DBAdapter database;
	public static int position = 0;
	
    static MyArrayAdapter adapter = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_patterns);
		rg_tables = (RadioGroup) findViewById(R.id.rg_tables);
		lv_patterns = (ListView) findViewById(R.id.lv_patterns);
		
		database = new DBAdapter(this);
		
		adapter = new MyArrayAdapter(CollectPatternsActivity.this, R.layout.pattern, Table.list);
        lv_patterns.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
		rg_tables.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
		    public void onCheckedChanged(RadioGroup rGroup, int checkedId)
		    {
		        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
		        if (checkedRadioButton.isChecked())
		        {
		        	database.open();
		        	Table.update(checkedRadioButton.getText().toString(), database);
		        	database.close();
		        	adapter.notifyDataSetChanged();
		        }
		    }
		});
	}
	public static void addPatterns(Context context, ArrayList<Acceleration> list)
    {
		String value = Table.list.get(position).value;
    	String ax = "";
    	String ay = "";
    	String az = "";
    	String a = "";
    	for(int i=0; i<list.size(); i++)
    	{
    		ax += Double.toString(list.get(i).ax) + "\t";
    		ay += Double.toString(list.get(i).ay) + "\t";
    		az += Double.toString(list.get(i).az) + "\t";
    		a += Double.toString(list.get(i).acceleration) + "\t";
    	}
    	database.open();
		database.insertElement(Table.value, value, ax.trim(), ay.trim(), az.trim(), a.trim());
		database.close();
		Table.list.get(position).NOP ++;
		adapter.notifyDataSetChanged();
		Toast.makeText(context, list.size()+"", Toast.LENGTH_SHORT).show();
    }
	public static void deletePatterns()
    {
		database.open();
		database.deleteElement(Table.value, Table.list.get(position).value);
		database.close();
		if(Table.list.get(position).NOP>0)
			Table.list.get(position).NOP --;
		adapter.notifyDataSetChanged();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collect_patterns, menu);
		return true;
	}

}
