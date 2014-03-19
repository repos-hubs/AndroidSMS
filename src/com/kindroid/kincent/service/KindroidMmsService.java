package com.kindroid.kincent.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKTokenizer;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivacyMessageDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.util.SmsUtils;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.InterceptDataBase;
import com.kindroid.security.util.PhoneType;
import com.kindroid.security.util.UpdateStaticsThread;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.SmsMessage;

public class KindroidMmsService extends Service {
	
	
	public static Map<String, Double> spamProbProps = null;
	private static final String keyWords = "赠送,精装,贵宾专线,赏车,订车,枪支,要账,暗杀,免抵押,绝对震撼,多重优惠,订座,公关,窃听,办证,豪宅,机打,别墅,本公司有,特价,预定,各类发票,火爆进行,限时抢,限量,开盘,实惠,便宜,特卖会,回馈,火热进行,缺现金,中奖,抽奖,投资,公寓,楼盘,赠好礼,折上折,发钱,好礼,现房,独享,聚划算,清仓,抢购,欲购从速,抵用券,促销,BocIncom,Bocedcom,boczucom,boczbcom,bocgmtk,10086qqcom,qq9com,139gdX6ws6,3ggqqin;经理,生,小姐,本,提供,优惠,折,万,欢迎,电,售,款,询,咨,汇,钱,账,帐,拨,回复,热线,户名,查收,申,奖,地,购,房,最,邀";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		String content = "";
		String address = "";
		Bundle bundle = intent.getExtras();

		SmsMessage[] msgs = null;
		if (bundle == null)
			return;
		if (address.length() > 11) {
			address = address.substring(address.length() - 11);
		}
		Object[] pdus = (Object[]) bundle.get("pdus");
		msgs = new SmsMessage[pdus.length];
		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			try{
				address = msgs[i].getOriginatingAddress();	
				content += msgs[i].getMessageBody().toString();							
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//intercept for private
		boolean isPrivateSms = checkForPrivate(this, address, content, intent);		
		//intercept for sms intercept
		if(!isPrivateSms){
			checkBlockingRules(this, address, content, intent);
		}
		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	private boolean checkForPrivate(Context context, String address, String content, Intent intent) {
		boolean ret = false;
		PhoneType ph = ConvertUtils.getPhonetype(address);
		List<PrivacyContactDataItem> contacts = PrivacyContactsActivity.sPrivacyContactsListItems;
		if(contacts == null || contacts.size() == 0){
			return false;
		}else{
			for(int i = 0; i < contacts.size(); i++){
				PrivacyContactDataItem mItem = contacts.get(i);
				String mPhoneNumber = mItem.getPhoneNumber();
				if(mPhoneNumber.contains(ph.getPhoneNo())){
					ContentValues values = new ContentValues();
					values.put(PrivateDBHelper.PrivateMsg.NAME, SafeUtils.getEncStr(context, mItem.getContactName()));
					values.put(PrivateDBHelper.PrivateMsg.ADDRESS, SafeUtils.getEncStr(context, mPhoneNumber));
					values.put(PrivateDBHelper.PrivateMsg.BODY, SafeUtils.getEncStr(context, content));
					values.put(PrivateDBHelper.PrivateMsg.DATE, System.currentTimeMillis());
					values.put(PrivateDBHelper.PrivateMsg.MMS_TYPE, PrivacyMessageDataItem.RECV_TYPE_INBOX);
					values.put(PrivateDBHelper.PrivateMsg.READ, PrivacyMessageDataItem.READ_STATUS_UNREAD);
					PrivacyDBUtils.getInstance(context).addMessage(context, values);
					//update notification
					SharedPreferences sh = KindroidMessengerApplication.getSharedPrefs(this);
					boolean notiforNew = sh.getBoolean(com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_NOTIFY_FOR_NEW, false);
					if(notiforNew){
						updateNotification();
						boolean vibrate = sh.getBoolean(com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_VIBRATE_NOTIFY, false);
						if(vibrate){
							Vibrator  vibrator = (Vibrator)getApplication().getSystemService(Service .VIBRATOR_SERVICE);
							vibrator.vibrate( new long[]{100,10,100,1000},-1);
							vibrator.cancel();
						}
					}
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	private void updateNotification(){
		PrivacyNotification mPrivacyNotification = KindroidMessengerApplication.getPrivacyNotification(this);
		mPrivacyNotification.updateMmsNotification();
	}
	private void checkBlockingRules(Context context, String address, String content, Intent intent) {

		int night_model = ConvertUtils.betweenNightModel(context);
		if (night_model == 0) {
			int model = KindroidMessengerApplication.getSharedPrefs(context).getInt(
					Constant.SHAREDPREFERENCES_BLOCKINGRULES, 1);
			blockRulesHandle(context, model, address, content, intent);

		} else {
			blockRulesHandle(context, night_model, address, content, intent);

		}

	}
	private void blockRulesHandle(Context context, int model, String address, String content, Intent intent) {
		if(PhoneUtils.isDefaultPhone(address)){
			return;
		}
		PhoneType ph = ConvertUtils.getPhonetype(address);
		if (ph.getPhoneNo() == null) {
			return;
		}
		if(PhoneUtils.isDefaultPhone(ph.getPhoneNo())){
			return;
		}
		
		boolean isIntercept=false;
		
		switch (model) {
		case 1:
			NativeCursor nc = new NativeCursor();
			nc.setmRequestType(1);
			nc = ConvertUtils.isBlackOrWhiteList(context, nc, ph);
			if (nc.ismIsExists() && nc.ismSmsStatus()) {
				nc = new NativeCursor();
				nc.setmRequestType(2);
				nc = ConvertUtils.isBlackOrWhiteList(context, nc, ph);
				if (!nc.ismIsExists()) {
					isIntercept=true;					
					InterceptDataBase.get(context).insertMmsPhone(3, address,
							Calendar.getInstance(), content, 0, "");
					sendBroact(context, 3);
				}
			} else {
				nc = new NativeCursor();
				nc.setmRequestType(2);
				nc = ConvertUtils.isBlackOrWhiteList(context, nc, ph);
				if (!nc.ismIsExists()) {
					boolean hasContact = ConvertUtils.hasNumInContact(
							ph.getPhoneNo(), context);
				
					if (!hasContact && containKeyWork(context,content)) {

						InterceptDataBase.get(context)
								.insertMmsPhone(3, address,
										Calendar.getInstance(), content, 0, "");
						isIntercept=true;	
						sendBroact(context, 3);
					}else if(!hasContact){
				
						IKTokenizer tokenizer = new IKTokenizer(new StringReader(content), true);
						double normalSum;
						double spamSum;
						List<Double> p = new ArrayList<Double>();
						try{
							if(spamProbProps == null){
								spamProbProps = SmsUtils.loadProbProps(context);
							}
							while(tokenizer.incrementToken())  
					        {
								CharTermAttribute ta = tokenizer.getAttribute(CharTermAttribute.class); 
					            String v = ta.toString();
					            
					            if(!spamProbProps.containsKey(v)){
					            	continue;
					            }
					            if(spamProbProps.get(v) == 0){
					            	continue;
					            }
					            if(spamProbProps.get(v) >= 1 && !keyWords.contains(v)){
					            	continue;
					            }
					            p.add(spamProbProps.get(v));
					        }
							if(p.size() > 0){
								double pup = p.get(0);
								double pdown = 1 - p.get(0);
								for(int j = 1; j < p.size(); j++){
									pup = pup * p.get(j);
									pdown = pdown * (1 - p.get(j));
								}
								if(pup + pdown > 0){
									double pmail = pup / (pup + pdown);
									
									if(pmail != Double.NaN){
										if(pmail > 0.75){											
											InterceptDataBase.get(context)
												.insertMmsPhone(3, address,
													Calendar.getInstance(), content, 0, "");	
											isIntercept=true;	
											sendBroact(context, 3);
											
										}else{
											updateStatics(content, context);
										}
									}
						        }
							}
						}catch(Throwable e){
							e.printStackTrace();
							
						}
						
						
					}else{
						updateStatics(content, context);
					}
				}
			}
			break;
		case 2:
			boolean hasContact = ConvertUtils.hasNumInContact(ph.getPhoneNo(),
					context);
			if (!hasContact) {
				NativeCursor n = new NativeCursor();
				n.setmRequestType(2);
				n = ConvertUtils.isBlackOrWhiteList(context, n, ph);
				if (!n.ismIsExists()) {
					isIntercept=true;	
					InterceptDataBase.get(context).insertMmsPhone(3, address,
							Calendar.getInstance(), content, 0, "");
					sendBroact(context, 3);
				}else{
					updateStatics(content, context);
				}
			}

			break;
		case 3:
			NativeCursor n = new NativeCursor();
			n.setmRequestType(2);
			n = ConvertUtils.isBlackOrWhiteList(context, n, ph);
			if (!n.ismIsExists()) {
				isIntercept=true;	
				InterceptDataBase.get(context).insertMmsPhone(3, address,
						Calendar.getInstance(), content, 0, "");
				sendBroact(context, 3);

			}else{
				updateStatics(content, context);
			}
			break;
		case 4:
			break;
		case 5:
			isIntercept=true;	
			InterceptDataBase.get(context).insertMmsPhone(3, address,
					Calendar.getInstance(), content, 0, "");
			sendBroact(context, 3);

			break;

		default:
			break;
		}
		
		if(!isIntercept){
//			Intent intent_c=new Intent();
//			Bundle bundle=intent.getExtras();
//			bundle.putBoolean("checkOver", true);
//			intent_c.putExtras(bundle);
//			intent_c.setAction("android.provider.Telephony.SMS_RECEIVED");
//			intent.setClass(this, null);
			
	
			sendBroadcast(intent);
		}
		
		

	}
	private boolean containKeyWork(Context context, String content) {
		boolean mHasKeyWork = false;

		if (content.trim().length() == 0) {
			return mHasKeyWork;
		}
		content = content.replace(" ", "");
		try {
			Cursor c = InterceptDataBase.get(context).selectKeyWordList(1);
			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {
					String keyword = c.getString(c
							.getColumnIndex(InterceptDataBase.KEYWORDZH));
					if (content.contains(keyword)) {
						mHasKeyWork = true;
						return mHasKeyWork;
					}
				}
			}
			if (c != null) {
				c.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mHasKeyWork;
	}
	private void updateStatics(String content, Context context){
		UpdateStaticsThread ust = new UpdateStaticsThread(content, 0, context);
		ust.start();
	}
	public static void sendBroact(Context context, int type) {
		int num = KindroidMessengerApplication.getSharedPrefs(context).getInt(
				Constant.INTERCEPT_NOTIFY_INFO, 1);
		if (num == 1) {
			NotificationService.showType = 1;
			context.startService(new Intent(context, NotificationService.class));
		}
		Intent intent = new Intent(Constant.BROACT_UPDATE_INTERCEPT_HISTORY);
		intent.putExtra("sms_or_phone", type);
		context.sendBroadcast(intent);
	}

}
