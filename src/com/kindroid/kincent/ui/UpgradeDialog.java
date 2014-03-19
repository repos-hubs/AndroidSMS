/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class UpgradeDialog extends Dialog implements View.OnClickListener {
	private ProgressBar mProgressBar;

	private Button downloadOkBtn;
	private Button downloadCancelBtn;

	private TextView downDes;
	private View promptLinear;
	private TextView releaseNotesText;
	private CheckBox upgradePromptRepeatCb;
	private TextView upgradeVersionTv;
	private View mProgressLinear;
	

	private int currentSize = 0;
	private int totalSize = 0;
	private boolean downloadCanceled = false;
	private boolean downloadFinished = false;
	private Context mContext;

	private String downUrl;
	private String releaseNotes;
	private String newVersionName;
	private int newVersionCode;
	
	private boolean isDownloading;
	
	private static final String LAYOUT_FILE = "upgrade_dialog";

	private Handler mProgressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mProgressBar.setMax(totalSize);
				mProgressBar.setProgress(currentSize);
				if (!downloadFinished) {
					if (downloadCanceled) {
						dismiss();
					}
				} else {
					 dismiss();
//					 Toast.makeText(mContext, R.string.upgrade_down_error_prompt, Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				dismiss();
				break;
			case 2:
				dismiss();
				break;
			case 3:
				Toast.makeText(mContext, R.string.sdcard_noexist, Toast.LENGTH_LONG).show();
				dismiss();
				break;
			case 4:
				 dismiss();
				 Toast.makeText(mContext, R.string.upgrade_down_error_prompt, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public UpgradeDialog(Context context, int theme, String downloadUrl, String releaseNots, String newVersionName
			,int newVersionCode) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		downUrl = downloadUrl;
		this.releaseNotes = releaseNots;
		this.newVersionName = newVersionName;
		this.newVersionCode = newVersionCode;
		initContentViews();
	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.upgrade_dialog);
		}else{
			setContentView(contentView);
		}
		mProgressBar = (ProgressBar)findViewById(R.id.downloadingProgress);
		downloadOkBtn = (Button)findViewById(R.id.downloadOkBtn);
		downloadCancelBtn = (Button)findViewById(R.id.downloadCancelBtn);
		downDes = (TextView)findViewById(R.id.down_des);
		promptLinear = findViewById(R.id.upgrade_prompt_linear);
		releaseNotesText = (TextView)findViewById(R.id.releasenotes_text);
		upgradePromptRepeatCb = (CheckBox)findViewById(R.id.upgrade_prompt_repeat);
		upgradeVersionTv = (TextView)findViewById(R.id.upgrade_version);
		mProgressLinear = findViewById(R.id.progress_linear);
		
		downloadOkBtn.setOnClickListener(this);
		downloadCancelBtn.setOnClickListener(this);
		
		releaseNotesText.setText(this.releaseNotes);
		upgradeVersionTv.setText(newVersionName);
		
		SharedPreferences sp = KindroidMessengerApplication.getSharedPrefs(this.mContext);
		int mShowUpgradePrompt = sp.getInt(
				com.kindroid.kincent.util.Constant.SHOW_UPGRADE_PROMPT, 1);
		if(mShowUpgradePrompt == 0){
			upgradePromptRepeatCb.setChecked(false);
		}else{
			upgradePromptRepeatCb.setChecked(true);
		}
		upgradePromptRepeatCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences sp = KindroidMessengerApplication.getSharedPrefs(mContext);
				Editor editor = sp.edit();

				if (isChecked) {
					editor.putInt(
							com.kindroid.kincent.util.Constant.SHOW_UPGRADE_PROMPT, 1);
				} else {
					editor.putInt(
							com.kindroid.kincent.util.Constant.SHOW_UPGRADE_PROMPT, 0);
					editor.putInt(
							com.kindroid.kincent.util.Constant.LAST_UPGRADE_VERSION,
							newVersionCode);
				}
				editor.commit();
			}

		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.downloadOkBtn:
			new DownloadingThread().start();
			promptLinear.setVisibility(View.GONE);
			mProgressLinear.setVisibility(View.VISIBLE);
			downDes.setText(R.string.upgrade_downloading_text);
			downDes.setVisibility(View.VISIBLE);
			upgradeVersionTv.setVisibility(View.GONE);
			downloadOkBtn.setVisibility(View.GONE);			
			break;
		case R.id.downloadCancelBtn:
			if(this.isDownloading){
				downloadCanceled = true;
				dismiss();
			}else{
				dismiss();
			}
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(downloadCanceled){
			CancelDialog cancelDialog = new CancelDialog(this.mContext, R.style.Theme_CustomDialog);
			cancelDialog.show();
		}else{
			super.onBackPressed();
		}
	}
	private class CancelDialog extends Dialog implements View.OnClickListener{
		private Button buttonOk;
		private Button buttonCancel;

		public CancelDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			initContentViews();
		}
		private void initContentViews(){
			setContentView(R.layout.upgrade_cancel_dialog);
			buttonOk = (Button)findViewById(R.id.button_ok);
			buttonCancel = (Button)findViewById(R.id.button_cancel);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.button_cancel:
				dismiss();
				break;
			case R.id.button_ok:
				downloadCanceled = true;
				dismiss();
				break;
			}
		}
		
	}

	private class DownloadingThread extends Thread {
		public void run() {
			mProgressBar.setVisibility(View.VISIBLE);
			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				mProgressHandler.sendEmptyMessage(3);
				return;
			}
			File downloadDir = new File(Environment.getExternalStorageDirectory() + Constant.DOWNLOAD_DIR);
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
			String fileName = Environment.getExternalStorageDirectory() + Constant.DOWNLOAD_DIR
					+ "/KindroidMessenger.apk";
			try {
				FileOutputStream out = new FileOutputStream(fileName, false);

				URL url = new URL(downUrl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();

				InputStream inputStream = connection.getInputStream();

				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
						out);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(
						inputStream);
				totalSize = connection.getContentLength();
				byte[] buf = new byte[4096];
				int bytesRead = 0;

				while (bytesRead >= 0 && !downloadCanceled) {
					bufferedOutputStream.write(buf, 0, bytesRead);
					bytesRead = bufferedInputStream.read(buf);
					currentSize += bytesRead;
					mProgressHandler.sendEmptyMessage(0);
				}

				bufferedOutputStream.flush();

				if ((currentSize < totalSize - 1) || (totalSize <= 0)) {
					downloadFinished = false;
					if (downloadCanceled) {
						mProgressHandler.sendEmptyMessage(1);
					} else {
						mProgressHandler.sendEmptyMessage(2);
					}
				} else {
					downloadFinished = true;
				}

				if (downloadFinished) {
					dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(fileName)),
							"application/vnd.android.package-archive");
					getContext().startActivity(intent);
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				mProgressHandler.sendEmptyMessage(4);
			} catch (ProtocolException e) {
				e.printStackTrace();
				mProgressHandler.sendEmptyMessage(4);
			} catch (IOException e) {
				mProgressHandler.sendEmptyMessage(4);
			}catch(Exception e){
				mProgressHandler.sendEmptyMessage(4);
			}
		}
	}

}
