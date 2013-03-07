package com.learnerstechlab.bluetoothtester;

import java.util.Formatter;
import java.util.Locale;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;

import android.text.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class BluetoothTestActivity extends Activity {
	private static final String LOGTAG = "BluetoothTestActivity";
	private static Context mContext;
	private static AudioManager mAudioManager;
	private static long mFirstEventTime = 0;
	private static TextView textView;
	private static ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_bluetooth_test);
        textView = (TextView) findViewById(R.id.output_view);
        scrollView = (ScrollView) findViewById(R.id.sv);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName()));
        
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	IntentFilter f = new IntentFilter();
        f.addAction("bt_tester_action");
        registerReceiver(mStatusListener, new IntentFilter(f));
    }
    
    @Override
    public void onDestroy () {
    	if (mAudioManager == null) {
    	mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName()));
    	mAudioManager = null;
    	}
    	super.onDestroy();  	
    }
    
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			long eventtime = intent.getLongExtra("eventtime", 0);
			if (mFirstEventTime == 0) {
				mFirstEventTime = eventtime;
			}
			eventtime = eventtime - mFirstEventTime;
			int keycode = intent.getIntExtra("keycode", -1);
			int action = intent.getIntExtra("action", -1);
			textView.append(
					makeTimeString(eventtime)
					+" :"+actionToString(action)
					+ " :"+keycodeToString(keycode) 
					+ "\n");
			scrollView.scrollTo(0, scrollView.getBottom());
		}
    	
    };
    
    private String keycodeToString (int keycode) {
    	switch (keycode) {
    	case -1:
    		return "-----";
    	case 85:
    		return "PLAY/PAUSE";
    	case 86:
    		return "STOP";
    	case 87:
    		return "NEXT";
    	case 88:
    		return "PREV";
    	case 89:
    		return "REW";
    	case 90:
    		return "FFWD";
    	case 126:
    		return "PLAY";
    	case 127:
    		return "PAUSE";
    	case 128:
    		return "CLOSE";
    	case 129:
    		return "EJECT";
    	case 130:
    		return "REC";
    	default:
    		return String.valueOf(keycode);
    	}
    }
    
    private String actionToString (int action) {
    	switch (action) {
    	case -1:
    		return "xx";
    	case 0:
    		return "DN";
    	case 1:
    		return "UP";
    	case 2:
    		return "ML";
    	default:
    		return String.valueOf(action);
    	}
    }
    
    public void ClearText(View v) {
    	textView.setText("");
    	mFirstEventTime = 0;
    }
    
    public void ExitApp(View v) {
    	finish();
    }
    
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[6];

    public static String makeTimeString(long msecs) {
    	long secs = msecs/1000;
        String durationformat = mContext.getString(R.string.millisecformat);
        
        /* Provide multiple arguments so the format can be changed easily
         * by modifying the xml.
         */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;
        timeArgs[1] = secs / 60;
        timeArgs[2] = (secs / 60) % 60;
        timeArgs[3] = secs;
        timeArgs[4] = secs % 60;
        timeArgs[5] = msecs % 1000;

        return sFormatter.format(durationformat, timeArgs).toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bluetooth_test, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.menu_copy) {
    		ClipboardManager clipboard = (ClipboardManager)
            getSystemService(Context.CLIPBOARD_SERVICE); 
    		clipboard.setText(textView.getText());
    	}
    	return true;
    }
}
