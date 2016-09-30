package hwr.main;

import hwr.database.Pattern;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Pattern>{
	Activity context=null;
	int layoutId;
	ArrayList<Pattern> myArray;
	
	public MyArrayAdapter(Activity context, int layoutId, ArrayList<Pattern> arr){
		 super(context, layoutId, arr);
		 this.context=context;
		 this.layoutId=layoutId;
		 this.myArray = arr;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(layoutId, null);
		if(myArray.size()>0 && position>=0){
			final TextView value = (TextView) convertView.findViewById(R.id.tv_value);
			final TextView nop = (TextView) convertView.findViewById(R.id.tv_nop);
			final Button btn_add = (Button) convertView.findViewById(R.id.btn_add);
			final Button btn_del = (Button) convertView.findViewById(R.id.btn_del);
			btn_add.setTag(position);
			btn_del.setTag(position);
			
			value.setText(myArray.get(position).value);
			nop.setText(Integer.toString(myArray.get(position).NOP));
			btn_add.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View arg0) {
	            	CollectPatternsActivity.position = position;
	            	HWBroadCast.isCollect = true;
	            }
	        });
			btn_del.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View arg0) {
	            	CollectPatternsActivity.position = position;
	            	CollectPatternsActivity.deletePatterns();
	            }
	        });
		}
		return convertView;
	}
}
