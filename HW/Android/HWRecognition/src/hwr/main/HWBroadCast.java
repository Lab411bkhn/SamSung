package hwr.main;

import hwr.neuralnetworks.Net;
import hwr.signal.Acceleration;
import hwr.signal.ExtractFeatures;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class HWBroadCast extends BroadcastReceiver{
	public static String broadcastName = "testBroadCast";
	String keyMsg = "message";
	public static String value = "";
	public static boolean isCollect = false;
	public static boolean isDetectCha = false;
	ArrayList<Acceleration> listacce = new ArrayList<Acceleration>();
	ArrayList<Acceleration> hw = new ArrayList<Acceleration>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(broadcastName)) {
			String ms = intent.getStringExtra(keyMsg);
			if (ms.equals("OK")) {
				Toast.makeText(context, "Gear connected!", Toast.LENGTH_LONG).show();
			}else {
				String[] ls = ms.split("\\|");
				Acceleration acceleration = new Acceleration(Double.parseDouble(ls[0]), Double.parseDouble(ls[1]), Double.parseDouble(ls[2]));
				if(isCollect)
				{
					listacce.add(acceleration);
					if(listacce.size() == 3)
					{
						double sum = 0.0;
						for(int i=0; i<listacce.size(); i++)
						{
							sum += listacce.get(i).acceleration;
						}
						sum = (double) sum/listacce.size();
						if(sum > 0.2)
						{
							for(int i=0; i<listacce.size(); i++)
							{
								hw.add(listacce.get(i));
							}
						}
						else
						{
							if(hw.size() > 0)
							{
								if(hw.size()>6 && hw.size()<40)
									CollectPatternsActivity.addPatterns(context, hw);
								else
									Toast.makeText(null, "Fail!", Toast.LENGTH_SHORT).show();
								isCollect = false;
								hw.clear();
							}
						}
						listacce.clear();
					}
				}
				if(isDetectCha)
				{
					listacce.add(acceleration);
					if(listacce.size() == 3)
					{
						double sum = 0.0;
						for(int i=0; i<listacce.size(); i++)
						{
							sum += listacce.get(i).acceleration;
						}
						sum = (double) sum/listacce.size();
						if(sum > 0.2)
						{
							for(int i=0; i<listacce.size(); i++)
							{
								hw.add(listacce.get(i));
							}
						}
						else
						{
							if(hw.size() > 0)
							{
								if(hw.size() > 6 && hw.size() < 40)
								{
									double[] input = ExtractFeatures.getFeatures(hw);
									String hwDetected = Net.detect(input);
									DetectOnlineActivity.detection(hwDetected);
								}
								else
									Toast.makeText(null, "Fail!", Toast.LENGTH_SHORT).show();
								isDetectCha = false;
								hw.clear();
							}
						}
						listacce.clear();
					}
				}
			}
		}
	}
}
