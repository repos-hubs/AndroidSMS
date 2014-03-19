/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:zili.chen,jingmiao.li,heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kindroid.android.model.CollectCategory;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneUtils {
	private static List<String> defaultPhones = new ArrayList<String>();
	private static SmsMessageSender sender;
	static {
		defaultPhones.add("10086");
		defaultPhones.add("10060");
		defaultPhones.add("10011");
		defaultPhones.add("10012");
		defaultPhones.add("10010");
		defaultPhones.add("10000");
		defaultPhones.add("10050");
		defaultPhones.add("10070");
		defaultPhones.add("10015");
		defaultPhones.add("13800138000");
		defaultPhones.add("10001");

		defaultPhones.add("11158");
		defaultPhones.add("11185");
		defaultPhones.add("95105366");
		defaultPhones.add("4008861888");
		defaultPhones.add("4008209868");
		defaultPhones.add("4008208388");
		defaultPhones.add("4008111111");
		defaultPhones.add("4006789000");
		defaultPhones.add("4008108000");

		defaultPhones.add("95555");
		defaultPhones.add("95588");
		defaultPhones.add("95566");
		defaultPhones.add("95533");
		defaultPhones.add("95512");
		defaultPhones.add("95599");
		defaultPhones.add("95559");
		defaultPhones.add("95528");
		defaultPhones.add("95561");
		defaultPhones.add("95595");
		defaultPhones.add("95501");
		defaultPhones.add("95568");
		defaultPhones.add("95558");
		defaultPhones.add("95508");
		defaultPhones.add("95577");
		defaultPhones.add("962888");
		defaultPhones.add("95516");
		defaultPhones.add("96169");
		defaultPhones.add("95580");
		defaultPhones.add("95500");
		defaultPhones.add("95502");
		defaultPhones.add("95505");
		defaultPhones.add("95510");
		defaultPhones.add("95511");
		defaultPhones.add("95515");
		defaultPhones.add("95518");
		defaultPhones.add("95519");
		defaultPhones.add("95522");
		defaultPhones.add("95567");
		defaultPhones.add("95569");
		defaultPhones.add("95585");
		defaultPhones.add("95589");
		defaultPhones.add("95590");
		defaultPhones.add("95596");
		defaultPhones.add("95105768");

	}

	public static boolean isDefaultPhone(String number) {
		return defaultPhones.contains(number);
	}

	/**
	 * check phoneNumber
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	static public com.android.internal.telephony.ITelephony getITelephony(
			TelephonyManager telMgr) throws Exception {
		Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod(
				"getITelephony");
		getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
		return (com.android.internal.telephony.ITelephony) getITelephonyMethod
				.invoke(telMgr);
	}

	/**
	 * 发送信息
	 * 
	 * @param context
	 * @param address
	 *            收件人电话号码
	 * @param timestamp
	 * @param body
	 *            发送文本
	 * @param messageType
	 *            信息类型
	 * @return
	 */
	public static boolean sendMessages(Context context, String address,
			String body, int messageType) {
		long threadId = MessengerUtils
				.findThreadIdFromAddress(context, address);
		// Send new message
		if (null == sender) {
			sender = new SmsMessageSender(context, new String[] { address },
					body, threadId);
		}
		sender.setThreadId(threadId);
		String[] addresses = { address };
		sender.setAddress(addresses);
		sender.setMessageBody(body);
		sender.setTimestamp();
		
		return sender.sendMessage();
	}

	/**
	 * 取List中的字段使以逗号分隔的字符串返回
	 * 
	 * @param nameList
	 * @return
	 */
	public static String formatList(List nameList) {
		String nameStr = "";
		boolean first = true;
		for (int i = 0; i < nameList.size(); i++) {
			if (first) {
				nameStr = nameList.get(i) + "";
				first = false;
			} else {
				nameStr += "," + nameList.get(i);
			}
		}
		return nameStr;
	}

	/**
	 * 
	 * @param nameList
	 * @return
	 */
	public static String formatCategoryList(List<CollectCategory> categoryList) {
		String nameStr = "";
		boolean first = true;
		for (int i = 0; i < categoryList.size(); i++) {
			CollectCategory category = categoryList.get(i);
			if (first) {
				nameStr = "\"" + category.getmName() + "\"";
				first = false;
			} else {
				nameStr += "," + "\"" + category.getmName() + "\"";
			}
		}
		return nameStr;
	}

	/**
	 * 取array中的字段使以逗号分隔的字符串返回
	 * 
	 * @param array
	 * @return
	 */
	public static String formatArray(Long[] array) {
		String nameStr = "";
		boolean first = true;
		for (int i = 0; i < array.length; i++) {
			if (first) {
				nameStr = array[i] + "";
				first = false;
			} else {
				nameStr += "," + array[i];
			}
		}
		return nameStr;
	}

	/**
	 * 获取归属地
	 * 
	 * @param addressStr
	 * @param ctx
	 * @return
	 */
	public static String getAddressLocation(String addressStr, Context ctx) {
		String fromStr = "";
		String tmep_str = ConvertUtils.getAddrForPhone(addressStr);

		if (null != tmep_str) {
			fromStr = tmep_str;
		} else {
			PhoneType type = ConvertUtils.getPhonetype(addressStr);
			if (type.getPhonetype() == 2) {
//				fromStr = ConvertUtils.getCityNameByCode(ctx,
//						ConvertUtils.getCode(ctx, type.getPhoneNo()));
			} else if (type.getPhonetype() == 1) {
				fromStr = ConvertUtils.getPhoneCity(ctx, type.getPhoneNo());
			}
		}
		
		return fromStr;
	}
}
