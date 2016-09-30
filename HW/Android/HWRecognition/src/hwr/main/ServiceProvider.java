package hwr.main;

import java.io.IOException;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

public class ServiceProvider extends SAAgent {

	public static final String Tag = "Service Provider";
	public static final int SERVICE_CONNECTION_RESULT_OK = 0;
	public static final int HELLOACCESSORY_CHANNEL_ID = 333;
	HashMap<Integer, ProviderConnection> mConnectionsMap = null;
	private final IBinder mBinder = new LocalBinder();
	
	Context context1;
	
	public class LocalBinder extends Binder {
		public ServiceProvider getService() {
			return ServiceProvider.this;
		}
	}
	
	public ServiceProvider() {
		super(Tag,ProviderConnection.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.d(Tag,"onFindPeerAgent arg1= "+arg1);
	}

	@Override
	public void onServiceConnectionResponse(SASocket thisConnection, int result) {
		// TODO Auto-generated method stub
		if(result==CONNECTION_SUCCESS){
			if(thisConnection!=null){
				ProviderConnection myConnection =(ProviderConnection)thisConnection;
				if(mConnectionsMap==null){
					mConnectionsMap=new HashMap<Integer, ServiceProvider.ProviderConnection>();
				}
				myConnection.mConnectionId=(int)(System.currentTimeMillis() &255);
				Log.d(Tag,"onServiceConnection connectionID= " +myConnection.mConnectionId);
				mConnectionsMap.put(myConnection.mConnectionId,myConnection);
				Intent i=new Intent("testBroadCast");
				i.putExtra("message",new String("OK"));
				getApplicationContext().sendBroadcast(i);
				Toast.makeText(getBaseContext(),"Connection is Established", Toast.LENGTH_SHORT).show();
			}
		}
		else if (result == CONNECTION_ALREADY_EXIST) {
			Log.e(Tag, "SASocket object is null");
		} else{
			Log.e(Tag,"onServiceConnectionResponse result error: "+result);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	//Send Data to Gear
	public class ProviderConnection extends SASocket {
		GearDataReceiver gearReceiver;
		private int mConnectionId;
		public ProviderConnection() {
			super(ProviderConnection.class.getName());
			// TODO Auto-generated constructor stub
			gearReceiver=new GearDataReceiver();
			IntentFilter filter=new IntentFilter("myCommand");
			registerReceiver(gearReceiver, filter);
		}

		@Override
		public void onError(int channelId, String errorString, int error) {
			// TODO Auto-generated method stub
			Log.e(Tag, "Connection is not Alive ERROR: " + errorString + " " + error);
		}

		@Override
		public void onReceive(int channelId, byte[] data) {
			// TODO Auto-generated method stub
			Log.d("po", new String(data));
			Intent i=new Intent("testBroadCast");
			i.putExtra("message",new String(data));
			getApplicationContext().sendBroadcast(i);
		}

		@Override
		public void onServiceConnectionLost(int errorCode) {
			// TODO Auto-generated method stub
			Log.e(Tag, "onServiceConectionLost  for peer = " + mConnectionId + "error code =" + errorCode);
			if (mConnectionsMap != null) {
				mConnectionsMap.remove(mConnectionId);
			}
		}
		public void sendNotification(final String notification){
			final ProviderConnection uHandler=mConnectionsMap.get(mConnectionId);
			if(uHandler == null){
				Log.e(Tag,"Error, can not get Handler");
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
						uHandler.send(HELLOACCESSORY_CHANNEL_ID,notification.getBytes());
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(Tag,"OnCreate of Smart View Provider Service");
		SA mAccessory=new SA();
		try{
			mAccessory.initialize(this);
		}catch(SsdkUnsupportedException e){
			
		}catch(Exception e1){
			Log.e(Tag,"Cant Initialize Accessory package.");
			e1.printStackTrace();
			stopSelf();
		}
	}
	
	@Override
	public void onServiceConnectionRequested(SAPeerAgent arg0) {
		// TODO Auto-generated method stub
		acceptServiceConnectionRequest(arg0);
	}
	
	public class GearDataReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("myCommand")){
				Log.i("Test","Command");
				String data=intent.getStringExtra("command");
				notifyGear(data);
			}
		}
	}
	
	public void notifyGear(String notification) {
        for(ProviderConnection provider : mConnectionsMap.values()) {
            provider.sendNotification(notification);
        }
    }
}
