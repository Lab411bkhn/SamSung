package hwr.main;

import java.io.IOException;
import hwr.database.DBAdapter;
import hwr.database.Table;
import hwr.neuralnetworks.Net;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetectOnlineActivity extends Activity {
	Button btn_loadNet, btn_detectCha, btn_detectWord, btn_clean;
	RadioGroup rg_tables;
	static ImageView img;
	static TextView tv_hwDetected;
	
	static boolean isDetect = false;
	
	static DetectChar dc;
	
	DBAdapter database;
	public static String hwDetected = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detect_online);
		btn_loadNet = (Button) findViewById(R.id.btn_loadNet);
		btn_detectCha = (Button) findViewById(R.id.btn_detectCharacter);
		btn_clean = (Button) findViewById(R.id.btn_clean);
		tv_hwDetected = (TextView) findViewById(R.id.tv_hwDetected);
		rg_tables = (RadioGroup) findViewById(R.id.rg_tables);
		img = (ImageView) findViewById(R.id.imv);
		img.setImageResource(R.drawable.ko);
		
		database = new DBAdapter(this);
		
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
//		        	try {
//						Net.loadNetworks(Table.value);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					Toast.makeText(getApplicationContext(), "Networks is loaded", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
		
		btn_loadNet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					Net.loadNetworks(Table.value);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), "Networks is loaded", Toast.LENGTH_SHORT).show();
			}
		});
		btn_detectCha.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				HWBroadCast.isDetectCha = true;
				
				if(isDetect)
				{
					isDetect = false;
					btn_detectCha.setText("Detect Character");
				}
				else
				{
					isDetect = true;
					btn_detectCha.setText("Stop");
					dc = new DetectChar();
					dc.start();
				}
			}
		});
		btn_clean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tv_hwDetected.setText("");
			}
		});
	}
	public class DetectChar extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				if(isDetect)
				{
					DetectOnlineActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							img.setImageResource(R.drawable.co);
						}
					});
					HWBroadCast.isDetectCha = true;
					isDetect = false;
				}
			}
			
		}
	}
	public static void detection(String hwDetected)
    {
		img.setImageResource(R.drawable.ko);
		tv_hwDetected.setText(hwDetected);
		try {
			Thread.sleep(2000);
			isDetect = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detect_online, menu);
		return true;
	}

}
