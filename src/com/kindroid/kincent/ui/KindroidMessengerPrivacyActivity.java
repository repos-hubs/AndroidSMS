package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;
import com.kindroid.security.util.ApkReportThread;
import com.kindroid.theme.ThemeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KindroidMessengerPrivacyActivity extends TabActivity implements
		View.OnClickListener {
	private TabHost mTabHost;
	private TabSpec tabsMessages;
	private TabSpec tabsPhones;
	private TabSpec tabsContacts;
	private TabSpec tabsSettings;

	private View mMessagesLinear;
	private View mPhonesLinear;
	private View mContactsLinear;
	private View mSettingsLinear;

	private ImageView mTabMessagesImage;
	private ImageView mTabPhonesImage;
	private ImageView mTabContactsImage;
	private ImageView mTabSettingsImage;

	private View mLockLinear;
	private View mMainLinear;
	private EditText mLockPwdEt;

	private int mCurrentTab;

	public static long timeForLock;

	public static final long PERIOD_FOR_LOCK = 60000;
	public static final int TIME_BEFORE_CHECK = 5000;

	private static Timer lockTimer = new Timer();
	private static KindroidMessengerPrivacyActivity instance;

	private static int mScreenHeight;

	private boolean checkSucc = false;

	private static final String LAYOUT_FILE = "privacy_main";

	private static List<AutoLockActivityInterface> mLockedActivity;
	private int mPwdErrorNum;

	private static long timeBeiginCheck;
	private static long beginCheckTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		instance = this;
		mPwdErrorNum = 0;
		initContentViews();
	}

	private void initContentViews() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.privacy_main);
		} else {
			setContentView(contentView);
		}
		findViews();
		bindListeners();
		addTabs();

	}

	public static void registLockedActivity(AutoLockActivityInterface act) {
		if (mLockedActivity == null) {
			mLockedActivity = new ArrayList<AutoLockActivityInterface>();
		}
		mLockedActivity.add(act);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		beginCheckTime = System.currentTimeMillis();
		checkLock();
	}

	private class TimerTaskForCheckLock extends TimerTask {
		private Context mContext;

		TimerTaskForCheckLock(Context context) {
			mContext = context;
		}

		public void run() {
			beginCheckTime = System.currentTimeMillis() + 10000;
			try {
				mHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				lock();
				break;
			}
		}

	};
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if (s.length() == 4) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
				// InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(mLockPwdEt.getWindowToken(), 0); // myEdit是你的EditText对象
				mLockPwdEt.clearComposingText();
				mLockPwdEt.clearFocus();
				mLockPwdEt.setText("");
				SharedPreferences sh = KindroidMessengerApplication
						.getSharedPrefs(KindroidMessengerPrivacyActivity.this);
				String savePwd = sh.getString(Constant.SHARE_PREFS_PRIVACY_PWD,
						"");

				if (savePwd.equals(SafeUtils.getMD5(s.toString().getBytes()))) {
					mLockLinear.setVisibility(View.GONE);
					// mMainLinear.setVisibility(View.VISIBLE);
					getTabHost().setVisibility(View.VISIBLE);
					timeForLock = System.currentTimeMillis();
					checkSucc = true;
					mPwdErrorNum = 0;
					updateTimerTask();
				} else {
					if (mPwdErrorNum > 1) {
						mLockLinear.setVisibility(View.GONE);
						// mMainLinear.setVisibility(View.GONE);
						getTabHost().setVisibility(View.GONE);
						checkSucc = false;
						// mPwdErrorNum = 0;
					} else {
						System.out.println("mPwdErrorNum :" + mPwdErrorNum);
						Toast.makeText(KindroidMessengerPrivacyActivity.this,
								R.string.privacy_unlock_pwd_error,
								Toast.LENGTH_SHORT).show();
						mPwdErrorNum = mPwdErrorNum + 1;
						System.out.println("mPwdErrorNum after ++ :"
								+ mPwdErrorNum);
					}
				}

			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		timeForLock = System.currentTimeMillis();
		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(this);
		if (sh.contains(Constant.SHARE_PREFS_PRIVACY_PWD)) {
			updateTimerTask();
		}
		return super.dispatchTouchEvent(ev);
	}

	private void lock() {
		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(this);
		if (sh.contains(Constant.SHARE_PREFS_PRIVACY_PWD)) {
			mLockLinear.setVisibility(View.VISIBLE);
			// mMainLinear.setVisibility(View.GONE);
			getTabHost().setVisibility(View.GONE);

			try {
				((AutoLockActivityInterface) getCurrentActivity()).onLock();
				if (mLockedActivity != null && !mLockedActivity.isEmpty()) {
					for (int i = 0; i < mLockedActivity.size(); i++) {
						AutoLockActivityInterface aai = mLockedActivity
								.get(i);
						aai.onLock();
					}
					mLockedActivity.clear();
				}
			} catch (Exception e) {

			}
		}
	}

	private void checkLock() {
		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(this);
		if (sh.contains(Constant.SHARE_PREFS_PRIVACY_PWD)) {
			if (checkTimeElapsed(this)) {
				mLockLinear.setVisibility(View.VISIBLE);
				// mMainLinear.setVisibility(View.GONE);
				getTabHost().setVisibility(View.GONE);

				try {
					((AutoLockActivityInterface) getCurrentActivity()).onLock();
					if (mLockedActivity != null && !mLockedActivity.isEmpty()) {
						for (int i = 0; i < mLockedActivity.size(); i++) {
							AutoLockActivityInterface aai = mLockedActivity
									.get(i);
							aai.onLock();
						}
						mLockedActivity.clear();
					}
				} catch (Exception e) {

				}
				// mLockPwdEt.addTextChangedListener(textWatcher);
			} else if (checkSucc) {
				mLockLinear.setVisibility(View.GONE);
				// mMainLinear.setVisibility(View.VISIBLE);
				getTabHost().setVisibility(View.VISIBLE);
			} else {
				mLockLinear.setVisibility(View.VISIBLE);
				// mMainLinear.setVisibility(View.GONE);
				getTabHost().setVisibility(View.GONE);
				try {
					((AutoLockActivityInterface) getCurrentActivity()).onLock();
					if (mLockedActivity != null && !mLockedActivity.isEmpty()) {
						for (int i = 0; i < mLockedActivity.size(); i++) {
							AutoLockActivityInterface aai = mLockedActivity
									.get(i);
							aai.onLock();
						}
						mLockedActivity.clear();
					}
				} catch (Exception e) {

				}
				mLockPwdEt.addTextChangedListener(textWatcher);
			}
		} else {
			mLockLinear.setVisibility(View.GONE);
			// mMainLinear.setVisibility(View.GONE);
			getTabHost().setVisibility(View.GONE);
			try {
				((AutoLockActivityInterface) getCurrentActivity()).onLock();
			} catch (Exception e) {

			}
			showInputPwdDialog();
		}
	}

	public static void updateLockTask() {
		instance.updateTimerTask();
	}

	private void updateTimerTask() {
		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(this);
		int mAutoLockTime = sh.getInt(
				Constant.SHARE_PREFS_PRIVACY_AUTO_LOCK_TIME, 0);
		long delayTime = PERIOD_FOR_LOCK;
		switch (mAutoLockTime) {
		case 0:
			delayTime = PERIOD_FOR_LOCK;
			break;
		case 1:
			delayTime = 2 * PERIOD_FOR_LOCK;
			break;
		case 2:
			delayTime = 5 * PERIOD_FOR_LOCK;
			break;
		case 3:
			delayTime = 10 * PERIOD_FOR_LOCK;
			break;
		default:
			delayTime = Long.MAX_VALUE;
		}
		try {
			lockTimer.cancel();
			lockTimer = new Timer();
			lockTimer.schedule(new TimerTaskForCheckLock(this), delayTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showInputPwdDialog() {
		InputPwdDialog inputDialog = new InputPwdDialog(this,
				R.style.Theme_CustomDialog);
		inputDialog.show();
	}

	private class InputPwdDialog extends Dialog implements View.OnClickListener {
		private EditText pwdEt;
		private EditText comfirmEt;
		private EditText emailEt;

		private Button mButtonOk;
		private Button mButtonCancel;

		private Context mContext;

		private static final String DIALOG_LAYOUT_FILE = "privacy_set_pwd_dialog";

		public InputPwdDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
			this.mContext = context;
			initContentViews();
		}

		private void initContentViews() {
			View contentView = null;
			try {
				contentView = KindroidMessengerApplication.mThemeRegistry
						.inflate(DIALOG_LAYOUT_FILE);
			} catch (Exception e) {
				contentView = null;
			}
			if (contentView == null) {
				this.setContentView(R.layout.privacy_set_pwd_dialog);
			} else {
				this.setContentView(contentView);
			}

			pwdEt = (EditText) findViewById(R.id.privacy_pwd_et);
			comfirmEt = (EditText) findViewById(R.id.privacy_pwd_confirm_et);
			emailEt = (EditText) findViewById(R.id.privacy_pwd_email_et);

			mButtonOk = (Button) findViewById(R.id.button_ok);
			mButtonCancel = (Button) findViewById(R.id.button_cancel);
			mButtonOk.setOnClickListener(this);
			mButtonCancel.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button_cancel:
				dismiss();
				break;
			case R.id.button_ok:
				String pwd = pwdEt.getText().toString().trim();
				String confirmPwd = comfirmEt.getText().toString().trim();
				String email = emailEt.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(mContext, R.string.pwd_empty_prompt,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (pwd.length() != 4) {
					Toast.makeText(mContext, R.string.privacy_pwd_length_error,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (!pwd.equals(confirmPwd)) {
					Toast.makeText(mContext, R.string.pwd_confirm_not_same,
							Toast.LENGTH_LONG).show();
					return;
				}
				if (!TextUtils.isEmpty(email)) {
					if (!SafeUtils.validEmail(email)) {
						Toast.makeText(KindroidMessengerPrivacyActivity.this,
								R.string.privacy_email_invalid_prompt,
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				SharedPreferences sh = KindroidMessengerApplication
						.getSharedPrefs(mContext);
				Editor editor = sh.edit();
				editor.putString(Constant.SHARE_PREFS_PRIVACY_PWD,
						SafeUtils.getMD5(pwd.getBytes()));

				if (!TextUtils.isEmpty(email)) {
					editor.putString(Constant.SHARE_PREFS_PRIVACY_EMAIL, email);
				}
				editor.commit();
				timeForLock = System.currentTimeMillis();
				mLockLinear.setVisibility(View.GONE);
				// mMainLinear.setVisibility(View.VISIBLE);
				getTabHost().setVisibility(View.VISIBLE);
				checkSucc = true;
				dismiss();
				break;
			}
		}

	}

	private static boolean checkTimeElapsed(Context context) {
		boolean ret = false;
		SharedPreferences sh = KindroidMessengerApplication
				.getSharedPrefs(context);
		int mAutoLockTime = sh.getInt(
				Constant.SHARE_PREFS_PRIVACY_AUTO_LOCK_TIME, 0);
		long timeForAutoLock = PERIOD_FOR_LOCK;
		
		if (mAutoLockTime == 0) {
			if (beginCheckTime - timeForLock > PERIOD_FOR_LOCK) {
				ret = true;
			} else {
				ret = false;
			}
		} else if (mAutoLockTime == 1) {
			if (beginCheckTime - timeForLock > 2 * PERIOD_FOR_LOCK) {
				ret = true;
			} else {
				ret = false;
			}
		} else if (mAutoLockTime == 2) {
			if (beginCheckTime - timeForLock > 5 * PERIOD_FOR_LOCK) {
				ret = true;
			} else {
				ret = false;
			}
		} else if (mAutoLockTime == 3) {
			if (beginCheckTime - timeForLock > 10 * PERIOD_FOR_LOCK) {
				ret = true;
			} else {
				ret = false;
			}
		} else {
			ret = false;
		}

		return ret;
	}

	private void findViews() {
		mTabHost = this.getTabHost();
		mMessagesLinear = findViewById(R.id.privacy_messages_linear);
		mMessagesLinear.setSelected(true);
		mPhonesLinear = findViewById(R.id.privacy_phones_linear);
		mPhonesLinear.setSelected(false);
		mContactsLinear = findViewById(R.id.privacy_contacts_linear);
		mContactsLinear.setSelected(false);
		mSettingsLinear = findViewById(R.id.privacy_settings_linear);
		mSettingsLinear.setSelected(false);
		mTabMessagesImage = (ImageView) findViewById(R.id.privacy_tab01_image);
		mTabMessagesImage.setSelected(true);
		mTabPhonesImage = (ImageView) findViewById(R.id.privacy_tab02_image);
		mTabPhonesImage.setSelected(false);
		mTabContactsImage = (ImageView) findViewById(R.id.privacy_tab03_image);
		mTabContactsImage.setSelected(false);
		mTabSettingsImage = (ImageView) findViewById(R.id.privacy_tab04_image);
		mTabSettingsImage.setSelected(false);

		mLockLinear = findViewById(R.id.privacy_lock_linear);
		mMainLinear = findViewById(R.id.privacy_main_linear);
		mLockPwdEt = (EditText) findViewById(R.id.privacy_lock_pwd_et);
		// mLockPwdEt.onWindowFocusChanged(true);

	}

	private void bindListeners() {
		mMessagesLinear.setOnClickListener(this);
		mPhonesLinear.setOnClickListener(this);
		mContactsLinear.setOnClickListener(this);
		mSettingsLinear.setOnClickListener(this);
		mLockPwdEt.addTextChangedListener(textWatcher);
	}

	private void addTabs() {
		Intent mMessagesIntent = new Intent(this, PrivacyMessagesActivity.class);
		tabsMessages = mTabHost.newTabSpec("tab1").setIndicator("tab1")
				.setContent(mMessagesIntent);
		Intent mPhonesIntent = new Intent(this, PrivacyPhonesActivity.class);
		tabsPhones = mTabHost.newTabSpec("tab2").setIndicator("tab2")
				.setContent(mPhonesIntent);
		Intent mContactsIntent = new Intent(this, PrivacyContactsActivity.class);
		tabsContacts = mTabHost.newTabSpec("tab3").setIndicator("tab3")
				.setContent(mContactsIntent);
		Intent mSettingsIntent = new Intent(this, PrivacySettingsActivity.class);
		tabsSettings = mTabHost.newTabSpec("tab4").setIndicator("tab4")
				.setContent(mSettingsIntent);

		mTabHost.addTab(tabsMessages);
		mTabHost.addTab(tabsPhones);
		mTabHost.addTab(tabsContacts);
		mTabHost.addTab(tabsSettings);
		mCurrentTab = mMessagesLinear.getId();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		imm.hideSoftInputFromWindow(mLockPwdEt.getWindowToken(), 0); // myEdit是你的EditText对象
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mCurrentTab == v.getId()) {
			return;
		}
		mCurrentTab = v.getId();
		switch (mCurrentTab) {
		case R.id.privacy_messages_linear:
			mTabHost.setCurrentTab(0);
			// mTabMessagesImage.setImageResource(R.drawable.privacy_tab01_on);
			// mTabPhonesImage.setImageResource(R.drawable.privacy_tab02);
			// mTabContactsImage.setImageResource(R.drawable.privacy_tab03);
			// mTabSettingsImage.setImageResource(R.drawable.privacy_tab04);
			// mMessagesLinear.setBackgroundResource(R.drawable.privacy_tab_bg_left);
			// mPhonesLinear.setBackgroundResource(0);
			// mContactsLinear.setBackgroundResource(0);
			// mSettingsLinear.setBackgroundResource(0);
			mMessagesLinear.setSelected(true);
			mPhonesLinear.setSelected(false);
			mContactsLinear.setSelected(false);
			mSettingsLinear.setSelected(false);
			mTabMessagesImage.setSelected(true);
			mTabPhonesImage.setSelected(false);
			mTabContactsImage.setSelected(false);
			mTabSettingsImage.setSelected(false);
			break;
		case R.id.privacy_phones_linear:
			mTabHost.setCurrentTab(1);
			// mTabMessagesImage.setImageResource(R.drawable.privacy_tab01);
			// mTabPhonesImage.setImageResource(R.drawable.privacy_tab02_on);
			// mTabContactsImage.setImageResource(R.drawable.privacy_tab03);
			// mTabSettingsImage.setImageResource(R.drawable.privacy_tab04);
			// mMessagesLinear.setBackgroundResource(0);
			// mPhonesLinear.setBackgroundResource(R.drawable.privacy_tab_bg_center);
			// mContactsLinear.setBackgroundResource(0);
			// mSettingsLinear.setBackgroundResource(0);
			mMessagesLinear.setSelected(false);
			mPhonesLinear.setSelected(true);
			mContactsLinear.setSelected(false);
			mSettingsLinear.setSelected(false);
			mTabMessagesImage.setSelected(false);
			mTabPhonesImage.setSelected(true);
			mTabContactsImage.setSelected(false);
			mTabSettingsImage.setSelected(false);
			break;
		case R.id.privacy_contacts_linear:
			mTabHost.setCurrentTab(2);
			// mTabMessagesImage.setImageResource(R.drawable.privacy_tab01);
			// mTabPhonesImage.setImageResource(R.drawable.privacy_tab02);
			// mTabContactsImage.setImageResource(R.drawable.privacy_tab03_on);
			// mTabSettingsImage.setImageResource(R.drawable.privacy_tab04);
			// mMessagesLinear.setBackgroundResource(0);
			// mPhonesLinear.setBackgroundResource(0);
			// mContactsLinear.setBackgroundResource(R.drawable.privacy_tab_bg_center);
			// mSettingsLinear.setBackgroundResource(0);
			mMessagesLinear.setSelected(false);
			mPhonesLinear.setSelected(false);
			mContactsLinear.setSelected(true);
			mSettingsLinear.setSelected(false);
			mTabMessagesImage.setSelected(false);
			mTabPhonesImage.setSelected(false);
			mTabContactsImage.setSelected(true);
			mTabSettingsImage.setSelected(false);
			break;
		case R.id.privacy_settings_linear:
			mTabHost.setCurrentTab(3);
			// mTabMessagesImage.setImageResource(R.drawable.privacy_tab01);
			// mTabPhonesImage.setImageResource(R.drawable.privacy_tab02);
			// mTabContactsImage.setImageResource(R.drawable.privacy_tab03);
			// mTabSettingsImage.setImageResource(R.drawable.privacy_tab04_on);
			// mMessagesLinear.setBackgroundResource(0);
			// mPhonesLinear.setBackgroundResource(0);
			// mContactsLinear.setBackgroundResource(0);
			// mSettingsLinear.setBackgroundResource(R.drawable.privacy_tab_bg_right);
			mMessagesLinear.setSelected(false);
			mPhonesLinear.setSelected(false);
			mContactsLinear.setSelected(false);
			mSettingsLinear.setSelected(true);
			mTabMessagesImage.setSelected(false);
			mTabPhonesImage.setSelected(false);
			mTabContactsImage.setSelected(false);
			mTabSettingsImage.setSelected(true);
			break;

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lockTimer.cancel();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PrivacySettingsActivity.REQUEST_CODE_RINGTONE
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = data
					.getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				SharedPreferences sh = KindroidMessengerApplication
						.getSharedPrefs(this);
				Editor editor = sh.edit();
				editor.putString(Constant.SHARE_PREFS_PRIVACY_MSG_RINGTONE,
						uri.toString());
				editor.commit();
			}

		}
	}

}
