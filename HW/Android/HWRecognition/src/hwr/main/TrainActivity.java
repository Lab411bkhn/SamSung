package hwr.main;

import hwr.database.DBAdapter;
import hwr.database.Table;
import hwr.neuralnetworks.Net;
import hwr.signal.Vector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class TrainActivity extends Activity {
	double LEARNING_RATE = 0.01;
	double MOMENTUM = 0.7;
	int LOOP = 200;
	
	Button btn_train;
	EditText edt_rate, edt_momen, edt_loop;
	RadioGroup rg_tables;
	
	boolean isTrain = false;
	
	DBAdapter database;
	
	int counter = 0;
	private XYMultipleSeriesDataset mDataSet1;
	private XYMultipleSeriesRenderer mRender1;
	private XYSeries mCurrentSeries1;
	private GraphicalView mChartView1;
	private XYMultipleSeriesDataset mDataSet2;
	private XYMultipleSeriesRenderer mRender2;
	private XYSeries mCurrentSeries2;
	private GraphicalView mChartView2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		btn_train = (Button) findViewById(R.id.btn_trainNet);
		edt_rate = (EditText) findViewById(R.id.edt_rate);
		edt_momen = (EditText) findViewById(R.id.edt_momen);
		edt_loop = (EditText) findViewById(R.id.edt_loop);
		rg_tables = (RadioGroup) findViewById(R.id.rg_tables);
		
		database = new DBAdapter(this);
		
		mDataSet1 = new XYMultipleSeriesDataset();
		mRender1 = new XYMultipleSeriesRenderer();
		mDataSet2 = new XYMultipleSeriesDataset();
		mRender2 = new XYMultipleSeriesRenderer();
		
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
		        }
		    }
		});
		
		btn_train.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				graphInit1();
				graphInit2();
				if(isTrain)
				{
					isTrain = false;
					btn_train.setText("Train Networks");
				}
				else
				{
					isTrain = true;
					counter = 0;
					btn_train.setText("Stop");
					
					LEARNING_RATE = Double.parseDouble(edt_rate.getText().toString());
					MOMENTUM = Double.parseDouble(edt_momen.getText().toString());
					LOOP = Integer.parseInt(edt_loop.getText().toString());
					
					mCurrentSeries1.add(counter, 1);
	    			mRender1.setXAxisMin(counter - LOOP);
	    			mRender1.setXAxisMax(counter);
	    			mChartView1.repaint();
	    			
					mCurrentSeries2.add(counter, 0);
	    			mRender2.setXAxisMin(counter - LOOP);
	    			mRender2.setXAxisMax(counter);
	    			mChartView2.repaint();
	    			
	    			counter ++;
	    			
	    			Training tr = new Training();
					tr.start();
				}
			}
		});
	}
	class Training extends Thread
    {
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		TrainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Loading data...", Toast.LENGTH_SHORT).show();
				}
			});
    		database.open();
    		ArrayList<Vector> trainData = Vector.loadTrainData(database);
    		ArrayList<Vector> testData = Vector.loadTestData(database);
    		database.close();
    		
    		Net.initNet(trainData.get(0).features.length, (int) (1.5*trainData.get(0).features.length), trainData.get(0).target.length);
    		Net.initializeWeights();
    		Net.setLearningParameters(LEARNING_RATE, MOMENTUM);
    		TrainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Start training...", Toast.LENGTH_SHORT).show();
				}
			});
    		Random random = new Random();
    		
    		String sb = "";
    		
    		while(isTrain && counter < LOOP)
    		{
    			Net.trainNetworks(random, trainData, testData);
    			mCurrentSeries1.add(counter, Net.mse);
    			mRender1.setXAxisMin(counter - LOOP);
    			mRender1.setXAxisMax(counter);
    			mChartView1.repaint();
    			mCurrentSeries2.add(counter, Net.accuracy);
    			mRender2.setXAxisMin(counter - LOOP);
    			mRender2.setXAxisMax(counter);
    			mChartView2.repaint();
    			counter ++;
    			
    			sb += Double.toString(Net.mse) + "\t" + Double.toString(Net.accuracy) + "\n";
    		}
    		
    		try {
    			File sdcard = Environment.getExternalStorageDirectory();
    	        String path = "/Download/HANDWRITING/NEURALNETWORKS/tkAlp.txt";
    			File fo = new File(sdcard, path);
    	        if (!fo.exists()) {
    	            fo.createNewFile();
    	        }
    			FileWriter fw = new FileWriter(fo.getAbsoluteFile());
    			BufferedWriter bw = new BufferedWriter(fw);
    	        bw.write(sb);
    	        bw.close();
    	        fw.close();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
            
    		TrainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Training complete!", Toast.LENGTH_SHORT).show();
				}
			});
    		try {
    			Net.saveNetworks(Table.value);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		TrainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "NetWorks saved!", Toast.LENGTH_SHORT).show();
					isTrain = false;
					btn_train.setText("Train Networks");
				}
			});
    	}
    }
	public void graphInit1() {
		mRender1.setApplyBackgroundColor(true);
		mRender1.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender1.setAxisTitleTextSize(30);
		mRender1.setChartTitleTextSize(30);
		mRender1.setLabelsTextSize(30);
		mRender1.setLegendTextSize(30);
		mRender1.setMargins(new int[] { 50, 70, 70, 10 });
		mRender1.setChartTitle("MSE");
		
		mRender1.setYAxisAlign(Align.LEFT, 0);
		mRender1.setYLabelsAlign(Align.RIGHT);
		mRender1.setLabelsColor(Color.YELLOW);
		mRender1.setZoomButtonsVisible(false);
		
		
		mRender1.setPointSize(2.0f);
		mRender1.setXAxisMin(0);
		mRender1.setXAxisMax(LOOP);
		mRender1.setYAxisMax(0.4);
		mRender1.setYAxisMin(0);
		
		mRender1.setPanEnabled(false, false);
		mRender1.setZoomEnabled(false, false);
		
		if (mChartView1 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.lv_mse);
			mChartView1 = ChartFactory.getLineChartView(getApplicationContext(),mDataSet1, mRender1);
			mRender1.setClickEnabled(true);
			mRender1.setSelectableBuffer(10);
			mChartView1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			layout.addView(mChartView1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView1.repaint();
		}
	
		XYSeries series = new XYSeries("LOOPS");
		mDataSet1.addSeries(series);
		mCurrentSeries1 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.GREEN);
		mRender1.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);
		
		mChartView1.repaint();
	}
	public void graphInit2() {
		mRender2.setApplyBackgroundColor(true);
		mRender2.setBackgroundColor(Color.argb(255, 30, 30, 30));
		mRender2.setAxisTitleTextSize(30);
		mRender2.setChartTitleTextSize(30);
		mRender2.setLabelsTextSize(30);
		mRender2.setLegendTextSize(30);
		mRender2.setMargins(new int[] { 50, 70, 70, 10 });
		mRender2.setChartTitle("Accuracy");
		mRender2.setYTitle("%");
		
		mRender2.setYAxisAlign(Align.LEFT, 0);
		mRender2.setYLabelsAlign(Align.RIGHT);
		mRender2.setLabelsColor(Color.YELLOW);
		mRender2.setZoomButtonsVisible(false);
		
		
		mRender2.setPointSize(2.0f);
		mRender2.setXAxisMin(0);
		mRender2.setXAxisMax(LOOP);
		mRender2.setYAxisMax(100);
		mRender2.setYAxisMin(0);
		
		mRender2.setPanEnabled(false, false);
		mRender2.setZoomEnabled(false, false);
		
		if (mChartView2 == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.lv_accuracy);
			mChartView2 = ChartFactory.getLineChartView(getApplicationContext(),mDataSet2, mRender2);
			mRender2.setClickEnabled(true);
			mRender2.setSelectableBuffer(10);
			mChartView2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			layout.addView(mChartView2, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		} else {
			mChartView2.repaint();
		}
	
		XYSeries series = new XYSeries("LOOPS");
		mDataSet2.addSeries(series);
		mCurrentSeries2 = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.YELLOW);
		mRender2.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setFillPoints(true);
		renderer.setLineWidth(2.0f);
		
		mChartView2.repaint();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.train, menu);
		return true;
	}

}
