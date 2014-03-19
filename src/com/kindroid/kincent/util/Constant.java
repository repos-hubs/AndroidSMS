/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao,jingmiao.li,zili.chen
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.util;

import com.kindroid.kincent.R;

import android.net.Uri;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public final class Constant {
	public static final String SHARE_PREFS_PRIVACY_PWD = "messengers_sp_p_pd";
	public static final String SHARE_PREFS_PRIVACY_DISPLAY_NAME = "messengers_sp_p_pn";
	public static final String SHARE_PREFS_PRIVACY_DISPLAY_ICON = "messengers_sp_p_pi";
	public static final String SHARE_PREFS_PRIVACY_EMAIL = "messengers_sp_p_pe";
	public static final String SHARE_PREFS_PRIVACY_NOTIFY_FOR_NEW = "messengers_sp_p_pnfn";
	public static final String SHARE_PREFS_PRIVACY_AUTO_LOCK_TIME = "messengers_sp_p_alt";
	public static final String SHARE_PREFS_PRIVACY_NOTIFY_TEXT = "messengers_sp_p_nt";
	public static final String SHARE_PREFS_PRIVACY_MSG_NOTIFY_ICON = "messengers_sp_p_mni";
	public static final String SHARE_PREFS_PRIVACY_CALL_IN_NOTIFY_ICON = "messengers_sp_p_cini";
	public static final String SHARE_PREFS_PRIVACY_VIBRATE_NOTIFY = "messengers_sp_p_vn";
	public static final String SHARE_PREFS_PRIVACY_AUTO_SEND_MSG = "messengers_sp_p_asm";
	public static final String SHARE_PREFS_PRIVACY_CONTACT_BLOCKED_TYPE = "messengers_sp_p_cbt";
	public static final String SHARE_PREFS_PRIVACY_MSG_RINGTONE = "messengers_sp_p_mrt";
	
	public static final String SHARE_PREFS_SETTINGS_DATE_FORMAT = "messengers_settings_date_format";
	
	public static final String SETTING_INFO = "setting_info";
	
	public static final String SHARE_PREFS_PRIVACY_LOCK_PWD = "messengers_sp_p_pw";
	// for privacy contact
	public static final String MIME_SMS_ADDRESS = "vnd.android.cursor.item/sms-address";
	public static final String SCHEME_TEL = "tel";
	public static final String SCHEME_SMSTO = "smsto";
	public static final String SCHEME_MAILTO = "mailto";
	public static final String SCHEME_IMTO = "imto";

	// for privacy contact import
	public static final String PRIVACY_CONTACT_IMPORT_SOURCE = "privacy_contact_import_source";	
	public static final int IMPORT_FROM_CONTACTS = 0;
	public static final int IMPORT_FROM_CALLOG = 1;
	public static final int IMPORT_FROM_SMS = 2;

	// for privacy contact import activity requestCode
	public static final int REQUEST_CODE_FOR_PRIVACY_CONTACT_IMPORT_ACTIVITY = 99;
	public static final String PRIVACY_CONTACT_IMPORT_RESULT = "privacy_contact_import_import_result";

	// for query contact info for phone number
	public static final String CALLER_ID_SELECTION = "PHONE_NUMBERS_EQUAL("
			+ Phone.NUMBER + ",?) AND " + Data.MIMETYPE + "='"
			+ Phone.CONTENT_ITEM_TYPE + "'" + " AND " + Data.RAW_CONTACT_ID
			+ " IN " + "(SELECT raw_contact_id " + " FROM phone_lookup"
			+ " WHERE normalized_number GLOB('+*'))";
	public static final Uri PHONES_WITH_PRESENCE_URI = Data.CONTENT_URI;
	public static final String[] CALLER_ID_PROJECTION = new String[] {
			Phone.NUMBER, // 0
			Phone.LABEL, // 1
			Phone.DISPLAY_NAME, // 2
			Phone.CONTACT_ID, // 3
			Phone.CONTACT_PRESENCE, // 4
			Phone.CONTACT_STATUS, // 5
	};
	public static final int PHONE_NUMBER_COLUMN = 0;
	public static final int PHONE_LABEL_COLUMN = 1;
	public static final int CONTACT_NAME_COLUMN = 2;
	public static final int CONTACT_ID_COLUMN = 3;
	public static final int CONTACT_PRESENCE_COLUMN = 4;
	public static final int CONTACT_STATUS_COLUMN = 5;
	
	public static final Integer[] mImageIds = { 
		R.drawable.sms_camera, R.drawable.sms_picture,
		R.drawable.sms_smile, R.drawable.sms_talk, R.drawable.sms_mic };
	
	//for theme
	public static final String THEME_PACKAGE_USED = "messengers_tpu";
	public static final String THEME_ID_USED = "messengers_tiu";
	public static final String THEME_ID_DEFAULT = "default";
	
	//for upgrade
	public static final String SHOW_UPGRADE_PROMPT = "show_upgrade_prompt";
	public static final String LAST_UPGRADE_VERSION = "last_upgrade_version";
	//for update prob
	public static final String LAST_UPDATE_PROB_TIME = "last_update_prob_time";
	
	//for privacy broadcast
	public static final String PRIVACY_SMS_BROADCAST_ACTION = "com.kindroid.messenger.privacy.SMS_BROADCAST";
	public static final String PRIVACY_CALL_BROADCAST_ACTION = "com.kindroid.messenger.privacy_CALL_BROADCAST";
	//broadcast from kindroidsecurity
	public static final String KINDROID_SECURITY_INTERCEPT_BROADCAST = "com.kindroid.security.intercept.TO_MESSENGER_BROADCAST";
	public static final String KINDROID_SECURITY_INTERCEPT_SMS_BODY = "com_kindroid_security_intercept_sms_body";
	public static final String KINDROID_SECURITY_INTERCEPT_SMS_ADDRESS = "com_kindroid_security_intercept_sms_address";
	
	public static final String KINDROID_REMOTE_SECURITY_BROADCAST = "com.kindroid.security.remotesec.TO_MESSENGER_BROADCAST";
	public static final String KINDROID_REMOTE_SECURITY_NUMBER = "com_kindroid_remote_security_number";
	public static final String KINDROID_REMOTE_SECURITY_ENABLE = "com_kindroid_remote_security_enable";
	
	public static final String KINCENT_FOR_REMOTE_SECURITY_BROADCAST = "com.kindroid.messenger.KINCENT_FOR_REMOTE_SECURITY_BROADCAST";
	public static final String KINCENT_FOR_REMOTE_SECURITY_SMS_BODY = "com_kincent_for_remote_security_sms_body";
}
