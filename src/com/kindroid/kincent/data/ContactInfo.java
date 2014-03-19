package com.kindroid.kincent.data;

public class ContactInfo {

	private long dataId;
	private String displayName;
	private String address;
	private String type;
	private long groupId;
	private String groupName;
	private long contactId;
	private long timestamp;
	private boolean chkBoxSelected = false;	
	private String firstCNLetter; 		//中文名字拼音首字母
	
	
	public String getFirstCNLetter() {
		return firstCNLetter;
	}
	public void setFirstCNLetter(String firstCNLetter) {
		this.firstCNLetter = firstCNLetter;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getContactId() {
		return contactId;
	}
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	
	public boolean isChkBoxSelected() {
		return chkBoxSelected;
	}
	public void setChkBoxSelected(boolean chkBoxSelected) {
		this.chkBoxSelected = chkBoxSelected;
	}
	public long getDataId() {
		return dataId;
	}
	public void setDataId(long dataId) {
		this.dataId = dataId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
}
