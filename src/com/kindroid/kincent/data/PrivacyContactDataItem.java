/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.data;

import java.io.Serializable;

public class PrivacyContactDataItem implements Serializable{
	private String mContactName;
	private String mPhoneNumber;
	private String mPreNumber;
	private int mBlockedType;
	private boolean mSelected;
	private boolean mShowSelect;
	private String mDesp;
	
	public PrivacyContactDataItem(String name, String preNumber, String number, int blockedType){
		this.mBlockedType = blockedType;
		this.mContactName = name;
		this.mPhoneNumber = number;
		this.mPreNumber = preNumber;
	}
	public void setDesp(String desp){
		this.mDesp = desp;
	}
	public String getDesp(){
		return this.mDesp;
	}
	public void setPreNumber(String preNumber){
		this.mPreNumber = preNumber;
	}
	public String getPreNumber(){
		return this.mPreNumber;
	}
	public void setShowSelect(boolean mShow){
		this.mShowSelect = mShow;
	}
	public boolean isShowSelected(){
		return this.mShowSelect;
	}
	public void setSelected(boolean selected){
		this.mSelected = selected;
	}
	public boolean isSelected(){
		return this.mSelected;
	}
	public String getContactName() {
		return mContactName;
	}
	public void setContactName(String mContactName) {
		this.mContactName = mContactName;
	}
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}
	public int getBlockedType() {
		return mBlockedType;
	}
	public void setBlockedType(int mBlockedType) {
		this.mBlockedType = mBlockedType;
	}
	
	
	

}
