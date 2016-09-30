package hwr.main;

import hwr.database.DBAdapter;
import hwr.database.Table;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button btn_collect, btn_train, btn_detect;
	static DBAdapter database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_collect = (Button) findViewById(R.id.btn_collect);
        btn_train = (Button) findViewById(R.id.btn_train);
        btn_detect = (Button) findViewById(R.id.btn_detect);
        
        registerReceiver(new HWBroadCast(), new IntentFilter(HWBroadCast.broadcastName));
        
        database = new DBAdapter(this);
		database.open();
		Table.update("ALPHABETS", database);
		database.close();
		
        btn_collect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent("myCommand");
				i.putExtra("command", "run");
				sendBroadcast(i);
				Intent intent = new Intent(MainActivity.this, CollectPatternsActivity.class);
				startActivity(intent);
			}
		});
        btn_train.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent("myCommand");
				i.putExtra("command", "stop");
				sendBroadcast(i);
				Intent intent = new Intent(MainActivity.this, TrainActivity.class);
				startActivity(intent);
			}
		});
        btn_detect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent("myCommand");
				i.putExtra("command", "run");
				sendBroadcast(i);
				Intent intent = new Intent(MainActivity.this, DetectOnlineActivity.class);
				startActivity(intent);
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
