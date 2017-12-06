package com.sfzt.zlauncher_wf;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener {

	private final static String TAG = "WFUI";
	private Context mContext;
	private static final String TIME_FORMAT = "kk:mm";
    private static final String DATE_FORMAT = "%02d:%02d";
	private String mYearFormat = "yyyy-MM-dd";
	private String mWeekFormat = "EEEE";
	private static final int REFRESH_DELAY = 500;
	private SharedPreferences mPrefs;
	
	private ReversalTextView mSetting, mNavigation, mApp, mDVR, mIE, mPlayBack;
	private ReversalLinearLayout mTime, mFmt;
	private ReversalFrameLayout mMusic;
	private TextView mClock, mWeek, mDate, mMusicName, mMusicAuthor;
	private ReversalImageView mMusicPre, mMusicPlay, mMusicNext;
	
	private static final String PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
	private static final String META_CHANGED = "com.android.music.metachanged";
    private static final String QUEUE_CHANGED = "com.android.music.queuechanged";
    private static final String PLAYBACK_COMPLETE = "com.android.music.playbackcomplete";
    private static final String QUIT_PLAYBACK = "com.android.music.quitplayback";
    
	private static final String SERVICECMD = "com.android.music.musicservicecommand";
	private static final String CMDNAME = "command";
	private static final String CMDSTOP = "stop";
	private static final String CMDPLAY = "play";
	private static final String CMDPREVIOUS = "previous";
	private static final String CMDNEXT = "next";
	
	private boolean isPlaying = false;
	private String mArtist, mTrack;
	
	private final Runnable mTimeRefresher = new Runnable() {

		@SuppressLint("NewApi") @Override
		public void run() {
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
					
			final Date d = new Date();
			calendar.setTime(d);

			mClock.setText(String.format(DATE_FORMAT,
					calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND)));
			mDate.setText(DateFormat.format(mYearFormat, calendar));
			mWeek.setText(DateFormat.format(mWeekFormat, calendar));
			mHandler.postDelayed(this, REFRESH_DELAY);
		}
	};
	
	private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        	switch (msg.what) {
			case 12:
				
				break;

			default:
				break;
			}
        }
    };
    
    private void registerMusicPlayerReceiver(){
        IntentFilter filter = new IntentFilter();   
        filter.addAction(PLAYSTATE_CHANGED);
        filter.addAction(META_CHANGED);
        filter.addAction(QUEUE_CHANGED);
        filter.addAction(PLAYBACK_COMPLETE);
        filter.addAction(QUIT_PLAYBACK);
        mContext.registerReceiver(mMusicPlayerReceiver,filter);
    }

    private void unRegisterMusicPlayerReceiver(){
        unregisterReceiver(mMusicPlayerReceiver);
    }

    private MusicPlayerReceiver mMusicPlayerReceiver = new MusicPlayerReceiver();

    public class MusicPlayerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mArtist = intent.getStringExtra("artist");
            mTrack = intent.getStringExtra("track");
            isPlaying = intent.getBooleanExtra("playing", false);
            
            if(isPlaying){
            	mMusicPlay.setImageDrawable(getResources().getDrawable(R.drawable.music_pause));
            }else{
            	mMusicPlay.setImageDrawable(getResources().getDrawable(R.drawable.music_play));
            }
            mMusicName.setText(mTrack);
            mMusicAuthor.setText(mArtist);
		}
    	
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideNavigation();
		setContentView(R.layout.launcher_main);
		mContext = getApplicationContext();
		mPrefs = getSharedPreferences("wfappNavi", Context.MODE_PRIVATE);
		initUI();
	}
	
	@SuppressLint("NewApi")
	private void hideNavigation(){
		int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(uiFlags);
	}

	private void initUI() {
		mSetting = (ReversalTextView)findViewById(R.id.btn_set);
		mNavigation = (ReversalTextView)findViewById(R.id.btn_natigation);
		mApp = (ReversalTextView)findViewById(R.id.btn_app);
		mIE = (ReversalTextView)findViewById(R.id.btn_ie);
		mPlayBack = (ReversalTextView)findViewById(R.id.btn_video_list);
		mFmt = (ReversalLinearLayout)findViewById(R.id.btn_fm);
		
		mTime = (ReversalLinearLayout)findViewById(R.id.btn_time);
		mClock = (TextView)findViewById(R.id.clockText);
		mWeek = (TextView)findViewById(R.id.weekText);
		mDate = (TextView)findViewById(R.id.dataText);
		mHandler.post(mTimeRefresher);
		
		mMusic = (ReversalFrameLayout)findViewById(R.id.btn_music);
		mMusicName = (TextView)findViewById(R.id.music_name);
		mMusicAuthor = (TextView)findViewById(R.id.music_author);
		mMusicPre = (ReversalImageView)findViewById(R.id.music_prev);
		mMusicPlay = (ReversalImageView)findViewById(R.id.music_start_pause);
		mMusicNext = (ReversalImageView)findViewById(R.id.music_next);
		
		mSetting.setOnClickListener(this);
		mNavigation.setOnClickListener(this);
		mNavigation.setOnLongClickListener(this);
		mApp.setOnClickListener(this);
		mIE.setOnClickListener(this);
		mPlayBack.setOnClickListener(this);
		
		mTime.setOnClickListener(this);
		mFmt.setOnClickListener(this);
		
		mMusic.setOnClickListener(this);
		mMusicPre.setOnClickListener(this);
		mMusicPlay.setOnClickListener(this);
		mMusicNext.setOnClickListener(this);
		
	}

	@SuppressLint("NewApi") @Override
	protected void onResume() {
		registerMusicPlayerReceiver();
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		hideNavigation();
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		ComponentName componetName = null;
		switch(v.getId()){
		case R.id.btn_set:
			componetName = new ComponentName("com.android.settings","com.android.settings.Settings");
            try {
                Intent intent = new Intent();
                intent.setComponent(componetName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
            }
			break;
		case R.id.btn_natigation:
			String pkg = mPrefs.getString("packageName", null);
			String cls = mPrefs.getString("className", null);
			if((pkg == null) && cls == null){
			}else{
				componetName = new ComponentName(pkg,cls);
				try {
					Intent intent = new Intent();
	                intent.setComponent(componetName);
	                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                startActivity(intent);
				} catch (Exception e) {
				}
			}		
			break;
		case R.id.btn_app:
			try {
				Intent intent = new Intent(getApplicationContext(), ShowAllAppList.class);
				Log.d(TAG, "btn_app");
				startActivity(intent);
			} catch (Exception e) {
			}
			break;
		case R.id.btn_ie:
			componetName = new ComponentName("com.android.browser","com.android.browser.BrowserActivity");
            try {
                Intent intent = new Intent();
                intent.setComponent(componetName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
            }
			break;
		case R.id.btn_video_list:
			componetName = new ComponentName("com.mediatek.videoplayer","com.mediatek.videoplayer.MovieListActivity");
            try {
                Intent intent = new Intent();
                intent.setComponent(componetName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
            }
			break;
		case R.id.btn_time:
			try {
				Intent intent =  new Intent(Settings.ACTION_DATE_SETTINGS);  
				startActivity(intent);
			} catch (Exception e) {
			}
			break;
		case R.id.btn_fm:
			componetName = new ComponentName("jtdFM.namespace","jtdFM.namespace.JtdFMActivity");
			try {
				Intent intent = new Intent();
				intent.setComponent(componetName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (Exception e) {
			}
			break;
		case R.id.btn_music:
			componetName = new ComponentName("com.android.music","com.android.music.MusicBrowserActivity");
            try {
                Intent intent = new Intent();
                intent.setComponent(componetName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
            }
			break;
		case R.id.music_prev:
			Intent i1 = new Intent(SERVICECMD);
			i1.putExtra(CMDNAME, CMDPREVIOUS);
			sendBroadcast(i1);
			break;
		case R.id.music_start_pause:
			Intent i2 = new Intent(SERVICECMD);
			if(isPlaying){
				i2.putExtra(CMDNAME, CMDSTOP);
			}else{
				i2.putExtra(CMDNAME, CMDPLAY);
			}
			sendBroadcast(i2);
			break;
		case R.id.music_next:
			Intent i3 = new Intent(SERVICECMD);
			i3.putExtra(CMDNAME, CMDNEXT);
			sendBroadcast(i3);
			break;
		}
		
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.btn_natigation:
			try {
				Intent intent = new Intent(getApplicationContext(), AppListActivity.class);
				Log.d(TAG, "btn_natigation");
				startActivity(intent);
			} catch (Exception e) {
			}
			break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
	}

}
