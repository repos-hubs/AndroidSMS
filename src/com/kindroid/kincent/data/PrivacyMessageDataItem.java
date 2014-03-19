/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.data;

public class PrivacyMessageDataItem {
	public static final int TYPE_SMS = 0;
	public static final int TYPE_MMS = 1;
	public static final int RECV_TYPE_INBOX = 1;
	
	public static final int READ_STATUS_UNREAD = 0;
	public static final int READ_STATUS_READ = 1;
	//Mms type;0:sms,1:mms
	private int mType;
	private String mName;
	private String mAddress;
	private long mDate;
	private String mSubject;
	private String mBody;
	private int mRead;
	private int mRecvType;
	private int mId;
	
	public void setId(int id){
		this.mId = id;
	}
	public int getId(){
		return mId;
	}
	
	public void setRecvType(int recvType){
		this.mRecvType = recvType;
	}
	public int getRecvType(){
		return this.mRecvType;
	}
	
	public int getRead(){
		return mRead;
	}
	public void setRead(int read){
		this.mRead = read;
	}
	
	public int getType() {
		return mType;
	}
	public void setType(int mType) {
		this.mType = mType;
	}
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	
	public String getAddress() {
		return mAddress;
	}
	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}
	public long getDate() {
		return mDate;
	}
	public void setDate(long mDate) {
		this.mDate = mDate;
	}
	public String getSubject() {
		return mSubject;
	}
	public void setSubject(String mSubject) {
		this.mSubject = mSubject;
	}
	public String getBody() {
		return mBody;
	}
	public void setBody(String mBody) {
		this.mBody = mBody;
	}
	
	
}
