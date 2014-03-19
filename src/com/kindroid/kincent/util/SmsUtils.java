package com.kindroid.kincent.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class SmsUtils {
	private static Map<String, Double> spamProbProps = null;
	public static final String SIGNATURE_CONTENT = "signature_content";
	public static final String SEND_APP_NAME_TAG = "send_app_name_tag";
	public static final String MESSAGE_SIGNATURE_SWITCH = "signature_switch";
	
	//保存签名
	public static void setSignatureContent(String signatureContent, Context ctx) {
		SharedPreferences.Editor share = ctx.getSharedPreferences(Constant.SETTING_INFO, 2).edit();
		share.putString(SIGNATURE_CONTENT, signatureContent);
		share.commit();
	}
	//获取签名
	public static String getSignatureContent(Context ctx) {
		SharedPreferences share = ctx.getSharedPreferences(Constant.SETTING_INFO, 1);
		String signature = share.getString(SIGNATURE_CONTENT, null);
		return signature;
	}
	//设置签名开关
	public static void setSignatureSwitch(boolean sigSwitch, Context ctx) {
		SharedPreferences.Editor share = ctx.getSharedPreferences(Constant.SETTING_INFO, 2).edit();
		share.putBoolean(MESSAGE_SIGNATURE_SWITCH, sigSwitch);
		share.commit();
	}
	//取得签名开关
	public static boolean getSignatureSwitch(Context ctx) {
		SharedPreferences share = ctx.getSharedPreferences(Constant.SETTING_INFO, 1);
		boolean sigSwitch = share.getBoolean(MESSAGE_SIGNATURE_SWITCH, false);
		return sigSwitch;
	}
	//设置发送App标志
	public static void setSendAppTag(boolean appTag, Context ctx) {
		SharedPreferences.Editor share = ctx.getSharedPreferences(Constant.SETTING_INFO, 2).edit();
		share.putBoolean(SEND_APP_NAME_TAG, appTag);
		share.commit();
	}
	//取得了送App标志
	public static boolean getSendAppTag(Context ctx) {
		SharedPreferences share = ctx.getSharedPreferences(Constant.SETTING_INFO, 1);
		boolean appTag = share.getBoolean(SEND_APP_NAME_TAG, false);
		return appTag;
	}
	
	public static boolean isSpamProbPropsNull(){
		return (spamProbProps == null);
	}
	public static boolean isSpamProbPropsContainWord(String word){
		return spamProbProps.containsKey(word);
	}
	public static double getSpamWordProbValue(String word){
		return spamProbProps.get(word);
	}
	public static void updateProbProps(Map<String, Double> probMap){
		if(probMap.size() >= spamProbProps.size()){
			spamProbProps = probMap;
		}
	}
	
	public synchronized static Map<String, Double> loadProbProps(final Context mContext){
		if (spamProbProps == null) {
			spamProbProps = new HashMap<String, Double>();
			new Thread() {
				public void run() {
					BufferedReader br = null;
					BufferedWriter bw = null;
					boolean mExist = true;
					try {
						File probPath = mContext.getDir("files", Context.MODE_PRIVATE);
						File probFile = new File(probPath, "prob.dat");
						if(probFile.exists()){
							br = new BufferedReader(
									new InputStreamReader(new FileInputStream(probFile), "utf-8"));
						}else{
							mExist = false;
							InputStream is = mContext.getAssets().open("prob.dat");
							br = new BufferedReader(
									new InputStreamReader(is, "utf-8"));
							bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(probFile), "utf-8"));
						}						
						
						String line = br.readLine();
						while (line != null) {
							if (line.contains("=")) {
								String[] tokens = line.split("=");
								try {
									spamProbProps.put(tokens[0],
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
		return spamProbProps;
	}
	
	// GENERAL_PUNCTUATION 判断中文的“号   
	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号   
	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号  
	private static final boolean isChinese(char c) {   
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);   
	    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS   
	            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS   
	            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A   
	            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION   
	            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION   
	            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {   
	        return true;   
	    }   
	    return false;   
	}   

	/**
	 * 判断是否中文
	 * @param strName
	 * @return
	 */
	public static final boolean isChinese(String strName) {   
	    char[] ch = strName.toCharArray();   
	    for (int i = 0; i < ch.length; i++) {   
	        char c = ch[i];   
	        if (isChinese(c)) {   
	            return true;   
	        }   
	    }   
	    return false;   
	}  

}
