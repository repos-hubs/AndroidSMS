/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.data;

import java.io.Serializable;

public class PrivacyCallogDataItem implements Serializable{
	public static final int CALL_IN_TYPE = 0;
	public static final int CALL_OUT_TYPE = 1;
	//type for call in or call out;0:call in,1:call out
	private int mType;
	private String mName;
	private String mPhoneNumber;
	private long mDate;
	private int mBlockedType;
	
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
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}
	public long getDate() {
		return mDate;
	}
	public void setDate(long mDate) {
		this.mDate = mDate;
	}
	public int getBlockedType() {
		return mBlockedType;
	}
	public void setBlockedType(int mBlockedType) {
		this.mBlockedType = mBlockedType;
	}
	
	
	

}
