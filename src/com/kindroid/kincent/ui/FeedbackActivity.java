/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:	heli.zhao
 * Date: 2011-12
 * Description:
 */

package com.kindroid.kincent.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.protoc.CommonProtoc;
import com.kindroid.kincent.protoc.FeedbackProtoc;
import com.kindroid.kincent.protoc.RequestProtoc;
import com.kindroid.kincent.protoc.ResponseProtoc;
import com.kindroid.kincent.protoc.ResponseProtoc.ResponseContext;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Base64Handler;
import com.kindroid.security.util.HttpRequestUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FeedbackActivity extends Activity {
	private EditText email_input;
	private EditText desp_input;
	private Spinner spinner_feedback;
	private String[] spinnerArray;
	private String feedBackType;
	private ArrayAdapter adapter;
	private ScrollView scrollView;
	private GestureDetector gestureDetector;
	private static List<CommonProtoc.FeedbackType> fdType;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private static final String LAYOUT_FILE = "feed_back";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
			setContentView(R.layout.feed_back);
		}else{
			setContentView(contentView);
		}
		
		findViews();
	}
	private void findViews(){
		fdType = new ArrayList<CommonProtoc.FeedbackType>();
		fdType.add(CommonProtoc.FeedbackType.GENERAL);
		fdType.add(CommonProtoc.FeedbackType.SEND_RECV_MSG);
		fdType.add(CommonProtoc.FeedbackType.COLLECT_MSG);
		fdType.add(CommonProtoc.FeedbackType.INTERCEPT_ANNOY);
		fdType.add(CommonProtoc.FeedbackType.PROTECT_PRIVACY);

		email_input = (EditText) findViewById(R.id.email_input);
		desp_input = (EditText) findViewById(R.id.desp_input);
		scrollView = (ScrollView) findViewById(R.id.scroview);
		scrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		gestureDetector = new GestureDetector(new MyGestureDetector());

		desp_input.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// TODO Auto-generated method stub
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;

			}
		});

//		spinner_feedback = (Spinner) findViewById(R.id.spinner_feedback);
		LinearLayout feedTypeLinear = (LinearLayout)findViewById(R.id.feed_back_type_linear);
		spinner_feedback = new Spinner(this);
		feedTypeLinear.addView(spinner_feedback, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		spinnerArray = getResources().getStringArray(
				R.array.feeback_list_string);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_feedback.setAdapter(adapter);
		spinner_feedback
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						feedBackType = (String) adapter.getItem(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						feedBackType = null;
					}

				});

		final Button button_ok = (Button) findViewById(R.id.button_ok);
		button_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (feedBackType == null) {
					Toast.makeText(FeedbackActivity.this,
							R.string.feedback_type_input_prompt,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (desp_input.getText().toString().trim().equals("")) {
					Toast.makeText(FeedbackActivity.this,
							R.string.feedback_desp_input_prompt,
							Toast.LENGTH_LONG).show();
					return;
				}
				String email = email_input.getText().toString().trim();
				if (!valid(email)) {
					Toast.makeText(FeedbackActivity.this,
							R.string.privacy_email_invalid_prompt,
							Toast.LENGTH_LONG).show();
					return;
				}
				button_ok.setClickable(false);
				submitFeedBack();
				button_ok.setClickable(true);
			}
		});
	}

	private boolean valid(String email) {
		if (TextUtils.isEmpty(email)) {
			return true;
		}
		Pattern pattern = Pattern
				.compile("^[a-zA-Z0-9][a-zA-Z0-9-_.]+?@([a-zA-Z0-9]+(?:\\.[a-zA-Z0-9-_]+){1,})$");
		return (pattern.matcher(email)).matches();
	}

	private void submitFeedBack() {
		if (!SafeUtils.checkNetwork(this)) {
			Toast.makeText(FeedbackActivity.this, R.string.feedback_fail_text,
					Toast.LENGTH_LONG).show();
			return;
		}
		
		FeedbackProtoc.Feedback.Builder feedback = FeedbackProtoc.Feedback
				.newBuilder();
		String email = email_input.getText().toString().trim();
		if (!email.equals("")) {
			feedback.setEmail(email);
		}
		String desp = desp_input.getText().toString().trim();
		feedback.setContent(desp);
		try{
			feedback.setBoard(Build.BOARD);
			feedback.setBrand(Build.BRAND);
			feedback.setFingerprint(Build.FINGERPRINT);
			feedback.setManuer(Build.MANUFACTURER);
			TelephonyManager telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imei=telephonyManager.getDeviceId();
			feedback.setImei(imei);
			feedback.setModel(Build.MODEL);
			feedback.setOsversion(Build.VERSION.RELEASE);
			feedback.setSdkversion(String.valueOf(Build.VERSION.SDK_INT));
		}catch(Exception e){
			
		}
		String packageName = getApplication().getApplicationInfo().packageName;
		PackageManager pm = getPackageManager();
		try{
			PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			feedback.setVersion(String.valueOf(pi.versionCode));
		}catch(Exception e){
			
		}

		feedback.setFeedbackType(fdType.get(spinner_feedback
				.getSelectedItemPosition()));

		FeedbackProtoc.FeedbackRequest.Builder feedbackRequ = FeedbackProtoc.FeedbackRequest
				.newBuilder();
		feedbackRequ.setFeedback(feedback);

		RequestProtoc.RequestContext.Builder context = RequestProtoc.RequestContext
				.newBuilder();
		//context.setAuthToken(mToken);

		RequestProtoc.Request.Builder request = RequestProtoc.Request
				.newBuilder();
		request.setFeedbackRequest(feedbackRequ);
		request.setContext(context);

		Base64Handler base64 = new Base64Handler();
		BufferedReader mReader = null;
		
		int ret = -1;
		try {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("request", base64.encode(request.build().toByteArray()));
			Map<String, Object> res = HttpRequestUtil.postData(
					com.kindroid.security.util.Constant.FEEDBACK_URL, param, 10000);
			ResponseProtoc.Response resp = ResponseProtoc.Response.parseFrom(base64.decodeBuffer(new String((byte[])res.get("Content"))));
			
			if (resp.hasContext()) {
				ResponseContext rc = resp.getContext();
				ret = rc.getResult();
			}

		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			if (mReader != null) {
				try {
					mReader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (ret == 0) {
			feedSucc();
		} else{
			Toast.makeText(FeedbackActivity.this, R.string.feedback_fail_prompt,
					Toast.LENGTH_LONG).show();
		}
		
	}

	private void feedSucc() {
		Toast.makeText(FeedbackActivity.this, R.string.feedback_succ_text,
				Toast.LENGTH_LONG).show();
		desp_input.setText("");
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			desp_input.scrollTo(0, (int) distanceY);
			
			return true;

		}

		@Override
		public void onLongPress(MotionEvent e) { // Do nothing }
			
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		try {
			int location[] = new int[2];
			desp_input.getLocationOnScreen(location);
			Rect r = new Rect();
			r.left = location[0];
			r.top = location[1];
			r.right = r.left + desp_input.getWidth();
			r.bottom = r.top + desp_input.getHeight();
			if (r.contains((int) ev.getX(), (int) ev.getY())) {
				desp_input.requestFocus();
				return desp_input.onTouchEvent(ev);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.dispatchTouchEvent(ev);
	}

}
