/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.codec.binary.Base64;

import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.ui.PrivacyContactsActivity;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

import java.security.Key;
import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class SafeUtils {
	private static Key key = null;

	public static boolean validEmail(String email) {
		Pattern p = Pattern
				.compile("^[a-zA-Z0-9][a-zA-Z0-9-_.]+?@([a-zA-Z0-9]+(?:\\.[a-zA-Z0-9-_]+){1,})$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static String getEncStr(Context mContext, String strMing) {
		if (strMing == null || strMing.equals("")) {
			return "";
		}

		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		try {
			byteMing = strMing.getBytes("utf-8");
			byteMi = getEncCode(mContext, byteMing);
			strMi = new String(Base64.encodeBase64(byteMi), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	/**
	 * 　* 加密以byte[]明文输入,byte[]密文输出 　* @param byteS 　* @return 　
	 */
	private static byte[] getEncCode(Context mContext, byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, getKey(mContext));
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 　* 解密 以String密文输入,String明文输出 　* @param strMi 　* @return 　
	 */
	public static String getDesString(Context mContext, String strMi) {
		if (strMi == null || strMi.equals("")) {
			return "";
		}

		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			byteMi = Base64.decodeBase64(strMi.getBytes("utf-8"));
			byteMing = getDesCode(mContext, byteMi);
			strMing = new String(byteMing, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/**
	 * 　* 解密以byte[]密文输入,以byte[]明文输出 　* @param byteD 　* @return 　
	 */
	private static byte[] getDesCode(Context mContext, byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, getKey(mContext));
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	private static Key getKey(Context mContext) {
		if (key == null) {
			try {
				KeyGenerator _generator = KeyGenerator.getInstance("DES");
				_generator.init(new SecureRandom(mContext.getPackageName()
						.getBytes()));
				key = _generator.generateKey();
				_generator = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return key;
	}

	public static String getMD5(byte[] source) {
		String s = "";
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
										// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16
											// 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节转换成 16
											// 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
															// >>>为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static boolean checkNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo != null) {
			if (networkinfo.isConnected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @description 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
	 * @param content
	 * @return
	 */
	public static int getCharacterNum(final String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	/**
	 * @description 返回字符串里中文字或者全角字符的个数
	 * @param s
	 * @return
	 */
	public static int getChineseNum(String s) {

		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;
	}
	public static PrivacyContactDataItem getPrivacyContact(String mPhoneNumber){
		List<PrivacyContactDataItem> sPrivacyContacts = PrivacyContactsActivity.sPrivacyContactsListItems;
		PhoneType pt = ConvertUtils.getPhonetype(mPhoneNumber);
		PrivacyContactDataItem ret = null;
		if(sPrivacyContacts == null){
			return ret;
		}
		for(PrivacyContactDataItem pcdi : sPrivacyContacts){
			String pnumber = pcdi.getPhoneNumber();
			if(pnumber.contains(mPhoneNumber) || pnumber.contains(pt.getPhoneNo())){
				ret = pcdi;
				break;
			}
		}
		return ret;
	}
}
