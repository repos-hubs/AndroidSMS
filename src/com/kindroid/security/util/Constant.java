/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:
 * Date:
 * Description:
 */

package com.kindroid.security.util;

public final class Constant {
	
	private static final String BASEURL254=Config.BASEURL254;
	
	public static final String FEEDBACK_URL = BASEURL254+"KindroidMessengerService/feedback/bug/";
	public static final String REPORT_URL = BASEURL254+"KindroidCommon/trace/client";
	
	public static final String UPGRADE_URL = BASEURL254+"KindroidCommon/client/version/json/";
	public static final int MESSENGER_PRODUCT_ID = 5;
	
	public static final String DOWNLOAD_DIR = "/Kindroid/download/";
	
	public static final String[] DOWNLOAD_STATUS={"Downloading","INSTALLED","UNINSTALLED","DOWNLOADED"};
	
	public static final String CURRENT_CLIENT_VERSION = "2.0";
	 
	public static final String SHAREDPREFERENCES_REMOTESECURITY="remote_security";
	
	public static final String SHAREDPREFERENCES_SAFEMOBILENUMBER="safe_mobile_number";
	
	public static final String SHAREDPREFERENCES_AFTERUPDATESIMSENDMES="after_update_sim_send_mes";
	
	public static final String SHAREDPREFERENCES_AFTERUPDATESIMTOLOCKMOBILE="after_update_sim_to_lock_mobile";
	
	public static final String SHAREDPREFERENCES_REMOTESECURITYPASSWD="remote_security_passwd";
	
	public static final String SHAREDPREFERENCES_SIMUNIQUETAG="sim_unique_tag";
	
	public static final String SHAREDPREFERENCES_TOKEN="token";
	
	public static final String SHAREDPREFERENCES_USERNAME="user_name";
	public static final String SHAREDPREFERENCES_NETWORKRX="network_rx";
	public static final String SHAREDPREFERENCES_NETWORKTX="network_tx";
	
	public static final String SHAREDPREFERENCES_NETWORKWIFIRX="network_wifirx";
	public static final String SHAREDPREFERENCES_NETWORKWIFITX="network_wifitx";
	
	public static final String SHAREDPREFERENCES_FIRSTINSTALLREMOTESECURITY="first_install_remote_security";
	
	public static final String SHAREDPREFERENCES_ENABLETRAFFICMOITER="enable_traffic_moiter";
	public static final String SHAREDPREFERENCES_ENABLEAPPTRAFFICMOITER="enable_app_traffic_moiter";
	public static final String SHAREDPREFERENCES_APPFILEEXIT="app_file_exit";
	public static final String SHAREDPREFERENCES_WINX="win_x";
	public static final String SHAREDPREFERENCES_WINy="win_y";
	public static final String SHAREDPREFERENCES_APPFIRST_INSTALL="app_item_first_install";
	
	/** block rule 
	 * 0 night model ,intercept data accoding the selected rules in the specific time period
	 * 1 default model, intercept blacklist and keywords.
	 * 2 contact modle, accept only contact list and white list.
	 * 3 whitelist mode
	 * 4 all accept model,accept all phone and sms.
	 * 5 all intercept model, intercept all phone and sms. 
	 * 6 all intercept phone model,intercept all phone and blacklist that has been set sms and keywod. 
	 * 
	 * 
	 * */
	public static final String SHAREDPREFERENCES_BLOCKINGRULES="blocking_rule";	
	public static final String SHAREDPREFERENCES_NIGHTBLOCKINGRULES="night_blocking_rule";
	/**
	 * 拦截处理模式,从1到5,分别表示不同的处理模式
	 */
	public static final String INTERCEPT_TREAT_MODE = "intercept_treat_mode";
	/**
	 * 0表示发生拦截时不通知拦截信息;1表示发生时通知拦截信息
	 */
	public static final String INTERCEPT_NOTIFY_INFO = "intercept_notify_info";
	public static final String NODISTURB_START_TIME = "nodisturb_start_time";
	public static final String NODISTURB_END_TIME = "nodisturb_end_time";
	public static final String NODISTURB_DAY_TIME = "nodisturb_day_time";	
	
	
	
	public static final String TAGDEL="ehoo_del";
	public static final String TAGGPS="ehoo_gps";
	public static final String APPTRAFFICDIR="/proc/uid_stat";
	
//	public static final String BROACTUPDATEINTERCEPT="com.update.intercept.list";
	
	public static final String BROACT_INTERCEPT_BALCKWHITE_LIST="com.update.intercept.balck.white.list";
	
	public static final String BROACT_UPDATE_INTERCEPT_HISTORY="com.update.intercept.history";
	
	
	public static final String BROACT_UPDATE_COLLECT_CATEGORY="com.update.collect.category";
	
	//更新会话的广播
	public static final String BROACT_UPDATE_MESSAGE_DIALOG="com.update.message.dialog";
	
	
}
