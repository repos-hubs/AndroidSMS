/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.kindroid.android.provider.Telephony;
import com.kindroid.kincent.data.AutoMsgDataItem;
import com.kindroid.kincent.data.PrivacyCallogDataItem;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.data.PrivacyMessageDataItem;
import com.kindroid.kincent.data.PrivacyMessagesItem;
import com.kindroid.kincent.data.PrivateDBHelper;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.data.PrivateDBHelper.PrivateAutoMsg;
import com.kindroid.security.util.ConvertUtils;
import com.kindroid.security.util.PhoneType;

import java.util.ArrayList;
import java.util.List;

public class PrivacyDBUtils {
	private static SQLiteDatabase mDb = null;	
	private static PrivacyDBUtils mInstance = null;
	
	private PrivacyDBUtils(Context mContext){
		mDb = PrivateDBHelper.getInstance(mContext).getWritableDatabase();
	}
	public synchronized static PrivacyDBUtils getInstance(Context mContext){
		if(mInstance == null){
			mInstance = new PrivacyDBUtils(mContext);
		}
		return mInstance;
	}
	
	public String getAutoMsg(){
		String ret = null;
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, new String[]{PrivateDBHelper.PrivateAutoMsg.MESSAGE}, PrivateDBHelper.PrivateAutoMsg.USED + "=1", null, null, null, null);
		if(mCursor != null){
			if(mCursor.moveToFirst()){
				ret = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateAutoMsg.MESSAGE));
			}
		}
		return ret;
	}
	public boolean hasUnreadMsg(Context mContext){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, PrivateDBHelper.PrivateMsg.READ + "='0'", null, null, null, null);
		if(mCursor != null && mCursor.getCount() > 0){
			return true;
		}else{
			return false;
		}
	}
	public List<AutoMsgDataItem> getAllAutoMsgs(){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, null, null, null, null, null, null);
		List<AutoMsgDataItem> items = new ArrayList<AutoMsgDataItem>();
		while(mCursor.moveToNext()){
			String msg = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateAutoMsg.MESSAGE));
			int used = mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateAutoMsg.USED));
			int id = mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateAutoMsg._ID));
			AutoMsgDataItem item = new AutoMsgDataItem(msg, used, id);
			items.add(item);
		}
		return items;
	}
	public void addAutoMsg(Context mContext, AutoMsgDataItem mItem){
		ContentValues cv = new ContentValues();
		DatabaseUtils.InsertHelper helper = new DatabaseUtils.InsertHelper(mDb, PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG);
		cv.put(PrivateAutoMsg.MESSAGE, mItem.getMsg());
		cv.put(PrivateAutoMsg.USED, mItem.getFlag());
		int id = (int)helper.insert(cv);
		mItem.setId(id);
	}
	public void delAutoMsg(Context mContext, AutoMsgDataItem mItem){
		int deletedRows = 0;
		try{
			deletedRows = mDb.delete(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, PrivateDBHelper.PrivateAutoMsg.MESSAGE + "='" + mItem.getMsg().trim() + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(deletedRows > 0 && mItem.getFlag() == 1){
			try{
				ContentValues cv = new ContentValues();
				cv.put(PrivateAutoMsg.USED, 1);
				mDb.update(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, cv, PrivateDBHelper.PrivateAutoMsg._ID + "='1'", null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public synchronized void setAutoMsg(Context mContext, AutoMsgDataItem mItem){
		ContentValues cv = new ContentValues();
		cv.put(PrivateAutoMsg.USED, 0);
		mDb.update(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, cv, null, null);
		
		cv = new ContentValues();
		cv.put(PrivateAutoMsg.USED, 1);
		mDb.update(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, cv, PrivateDBHelper.PrivateAutoMsg.MESSAGE + "='" + mItem.getMsg() + "'", null);
	}
	public void updateAutoMsg(Context mContext, AutoMsgDataItem mItem){
		ContentValues cv = new ContentValues();
		cv.put(PrivateAutoMsg.MESSAGE, mItem.getMsg());
		cv.put(PrivateAutoMsg.USED, mItem.getFlag());
		mDb.update(PrivateDBHelper.TABLE_PRIVATE_AUTO_MSG, cv, PrivateDBHelper.PrivateAutoMsg._ID + "='" + mItem.getId() + "'", null);
	}
	public List<PrivacyContactDataItem> getAllContacts(Context mContext){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, new String[]{PrivateDBHelper.PrivateContact.CONTACT_NAME, PrivateDBHelper.PrivateContact.PRE_NUMBER, PrivateDBHelper.PrivateContact.PHONE_NUMBER, PrivateDBHelper.PrivateContact.BLOCKED_TYPE}, null, null, null, null, null);
		List<PrivacyContactDataItem> mItems = new ArrayList<PrivacyContactDataItem>();
		while(mCursor.moveToNext()){
			String mContactName = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateContact.CONTACT_NAME));
			mContactName = SafeUtils.getDesString(mContext, mContactName);
			String mPreNumber = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateContact.PRE_NUMBER));
			mPreNumber = SafeUtils.getDesString(mContext, mPreNumber);
			String mPhoneNumber = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateContact.PHONE_NUMBER));
			mPhoneNumber = SafeUtils.getDesString(mContext, mPhoneNumber);
			int mBlockedType = mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateContact.BLOCKED_TYPE));
			PrivacyContactDataItem item = new PrivacyContactDataItem(mContactName, mPreNumber, mPhoneNumber, mBlockedType);
			mItems.add(item);
		}
		return mItems;
	}
	public void updateContact(Context mContext, PrivacyContactDataItem mItem){	
		PhoneType pt = ConvertUtils.getPhonetype(mItem.getPhoneNumber());
		String encNo = SafeUtils.getEncStr(mContext, pt.getPhoneNo());
		ContentValues cv = new ContentValues();
		cv.put(PrivateDBHelper.PrivateContact.BLOCKED_TYPE, mItem.getBlockedType());
		cv.put(PrivateDBHelper.PrivateContact.CONTACT_NAME, SafeUtils.getEncStr(mContext, mItem.getContactName()));
		cv.put(PrivateDBHelper.PrivateContact.PHONE_NUMBER, encNo);
		try{
			mDb.update(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, cv, PrivateDBHelper.PrivateContact.PHONE_NUMBER + "='" + encNo + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public int addContact(Context mContext, PrivacyContactDataItem mItem){		
		PhoneType pt = ConvertUtils.getPhonetype(mItem.getPhoneNumber());
		String encNo = SafeUtils.getEncStr(mContext, pt.getPhoneNo());
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, new String[]{PrivateDBHelper.PrivateContact.CONTACT_NAME}, PrivateDBHelper.PrivateContact.PHONE_NUMBER + "='" + encNo + "'", null, null, null, null);
		if(mCursor != null && mCursor.getCount() > 0){
			return 0;
		}
		
		ContentValues cv = new ContentValues();
		if(!TextUtils.isEmpty(mItem.getContactName())){
			cv.put(PrivateDBHelper.PrivateContact.CONTACT_NAME, SafeUtils.getEncStr(mContext, mItem.getContactName()));
		}
		if(!TextUtils.isEmpty(mItem.getPhoneNumber())){
			pt = ConvertUtils.getPhonetype(mItem.getPhoneNumber());
			cv.put(PrivateDBHelper.PrivateContact.PHONE_NUMBER, SafeUtils.getEncStr(mContext, pt.getPhoneNo()));
		}
		cv.put(PrivateDBHelper.PrivateContact.BLOCKED_TYPE, mItem.getBlockedType());
		long ret = 0;
		try{
			ret = mDb.insertOrThrow(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, null, cv);
		}catch(Exception e){
			e.printStackTrace();
			ret = -1;
		}
		return (int)ret;
	}
	public String getContactName(Context mContext, String mPhoneNumber){
		String ret = null;
		if(!TextUtils.isEmpty(mPhoneNumber)){
			PhoneType pt = ConvertUtils.getPhonetype(mPhoneNumber);
			String encNo = SafeUtils.getEncStr(mContext, pt.getPhoneNo());
			Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, new String[]{PrivateDBHelper.PrivateContact.CONTACT_NAME}, PrivateDBHelper.PrivateContact.PHONE_NUMBER + " LIKE '%" + encNo + "%'", null, null, null, null);
			if(mCursor.moveToNext()){
				ret = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateContact.CONTACT_NAME));
			}
			try{
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return ret;
	}
	public void delContact(Context mContext, PrivacyContactDataItem mItem, boolean includeMsg){
		String mPhoneNumber = mItem.getPhoneNumber();
		if(!TextUtils.isEmpty(mPhoneNumber)){
			PhoneType pt = ConvertUtils.getPhonetype(mPhoneNumber);
			String encNo = SafeUtils.getEncStr(mContext, pt.getPhoneNo());
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, PrivateDBHelper.PrivateContact.PHONE_NUMBER + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_IN, PrivateDBHelper.PrivateCllIn.NUMBER + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_OUT, PrivateDBHelper.PrivateCallOut.NUMBER + "=?", new String[]{encNo});
			if(includeMsg){
				mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg.ADDRESS + "=?", new String[]{encNo});
			}
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MMS_IN, PrivateDBHelper.PrivateMmsIn.ADDRESS + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MMS_OUT, PrivateDBHelper.PrivateMmsOut.ADDRESS + "=?", new String[]{encNo});
		}
	}
	public void delContact(Context mContext, PrivacyContactDataItem mItem){
		String mPhoneNumber = mItem.getPhoneNumber();
		if(!TextUtils.isEmpty(mPhoneNumber)){
			PhoneType pt = ConvertUtils.getPhonetype(mPhoneNumber);
			String encNo = SafeUtils.getEncStr(mContext, pt.getPhoneNo());
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CONTACTS, PrivateDBHelper.PrivateContact.PHONE_NUMBER + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_IN, PrivateDBHelper.PrivateCllIn.NUMBER + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_OUT, PrivateDBHelper.PrivateCallOut.NUMBER + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg.ADDRESS + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MMS_IN, PrivateDBHelper.PrivateMmsIn.ADDRESS + "=?", new String[]{encNo});
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MMS_OUT, PrivateDBHelper.PrivateMmsOut.ADDRESS + "=?", new String[]{encNo});
		}
	}
	public List<PrivacyMessagesItem> getThreads(Context mContext){
		Cursor mCursor = mDb.query(true, PrivateDBHelper.TABLE_PRIVATE_MESSAGE, new String[]{PrivateDBHelper.PrivateMsg.NAME, PrivateDBHelper.PrivateMsg.ADDRESS}, null, null, null, null, "date DESC", null);

		if(mCursor != null && mCursor.getCount() > 0){
			List<PrivacyMessagesItem> mItems = new ArrayList<PrivacyMessagesItem>();
			while(mCursor.moveToNext()){
				PrivacyMessagesItem item = new PrivacyMessagesItem(mContext);
				String addr = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.ADDRESS));
				String name = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.NAME));
				item.setPhoneNumber(SafeUtils.getDesString(mContext, addr));
				item.setContactName(SafeUtils.getDesString(mContext, name));
				Cursor cursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, PrivateDBHelper.PrivateMsg.ADDRESS + "='" + addr + "'", null, null, null, "date DESC");
				try{
					item.setMsgNum(cursor.getCount());
					if(cursor.moveToFirst()){
						item.setLastDate(cursor.getLong(cursor.getColumnIndex(PrivateDBHelper.PrivateMsg.DATE)));
						item.setLastMsg(SafeUtils.getDesString(mContext, cursor.getString(cursor.getColumnIndex(PrivateDBHelper.PrivateMsg.BODY))));
						item.setRead(cursor.getInt(cursor.getColumnIndex(PrivateDBHelper.PrivateMsg.READ)));
						mItems.add(item);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(cursor != null){
						try{
							cursor.close();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
			try{
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			return mItems;
		}else{
			try{
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
	}
	public List<SmsMmsMessage> getSmsMmsMessages(Context mContext, String number){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, PrivateDBHelper.PrivateMsg.ADDRESS + "='" + SafeUtils.getEncStr(mContext, number) + "'", null, null, null, "date ASC");
		List<SmsMmsMessage> mItems = null;
		if(mCursor != null && mCursor.getCount() > 0){
			mItems = new ArrayList<SmsMmsMessage>();
			while(mCursor.moveToNext()){
				SmsMmsMessage item = new SmsMmsMessage();
				item.setFromAddress(number);
				item.setMessageId(mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg._ID)));
				item.setMessageBody(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.BODY))));
				item.setMessageType(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE)));
				item.setReadType(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.READ)));
				item.setTimestamp(mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.DATE)));
				item.setContactName(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.NAME))));
				String subject = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.SUBJECT));
				if(subject != null){
					item.setMessageSubject(subject);
				}
				mItems.add(item);
				try{
					//update read status
					ContentValues cv = new ContentValues();
					cv.put(PrivateDBHelper.PrivateMsg.READ, 1);
					mDb.update(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, cv, PrivateDBHelper.PrivateMsg._ID + "='" + item.getMessageId() + "'", null);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return mItems;
	}
	public int delSmsMmsMessage(Context mContext, long id){
		int ret = 0;
		try{
			ret = mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg._ID + "='" + id + "'", null);
		}catch(Exception e){
			
		}
		return ret;
	}
	public List<PrivacyMessageDataItem> getMessages(Context mContext){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, null, null, null, null, "date DESC");
		List<PrivacyMessageDataItem> mItems = null;
		if(mCursor != null && mCursor.getCount() > 0){
			mItems = new ArrayList<PrivacyMessageDataItem>();
			while(mCursor.moveToNext()){
				PrivacyMessageDataItem item = new PrivacyMessageDataItem();
				item.setId(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg._ID)));
				item.setAddress(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.ADDRESS))));
				item.setBody(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.BODY))));
				String subject = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.SUBJECT));
				if(!TextUtils.isEmpty(subject)){
					item.setSubject(SafeUtils.getDesString(mContext, subject));
				}
				item.setDate(mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.DATE)));
				item.setName(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.NAME))));
				item.setType(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.MMS_TYPE)));
				item.setRead(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.READ)));
				item.setRecvType(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE)));
				mItems.add(item);
			}
			try{
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return mItems;
	}
	public void addMessage(Context mContext, ContentValues cv){
		try{
			mDb.insertOrThrow(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, cv);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delMessages(Context mContext){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delMessages(Context mContext, PrivacyMessagesItem item){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg.ADDRESS + "='" + SafeUtils.getEncStr(mContext, item.getPhoneNumber()) + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delMessage(Context mContext, PrivacyMessageDataItem item){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg._ID + "='" + item.getId() + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delMessages(Context mContext, String mPhoneNumber){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg.ADDRESS + "='" + SafeUtils.getEncStr(mContext, mPhoneNumber) + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void restoreMessages(Context mContext, List<PrivacyMessageDataItem> mItems){
		if(mItems != null){
			for(int i = 0; i < mItems.size(); i++){
				restoreMessage(mContext, mItems.get(i));
			}
		}
	}
	public void restoreMessages(Context mContext){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, null, null, null, null, null);
		if(mCursor != null && mCursor.getCount() > 0){
			while(mCursor.moveToNext()){
				ContentValues values = new ContentValues();
				values.put(Telephony.Sms.ADDRESS, SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.ADDRESS))));
				values.put(Telephony.Sms.BODY, SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.BODY))));
				values.put(Telephony.Sms.DATE, mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.DATE)));
				values.put(Telephony.Sms.READ, mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.READ)));
				values.put(Telephony.Sms.TYPE, mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE)));
				String subject = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.SUBJECT));
				if(subject != null){
					values.put(Telephony.Sms.SUBJECT, subject);
				}
				try{
					mContext.getContentResolver().insert(
							Uri.parse("content://sms"), values);
					mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg._ID + "='" + mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg._ID)) + "'", null);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
	}
	public int restoreMessages(Context mContext, SmsMmsMessage item){
		int ret = 0;
		ContentValues values = new ContentValues();
		values.put(Telephony.Sms.ADDRESS, item.getFromAddress());
		values.put(Telephony.Sms.BODY, item.getMessageBody());
		values.put(Telephony.Sms.DATE, item.getTimestamp());
		values.put(Telephony.Sms.READ, item.getReadType());
		values.put(Telephony.Sms.TYPE, item.getMessageType());
		if(item.getMessageSubject() != null){
			values.put(Telephony.Sms.SUBJECT, item.getMessageSubject());
		}
		try{
			mContext.getContentResolver().insert(
					Uri.parse("content://sms"), values);
			ret = mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg._ID + "='" + item.getMessageId() + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	public void restoreMessages(Context mContext, PrivacyMessagesItem item){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, PrivateDBHelper.PrivateMsg.ADDRESS + "='" + SafeUtils.getEncStr(mContext, item.getPhoneNumber()) + "'", null, null, null, null);
		if(mCursor != null && mCursor.getCount() > 0){
			while(mCursor.moveToNext()){
				ContentValues values = new ContentValues();
				values.put(Telephony.Sms.ADDRESS, SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.ADDRESS))));
				values.put(Telephony.Sms.BODY, SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.BODY))));
				values.put(Telephony.Sms.DATE, mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.DATE)));
				values.put(Telephony.Sms.READ, mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.READ)));
				values.put(Telephony.Sms.TYPE, mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE)));
				String subject = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.SUBJECT));
				if(subject != null){
					values.put(Telephony.Sms.SUBJECT, subject);
				}
				try{
					mContext.getContentResolver().insert(
							Uri.parse("content://sms"), values);
					mDb.delete(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, PrivateDBHelper.PrivateMsg._ID + "='" + mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg._ID)) + "'", null);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
	}
	public void restoreMessage(Context mContext, PrivacyMessageDataItem item){
		ContentValues values = new ContentValues();
		values.put(Telephony.Sms.ADDRESS, item.getAddress());
		values.put(Telephony.Sms.BODY, item.getBody());
		values.put(Telephony.Sms.DATE, item.getDate());
		values.put(Telephony.Sms.READ, item.getRead());
		values.put(Telephony.Sms.TYPE, item.getRecvType());
		if(item.getSubject() != null){
			values.put(Telephony.Sms.SUBJECT, item.getSubject());
		}
		try{
			mContext.getContentResolver().insert(
					Uri.parse("content://sms"), values);
			delMessage(mContext, item);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void restoreMessages(Context mContext, String number){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_MESSAGE, null, PrivateDBHelper.PrivateMsg.ADDRESS + "='" + SafeUtils.getEncStr(mContext, number) + "'", null, null, null, "date DESC");
		if(mCursor != null && mCursor.getCount() > 0){			
			while(mCursor.moveToNext()){
				ContentValues values = new ContentValues();
				values.put(Telephony.Sms.ADDRESS, SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.ADDRESS))));
				values.put(Telephony.Sms.BODY, SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.BODY))));
				values.put(Telephony.Sms.DATE, mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.DATE)));
				values.put(Telephony.Sms.READ, mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.READ)));
				values.put(Telephony.Sms.TYPE, mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.MMS_RECV_TYPE)));
				String subject = mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateMsg.SUBJECT));
				if(!TextUtils.isEmpty(subject)){
					values.put(Telephony.Sms.SUBJECT, subject);
				}
				
				try{
					mContext.getContentResolver().insert(
							Uri.parse("content://sms"), values);
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			delMessages(mContext, number);
		}
		
	}
	public void addMmsIn(Context mContext, String mPhoneNumber, String mBody, String mSubject){
		
	}
	public void delMmsIn(Context mContext, String mPhoneNumber){
		
	}
	public void addMmsOut(Context mContext, String mPhoneNumber, String mBody, String mSubject){
		
	}
	public void delMmsOut(Context mContext, String mPhoneNumber){
		
	}
	public List<PrivacyCallogDataItem> getCallInLogs(Context mContext){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_CALL_IN, null, null, null, null, null, "date DESC");
		List<PrivacyCallogDataItem> mItems = null;
		if(mCursor != null && mCursor.getCount() > 0){
			mItems = new ArrayList<PrivacyCallogDataItem>();
			while(mCursor.moveToNext()){
				PrivacyCallogDataItem item = new PrivacyCallogDataItem();
				item.setType(PrivacyCallogDataItem.CALL_IN_TYPE);
				item.setDate(mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateCllIn.DATE)));
				item.setName(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateCllIn.NAME))));
				item.setPhoneNumber(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateCllIn.NUMBER))));
				item.setBlockedType(mCursor.getInt(mCursor.getColumnIndex(PrivateDBHelper.PrivateCallOut.BLOCKED_TYPE)));
				mItems.add(item);
			}
			try{
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return mItems;
	}
	public List<PrivacyCallogDataItem> getCallOutLogs(Context mContext){
		Cursor mCursor = mDb.query(PrivateDBHelper.TABLE_PRIVATE_CALL_OUT, null, null, null, null, null, "date DESC");
		List<PrivacyCallogDataItem> mItems = null;
		if(mCursor != null && mCursor.getCount() > 0){
			mItems = new ArrayList<PrivacyCallogDataItem>();
			while(mCursor.moveToNext()){
				PrivacyCallogDataItem item = new PrivacyCallogDataItem();
				item.setType(PrivacyCallogDataItem.CALL_OUT_TYPE);
				item.setDate(mCursor.getLong(mCursor.getColumnIndex(PrivateDBHelper.PrivateCallOut.DATE)));
				item.setName(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateCallOut.NAME))));
				item.setPhoneNumber(SafeUtils.getDesString(mContext, mCursor.getString(mCursor.getColumnIndex(PrivateDBHelper.PrivateCallOut.NUMBER))));
				mItems.add(item);
			}
			try{
				mCursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return mItems;
	}
	public List<PrivacyCallogDataItem> getCallogs(Context mContext){
		List<PrivacyCallogDataItem> mCallInItems = getCallInLogs(mContext);
		List<PrivacyCallogDataItem> mCallOutItems = getCallOutLogs(mContext);
		if(mCallInItems == null){
			return mCallOutItems;
		}else if(mCallOutItems == null){
			return mCallInItems;
		}else{
			mCallInItems.addAll(mCallOutItems);
			return mCallInItems;
		}
		
	}
	public void addCallIn(Context mContext, ContentValues cv){
		try{
			mDb.insertOrThrow(PrivateDBHelper.TABLE_PRIVATE_CALL_IN, null, cv);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delCallIn(Context mContext, String mNumber){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_IN, PrivateDBHelper.PrivateCllIn.NUMBER + "='" + SafeUtils.getEncStr(mContext, mNumber) + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void addCallOut(Context mContext, ContentValues cv){
		try{
			mDb.insertOrThrow(PrivateDBHelper.TABLE_PRIVATE_CALL_OUT, null, cv);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delCallOut(Context mContext, String mNumber){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_OUT, PrivateDBHelper.PrivateCallOut.NUMBER + "='" + SafeUtils.getEncStr(mContext, mNumber) + "'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void delCallogs(Context mContext){
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_IN, null, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			mDb.delete(PrivateDBHelper.TABLE_PRIVATE_CALL_OUT, null, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void closeDb(){
		if(mDb == null){
			mInstance = null;
			return;
		}
		try{
			mDb.close();
		}catch(Throwable ex){
			ex.printStackTrace();
		}
		mInstance = null;
	}
	@Override
	protected void finalize(){
		// TODO Auto-generated method stub
		try{
			super.finalize();
		}catch(Throwable ex){
			ex.printStackTrace();
		}
		try{
			mDb.close();			
		}catch(Throwable ex){
			ex.printStackTrace();
		}finally{
			mDb = null;
		}
	}

}
