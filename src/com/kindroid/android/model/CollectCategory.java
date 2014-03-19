package com.kindroid.android.model;

public class CollectCategory {
	
	private int mId;
	private String mName;
	public int getmId() {
		return mId;
	}
	public void setmId(int mId) {
		this.mId = mId;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	
	
	public class CollectSms{
		private int mId;
		private String mSubject;
		private String mBody;
		private String mDate;
		private String mAddress;
		private int mCategoryId;
		
		public int getmId() {
			return mId;
		}
		public void setmId(int mId) {
			this.mId = mId;
		}
		public String getmSubject() {
			return mSubject;
		}
		public void setmSubject(String mSubject) {
			this.mSubject = mSubject;
		}
		public String getmBody() {
			return mBody;
		}
		public void setmBody(String mBody) {
			this.mBody = mBody;
		}
		public String getmDate() {
			return mDate;
		}
		public void setmDate(String mDate) {
			this.mDate = mDate;
		}
		public String getmAddress() {
			return mAddress;
		}
		public void setmAddress(String mAddress) {
			this.mAddress = mAddress;
		}
		public int getmCategoryId() {
			return mCategoryId;
		}
		public void setmCategoryId(int mCategoryId) {
			this.mCategoryId = mCategoryId;
		}
		 
		
		
		
	
	}
	
	
	

}
