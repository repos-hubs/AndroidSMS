/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.kincent.receiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Set;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.Contacts.People;

import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKTokenizer;
import org.wltea.analyzer.dic.Dictionary;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.android.util.dialog.SureRestorySmsDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivacyMessageDataItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.notification.PrivacyNotification;
import com.kindroid.kincent.service.NotificationService;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.kincent.ui.PrivacySettingsActivity;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;

import com.kindroid.security.util.Constant;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;

import com.kindroid.security.util.PhoneType;
import com.kindroid.security.util.UpdateStaticsThread;

public class SmsReceiver extends BroadcastReceiver {

	Context context;
	public static Map<String, Double> spamProbProps = null;
	//private static List<String> defaultPhones = new ArrayList<String>();
	private static final String keyWords = "赠送,精装,贵宾专线,赏车,订车,枪支,要账,暗杀,免抵押,绝对震撼,多重优惠,订座,公关,窃听,办证,豪宅,机打,别墅,本公司有,特价,预定,各类发票,火爆进行,限时抢,限量,开盘,实惠,便宜,特卖会,回馈,火热进行,缺现金,中奖,抽奖,投资,公寓,楼盘,赠好礼,折上折,发钱,好礼,现房,独享,聚划算,清仓,抢购,欲购从速,抵用券,促销,BocIncom,Bocedcom,boczucom,boczbcom,bocgmtk,10086qqcom,qq9com,139gdX6ws6,3ggqqin;经理,生,小姐,本,提供,优惠,折,万,欢迎,电,售,款,询,咨,汇,钱,账,帐,拨,回复,热线,户名,查收,申,奖,地,购,房,最,邀";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;

		String content = "";
		String address = "";
		if(intent.getAction().equals(com.kindroid.kincent.util.Constant.KINDROID_SECURITY_INTERCEPT_BROADCAST)){
			content = intent.getStringExtra(com.kindroid.kincent.util.Constant.KINDROID_SECURITY_INTERCEPT_SMS_BODY);
			address = intent.getStringExtra(com.kindroid.kincent.util.Constant.KINDROID_SECURITY_INTERCEPT_SMS_ADDRESS);			
		}else{
			Bundle bundle = intent.getExtras();
	
			SmsMessage[] msgs = null;
			if (bundle == null)
				return;
	
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				try {
					address = msgs[i].getOriginatingAddress();
					content += msgs[i].getMessageBody().toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		if (address.length() > 11) {
			address = address.substring(address.length() - 11);
		}
		boolean isTrue = checkRemoteSecurity(content, address);		
		if (isTrue) {
			abortBroadcast();
			Intent mIntent = new Intent(com.kindroid.kincent.util.Constant.KINCENT_FOR_REMOTE_SECURITY_BROADCAST);
			mIntent.putExtra(com.kindroid.kincent.util.Constant.KINCENT_FOR_REMOTE_SECURITY_SMS_BODY, content);
			mIntent.putExtra(com.kindroid.kincent.util.Constant.KINDROID_REMOTE_SECURITY_NUMBER, address);
			context.sendBroadcast(mIntent);			
			return;
		}
		// intercept for private
		boolean isPrivateSms = checkForPrivate(context, address, content,
				intent);
		// intercept for sms intercept
		if (!isPrivateSms) {
			boolean isIntercept = checkBlockingRules(address, content, intent);
			if (!isIntercept) {
				NotificationService.showType = 2;
				context.startService(new Intent(context,
						NotificationService.class));
				NotificationService.content = content;

				HistoryNativeCursor hnc = new HistoryNativeCursor();
				hnc.setmOriginDate(InterceptDataBase.DF_DATE.format(new Date()));
				hnc.setmAddress(address);
				hnc.setmRead(0);
				hnc.setmBody(content);
				hnc.setmRequestType(3);
				SureRestorySmsDailog.InsertSmstoBox(context, hnc);
				
				
				 intent = new Intent(Constant.BROACT_UPDATE_MESSAGE_DIALOG);
				
				context.sendBroadcast(intent);				

				abortBroadcast();
			}

		}

	}
	private boolean checkRemoteSecurity(String content, String address) {
		boolean isTrue = false;
		SharedPreferences prefs = KindroidMessengerApplication.getSharedPrefs(context);
		boolean kindSecurityFuc = prefs.getBoolean(
				Constant.SHAREDPREFERENCES_REMOTESECURITY, false);
		if (!kindSecurityFuc)
			return isTrue;		
		String mobile = prefs.getString(
				Constant.SHAREDPREFERENCES_SAFEMOBILENUMBER, "");
		if (mobile.equals(""))
			return isTrue;

		if (!mobile.equals(address)){
			PhoneType pt = ConvertUtils.getPhonetype(address);
			if(!mobile.equals(pt.getPhoneNo())){
				return isTrue;
			}
		}

		String strContent[] = content.split("#");
		if (strContent.length != 3)
			return isTrue;		
		
		return true;
	}

	private boolean checkForPrivate(Context context, String address,
			String content, Intent intent) {
		boolean ret = false;
		PhoneType ph = ConvertUtils.getPhonetype(address);
		List<PrivacyContactDataItem> contacts = PrivacyContactsActivity.sPrivacyContactsListItems;
		long currentTime = System.currentTimeMillis();
		if (contacts == null || contacts.size() == 0) {
			return false;
		} else {
			for (int i = 0; i < contacts.size(); i++) {
				PrivacyContactDataItem mItem = contacts.get(i);
				String mPhoneNumber = mItem.getPhoneNumber();
				if (mPhoneNumber.contains(ph.getPhoneNo())) {
					abortBroadcast();
					ContentValues values = new ContentValues();
					values.put(PrivateDBHelper.PrivateMsg.NAME, SafeUtils
							.getEncStr(context, mItem.getContactName()));
					values.put(PrivateDBHelper.PrivateMsg.ADDRESS,
							SafeUtils.getEncStr(context, mPhoneNumber));
					values.put(PrivateDBHelper.PrivateMsg.BODY,
							SafeUtils.getEncStr(context, content));
					values.put(PrivateDBHelper.PrivateMsg.DATE,
							currentTime);
					values.put(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE,
							PrivacyMessageDataItem.RECV_TYPE_INBOX);
					values.put(PrivateDBHelper.PrivateMsg.READ,
							PrivacyMessageDataItem.READ_STATUS_UNREAD);
					PrivacyDBUtils.getInstance(context).addMessage(context,
							values);
					
					
					// update notification
					SharedPreferences sh = KindroidMessengerApplication
							.getSharedPrefs(context);
					boolean notiforNew = sh
							.getBoolean(
									com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_NOTIFY_FOR_NEW,
									true);
					if (notiforNew) {
						updateNotification(context);

						boolean vibrate = sh.getBoolean(com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_VIBRATE_NOTIFY, true);
						if(vibrate){
							Vibrator  vibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
							vibrator.vibrate( new long[]{100,10,100,1000},-1);
						}
						if (sh.contains(com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_MSG_RINGTONE)) {
							String ringTone = sh
									.getString(
											com.kindroid.kincent.util.Constant.SHARE_PREFS_PRIVACY_MSG_RINGTONE,
											PrivacySettingsActivity.DEFAULT_MSG_NOTIFY_RINGTONE);
							try {
								MediaPlayer mp = new MediaPlayer();
								mp.reset();
								mp.setDataSource(context, Uri.parse(ringTone));
								mp.prepare();
								mp.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					ret = true;
					//send broadcast
					Intent mIntent = new Intent(com.kindroid.kincent.util.Constant.PRIVACY_SMS_BROADCAST_ACTION);
					mIntent.putExtra(PrivateDBHelper.PrivateMsg.NAME, mItem.getContactName());
					mIntent.putExtra(PrivateDBHelper.PrivateMsg.ADDRESS, mPhoneNumber);
					mIntent.putExtra(PrivateDBHelper.PrivateMsg.BODY, content);
					mIntent.putExtra(PrivateDBHelper.PrivateMsg.DATE, currentTime);
					mIntent.putExtra(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE, PrivacyMessageDataItem.RECV_TYPE_INBOX);
					mIntent.putExtra(PrivateDBHelper.PrivateMsg.READ, PrivacyMessageDataItem.READ_STATUS_UNREAD);				
					
					context.sendBroadcast(mIntent);

					break;
				}
			}
		}
		return ret;
	}

	private void updateNotification(Context context) {
		PrivacyNotification mPrivacyNotification = KindroidMessengerApplication
				.getPrivacyNotification(context);
		mPrivacyNotification.updateMmsNotification();
	}

	private boolean checkBlockingRules(String address, String content,
			Intent intent) {

		int night_model = ConvertUtils.betweenNightModel(context);
		if (night_model == 0) {
			int model = KindroidMessengerApplication.getSharedPrefs(context)
					.getInt(Constant.SHAREDPREFERENCES_BLOCKINGRULES, 1);
			return blockRulesHandle(model, address, content, intent);

		} else {
			return blockRulesHandle(night_model, address, content, intent);

		}

	}

	private boolean blockRulesHandle(int model, String address, String content,
			Intent intent) {
		boolean isIntercept = false;
		PhoneType ph = ConvertUtils.getPhonetype(address);
		if (ph.getPhoneNo() == null) {
			return isIntercept;
		}

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
					abortBroadcast();
					isIntercept = true;
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
					/*
					 * try{ if(!hasContact && address.startsWith("12520")){
					 * break; }
					 * 
					 * }catch(Exception e){
					 * 
					 * }
					 */
					if (hasContact) {
						updateStatics(content, context);
						return isIntercept;
					} else if (PhoneUtils.isDefaultPhone(address)) {
						return isIntercept;
					} else if (containKeyWork(content)) {
						InterceptDataBase.get(context)
								.insertMmsPhone(3, address,
										Calendar.getInstance(), content, 0, "");
						abortBroadcast();
						isIntercept = true;
						sendBroact(context, 3);
					} else {
						IKTokenizer tokenizer = new IKTokenizer(
								new StringReader(content), true);
						double normalSum;
						double spamSum;
						List<Double> p = new ArrayList<Double>();
						try {
							while (tokenizer.incrementToken()) {
								CharTermAttribute ta = tokenizer
										.getAttribute(CharTermAttribute.class);
								String v = ta.toString();

								if (!spamProbProps.containsKey(v)) {
									continue;
								}
								if (spamProbProps.get(v) == 0) {
									continue;
								}
								if (spamProbProps.get(v) >= 1
										&& !keyWords.contains(v)) {
									continue;
								}
								p.add(spamProbProps.get(v));
							}
							if (p.size() > 0) {
								double pup = p.get(0);
								double pdown = 1 - p.get(0);
								for (int j = 1; j < p.size(); j++) {
									pup = pup * p.get(j);
									pdown = pdown * (1 - p.get(j));
								}
								if (pup + pdown > 0) {
									double pmail = pup / (pup + pdown);

									if (pmail != Double.NaN) {
										if (pmail > 0.75) {
											InterceptDataBase
													.get(context)
													.insertMmsPhone(
															3,
															address,
															Calendar.getInstance(),
															content, 0, "");
											abortBroadcast();
											isIntercept = true;
											sendBroact(context, 3);

										} else {
											updateStatics(content, context);
										}
									}
								}
							}
						} catch (Throwable e) {
							e.printStackTrace();

						}

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
					abortBroadcast();
					isIntercept = true;
					InterceptDataBase.get(context).insertMmsPhone(3, address,
							Calendar.getInstance(), content, 0, "");
					sendBroact(context, 3);
				} else {
					updateStatics(content, context);
				}
			}

			break;
		case 3:
			NativeCursor n = new NativeCursor();
			n.setmRequestType(2);
			n = ConvertUtils.isBlackOrWhiteList(context, n, ph);
			if (!n.ismIsExists()) {
				abortBroadcast();
				isIntercept = true;
				InterceptDataBase.get(context).insertMmsPhone(3, address,
						Calendar.getInstance(), content, 0, "");
				sendBroact(context, 3);

			} else {
				updateStatics(content, context);
			}
			break;
		case 4:
			break;
		case 5:
			abortBroadcast();
			isIntercept = true;
			InterceptDataBase.get(context).insertMmsPhone(3, address,
					Calendar.getInstance(), content, 0, "");
			sendBroact(context, 3);

			break;

		default:
			break;
		}
		return isIntercept;

	}

	private void updateStatics(String content, Context context) {
		UpdateStaticsThread ust = new UpdateStaticsThread(content, 0, context);
		ust.start();
	}

	public static void sendBroact(Context context, int type) {
		// int num = KindroidMessengerApplication.sh.getInt(
		// Constant.INTERCEPT_NOTIFY_INFO, 1);
		// if (num == 1) {
		//
		// }
		NotificationService.showType = 1;
		context.startService(new Intent(context, NotificationService.class));
		Intent intent = new Intent(Constant.BROACT_UPDATE_INTERCEPT_HISTORY);
		intent.putExtra("sms_or_phone", type);
		context.sendBroadcast(intent);

	}

	private boolean containKeyWork(String content) {
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

	private void sendSMS(final String phoneNumber, final String message,
			Context context) {
		final SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);

	}

	final LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

		}
	};

}
