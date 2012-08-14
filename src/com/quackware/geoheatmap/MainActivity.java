package com.quackware.geoheatmap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private GpsService mBoundService;
	private boolean mIsBound;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupListeners();
    }
    
    private void setupListeners()
    {
    	((Button)findViewById(R.id.start)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				doBindService();
				
			}});
    	((Button)findViewById(R.id.stop)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				doUnbindService();
			}});
    	((Button)findViewById(R.id.map)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				
			}});
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((GpsService.GpsBinder)service).getService();
        }

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
		}
    };
    

    private void doUnbindService()
    {
    	if(mIsBound)
    	{
    		mIsBound = false;
    		unbindService(mConnection);
    	}
    }
    private void doBindService()
    {
    	Intent intent = new Intent(MainActivity.this,GpsService.class);
    	bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
    	mIsBound = true;
    }
    
}