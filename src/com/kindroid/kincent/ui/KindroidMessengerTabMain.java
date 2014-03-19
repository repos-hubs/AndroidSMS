package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost.TabSpec;


import com.kindroid.android.util.dialog.FinishActivityDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.service.UpdateProbService;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.R;
import com.kindroid.security.util.ApkReportThread;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.theme.ThemeRegistry;

public class KindroidMessengerTabMain extends TabActivity implements View.OnClickListener {
	
	private final static String TAB_DIALOGUE = "tab_dialogue";
	private final static String TAB_COLLECTION = "tab_collection";
	private final static String TAB_INTERCEPT = "tab_intercept";
	private final static String TAB_PRIVACY = "tab_privacy";
	
	private ImageView mDialogueImageView;
	private LinearLayout dialogueLayout;
	private TextView dialogueTextView;
	private ImageView mCollectionImageView;
	private LinearLayout collectionLayout;
	private TextView collectionTextView;
	private ImageView mInterceptImageView;
	private LinearLayout interceptLayout;
	private TextView interceptTextView;
	private ImageView mPrivacyImageView;
	private LinearLayout privacyLayout;
	private TextView privacyTextView;
	
	private LinearLayout dialogListLayout;
	private View classifyLayout;
	private ImageView rightArrowImageView;
	private ImageView tabControlImageView;
	private TextView smsTabTitleTextView;
	private ImageView writeMessageImageView; //写信息
	private LinearLayout tabContainerLayout;// tab container
	
	//功能操作ImageView
	private ImageView functionImageView;
	
	private Intent mDialogueIntent;
	private Intent mCollectinoIntent;
	private Intent mInterceptIntent;
	private Intent mPrivacyIntent;
	
	private TabSpec tabsDialogue;
	private TabSpec tabsCollection;
	private TabSpec tabsIntercept;
	private TabSpec tabsPrivacy;
	
	private TabHost mTabHost;
	private boolean classifyTag = false;// classify tag
	private boolean tabControlTag = false;//title tag
	public static boolean functionTag = false;//对话页面功能栏显示标记
	
	//用于Dialogue页面传递的View
	private static View view;
	
	private static KindroidMessengerTabMain instance;
	
	
	private  TextView mInterceptSumTv;
	private ImageView mCloseIv;
	private InterceptBroactReciver mReciver;
	private static RelativeLayout mInterceptBgRl;
	
	private ThemeRegistry mThemeRegistry;
	private static final String LAYOUT_FILE = "sms_messengers_main";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		mThemeRegistry = ThemeRegistry.getInstance(this);
		initContentViews();
		
		((KindroidMessengerApplication)getApplication()).setActive(true);
		

	}
	private void initContentViews(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.sms_messengers_main);
		}else{
			setContentView(contentView);
		}
		
		findViews();
		prepareIntent();
		setupIntent();
		registerInterceptReciver();
	}
	
	private void findViews() {
		mDialogueImageView = (ImageView) findViewById(R.id.dialogueImageView);
//		mDialogueImageView.setImageResource(R.drawable.tab_dialogue_icon_on);
		mDialogueImageView.setSelected(true);
		mCollectionImageView = (ImageView) findViewById(R.id.collectionImageView);
		mInterceptImageView = (ImageView) findViewById(R.id.interceptImageView);
		mPrivacyImageView = (ImageView) findViewById(R.id.privacyImageView);
		
		dialogueLayout = (LinearLayout) findViewById(R.id.dialogueLayout);
		dialogueLayout.setOnClickListener(this);
		collectionLayout = (LinearLayout) findViewById(R.id.collectionLayout);
		collectionLayout.setOnClickListener(this);
		interceptLayout = (LinearLayout) findViewById(R.id.interceptLayout);
		interceptLayout.setOnClickListener(this);
		privacyLayout = (LinearLayout) findViewById(R.id.privacyLayout);
		privacyLayout.setOnClickListener(this);
		
		dialogueTextView = (TextView) findViewById(R.id.dialogueTextView);
		dialogueTextView.setSelected(true);
		
		collectionTextView = (TextView) findViewById(R.id.collectionTextView);
		interceptTextView = (TextView) findViewById(R.id.interceptTextView);
		privacyTextView = (TextView) findViewById(R.id.privacyTextView);
		
		dialogListLayout = (LinearLayout) findViewById(R.id.smsMessageLayout);
		
		//tab imageview
//		tabControlImageView = (ImageView) findViewById(R.id.tabControlImageView);
//		tabControlImageView.setOnClickListener(this);
		//tab title
		smsTabTitleTextView = (TextView) findViewById(R.id.smsTabTitleTextView);
//		smsTabTitleTextView.setOnClickListener(this);
		
		tabContainerLayout = (LinearLayout) findViewById(R.id.tabContainerLayout);
		
		//分类布局
		classifyLayout = (View) findViewById(R.id.classifyLayout);
		
		//set linearlayout params
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int mScreenWidth = metric.widthPixels;
		
		dialogListLayout.setLayoutParams(new LayoutParams(mScreenWidth, LayoutParams.FILL_PARENT)); 
		dialogListLayout.setBackgroundResource(R.color.bg_color);
		dialogListLayout.requestLayout();
		
		//分类按钮
		rightArrowImageView = (ImageView) findViewById(R.id.classifySmsImageView);
		rightArrowImageView.setOnClickListener(this);
		rightArrowImageView.setSelected(false);
		//Dialogue 页面用到的Classify的View
		view = findViewById(R.id.classifyLayout);
		
		//功能按钮
		functionImageView = (ImageView) findViewById(R.id.smsDialogueOperationImageView);
		functionImageView.setOnClickListener(this);
		
		//写信息按钮
		writeMessageImageView = (ImageView) findViewById(R.id.smsDialogueWriteImageView);
		writeMessageImageView.setOnClickListener(this);
		
		
		mInterceptSumTv=(TextView) findViewById(R.id.intercept_sum_tv);
		
		mInterceptSumTv.setOnClickListener(this);
		
		mCloseIv=(ImageView) findViewById(R.id.close_iv);
		
		mCloseIv.setOnClickListener(this);
		mInterceptBgRl=(RelativeLayout) findViewById(R.id.intercept_bg_rl);
		
		
		setPrivacyDisplayOff();
	}
	public static void setNewDisplay(){
		try{
			instance.setPrivacyDisplayOn();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void dismissInterceptBgRl(){
		if(mInterceptBgRl.getVisibility()==View.VISIBLE){
			mInterceptBgRl.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			
			((KindroidMessengerApplication)(((Activity)(KindroidMessengerTabMain.this)).getApplication())).setActive(false);
			
		//	new FinishActivityDailog(KindroidMessengerTabMain.this, R.style.Theme_CustomDialog).showDialog();
			
		}

		return super.dispatchKeyEvent(event);
	}
	
	
	
	private void setPrivacyDisplayOff(){
		//根据用户设定更改隐私图标和文字
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(this);
		String privacyText = sh.getString(Constant.SHARE_PREFS_PRIVACY_DISPLAY_NAME, getString(R.string.sms_dialogue_title_privacy));
		privacyTextView.setText(privacyText);
//		privacyTextView.setTextColor(getResources().getColor(R.color.grey));
		privacyTextView.setSelected(false);
		int iconIndex = sh.getInt(Constant.SHARE_PREFS_PRIVACY_DISPLAY_ICON, 0);
//		int iconId = getResources().getIdentifier(
//				PrivacyChangeDisplayDialog.PRIVACY_ICON_NAME_PREF + iconIndex, "drawable",
//				getPackageName());
		String themeId = KindroidMessengerApplication.mThemeRegistry.getCurrentTheme().getId();
		int iconId = KindroidMessengerApplication.mThemeRegistry.getIconId(themeId + "_" + PrivacyChangeDisplayDialog.PRIVACY_ICON_NAME_PREF + iconIndex);
		if(iconId > 0){
//			mPrivacyImageView.setImageResource(iconId);
			mPrivacyImageView.setImageDrawable(KindroidMessengerApplication.mThemeRegistry.getIconDrawable(iconId));
		}else{
			mPrivacyImageView.setImageResource(R.drawable.green_tab_privacy_icon_off);
		}
	}
	private void setPrivacyDisplayOn(){
		//根据用户设定更改隐私图标和文字
		SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(this);
		String privacyText = sh.getString(Constant.SHARE_PREFS_PRIVACY_DISPLAY_NAME, getString(R.string.sms_dialogue_title_privacy));
		privacyTextView.setText(privacyText);
//		privacyTextView.setTextColor(getResources().getColor(R.color.white));
		privacyTextView.setSelected(true);
		int iconIndex = sh.getInt(Constant.SHARE_PREFS_PRIVACY_DISPLAY_ICON, 0);
//		int iconId = getResources().getIdentifier(
//				PrivacyChangeDisplayDialog.PRIVACY_ICON_NAME_PREF + iconIndex + "_on", "drawable",
//				getPackageName());
		String themeId = KindroidMessengerApplication.mThemeRegistry.getCurrentTheme().getId();
		int iconId = KindroidMessengerApplication.mThemeRegistry.getIconId(themeId + "_" + PrivacyChangeDisplayDialog.PRIVACY_ICON_NAME_PREF + iconIndex + "_on");
		if(iconId > 0){
//			mPrivacyImageView.setImageResource(iconId);
			mPrivacyImageView.setImageDrawable(KindroidMessengerApplication.mThemeRegistry.getIconDrawable(iconId));
		}else{
			mPrivacyImageView.setImageResource(R.drawable.green_tab_privacy_icon_on);
		}
		smsTabTitleTextView.setText(privacyText);
	}
	
	private void prepareIntent() {
		mDialogueIntent = new Intent(this, KindroidMessengerDialogueActivity.class);
		mCollectinoIntent = new Intent(this, KindroidMessengerCollectionActivity.class);
		mInterceptIntent = new Intent(this, InterceptTabMain.class);
		mPrivacyIntent = new Intent(this, KindroidMessengerPrivacyActivity.class);
	}
	
	private void setupIntent() {
		this.mTabHost = getTabHost();
		mTabHost.setup();
		tabsDialogue = mTabHost.newTabSpec("tab1").setIndicator("tab1").setContent(mDialogueIntent);
		tabsCollection = mTabHost.newTabSpec("tab2").setIndicator("tab2").setContent(mCollectinoIntent);
		tabsIntercept = mTabHost.newTabSpec("tab3").setIndicator("tab3").setContent(mInterceptIntent);
		tabsPrivacy = mTabHost.newTabSpec("tab4").setIndicator("tab4").setContent(mPrivacyIntent);
		
		mTabHost.addTab(tabsDialogue);
		mTabHost.addTab(tabsCollection);
		mTabHost.addTab(tabsIntercept);
		mTabHost.addTab(tabsPrivacy);
		
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.dialogueLayout:
			setGreyStatus();
			//显示功能按钮
			functionImageView.setVisibility(View.VISIBLE);
			//分类按钮
//			rightArrowImageView.setVisibility(View.VISIBLE);
//			dialogueTextView.setTextColor(getResources().getColor(R.color.white));
			dialogueTextView.setSelected(true);
			mDialogueImageView.setSelected(true);
//			mDialogueImageView.setImageResource(R.drawable.tab_dialogue_icon_on);
			smsTabTitleTextView.setText(getResources().getString(R.string.sms_dialogue_title_msg));
			mTabHost.setCurrentTab(0);
			tabControlTag = !tabControlTag;
			if (tabControlTag) {
				tabContainerLayout.setVisibility(View.VISIBLE);
				rightArrowImageView.setSelected(true);
//				tabControlImageView.setSelected(true);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_up_arrow_icon));
			} else {
				tabContainerLayout.setVisibility(View.GONE);
				rightArrowImageView.setSelected(false);
//				tabControlImageView.setSelected(false);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_down_arrow_icon));
			}
			break;
		case R.id.collectionLayout:
			setGreyStatus();
			//功能按钮
			functionImageView.setVisibility(View.INVISIBLE);
			//分类按钮
//			rightArrowImageView.setVisibility(View.INVISIBLE);
//			collectionTextView.setTextColor(getResources().getColor(R.color.white));
			collectionTextView.setSelected(true);
			mCollectionImageView.setSelected(true);
//			mCollectionImageView.setImageResource(R.drawable.tab_collection_icon_on);
			smsTabTitleTextView.setText(getResources().getString(R.string.sms_dialogue_title_collection));
			mTabHost.setCurrentTab(1);
			if (classifyTag) {				
				classifyLayout.setVisibility(View.GONE);
				rightArrowImageView.setSelected(false);	
				classifyTag = !classifyTag;
			}
			break;
		case R.id.interceptLayout:
			setGreyStatus();
			//功能按钮
			functionImageView.setVisibility(View.INVISIBLE);
			//分类按钮
//			rightArrowImageView.setVisibility(View.INVISIBLE);
//			interceptTextView.setTextColor(getResources().getColor(R.color.white));
			interceptTextView.setSelected(true);
			mInterceptImageView.setSelected(true);
//			mInterceptImageView.setImageResource(R.drawable.tab_interupt_icon_on);
			smsTabTitleTextView.setText(getResources().getString(R.string.sms_dialogue_title_intercept));
			mTabHost.setCurrentTab(2);
			if (classifyTag) {				
				classifyLayout.setVisibility(View.GONE);
				rightArrowImageView.setSelected(false);	
				classifyTag = !classifyTag;
			}
			break;
		case R.id.privacyLayout:
			setGreyStatus();
			//功能按钮
			functionImageView.setVisibility(View.INVISIBLE);
			//分类按钮
//			rightArrowImageView.setVisibility(View.INVISIBLE);
			setPrivacyDisplayOn();
			KindroidMessengerPrivacyActivity.timeForLock = 0;
			mTabHost.setCurrentTab(3);
			if (classifyTag) {				
				classifyLayout.setVisibility(View.GONE);
				rightArrowImageView.setSelected(false);	
				classifyTag = !classifyTag;
			}
			break;
		case R.id.classifySmsImageView:	
			tabControlTag = !tabControlTag;
			if (tabControlTag) {
				tabContainerLayout.setVisibility(View.VISIBLE);
				rightArrowImageView.setSelected(true);
//				tabControlImageView.setSelected(true);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_up_arrow_icon));
			} else {
				tabContainerLayout.setVisibility(View.GONE);
				rightArrowImageView.setSelected(false);
//				tabControlImageView.setSelected(false);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_down_arrow_icon));
			}
			break;
//		case R.id.tabControlImageView:
//			tabControlTag = !tabControlTag;
//			if (tabControlTag) {
//				tabContainerLayout.setVisibility(View.VISIBLE);
//				tabControlImageView.setSelected(true);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_up_arrow_icon));
//			} else {
//				tabContainerLayout.setVisibility(View.GONE);
//				tabControlImageView.setSelected(false);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_down_arrow_icon));
//			}
//			break;
		case R.id.smsTabTitleTextView:
			tabControlTag = !tabControlTag;
			if (tabControlTag) {
				tabContainerLayout.setVisibility(View.VISIBLE);
				tabControlImageView.setSelected(true);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_up_arrow_icon));
			} else {
				tabContainerLayout.setVisibility(View.GONE);
				tabControlImageView.setSelected(false);
//				tabControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.sms_messenger_down_arrow_icon));
			}
			break;
		case R.id.smsDialogueOperationImageView:
			functionTag = KindroidMessengerDialogueActivity.showFunctionBar(functionTag);
			break;
		case R.id.smsDialogueWriteImageView:
			Intent intent = new Intent(KindroidMessengerTabMain.this, KindroidMessengerWriteMessageActivity.class);
			startActivity(intent);
			break;
		case R.id.intercept_sum_tv:
			setGreyStatus();
			interceptTextView.setTextColor(getResources().getColor(R.color.white));
			mInterceptImageView.setSelected(true);
//			mInterceptImageView.setImageResource(R.drawable.tab_interupt_icon_on);
			smsTabTitleTextView.setText(getResources().getString(R.string.sms_dialogue_title_intercept));
			mTabHost.setCurrentTab(2);
			break;
		case R.id.close_iv:
			dismissInterceptBgRl();
			break;
		default:
			break;
		}
		
	}
	
	public static View getClassifyView() {
		return view;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("this is onActivityResult of KindroidMessengerTabMain");
	}
	
	private void setGreyStatus() {
//		dialogueTextView.setTextColor(getResources().getColor(R.color.grey));
//		collectionTextView.setTextColor(getResources().getColor(R.color.grey));
//		interceptTextView.setTextColor(getResources().getColor(R.color.grey));
		dialogueTextView.setSelected(false);
		collectionTextView.setSelected(false);
		interceptTextView.setSelected(false);
		
//		mDialogueImageView.setImageResource(R.drawable.tab_dialogue_icon_off);
		mDialogueImageView.setSelected(false);
//		mCollectionImageView.setImageResource(R.drawable.tab_collection_icon_off);
		mCollectionImageView.setSelected(false);
//		mInterceptImageView.setImageResource(R.drawable.tab_interupt_icon_off);
		mInterceptImageView.setSelected(false);
		setPrivacyDisplayOff();
	}
	
	
	
	private void registerInterceptReciver(){
		mReciver=new InterceptBroactReciver();
		IntentFilter it=new IntentFilter(com.kindroid.security.util.Constant.BROACT_UPDATE_INTERCEPT_HISTORY);
		registerReceiver(mReciver, it);
	}
	
	
	
	class InterceptBroactReciver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(getTabHost().getCurrentTab()==2){
				return;
			}
			
			HistoryNativeCursor hnc = new HistoryNativeCursor();
			hnc.setmRequestType(3);
			int sms = InterceptDataBase.get(KindroidMessengerTabMain.this).getHistoryNum(hnc);
			if(sms>0){
				mInterceptBgRl.setVisibility(View.VISIBLE);
				mInterceptSumTv.setText(String.format(getString(R.string.sms_intercept_msg), sms));
			}else{
				mInterceptBgRl.setVisibility(View.GONE);
			}
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReciver);
		
		
		Intent intent = new Intent(this, UpdateProbService.class);
		startService(intent);
	}
	
	
}
