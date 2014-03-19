package com.kindroid.kincent.data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.kindroid.google.android.mms.pdu.EncodedStringValue;
import com.kindroid.kincent.util.Constant;
import com.kindroid.security.util.DateTimeUtil;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.text.format.DateUtils;


public class SmsMmsMessage implements Serializable {
	// Message types
	public static final int MESSAGE_TYPE_SMS = 0;
	public static final int MESSAGE_TYPE_MMS = 1;
	public static final int MESSAGE_TYPE_MESSAGE = 2;
	public static final int INBOX  = 1;
	public static final int SENT   = 2; 
	public static final int DRAFT  = 3; 
	public static final int OUTBOX = 4; 
	public static final int FAILED = 5; 
	public static final int QUEUED = 6;
	
	public static final int READ = 1;
	public static final int UNREAD = 0;
	
	// Main message object private vars
	private Context context;
	private String fromAddress = null;
	private String messageBody = null;
	private long timestamp = 0;
	private int unreadCount = 0;
	private long threadId = 0;
	private String contactId = "";
	private String contactLookupKey = null;
	private String contactName = "";
	private int messageType = 0;
	private boolean notify = true;
	private int reminderCount = 0;
	private int totalCount = 0;
	private long messageId = 0;
	private int readType = 1;
	private String messageSubject = null;
	
	private String addressLoaction;
	private boolean isSelected = false; //Radio button的标记用于相关功能操作
	private boolean isShown = false; //Radio button 是否显示的标记
	
	private boolean chkBoxSelected = false;
	
	public String getMessageSubject(){
		return messageSubject;
	}
	public void setMessageSubject(String subject){
		this.messageSubject = subject;
	}
	public String getAddressLoaction() {
		return addressLoaction;
	}
	public void setAddressLoaction(String addressLoaction) {
		this.addressLoaction = addressLoaction;
	}
	public boolean isChkBoxSelected() {
		return chkBoxSelected;
	}
	public void setChkBoxSelected(boolean chkBoxSelected) {
		this.chkBoxSelected = chkBoxSelected;
	}
	public boolean isShown() {
		return isShown;
	}
	public void setShown(boolean isShown) {
		this.isShown = isShown;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getMessageBody() {
//		EncodedStringValue encode = new EncodedStringValue(messageBody);
//		return messageBody;
		try {
			return new String(messageBody.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getUnreadCount() {
		return unreadCount;
	}
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
	public long getThreadId() {
		return threadId;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getContactLookupKey() {
		return contactLookupKey;
	}
	public void setContactLookupKey(String contactLookupKey) {
		this.contactLookupKey = contactLookupKey;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public int getMessageType() {
		return messageType;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public int getReminderCount() {
		return reminderCount;
	}
	public void setReminderCount(int reminderCount) {
		this.reminderCount = reminderCount;
	}
	public long getMessageId() {
		return messageId;
	}
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	
	public int getReadType() {
		return readType;
	}
	public void setReadType(int readType) {
		this.readType = readType;
	}
	
	public CharSequence getFormattedTimestamp() {
//		return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR | DateUtils.FORMAT_SHOW_DATE);//FORMAT_SHOW_TIME
		return DateTimeUtil.formatTimeStampString(timestamp, false);//FORMAT_SHOW_TIME
	}
	public Uri getContactLookupUri() {
	    if (contactId == null) {
	      return null;
	    }

	    return Uri.withAppendedPath(Contacts.CONTENT_URI, contactId);
	}
}
