/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.android.model;

import java.io.Serializable;

public class NativeCursor implements Serializable {
	/**
	 * requestType is 1 or 2，blacklist or whitelist
	 * */
	private int mRequestType;
	
	
	/**
	 * black white list item click to show diff dialog example del ,edit,more
	 * */
	private int mShowDialogStyle;
	private String mContactName;
	private String mPhoneNum;
	private int mPhoneType;
	private boolean mSmsStatus;
	private boolean mRingStatus;
	
	private boolean mIsExists=false;
	
	private int mId;
	
	private boolean mIsSelected=false;
	
	
	
	public boolean ismIsSelected() {
		return mIsSelected;
	}
	public void setmIsSelected(boolean mIsSelected) {
		this.mIsSelected = mIsSelected;
	}
	public int getmId() {
		return mId;
	}
	public void setmId(int mId) {
		this.mId = mId;
	}
	public int getmPhoneType() {
		return mPhoneType;
	}
	public void setmPhoneType(int mPhoneType) {
		this.mPhoneType = mPhoneType;
	}

	public int getmRequestType() {
		return mRequestType;
	}
	public void setmRequestType(int mRequestType) {
		this.mRequestType = mRequestType;
	}
	public String getmPhoneNum() {
		return mPhoneNum;
	}
	public void setmPhoneNum(String mPhoneNum) {
		this.mPhoneNum = mPhoneNum;
	}
	public String getmContactName() {
		return mContactName;
	}
	public void setmContactName(String mContactName) {
		this.mContactName = mContactName;
	}
	public boolean ismIsExists() {
		return mIsExists;
	}
	public void setmIsExists(boolean mIsExists) {
		this.mIsExists = mIsExists;
	}
	public boolean ismSmsStatus() {
		return mSmsStatus;
	}
	public void setmSmsStatus(boolean mSmsStatus) {
		this.mSmsStatus = mSmsStatus;
	}
	public boolean ismRingStatus() {
		return mRingStatus;
	}
	public void setmRingStatus(boolean mRingStatus) {
		this.mRingStatus = mRingStatus;
	}
	public int getmShowDialogStyle() {
		return mShowDialogStyle;
	}
	public void setmShowDialogStyle(int mShowDialogStyle) {
		this.mShowDialogStyle = mShowDialogStyle;
	}

	

}
