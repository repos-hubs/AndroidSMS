package com.kindroid.kincent.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.util.LruCache;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.kindroid.google.android.mms.pdu.CharacterSets;
import com.kindroid.google.android.mms.pdu.EncodedStringValue;
import com.kindroid.google.android.mms.pdu.PduPersister;
import com.kindroid.kincent.data.ContactInfo;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.R;
import com.kindroid.security.util.DateTimeUtil;

public class MessengerUtils {
	private static final String TAG = "MessengerUtils";
	public static final Uri MMS_SMS_CONTENT_URI = Uri
			.parse("content://mms-sms/");
	public static final Uri THREAD_ID_CONTENT_URI = Uri.withAppendedPath(
			MMS_SMS_CONTENT_URI, "threadID");
	// public static final Uri PHONE_LOOKUP_URI =
	// Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode("*"));
	public static final Uri PHONE_LOOKUP_URI = Uri
			.parse("content://com.android.contacts/data/phones");
	public static final Uri PHONE_CONTACTS_URI = Uri
			.parse("content://com.android.contacts/data");
	public static final Uri CONVERSATION_CONTENT_URI = Uri
			.withAppendedPath(MMS_SMS_CONTENT_URI, "conversations").buildUpon()
			.appendQueryParameter("simple", "true").build();
	public static final String SMSTO_URI = "smsto:";
	private static final String UNREAD_CONDITION = "read=0";

	public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
	public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(
			SMS_CONTENT_URI, "inbox");

	public static final Uri MMS_CONTENT_URI = Uri.parse("content://mms");
	public static final Uri MMS_INBOX_CONTENT_URI = Uri.withAppendedPath(
			MMS_CONTENT_URI, "inbox");

	public static final String SMSMMS_ID = "_id";
	public static final String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
	public static final int READ_THREAD = 1;
	public static final int MESSAGE_TYPE_SMS = 1;
	public static final int MESSAGE_TYPE_MMS = 2;

	public static final int YESTERDAY_HISTORY = 0;
	public static final int LAST_WEEK_HISTORY = 1;
	public static final int LAST_MONTH_HISTORY = 2;
	public static final int HALF_YEAR_HISTORY = 3;
	public static final int MORE_HISTORY = 4;
	public static final int INBOX = 1;
	public static final int SENT = 2;
	public static final int DRAFT = 3;

	private static final int SNIPPET = 5;
	private static final int SNIPPET_CS = 6;

	// The max size of favorite contacts
	public static final int MAX_FAVORITE_CONTACTS = 5;

	// The max size of either the width or height of the contact photo
	public static final int CONTACT_PHOTO_MAXSIZE = 1024;

	// Bitmap cache
	private static final int bitmapCacheSize = 5;
	private static LruCache<Uri, Bitmap> bitmapCache = null;

	/**
	 * Fetches a list of messages from the system database
	 * 
	 * @param context
	 *            app context
	 * @param ignoreMessageId
	 *            message id to ignore (the one being displayed), setting this
	 *            to 0 will return all unread messages
	 * @return ArrayList of SmsMmsMessage
	 */
	public static ArrayList<SmsMmsMessage> getUnreadMessages(Context context,
			long ignoreMessageId) {

		ArrayList<SmsMmsMessage> messages = null;

		final String[] projection = new String[] { "_id", "thread_id",
				"address", "date", "body" };
		String selection = UNREAD_CONDITION;
		String[] selectionArgs = null;
		final String sortOrder = "date DESC";

		// Ignore message id if set
		if (ignoreMessageId > 0) {
			selection += " and _id != ?";
			selectionArgs = new String[] { String.valueOf(ignoreMessageId) };
		}

		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				SMS_INBOX_CONTENT_URI, projection, selection, selectionArgs,
				sortOrder);

		long messageId;
		long threadId;
		String address;
		long timestamp;
		String body;

		if (cursor != null) {
			try {
				int count = cursor.getCount();
				if (count > 0) {
					messages = new ArrayList<SmsMmsMessage>(count);

					while (cursor.moveToNext()) {

						messageId = cursor.getLong(0);
						threadId = cursor.getLong(1);
						address = cursor.getString(2);
						timestamp = cursor.getLong(3);
						body = cursor.getString(4);

						SmsMmsMessage message = new SmsMmsMessage();
						message.setFromAddress(address);
						message.setThreadId(threadId);
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setMessageId(messageId);
						message.setUnreadCount(count);
						messages.add(message);
					}
				}

			} finally {

				cursor.close();
			}
		}

		return messages;
	}

	// 取得联系人信息
	public static List<ContactInfo> getContactsData(Context context) {
		ArrayList<ContactInfo> infos = new ArrayList<ContactInfo>();
		// final String[] projection = new String[] {PhoneLookup._ID,
		// PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE,
		// PhoneLookup.IN_VISIBLE_GROUP};
		String[] projection = {
		/*
		 * "_id", "display_name", "data1"
		 */
		Phone._ID, Phone.DISPLAY_NAME, Phone.DATA1, Phone.CONTACT_ID,
		// "sort_key"
		};
		final String sortOrder = "display_name DESC";
		Cursor cursor = context.getContentResolver().query(PHONE_LOOKUP_URI,
				projection, null, null, sortOrder);

		// 存放首字母的容器，只存储相同的首字母一次
		Set<String> letterSet = new HashSet<String>();
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {
						long dataId = cursor.getLong(cursor
								.getColumnIndexOrThrow("_id"));
						long contactId = cursor.getLong(cursor
								.getColumnIndexOrThrow("contact_id"));
						String address = cursor.getString(cursor
								.getColumnIndexOrThrow("data1"));
						String displayName = cursor.getString(cursor
								.getColumnIndexOrThrow("display_name"));
						String firstLetter = "";

						if (Build.VERSION.SDK_INT <= 7) {
							firstLetter = getFirstCNLetterByContactId(context,
									contactId);
						} else {
							firstLetter = getFirstCNLetterByContactId8(context,
									contactId);
						}

						ContactInfo info = new ContactInfo();
						info.setDataId(dataId);
						info.setAddress(address);
						info.setDisplayName(displayName);
						info.setContactId(contactId);
						info.setChkBoxSelected(false);
						/*
						 * if (!letterSet.contains(firstLetter)) {
						 * letterSet.add(firstLetter);
						 * info.setFirstCNLetter(firstLetter); } else {
						 * info.setFirstCNLetter(""); }
						 */
						info.setFirstCNLetter(firstLetter);
						infos.add(info);
					}
				}

			} finally {
				cursor.close();
			}
		}
		return infos;
	}

	/**
	 * 通过首字母或汉字查询联系人
	 * 
	 * @param context
	 * @param letters
	 * @return
	 */
	public static List<ContactInfo> getContactsByLetters(Context context,
			String letters) {
		ArrayList<ContactInfo> infos = new ArrayList<ContactInfo>();

		String[] projection = { Data._ID, Data.DISPLAY_NAME, Data.CONTACT_ID,
				Data.DATA1, Data.DATA12, };
		String where = "";
		if (SmsUtils.isChinese(letters)) {
			where = " display_name like ?";
		} else {
			where = "data12 like ?";
		}
		final String sortOrder = null;
		Cursor cursor = context.getContentResolver().query(PHONE_CONTACTS_URI,
				projection, where, new String[] { letters + "%" }, sortOrder);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					Set set = new HashSet();
					while (cursor.moveToNext()) {
						long dataId = cursor.getLong(cursor
								.getColumnIndexOrThrow("_id"));
						long contactId = cursor.getLong(cursor
								.getColumnIndexOrThrow("contact_id"));
						String displayName = cursor.getString(cursor
								.getColumnIndexOrThrow("display_name"));
						if (set.contains(contactId)) {
							continue;
						} else {
							set.add(contactId);
						}
						String address = "";
						// 获取联系人手机号码
						Cursor phones = context
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + contactId, null, null);
						while (phones.moveToNext()) {
							address = phones.getString(phones
									.getColumnIndex("data1"));
						}
						ContactInfo info = new ContactInfo();
						info.setDataId(dataId);
						info.setAddress(address);
						info.setDisplayName(displayName);
						info.setContactId(contactId);
						info.setChkBoxSelected(false);
						infos.add(info);
					}
				}

			} finally {
				cursor.close();
			}
		}

		return infos;
	}

	public static String getFirstCNLetterByContactId(Context context,
			long contactId) {
		String result = "";
		String[] projection = {
		// Data._ID,
		// Data.DISPLAY_NAME,
		// Data.CONTACT_ID,
		Data.DATA12, };
		String where = Data.CONTACT_ID + "=?";
		final String sortOrder = null;
		Cursor cursor = context.getContentResolver().query(PHONE_CONTACTS_URI,
				projection, where, new String[] { String.valueOf(contactId) },
				sortOrder);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						String nameLetters = cursor.getString(cursor
								.getColumnIndexOrThrow("data12"));
						if (null != nameLetters && !"".equals(nameLetters)) {
							result = nameLetters.substring(0, 1).toUpperCase();
							break;
						}
					}
				}

			} finally {
				cursor.close();
			}
		}

		return result;
	}

	// 2.2及以上版本
	public static String getFirstCNLetterByContactId8(Context context,
			long contactId) {
		String result = "";
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		String where = Data.CONTACT_ID + "=?";
		final String sortOrder = null;
		Cursor cursor = context.getContentResolver().query(PHONE_CONTACTS_URI,
				projection, where, new String[] { String.valueOf(contactId) },
				sortOrder);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						String nameLetters = cursor.getString(cursor
								.getColumnIndexOrThrow("sort_key"));
						if (null != nameLetters && !"".equals(nameLetters)) {
							result = nameLetters.substring(0, 1).toUpperCase();
							break;
						}
					}
				}

			} finally {
				cursor.close();
			}
		}

		return result;
	}

	/**
	 * 取得所有联系人分组
	 * 
	 * @param context
	 * @return
	 */
	public static List<ContactInfo> getContactGroups(Context context) {
		ArrayList<ContactInfo> infos = new ArrayList<ContactInfo>();
		String[] projection = { ContactsContract.Groups.TITLE,
				ContactsContract.Groups._ID, };
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Groups.CONTENT_URI, projection, null, null,
				null);
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						long dataId = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(ContactsContract.Groups._ID));
						String displayName = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ContactsContract.Groups.TITLE));
						ContactInfo info = new ContactInfo();
						info.setGroupId(dataId);
						info.setGroupName(displayName);
						info.setChkBoxSelected(false);
						infos.add(info);
					}
				}
			} finally {
				cursor.close();
			}
		}

		return infos;
	}

	/**
	 * 通过GroupId查询所有联系人信息
	 * 
	 * @param context
	 * @param groupId
	 * @return
	 */
	public static List<ContactInfo> getContactsByGroupId(Context context,
			long groupId) {
		ArrayList<ContactInfo> infos = new ArrayList<ContactInfo>();
		String[] projection = { ContactsContract.Data.RAW_CONTACT_ID };
		// 通过GroupId和Mimetype查询Raw_contact_id
		String where = ContactsContract.Data.MIMETYPE + " = '"
				+ GroupMembership.CONTENT_ITEM_TYPE + "' AND "
				+ ContactsContract.Data.DATA1 + " = " + groupId;
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, projection, where, null,
				null);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						long rawContactId = cursor
								.getLong(cursor
										.getColumnIndexOrThrow(ContactsContract.Data.RAW_CONTACT_ID));
						// 通过Raw_contact_id查询Contact_id
						String rawWhere = RawContacts._ID + " = "
								+ rawContactId;
						Cursor contactIdCursor = context
								.getContentResolver()
								.query(ContactsContract.RawContacts.CONTENT_URI,
										new String[] { ContactsContract.RawContacts.CONTACT_ID },
										rawWhere, null, null);
						long contactId = 0;
						try {
							if (contactIdCursor.getCount() > 0) {
								while (contactIdCursor.moveToNext()) {
									contactId = contactIdCursor
											.getLong(contactIdCursor
													.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
								}
							}
						} catch (IllegalArgumentException e2) {
							e2.printStackTrace();
						} finally {
							contactIdCursor.close();
						}
						// 通过Contact_id查询联系人
						Cursor contactNameCursor = context
								.getContentResolver()
								.query(ContactsContract.Contacts.CONTENT_URI,
										new String[] { ContactsContract.Contacts.DISPLAY_NAME },
										ContactsContract.Contacts._ID + " = "
												+ contactId, null, null);
						String contactName = "";
						try {
							if (contactNameCursor.getCount() > 0) {
								while (contactNameCursor.moveToNext()) {
									contactName = contactNameCursor
											.getString(contactNameCursor
													.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
								}
							}
						} catch (IllegalArgumentException e1) {
							e1.printStackTrace();
						} finally {
							contactNameCursor.close();
						}
						// 通过raw_contact_id查询电话号码
						String address = "";
						/*
						 * Cursor phoneCursor =
						 * context.getContentResolver().query(
						 * ContactsContract.Data.CONTENT_URI, new String[] {
						 * ContactsContract.Data.DATA4 },
						 * ContactsContract.Data.RAW_CONTACT_ID + "=" +
						 * rawContactId + " AND " +
						 * ContactsContract.Data.MIMETYPE + " = '" +
						 * GroupMembership.CONTENT_ITEM_TYPE + "'", null, null);
						 */
						Cursor phoneCursor = context
								.getContentResolver()
								.query(Data.CONTENT_URI,
										new String[] { Data._ID, Phone.NUMBER,
												Phone.TYPE, Phone.LABEL },
										Data.CONTACT_ID + "=?" + " AND "
												+ Data.MIMETYPE + "='"
												+ Phone.CONTENT_ITEM_TYPE + "'",
										new String[] { String
												.valueOf(contactId) }, null);
						try {
							if (phoneCursor.getCount() > 0) {
								while (phoneCursor.moveToNext()) {
									address = phoneCursor
											.getString(phoneCursor
													.getColumnIndexOrThrow(Phone.NUMBER));
								}
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} finally {
							phoneCursor.close();
						}

						ContactInfo info = new ContactInfo();
						info.setAddress(address);
						info.setDisplayName(contactName);
						info.setChkBoxSelected(true);
						infos.add(info);
					}
				}

			} finally {
				cursor.close();
			}
		}

		return infos;
	}

	// 取得最近联系人信息
	public static List<ContactInfo> getRecentContactsData(Context context) {
		ArrayList<ContactInfo> infos = new ArrayList<ContactInfo>();
		String[] projection = { CallLog.Calls._ID, CallLog.Calls.NUMBER,
				CallLog.Calls.CACHED_NAME };
		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, projection, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {
						long dataId = cursor.getLong(cursor
								.getColumnIndexOrThrow(CallLog.Calls._ID));
						String address = cursor.getString(cursor
								.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
						String displayName = cursor
								.getString(cursor
										.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));

						ContactInfo info = new ContactInfo();
						info.setDataId(dataId);
						info.setAddress(address);
						info.setDisplayName(displayName);
						info.setChkBoxSelected(false);
						infos.add(info);
					}
				}

			} finally {
				cursor.close();
			}
		}

		return infos;
	}

	/**
	 * get messages from threads and canonical_addresses tables
	 * 
	 * @param context
	 * @return
	 */
	public static List<SmsMmsMessage> getMessages(Context context) {
		ArrayList<SmsMmsMessage> messages = new ArrayList<SmsMmsMessage>();

		final String[] projection = new String[] { "threads._id, threads.message_count, canonical_addresses.address,"
				+ " threads.date, threads.type, threads.snippet, threads.snippet_cs, threads.read "
				+ "from threads, canonical_addresses"
				+ " where threads.recipient_ids = canonical_addresses._id" +
				// " and sms.thread_id = threads._id and sms.body = threads.snippet "
				// +
				" order by threads.date desc --" };
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				CONVERSATION_CONTENT_URI, projection, null, null, null);

		String address;
		long threadId;
		int count;
		long timestamp;
		String body;
		int type;
		int read;
		EncodedStringValue encodeString;
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {
						String displayName = "";
						// String addrLocation = "";

						threadId = cursor.getLong(cursor.getColumnIndex("_id"));
						count = cursor.getInt(cursor
								.getColumnIndex("message_count"));
						address = cursor.getString(cursor
								.getColumnIndex("address"));
						timestamp = cursor.getLong(cursor
								.getColumnIndex("date"));
						// body =
						// cursor.getString(cursor.getColumnIndex("snippet"));
						body = extractEncStrFromCursor(cursor, SNIPPET,
								SNIPPET_CS);
						type = getMessageTypeByThreadAndBody(context, threadId,
								new String(body.getBytes(), "utf-8"));
						read = cursor.getInt(cursor.getColumnIndex("read"));

						ContactIdentification contact = getPersonIdFromPhoneNumber(
								context, address);
						String personId = "";
						if (contact != null) {
							personId = contact.contactId;
						}

						displayName = getPersonName(context, personId.trim(),
								address);

						SmsMmsMessage message = new SmsMmsMessage();

						message.setFromAddress(address);
						message.setThreadId(threadId);
						message.setContactId(personId.trim());
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setTotalCount(count);
						message.setContactName(displayName);
						message.setMessageType(type);
						message.setReadType(read);

						messages.add(message);

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}

		return messages;
	}

	// 对彩信Subject进行编码
	private static String extractEncStrFromCursor(Cursor cursor,
			int columnRawBytes, int columnCharset) {
		String rawBytes = cursor.getString(columnRawBytes);
		int charset = cursor.getInt(columnCharset);

		if (TextUtils.isEmpty(rawBytes)) {
			return "";
		} else if (charset == CharacterSets.ANY_CHARSET) {
			return rawBytes;
		} else {
			return new EncodedStringValue(charset,
					PduPersister.getBytes(rawBytes)).getString();
		}
	}

	public static List<SmsMmsMessage> getConversitionByThread(Context context,
			String threadId) {
		ArrayList<SmsMmsMessage> messages = new ArrayList<SmsMmsMessage>();

		final String[] projection = new String[] { "threads._id, threads.message_count, canonical_addresses.address,"
				+ " threads.date, threads.type, threads.snippet "
				+ "from threads, canonical_addresses"
				+ " where threads.recipient_ids = canonical_addresses._id"
				+ " and threads._id = "
				+ threadId
				+ " order by threads.date desc --" };
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				CONVERSATION_CONTENT_URI, projection, null, null, null);

		String address;
		int count;
		long timestamp;
		String body;
		int type;
		long id = 0;
		EncodedStringValue encodeString;
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {
						String displayName = "";
						id = cursor.getLong(cursor.getColumnIndex("_id"));
						count = cursor.getInt(cursor
								.getColumnIndex("message_count"));
						address = cursor.getString(cursor
								.getColumnIndex("address"));
						timestamp = cursor.getLong(cursor
								.getColumnIndex("date"));
						body = cursor.getString(cursor
								.getColumnIndex("snippet"));
						type = getMessageTypeByThreadAndBody(context, id,
								new String(body.getBytes(), "utf-8"));

						ContactIdentification contact = getPersonIdFromPhoneNumber(
								context, address);
						String personId = "";
						if (contact != null) {
							personId = contact.contactId;
						}
						displayName = getPersonName(context, personId.trim(),
								address);

						SmsMmsMessage message = new SmsMmsMessage();

						message.setFromAddress(address);
						message.setThreadId(id);
						message.setContactId(personId.trim());
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setTotalCount(count);
						message.setContactName(displayName);
						message.setMessageType(type);
						messages.add(message);

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}

		return messages;
	}

	/**
	 * 读取Threads表最新消息类型
	 * 
	 * @param context
	 * @param threadId
	 * @param body
	 * @return
	 */
	public static int getMessageTypeByThreadAndBody(Context context,
			long threadId, String body) {
		int type = 1;
		final String[] projection = new String[] { " sms.type " + " from sms "
				+ " where sms.thread_id = " + threadId + " and sms.body = '"
				+ body + "' --" };
		// Create cursor
		/*
		 * Cursor cursor = context.getContentResolver().query( SMS_CONTENT_URI,
		 * projection, null, null, null);
		 */
		Cursor cursor = context.getContentResolver().query(SMS_CONTENT_URI,
				new String[] { "type" }, "sms.thread_id = ? and sms.body = ?",
				new String[] { String.valueOf(threadId), body }, null);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {
						type = cursor.getInt(cursor
								.getColumnIndexOrThrow("type"));
						return type;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}

		return type;
	}

	public static List<SmsMmsMessage> getMessagesByThreadId(Context context,
			long threadId) {
		ArrayList<SmsMmsMessage> messages = null;
		StringBuffer strBuf = new StringBuffer(
				" _id, thread_id, address, date, body, type, read from sms ");
		strBuf.append(" where thread_id = " + threadId);
		strBuf.append(" and type <> 3 ");
		strBuf.append(" order by date asc --");
		// 设置消息已读
		setThreadRead(context, threadId, true);
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				CONVERSATION_CONTENT_URI, new String[] { strBuf.toString() },
				null, null, null);

		long messageId;
		String address;
		int read;
		long timestamp;
		String body;
		int typeInt;
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					messages = new ArrayList<SmsMmsMessage>();

					while (cursor.moveToNext()) {
						String displayName = "";

						messageId = cursor
								.getLong(cursor.getColumnIndex("_id"));
						read = cursor.getInt(cursor.getColumnIndex("read"));
						typeInt = cursor.getInt(cursor.getColumnIndex("type"));
						address = cursor.getString(cursor
								.getColumnIndex("address"));
						timestamp = cursor.getLong(cursor
								.getColumnIndex("date"));
						body = cursor.getString(cursor.getColumnIndex("body"));

						ContactIdentification contact = getPersonIdFromPhoneNumber(
								context, address);
						String personId = "";
						if (contact != null) {
							personId = contact.contactId;
						}
						displayName = getPersonName(context, personId.trim(),
								address);

						SmsMmsMessage message = new SmsMmsMessage();

						message.setFromAddress(address);
						message.setThreadId(threadId);
						message.setContactId(personId.trim());
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setMessageType(typeInt);
						message.setMessageId(messageId);
						message.setReadType(read);
						message.setContactName(displayName);
						messages.add(message);

					}
				}
			} finally {
				cursor.close();
			}
		}

		((KindroidMessengerDialogueDetailActivity) context).showNotification();

		return messages;
	}

	/**
	 * 
	 * Tries to locate the message thread id given the address (phone or email)
	 * of the message sender.
	 * 
	 * @param context
	 *            a context to use
	 * @param address
	 *            phone number or email address of sender
	 * @return the thread id (or 0 if there was a problem)
	 */
	public static long findThreadIdFromAddress(Context context, String address) {
		if (address == null)
			return 0;
		String THREAD_RECIPIENT_QUERY = "recipient";

		Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();
		uriBuilder.appendQueryParameter(THREAD_RECIPIENT_QUERY, address);

		long threadId = 0;

		Cursor cursor = context.getContentResolver().query(uriBuilder.build(),
				new String[] { Contacts._ID }, null, null, null);

		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {

					threadId = cursor.getLong(0);
				}
			} finally {
				cursor.close();
			}
		}
		return threadId;
	}

	public static long findThreadIdFromAddressByDb(Context context,
			String address) {
		if (address == null)
			return 0;
		long threadId=0;

		final String[] projection = new String[] { "threads._id"
				+ " from threads, canonical_addresses"
				+ " where threads.recipient_ids = canonical_addresses._id and "
				+ "canonical_addresses.address=" + address
				+ " order by threads.date desc --" };
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				CONVERSATION_CONTENT_URI, projection, null, null, null);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {
						String displayName = "";
						// String addrLocation = "";

						threadId = cursor.getLong(cursor.getColumnIndex("_id"));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}

		return threadId;
	}

	/**
	 * Tries to locate the message id (from the system database), given the
	 * message thread id, the timestamp of the message and the type of message
	 * (sms/mms)
	 */
	synchronized public static long findMessageId(Context context,
			long threadId, long timestamp, String body, int messageType) {

		long id = 0;
		String selection = "body = " + DatabaseUtils.sqlEscapeString(body);
		final String sortOrder = "date DESC";
		final String[] projection = new String[] { "_id", "date", "thread_id",
				"body" };

		if (threadId > 0) {
			if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
				// It seems MMS timestamps are stored in a seconds, whereas SMS
				// timestamps are in millis
				selection += " and date = " + (timestamp / 1000);
			}

			Cursor cursor = context.getContentResolver().query(
					ContentUris.withAppendedId(CONVERSATION_CONTENT_URI,
							threadId), projection, selection, null, sortOrder);

			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						id = cursor.getLong(0);
					}
				} finally {
					cursor.close();
				}
			}
		}
		return id;
	}

	/**
	 * get favorite contacts
	 * 
	 * @param context
	 * @return
	 */
	public static List<SmsMmsMessage> getFavoriteContacts(Context context,
			boolean isLimit) {
		ArrayList<SmsMmsMessage> messages = null;
		StringBuffer strBuf = new StringBuffer(
				"threads._id, threads.message_count, canonical_addresses.address,"
						+ " threads.date, threads.snippet "
						+ "from threads, canonical_addresses"
						+ " where threads.recipient_ids = canonical_addresses._id order by threads.message_count desc ");
		if (isLimit) {
			strBuf.append(" limit 0,5 --");
		} else {
			strBuf.append("--");
		}
		final String[] projection = new String[] { strBuf.toString() };
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				CONVERSATION_CONTENT_URI, projection, null, null, null);

		String address;
		long threadId;
		int count;
		long timestamp;
		String body;

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					messages = new ArrayList<SmsMmsMessage>();
					while (cursor.moveToNext()) {
						String displayName = "";

						threadId = cursor.getLong(cursor.getColumnIndex("_id"));
						count = cursor.getInt(cursor
								.getColumnIndex("message_count"));
						address = cursor.getString(cursor
								.getColumnIndex("address"));
						timestamp = cursor.getLong(cursor
								.getColumnIndex("date"));
						body = cursor.getString(cursor
								.getColumnIndex("snippet"));

						ContactIdentification contact = getPersonIdFromPhoneNumber(
								context, address);
						String personId = "";
						if (contact != null) {
							personId = contact.contactId;
						}
						displayName = getPersonName(context, personId.trim(),
								address);

						SmsMmsMessage message = new SmsMmsMessage();

						message.setFromAddress(address);
						message.setThreadId(threadId);
						message.setContactId(personId.trim());
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setTotalCount(count);
						message.setContactName(displayName);
						message.setChkBoxSelected(false);

						messages.add(message);
					}
				}
			} finally {
				cursor.close();
			}
		}
		return messages;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static List<SmsMmsMessage> getThreadsByIds(Context context,
			Long[] threadIds) {
		ArrayList<SmsMmsMessage> messages = null;
		String threadIdsStr = PhoneUtils.formatArray(threadIds);

		StringBuffer strBuf = new StringBuffer(
				"threads._id, threads.message_count, canonical_addresses.address,"
						+ " threads.date, threads.snippet "
						+ "from threads, canonical_addresses"
						+ " where threads.recipient_ids = canonical_addresses._id ");
		strBuf.append(" and threads._id in (" + threadIdsStr + ")");
		strBuf.append(" order by threads.message_count desc --");
		final String[] projection = new String[] { strBuf.toString() };
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				CONVERSATION_CONTENT_URI, projection, null, null, null);

		String address;
		long threadId;
		int count;
		long timestamp;
		String body;

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					messages = new ArrayList<SmsMmsMessage>();
					while (cursor.moveToNext()) {
						String displayName = "";

						threadId = cursor.getLong(cursor.getColumnIndex("_id"));
						count = cursor.getInt(cursor
								.getColumnIndex("message_count"));
						address = cursor.getString(cursor
								.getColumnIndex("address"));
						timestamp = cursor.getLong(cursor
								.getColumnIndex("date"));
						body = cursor.getString(cursor
								.getColumnIndex("snippet"));

						ContactIdentification contact = getPersonIdFromPhoneNumber(
								context, address);
						String personId = "";
						if (contact != null) {
							personId = contact.contactId;
						}
						displayName = getPersonName(context, personId.trim(),
								address);

						SmsMmsMessage message = new SmsMmsMessage();

						message.setFromAddress(address);
						message.setThreadId(threadId);
						message.setContactId(personId.trim());
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setTotalCount(count);
						message.setContactName(displayName);
						message.setChkBoxSelected(false);

						messages.add(message);
					}
				}
			} finally {
				cursor.close();
			}
		}
		return messages;
	}

	/**
	 * Marks a specific message thread as read - all messages in the thread will
	 * be marked read
	 */
	synchronized public static void setThreadRead(Context context,
			long threadId, boolean markRead) {
		if (!markRead)
			return;
		if (threadId > 0) {
			ContentValues values = new ContentValues(1);
			values.put("read", READ_THREAD);
			ContentResolver cr = context.getContentResolver();
			int result = 0;
			try {
				result = cr
						.update(ContentUris.withAppendedId(
								CONVERSATION_CONTENT_URI, threadId), values,
								null, null);
				setMessageReadByThreadId(context, threadId,
						SmsMmsMessage.MESSAGE_TYPE_MESSAGE, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Marks a specific message as read
	 */
	synchronized public static void setMessageRead(Context context,
			long messageId, int messageType, boolean markRead) {
		if (!markRead)
			return;

		if (messageId > 0) {
			ContentValues values = new ContentValues(1);
			values.put("read", READ_THREAD);

			Uri messageUri;

			if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
				// Used to use URI of MMS_CONTENT_URI and it wasn't working, not
				// sure why
				// this is diff to SMS
				messageUri = Uri.withAppendedPath(MMS_INBOX_CONTENT_URI,
						String.valueOf(messageId));
			} else if (SmsMmsMessage.MESSAGE_TYPE_SMS == messageType) {
				messageUri = Uri.withAppendedPath(SMS_CONTENT_URI,
						String.valueOf(messageId));
			} else {

				ContentResolver cr = context.getContentResolver();
				int result;
				try {
					cr.update(MMS_INBOX_CONTENT_URI, values, null, null);
					cr.update(SMS_CONTENT_URI, values, null, null);
				} catch (Exception e) {
					result = 0;
				}
				return;
			}

			ContentResolver cr = context.getContentResolver();
			int result;
			try {
				result = cr.update(messageUri, values, null, null);
			} catch (Exception e) {
				result = 0;
			}
		}
	}

	/**
	 * 设置消息已读
	 * 
	 * @param context
	 * @param threadId
	 *            会话Id
	 * @param messageType
	 * @param markRead
	 */
	synchronized public static void setMessageReadByThreadId(Context context,
			long threadId, int messageType, boolean markRead) {
		ContentValues values = new ContentValues(1);
		values.put("read", READ_THREAD);

		Uri messageUri;
		if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
			// Used to use URI of MMS_CONTENT_URI and it wasn't working, not
			// sure why
			// this is diff to SMS
			messageUri = MMS_INBOX_CONTENT_URI;
		} else if (SmsMmsMessage.MESSAGE_TYPE_SMS == messageType) {
			messageUri = SMS_CONTENT_URI;
		} else {
			messageUri = MMS_SMS_CONTENT_URI;
		}
		ContentResolver cr = context.getContentResolver();
		int result;
		String where = "thread_id = " + threadId;
		try {
			result = cr.update(messageUri, values, where, null);
		} catch (Exception e) {
			result = 0;
		}
	}

	/**
	 * Tries to delete a message from the system database, given the thread id,
	 * the timestamp of the message and the message type (sms/mms).
	 */
	public static int deleteMessage(Context context, long messageId,
			long threadId, int messageType) {

		if (messageId > 0) {
			// We need to mark this message read first to ensure the entire
			// thread is marked as read
			setMessageRead(context, messageId, messageType, true);

			// Construct delete message uri
			Uri deleteUri;

			if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
				deleteUri = Uri.withAppendedPath(MMS_CONTENT_URI,
						String.valueOf(messageId));
			} else if (SmsMmsMessage.MESSAGE_TYPE_SMS == messageType) {
				deleteUri = Uri.withAppendedPath(SMS_CONTENT_URI,
						String.valueOf(messageId));
			} else {
				return 0;
			}

			int count = 0;
			try {
				count = context.getContentResolver().delete(deleteUri, null,
						null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return count;
		} else {
			return 0;
		}
	}

	/**
	 * 根据ThreadId 删除消息
	 * 
	 * @param context
	 * @param threadId
	 * @param messageType
	 */
	public static void deleteMessageByThreadId(Context context, long threadId,
			int messageType) {

		// Construct delete message uri
		Uri deleteUri;

		if (SmsMmsMessage.MESSAGE_TYPE_MMS == messageType) {
			deleteUri = MMS_CONTENT_URI;
		} else if (SmsMmsMessage.MESSAGE_TYPE_SMS == messageType) {
			deleteUri = SMS_CONTENT_URI;
		} else {
			int count = 0;
			try {
				count = context.getContentResolver().delete(MMS_CONTENT_URI,
						"thread_id = " + threadId, null);
				count = context.getContentResolver().delete(SMS_CONTENT_URI,
						"thread_id = " + threadId, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		int count = 0;
		try {
			context.getContentResolver().delete(CONVERSATION_CONTENT_URI,
					"thread_id = " + threadId, null);
			count = context.getContentResolver().delete(deleteUri, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteThread(Context context, long threadId,
			int messageType) {
		deleteMessageByThreadId(context, threadId, messageType);
	}

	/**
	 * get history contacts
	 * 
	 * @param context
	 * @return
	 */
	public static List<SmsMmsMessage> getHistoryMessages(Context context,
			int type) {
		ArrayList<SmsMmsMessage> messages = null;
		StringBuffer strBuf = new StringBuffer(
				" _id, thread_id, address, date, body, type, read from sms ");
		String start;
		String end;
		if (type == YESTERDAY_HISTORY) {
			strBuf.append(" where date ");
			strBuf.append(" between strftime('%s', datetime('now', 'start of day', '-1 day'), 'localtime') and ");
			strBuf.append(" strftime('%s', datetime('now', 'start of day', '-1 second'), 'localtime') ");
		} else if (type == LAST_WEEK_HISTORY) {
			start = DateTimeUtil
					.getStartAndEndDate(DateTimeUtil.DateTag.LAST_WEEK_START);
			end = DateTimeUtil
					.getStartAndEndDate(DateTimeUtil.DateTag.LAST_WEEK_END);
			strBuf.append(" where date ");
			strBuf.append(" between strftime('%s', datetime('" + start
					+ "'), 'localtime') and ");
			strBuf.append(" strftime('%s', datetime('" + end
					+ "'), 'localtime') ");
		} else if (type == LAST_MONTH_HISTORY) {
			start = DateTimeUtil
					.getStartAndEndDate(DateTimeUtil.DateTag.LAST_MONTH_START);
			end = DateTimeUtil
					.getStartAndEndDate(DateTimeUtil.DateTag.LAST_MONTH_END);
			strBuf.append(" where date ");
			strBuf.append(" between strftime('%s', datetime('" + start
					+ "'), 'localtime') and ");
			strBuf.append(" strftime('%s', datetime('" + end
					+ "'), 'localtime') ");
		} else if (type == HALF_YEAR_HISTORY) {
			strBuf.append(" where date < strftime('%s', datetime('now', 'start of month', '-6 month'), 'localtime') ");
		}
		strBuf.append(" order by date desc --");

		Log.v(TAG, "sql str:" + strBuf.toString());
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				SMS_INBOX_CONTENT_URI, new String[] { strBuf.toString() },
				null, null, null);
		long messageId;
		String address;
		long threadId;
		int read;
		long timestamp;
		String body;
		int typeInt;

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					messages = new ArrayList<SmsMmsMessage>();

					while (cursor.moveToNext()) {
						String displayName = "";

						messageId = cursor
								.getLong(cursor.getColumnIndex("_id"));
						threadId = cursor.getLong(cursor
								.getColumnIndex("thread_id"));
						read = cursor.getInt(cursor.getColumnIndex("read"));
						typeInt = cursor.getInt(cursor.getColumnIndex("type"));
						address = cursor.getString(cursor
								.getColumnIndex("address"));
						timestamp = cursor.getLong(cursor
								.getColumnIndex("date"));
						body = cursor.getString(cursor.getColumnIndex("body"));

						ContactIdentification contact = getPersonIdFromPhoneNumber(
								context, address);
						String personId = "";
						if (contact != null) {
							personId = contact.contactId;
						}
						displayName = getPersonName(context, personId.trim(),
								address);
						displayName = "";
						SmsMmsMessage message = new SmsMmsMessage();

						message.setFromAddress(address);
						message.setThreadId(threadId);
						message.setContactId(personId.trim());
						message.setMessageBody(body);
						message.setTimestamp(timestamp);
						message.setMessageType(typeInt);
						message.setMessageId(messageId);
						message.setReadType(read);
						message.setContactName(displayName);
						messages.add(message);
					}
				}

			} finally {

				cursor.close();
			}
		}

		return messages;
	}

	/*
	 * Class to hold contact lookup info (as of Android 2.0+ we need the id and
	 * lookup key)
	 */
	public static class ContactIdentification {
		public String contactId = null;
		String contactLookup = null;
		String contactName = null;

		public ContactIdentification(String _contactId, String _contactLookup,
				String _contactName) {
			contactId = _contactId;
			contactLookup = _contactLookup;
			contactName = _contactName;
		}
	}

	/**
	 * Looks up a contacts id, given their address (phone number in this case).
	 * Returns null if not found
	 */
	public static ContactIdentification getPersonIdFromPhoneNumber(
			Context context, String address) {
		if (address == null)
			return null;

		Cursor cursor = context.getContentResolver().query(
				Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
						Uri.encode(address)),
				new String[] { PhoneLookup._ID, PhoneLookup.DISPLAY_NAME,
						PhoneLookup.LOOKUP_KEY }, null, null, null);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					String contactId = String.valueOf(cursor.getLong(0));
					String contactName = cursor.getString(1);
					String contactLookup = cursor.getString(2);

					return new ContactIdentification(contactId, contactLookup,
							contactName);
				}
			} finally {
				cursor.close();
			}
		}

		return null;
	}

	/**
	 * Looks up a contacts display name by contact id - if not found, the
	 * address (phone number) will be formatted and returned instead.
	 */
	public static String getPersonName(Context context, String id,
			String address) {

		// Check for id, if null return the formatting phone number as the name
		if (id == null || "".equals(id.trim())) {
			if (address != null && !"".equals(address.trim())) {
				return PhoneNumberUtils.formatNumber(address);
			} else {
				return null;
			}
		}

		Cursor cursor = context.getContentResolver().query(
				Uri.withAppendedPath(Contacts.CONTENT_URI, id),
				new String[] { Contacts.DISPLAY_NAME }, null, null, null);

		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					String name = cursor.getString(0);
					return name;
				}
			} finally {
				cursor.close();
			}
		}

		if (address != null) {
			return PhoneNumberUtils.formatNumber(address);
		}

		return null;
	}

	/**
	 * 
	 * Looks up a contact photo by contact id, returns a Bitmap array that
	 * represents their photo (or null if not found or there was an error.
	 * 
	 * I do my own scaling and validation of sizes - Android supports any size
	 * for contact photos and some apps are adding huge photos to contacts.
	 * Doing the scaling myself allows me more control over how things play out
	 * in those cases.
	 * 
	 * @param context
	 *            the context
	 * @param id
	 *            contact id
	 * @param maxThumbSize
	 *            the max size the thumbnail can be
	 * @return Bitmap of the contacts photo (null if none or an error)
	 */
	public static Bitmap getPersonPhoto(Context context, final Uri contactUri,
			final int thumbSize) {

		if (contactUri == null)
			return null;

		// Init cache
		if (bitmapCache == null) {
			int cacheSize = bitmapCacheSize * thumbSize * thumbSize;
			bitmapCache = new LruCache<Uri, Bitmap>(cacheSize) {
				protected int sizeOf(Uri key, Bitmap value) {
					return thumbSize * thumbSize;
				}
			};
		}

		// Check bitmap cache
		synchronized (bitmapCache) {
			if (bitmapCache.get(contactUri) != null) {
				return bitmapCache.get(contactUri);
			}
		}

		// First let's just check the dimensions of the contact photo
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		// The height and width are stored in 'options' but the photo itself is
		// not loaded
		loadContactPhoto(context, contactUri, 0, options);

		// Raw height and width of contact photo
		int height = options.outHeight;
		int width = options.outWidth;

		// If photo is too large or not found get out
		if (height > CONTACT_PHOTO_MAXSIZE || width > CONTACT_PHOTO_MAXSIZE
				|| width == 0 || height == 0)
			return null;

		// This time we're going to do it for real
		options.inJustDecodeBounds = false;

		int newHeight = thumbSize;
		int newWidth = thumbSize;

		// If we have an abnormal photo size that's larger than thumbsize then
		// sample it down
		boolean sampleDown = false;

		if (height > thumbSize || width > thumbSize) {
			sampleDown = true;
		}

		// If the dimensions are not the same then calculate new scaled
		// dimenions
		if (height < width) {
			if (sampleDown) {
				options.inSampleSize = Math.round(height / thumbSize);
			}
			newHeight = Math.round(thumbSize * height / width);
		} else {
			if (sampleDown) {
				options.inSampleSize = Math.round(width / thumbSize);
			}
			newWidth = Math.round(thumbSize * width / height);
		}

		// Fetch the real contact photo (sampled down if needed)
		Bitmap contactBitmap = null;
		try {
			contactBitmap = loadContactPhoto(context, contactUri, 0, options);
		} catch (OutOfMemoryError e) {
		}

		// Not found or error, get out
		if (contactBitmap == null)
			return null;

		// Bitmap scaled to new height and width
		Bitmap finalBitmap = Bitmap.createScaledBitmap(contactBitmap, newWidth,
				newHeight, true);

		// Add to bitmap cache
		synchronized (bitmapCache) {
			if (bitmapCache.get(contactUri) == null) {
				bitmapCache.put(contactUri, finalBitmap);
			}
		}

		return finalBitmap;
	}

	/**
	 * Return current unread message count from system db (sms and mms)
	 * 
	 * @param context
	 * @return unread sms+mms message count
	 */
	public static int getUnreadMessagesCount(Context context) {
		return getUnreadMessagesCount(context, 0, null);
	}

	/**
	 * Return current unread message count from system db (sms and mms)
	 * 
	 * @param context
	 * @param timestamp
	 *            only messages before this timestamp will be counted
	 * @return unread sms+mms message count
	 */
	synchronized public static int getUnreadMessagesCount(Context context,
			long timestamp, String messageBody) {
		return getUnreadSmsCount(context, timestamp, messageBody)
				+ getUnreadMmsCount(context);
	}

	/**
	 * Return current unread message count from system db (sms only)
	 * 
	 * @param context
	 * @param timestamp
	 *            only messages before this timestamp will be counted
	 * @return unread sms message count
	 */
	private static int getUnreadSmsCount(Context context, long timestamp,
			String messageBody) {
		final String[] projection = new String[] { SMSMMS_ID, "body" };
		final String selection = UNREAD_CONDITION;
		final String[] selectionArgs = null;
		final String sortOrder = "date DESC";
		int count = 0;
		Cursor cursor = context.getContentResolver().query(
				SMS_INBOX_CONTENT_URI, projection, selection, selectionArgs,
				sortOrder);

		if (cursor != null) {
			try {
				count = cursor.getCount();

				/*
				 * We need to check if the message received matches the most
				 * recent one in the db or not (to find out if our code ran
				 * before the system code or vice-versa)
				 */
				if (messageBody != null && count > 0) {
					if (cursor.moveToFirst()) {
						/*
						 * Check the most recent message, if the body does not
						 * match then it hasn't yet been inserted into the
						 * system database, therefore we need to add one to our
						 * total count
						 */
						if (!messageBody.equals(cursor.getString(1))) {
							count++;
						}
					}
				}
			} finally {
				cursor.close();
			}
		}

		/*
		 * If count is still 0 and timestamp is set then its likely the system
		 * db had not updated when this code ran, therefore let's add 1 so the
		 * notify will run correctly.
		 */
		if (count == 0 && timestamp > 0) {
			count = 1;
		}
		return count;
	}

	/**
	 * Return current unread message count from system db (mms only)
	 * 
	 * @param context
	 * @return unread mms message count
	 */
	private static int getUnreadMmsCount(Context context) {

		final String selection = UNREAD_CONDITION;
		final String[] projection = new String[] { SMSMMS_ID };

		int count = 0;
		Cursor cursor = context.getContentResolver().query(
				MMS_INBOX_CONTENT_URI, projection, selection, null, null);

		if (cursor != null) {
			try {
				count = cursor.getCount();
			} finally {
				cursor.close();
			}
		}
		return count;
	}

	public static Bitmap getPersonPhoto(Context context, Uri contactUri) {
		Resources res = context.getResources();
		int thumbSize = (int) res.getDimension(R.dimen.contact_thumbnail_size);
		int thumbBorder = (int) res
				.getDimension(R.dimen.contact_thumbnail_border);
		return getPersonPhoto(context, contactUri, thumbSize - thumbBorder);
	}

	/**
	 * Opens an InputStream for the person's photo and returns the photo as a
	 * Bitmap. If the person's photo isn't present returns the
	 * placeholderImageResource instead.
	 * 
	 * @param context
	 *            the Context
	 * @param id
	 *            the id of the person
	 * @param placeholderImageResource
	 *            the image resource to use if the person doesn't have a photo
	 * @param options
	 *            the decoding options, can be set to null
	 */
	public static Bitmap loadContactPhoto(Context context, Uri contactUri,
			int placeholderImageResource, BitmapFactory.Options options) {

		if (contactUri == null) {
			return loadPlaceholderPhoto(placeholderImageResource, context,
					options);
		}

		InputStream stream = Contacts.openContactPhotoInputStream(
				context.getContentResolver(), contactUri);

		Bitmap bm = stream != null ? BitmapFactory.decodeStream(stream, null,
				options) : null;
		if (bm == null) {
			bm = loadPlaceholderPhoto(placeholderImageResource, context,
					options);
		}

		return bm;
	}

	private static Bitmap loadPlaceholderPhoto(int placeholderImageResource,
			Context context, BitmapFactory.Options options) {
		if (placeholderImageResource == 0) {
			return null;
		}
		return BitmapFactory.decodeResource(context.getResources(),
				placeholderImageResource, options);
	}

	synchronized public static SmsMmsMessage getSmsDetails(Context context,
			long ignoreThreadId, boolean unreadOnly) {

		final String[] projection = new String[] { "_id", "thread_id",
				"address", "date", "body" };
		String selection = unreadOnly ? UNREAD_CONDITION : null;
		String[] selectionArgs = null;
		final String sortOrder = "date DESC";

		int count = 0;

		if (ignoreThreadId > 0) {
			selection = (selection == null) ? "" : selection + " and ";
			selection += "thread_id != ?";
			selectionArgs = new String[] { String.valueOf(ignoreThreadId) };
		}

		Cursor cursor = context.getContentResolver().query(
				SMS_INBOX_CONTENT_URI, projection, selection, selectionArgs,
				sortOrder);

		if (cursor != null) {
			try {
				count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();

					long messageId = cursor.getLong(0);
					long threadId = cursor.getLong(1);
					String address = cursor.getString(2);
					long timestamp = cursor.getLong(3);
					String body = cursor.getString(4);

					if (!unreadOnly) {
						count = 0;
					}

					SmsMmsMessage smsMessage = new SmsMmsMessage();
					smsMessage.setMessageId(messageId);
					smsMessage.setThreadId(threadId);
					smsMessage.setFromAddress(address);
					smsMessage.setTimestamp(timestamp);
					smsMessage.setMessageBody(body);
					return smsMessage;
				}
			} finally {
				cursor.close();
			}
		}
		return null;
	}

	public static SmsMmsMessage getSmsDetails(Context context) {
		return getSmsDetails(context, 0);
	}

	public static SmsMmsMessage getSmsDetails(Context context,
			boolean unreadOnly) {
		return getSmsDetails(context, 0, unreadOnly);
	}

	public static SmsMmsMessage getSmsDetails(Context context,
			long ignoreThreadId) {
		return getSmsDetails(context, ignoreThreadId, true);
	}

	synchronized public static SmsMmsMessage getMmsDetails(Context context,
			long ignoreThreadId) {

		final String[] projection = new String[] { "_id", "thread_id", "date",
				"sub", "sub_cs" };
		String selection = UNREAD_CONDITION;
		String[] selectionArgs = null;
		final String sortOrder = "date DESC";
		int count = 0;

		if (ignoreThreadId > 0) {
			selection += " and thread_id != ?";
			selectionArgs = new String[] { String.valueOf(ignoreThreadId) };
		}

		Cursor cursor = context.getContentResolver().query(
				MMS_INBOX_CONTENT_URI, projection, selection, selectionArgs,
				sortOrder);

		if (cursor != null) {
			try {
				count = cursor.getCount();
				if (count > 0) {
					cursor.moveToFirst();

					long messageId = cursor.getLong(0);
					long threadId = cursor.getLong(1);
					long timestamp = cursor.getLong(2) * 1000;
					String subject = cursor.getString(3);

					SmsMmsMessage smsMessage = new SmsMmsMessage();
					smsMessage.setMessageId(messageId);
					smsMessage.setThreadId(threadId);
					smsMessage.setTimestamp(timestamp);
					smsMessage.setMessageBody(subject);
					return smsMessage;
				}
			} finally {
				cursor.close();
			}
		}
		return null;
	}

	public static SmsMmsMessage getMmsDetails(Context context) {
		return getMmsDetails(context, 0);
	}

	public static String getMmsAddress(Context context, long messageId) {
		final String[] projection = new String[] { "address", "contact_id",
				"charset", "type" };
		final String selection = "type=137"; // "type="+ PduHeaders.FROM,

		Uri.Builder builder = MMS_CONTENT_URI.buildUpon();
		builder.appendPath(String.valueOf(messageId)).appendPath("addr");

		Cursor cursor = context.getContentResolver().query(builder.build(),
				projection, selection, null, null);

		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					return cursor.getString(0);
				}
			} finally {
				cursor.close();
			}
		}

		return context.getString(android.R.string.unknownName);
	}
}
