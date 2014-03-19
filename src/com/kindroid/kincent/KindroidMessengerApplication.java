package com.kindroid.kincent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONObject;

import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.receiver.SmsReceiver;
import com.kindroid.kincent.service.OnStartService;
import com.kindroid.kincent.util.Constant;
import com.kindroid.security.util.ApkReportThread;
import com.kindroid.security.util.UpdateStaticsThread;
import com.kindroid.theme.ThemeRegistry;


public class KindroidMessengerApplication extends Application {

	public static SharedPreferences sh = null;

	private static PrivacyNotification mPrivacyNotification = null;

	public static ThemeRegistry mThemeRegistry;

	public static Context context;
	
	private static Timer reportTimer = new Timer();
	private static final long REPORT_PERIOD = 43200000;
	private static Timer updateProbTimer = new Timer();
	private static final long UPDATE_PROB_PERIOD = 432000000;
	
	private boolean isActive=false;
	
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = getApplicationContext();
		sh = PreferenceManager.getDefaultSharedPreferences(this);		
		Intent intent = new Intent(this, OnStartService.class);
		startService(intent);
		mPrivacyNotification = new PrivacyNotification(this);
		mThemeRegistry = ThemeRegistry.getInstance(this);
		loadProbProps();
		reportApkInfo();
		reportTimer.schedule(new TimerTaskForReport(), REPORT_PERIOD, REPORT_PERIOD);
		updateProb();
	}
	
	private void updateProb(){
		long currentTime = System.currentTimeMillis();
		long lastUpdateTime = sh.getLong(Constant.LAST_UPDATE_PROB_TIME, currentTime);
		if((lastUpdateTime == currentTime) || (currentTime - lastUpdateTime) >= UPDATE_PROB_PERIOD){
			if(lastUpdateTime == currentTime){
				lastUpdateTime = 0;
			}
			updateStatics(lastUpdateTime);
			Editor editor = sh.edit();
			editor.putLong(Constant.LAST_UPDATE_PROB_TIME, currentTime);
			editor.commit();
			updateProbTimer.schedule(new TimerTaskForUpdateProb(currentTime), UPDATE_PROB_PERIOD, UPDATE_PROB_PERIOD);
		}else{
			updateProbTimer.schedule(new TimerTaskForUpdateProb(lastUpdateTime), UPDATE_PROB_PERIOD - (currentTime - lastUpdateTime), UPDATE_PROB_PERIOD);
		}
	}
	private class TimerTaskForUpdateProb extends TimerTask {
		private long lastUpdateTime;
		public TimerTaskForUpdateProb(long lastUpdateTime){
			this.lastUpdateTime = lastUpdateTime;
		}
		public void run() {
			updateStatics(lastUpdateTime);
			long currentTime = System.currentTimeMillis();
			lastUpdateTime = currentTime;
			Editor editor = sh.edit();
			editor.putLong(Constant.LAST_UPDATE_PROB_TIME, currentTime);
			editor.commit();
		}
	}
	private void updateStatics(long lastUpdateTime){
		StringBuilder sb = new StringBuilder();
		try{
			Uri localUri = Uri.parse("content://sms/inbox");
			Cursor localCursor = getContentResolver().query(localUri, new String[] { "body", "date", "read"},  "date>='" + lastUpdateTime + "' AND read='1'",
					null,  "date desc");
			if (localCursor == null || localCursor.getCount() <= 0) {
				return;
			}
			while(localCursor.moveToNext()){
				String body = localCursor.getString(localCursor.getColumnIndex("body"));
				if(!TextUtils.isEmpty(body)){
					sb.append(body).append("\n");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(sb.length() > 0){
			UpdateStaticsThread ust = new UpdateStaticsThread(sb.toString(), 0, this, true);
			ust.start();
		}
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	private void reportApkInfo(){
		try{
			new ApkReportThread(this).start();
		}catch(Exception e){
			
		}
	}
	private class TimerTaskForReport extends TimerTask {		
		public void run() {
			reportApkInfo();
		}
	}
	
	public static SharedPreferences getSharedPrefs(Context mContext){
		if(sh == null){
			sh = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
		}
		return sh;
	}
	
	public static PrivacyNotification getPrivacyNotification(Context mContext){
		if(mPrivacyNotification == null){
			mPrivacyNotification = new PrivacyNotification(mContext);
		}
		return mPrivacyNotification;
	}
	
	
	private void loadProbProps() {
		if (SmsReceiver.spamProbProps == null) {
			new Thread() {
				public void run() {
					BufferedReader br = null;
					BufferedWriter bw = null;
					boolean mExist = true;
					try {
						File probPath = getDir("files", Context.MODE_PRIVATE);
						File probFile = new File(probPath, "prob.dat");
						if(probFile.exists()){
							br = new BufferedReader(
									new InputStreamReader(new FileInputStream(probFile), "utf-8"));
						}else{
							mExist = false;
							InputStream is = getAssets().open("prob.dat");
							br = new BufferedReader(
									new InputStreamReader(is, "utf-8"));
							bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(probFile), "utf-8"));
						}						
						
						SmsReceiver.spamProbProps = new HashMap<String, Double>();
						String line = br.readLine();
						while (line != null) {
							if (line.contains("=")) {
								String[] tokens = line.split("=");
								try {
									SmsReceiver.spamProbProps.put(tokens[0],
											Double.parseDouble(tokens[1]));
								} catch (Exception e) {

								}
							}
							if(!mExist){
								bw.write(line);
								bw.write("\n");
							}
							line = br.readLine();
						}
						br.close();
						if(bw != null){
							bw.flush();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(br != null){
							try{
								br.close();
							}catch(Exception e){
								
							}
						}
						if(bw != null){
							try{
								bw.close();
							}catch(Exception e){
								
							}
						}
					}
				}
			}.start();
		}
	}
	
	
	
	
	
	
	
	

}
