/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.data;

import android.content.Context;

import java.io.Serializable;

public class PrivacyMessagesItem implements Serializable{
	private Context mContext;
	private String mPhoneNumber;
	private String mContactName;
	private int mMsgNum;
	private String mLastMsg;
	private long mLastDate;
	private int mRead;
	
	public void setPhoneNumber(String number){
		this.mPhoneNumber = number;
	}
	public String getPhoneNumber(){
		return this.mPhoneNumber;
	}
	
	public void setRead(int read){
		this.mRead = read;
	}
	public int getRead(){
		return this.mRead;
	}
	
	public PrivacyMessagesItem(Context context){
		this.mContext = context;
	}
	
	public String getContactName() {
		return mContactName;
	}
	public void setContactName(String mContactName) {
		this.mContactName = mContactName;
	}
	public int getMsgNum() {
		return mMsgNum;
	}
	public void setMsgNum(int mMsgNum) {
		this.mMsgNum = mMsgNum;
	}
	public String getLastMsg() {
		return mLastMsg;
	}
	public void setLastMsg(String lastMsg) {
		this.mLastMsg = lastMsg;
	}
	public long getLastDate() {
		return mLastDate;
	}
	public void setLastDate(long lastDate) {
		this.mLastDate = lastDate;
	}
	
	
}
