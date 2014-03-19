package com.kindroid.kincent.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Data;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.android.util.dialog.AboutUsDailog;
import com.kindroid.android.util.dialog.DialogueOperationDialog;
import com.kindroid.android.util.dialog.GenealDailog;
import com.kindroid.android.util.dialog.SureDelDialogGroupDailog;
import com.kindroid.android.util.dialog.SureMarkDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.DialogueMessengerListAdapter;
import com.kindroid.kincent.adapter.FavoriteContactsListAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.notification.InterceptNotification;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.SafeUtils;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Config;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.DateTimeUtil;
import com.kindroid.security.util.HttpRequestUtil;
import com.kindroid.security.util.InterceptDataBase;

public class KindroidMessengerDialogueActivity extends Activity implements
		View.OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private final static String TAG = "KindroidMessengerActivity";
	private final static int HANDLE_MESSAGES = 0;
	private final static int HANDLE_CONTACTS = 1;
	private final static int FROM_THREAD = 0;
	private final static int FROM_TIME_STAMP = 1;

	private final static int MENU_DELETE = 0;
	private final static int MENU_DIAL = 1;
	private final static int MENU_SEND_MESSAGE = 2;
	private final static int MENU_ADD_TO_BLACK = 3;
	private final static int MENU_ADD_TO_WHITE = 4;

	private final static int TYPE_BLACK = 1;
	private final static int TYPE_WHITE = 2;

	private ListView messengerListView;
	private ListView contactPersonListView;
	private DialogueMessengerListAdapter adapter;
	private FavoriteContactsListAdapter contactAdapter;
	private static LinearLayout dialogueFuncLayout;

	private TextView historyYestordayTextView;
	private TextView historyLastWeekTextView;
	private TextView historyLastMonthTextView;
	private TextView historyHalfYearTextView;
	private TextView historyMoreTextView;

	private TextView selectAllTextView;
	private TextView markReadTextView;
	private TextView deleteTextView;
	private TextView moreFuncTextView;

	private static List<SmsMmsMessage> dataList = new ArrayList<SmsMmsMessage>(); // 短信对话数据列表
	List<SmsMmsMessage> contactsList; // 常用联系人数据列表

	private static KindroidMessengerDialogueActivity instance;

	private static final String MENU_LAYOUT_FILE = "menu_main";
	private boolean menuShowed = false;
	private PopupWindow menuWindow;

	private static final String LAYOUT_FILE = "sms_messengers_dialogue_list";
	private GenealDailog dialog;

	// for upgrade
	private boolean isActive;
	public final static int UPGRADE_VERSION = 1001;
	public final static int UPGRADE_VERSION_NO = 1002;
	private String newVersionName;
	private int newVersionCode;
	private String releaseNotes;
	private String upgradeUrl;

	private SmsBroadCastReceiver smsBroadCastReceiver;

	public static boolean SHOW_UPGRADE_DIALOG = true;
	private boolean result; //会话操作栏状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		initContentViews();
		dialog = new GenealDailog(this, R.style.Theme_CustomDialog);

		// new LoadingFavoriteContact().start();
		if (SHOW_UPGRADE_DIALOG) {
			new UpdatingThread().start();
		} else {
			SHOW_UPGRADE_DIALOG = true;
		}
		// new LoadingMessagesContext().start();
		// 主菜单
		setupMenu();

		// 生成广播处理
		smsBroadCastReceiver = new SmsBroadCastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(
				Constant.BROACT_UPDATE_MESSAGE_DIALOG);
		// 注册广播
		registerReceiver(smsBroadCastReceiver, intentFilter);

		adapter = new DialogueMessengerListAdapter(
				KindroidMessengerDialogueActivity.this, dataList);
		messengerListView.setVisibility(View.VISIBLE);
		messengerListView.setAdapter(adapter);
		if (dataList.size() == 0) {
			dialog.showDialog();
		}
		new AysLoadMessage().execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		isActive = true;
		//设置操作栏状态
		if (result) {
			dialogueFuncLayout.setVisibility(View.VISIBLE);
		} else {
			dialogueFuncLayout.setVisibility(View.GONE);
		}
		disNotification();

	}

	public void disNotification() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				new InterceptNotification(getApplication(),
						KindroidMessengerDialogueActivity.this)
						.disNotification();
			}
		});
	}

	private void initContentViews() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.sms_messengers_dialogue_list);
		} else {
			setContentView(contentView);
		}
		findViews();
	}

	private void findViews() {

		messengerListView = (ListView) findViewById(R.id.msgListView);
		messengerListView.setOnItemLongClickListener(this);
		messengerListView.setOnItemClickListener(this);

		View view = KindroidMessengerTabMain.getClassifyView();
		contactPersonListView = (ListView) view
				.findViewById(R.id.favoriteContactPersonListView);
		contactPersonListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				SmsMmsMessage message = contactsList.get(position);
				long threadId = message.getThreadId();
				int msgListPos = getDataListPosition(FROM_THREAD, threadId,
						null, null);
				messengerListView.setSelection(msgListPos);
			}
		});
		historyHalfYearTextView = (TextView) view
				.findViewById(R.id.hisHalfYearTextView);
		historyHalfYearTextView.setOnClickListener(this);
		historyLastMonthTextView = (TextView) view
				.findViewById(R.id.hisLastMonthTextView);
		historyLastMonthTextView.setOnClickListener(this);
		historyLastWeekTextView = (TextView) view
				.findViewById(R.id.hisLastWeekTextView);
		historyLastWeekTextView.setOnClickListener(this);
		historyMoreTextView = (TextView) view
				.findViewById(R.id.hisMoreTextView);
		historyMoreTextView.setOnClickListener(this);
		historyYestordayTextView = (TextView) view
				.findViewById(R.id.hisYesterdayTextView);
		historyYestordayTextView.setOnClickListener(this);

		// 功能栏Layout
		dialogueFuncLayout = (LinearLayout) findViewById(R.id.dialogueFuncLayout);

		selectAllTextView = (TextView) findViewById(R.id.selectAllTextView);
		selectAllTextView.setOnClickListener(this);
		markReadTextView = (TextView) findViewById(R.id.markReadTextView);
		markReadTextView.setOnClickListener(this);
		deleteTextView = (TextView) findViewById(R.id.deleteTextView);
		deleteTextView.setOnClickListener(this);
		moreFuncTextView = (TextView) findViewById(R.id.moreTextView);
		moreFuncTextView.setOnClickListener(this);

	}

	private View initView() {
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry
					.inflate(MENU_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			contentView = inflater.inflate(R.layout.menu_main, null);
		}
		return contentView;
	}

	private void setupMenu() {
		View menuLayout = initView();

		menuWindow = new PopupWindow(menuLayout, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		menuLayout.findViewById(R.id.menu_about_item_linear)
				.setOnClickListener(menuListener);
		menuLayout.findViewById(R.id.menu_feedback_item_linear)
				.setOnClickListener(menuListener);
		menuLayout.findViewById(R.id.menu_setting_item_linear)
				.setOnClickListener(menuListener);
		menuLayout.findViewById(R.id.menu_theme_item_linear)
				.setOnClickListener(menuListener);

	}

	private OnClickListener menuListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.menu_about_item_linear:

				new AboutUsDailog(KindroidMessengerDialogueActivity.this,
						R.style.Theme_CustomDialog).showDialog();

				break;
			case R.id.menu_feedback_item_linear:
				Intent intent = new Intent(
						KindroidMessengerDialogueActivity.this,
						FeedbackActivity.class);
				startActivity(intent);
				break;
			case R.id.menu_setting_item_linear:
				intent = new Intent(KindroidMessengerDialogueActivity.this,
						SumSettingListActivity.class);
				startActivity(intent);

				break;
			case R.id.menu_theme_item_linear:
				intent = new Intent(getParent(), KindroidThemeActivity.class);
				startActivity(intent);

				break;
			}

			menuWindow.dismiss();
			menuShowed = false;

		}
	};

	// 注册广播接收
	public class SmsBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			new AysLoadMessage().execute();

		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
			return true;
		}
		if (event.getAction() != KeyEvent.ACTION_UP) {
			return super.dispatchKeyEvent(event);
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			if (!menuShowed) {
				if (menuWindow == null) {
					setupMenu();
				} else {
					menuWindow.showAtLocation(
							findViewById(R.id.dialogueFuncLayout),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				}
				menuShowed = true;
			} else {
				menuWindow.dismiss();
				menuShowed = false;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		if (menuShowed) {
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				menuWindow.dismiss();
				menuShowed = false;
			}
			return true;
		}

		return super.dispatchTouchEvent(ev);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case HANDLE_MESSAGES:
				dialog.disMisDialog();

				if (adapter == null) {

				} else {

					adapter.notifyDataSetChanged();
					messengerListView.setVisibility(View.VISIBLE);
				}
				//
				//
				//
				// if (null != dataList && dataList.size() > 0) {
				//
				//
				// }
				// if (null != adapter) {
				// adapter.notifyDataSetChanged();
				// }

				break;
			case HANDLE_CONTACTS:

				if (null != contactsList && contactsList.size() > 0) {
					contactAdapter = new FavoriteContactsListAdapter(
							KindroidMessengerDialogueActivity.this,
							contactsList);
					contactPersonListView.setAdapter(contactAdapter);
					contactAdapter.notifyDataSetChanged();
				}

				break;

			default:
				break;
			}
		}

	};

	/**
	 * 控制功能栏是否显示，用于TabMain来调用
	 * 
	 * @param showOrNot
	 * @return
	 */
	public static boolean showFunctionBar(boolean showOrNot) {
		return instance.showFunctionBarImpl(showOrNot);
	}

	private boolean showFunctionBarImpl(boolean showOrNot) {
		result = !showOrNot;
		if (result) {
			dialogueFuncLayout.setVisibility(View.VISIBLE);
		} else {
			dialogueFuncLayout.setVisibility(View.GONE);
		}
		setRadioButtonStatus(result);
		return result;
	}

	private void setRadioButtonStatus(boolean isSelected) {
		if (null != dataList && dataList.size() > 0) {
			for (int i = 0; i < dataList.size(); i++) {
				SmsMmsMessage msg = dataList.get(i);
				msg.setShown(isSelected);
				msg.setSelected(false);
			}
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 加载会话
	 * 
	 * @author JMLI
	 * 
	 */
	// class LoadingMessagesContext extends Thread {
	//
	// @Override
	// public void run() {
	// // if (null != dataList) {
	// // dataList.clear();
	// // }
	// // dataList = MessengerUtils
	// // .getMessages(KindroidMessengerDialogueActivity.this);
	//
	// dataList.clear();
	// dataList.addAll( MessengerUtils
	// .getMessages(KindroidMessengerDialogueActivity.this));
	// Message msg = mHandler.obtainMessage();
	// msg.obj = dataList;
	// msg.arg1 = HANDLE_MESSAGES;
	// mHandler.sendMessage(msg);
	// }
	// }

	class AysLoadMessage extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			List<SmsMmsMessage> lists = MessengerUtils
					.getMessages(KindroidMessengerDialogueActivity.this);

			dataList.clear();
			dataList.addAll(lists);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.disMisDialog();

			if (adapter == null) {
				adapter = new DialogueMessengerListAdapter(
						KindroidMessengerDialogueActivity.this, dataList);
				messengerListView.setVisibility(View.VISIBLE);
				messengerListView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
				messengerListView.setVisibility(View.VISIBLE);
			}

		}

	}

	/**
	 * 加载常用联系人
	 * 
	 * @author JMLI
	 * 
	 */
	class LoadingFavoriteContact extends Thread {

		@Override
		public void run() {
			contactsList = MessengerUtils.getFavoriteContacts(
					KindroidMessengerDialogueActivity.this, true);
			Message msg = mHandler.obtainMessage();
			msg.obj = contactsList;
			msg.arg1 = HANDLE_CONTACTS;
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void onClick(View view) {
		Animation mAnimation = AnimationUtils.loadAnimation(
				KindroidMessengerDialogueActivity.this,
				R.anim.sms_dialogue_func_text_click);
		switch (view.getId()) {
		case R.id.hisHalfYearTextView:
			// historyHalfYearTextView.startAnimation(mAnimation);
			if (null != dataList && dataList.size() > 0) {
				messengerListView
						.setSelection(getDataListPosition(
								FROM_TIME_STAMP,
								0L,
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.HALF_YEAR_AGO),
								null));
			}
			break;
		case R.id.hisLastMonthTextView:
			// historyLastMonthTextView.startAnimation(mAnimation);
			if (null != dataList && dataList.size() > 0) {
				messengerListView
						.setSelection(getDataListPosition(
								FROM_TIME_STAMP,
								0L,
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.LAST_MONTH_START),
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.LAST_MONTH_END)));
			}
			break;
		case R.id.hisLastWeekTextView:
			// historyLastWeekTextView.startAnimation(mAnimation);
			if (null != dataList && dataList.size() > 0) {
				messengerListView
						.setSelection(getDataListPosition(
								FROM_TIME_STAMP,
								0L,
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.LAST_WEEK_START),
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.LAST_WEEK_END)));
			}
			break;
		case R.id.hisMoreTextView:
			// historyMoreTextView.startAnimation(mAnimation);
			if (null != dataList && dataList.size() > 0) {
				messengerListView
						.setSelection(getDataListPosition(
								FROM_TIME_STAMP,
								0L,
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.ONE_YEAR_AGO),
								null));
			}
			break;
		case R.id.hisYesterdayTextView:
			// historyYestordayTextView.startAnimation(mAnimation);
			if (null != dataList && dataList.size() > 0) {
				messengerListView
						.setSelection(getDataListPosition(
								FROM_TIME_STAMP,
								0L,
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.YESTERDAY_START),
								DateTimeUtil
										.getStartAndEndDate(DateTimeUtil.DateTag.YESTERDAY_END)));
			}
			break;

		case R.id.selectAllTextView:
			// selectAllTextView.startAnimation(mAnimation);
			if (null != dataList && dataList.size() > 0) {
				for (int i = 0; i < dataList.size(); i++) {
					SmsMmsMessage msg = dataList.get(i);
					msg.setSelected(true);
				}
			}
			if (null != adapter) {
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.markReadTextView:

			boolean isTrue = false;
			for (int i = 0; i < dataList.size(); i++) {
				SmsMmsMessage msg = dataList.get(i);
				if (msg.isSelected()) {
					isTrue = true;
					break;
				}
			}
			if (!isTrue) {
				Toast.makeText(KindroidMessengerDialogueActivity.this,
						R.string.select_the_item_mark, Toast.LENGTH_SHORT)
						.show();

			} else {
				showConfirmDialog();
			}
			break;
		case R.id.deleteTextView:
			isTrue = false;
			for (int i = 0; i < dataList.size(); i++) {
				SmsMmsMessage msg = dataList.get(i);
				if (msg.isSelected()) {
					isTrue = true;
					break;
				}
			}
			if (!isTrue) {
				Toast.makeText(KindroidMessengerDialogueActivity.this,
						R.string.select_the_item_del, Toast.LENGTH_SHORT)
						.show();
			} else {
				showConfirmDeleteDialog();
			}

			break;
		case R.id.moreTextView:
			break;
		default:
			break;
		}

	}

	/**
	 * @param type
	 *            通过ThreadId查找或TimeStamp查找
	 * @param id
	 *            threadId 或 timestamp
	 * @return
	 */
	public int getDataListPosition(int type, long id, String from, String to) {
		if (null != dataList && dataList.size() > 0) {
			for (int i = 0; i < dataList.size(); i++) {
				SmsMmsMessage msg = dataList.get(i);
				Long threadId = msg.getThreadId();
				Long timestamp = msg.getTimestamp();
				if (type == FROM_THREAD) {
					if (id == threadId) {
						return i;
					}
				}
				if (type == FROM_TIME_STAMP) {
					// 输出eg:2011-12-12 17:12
					CharSequence dateClause = DateUtils.formatDateRange(this,
							timestamp, timestamp, DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_NUMERIC_DATE
									| DateUtils.FORMAT_SHOW_YEAR
									| DateUtils.FORMAT_24HOUR);

					Date myDate = DateTimeUtil.String2Date(dateClause
							.toString());
					Date fromDate = DateTimeUtil.String2Date(from);
					if (null == to || "".equals(to) || to.length() == 0) {
						if (myDate.before(fromDate)) { // 主要针对半年前和更早
							return i;
						}
					} else {
						Date toDate = DateTimeUtil.String2Date(to);
						if (myDate.after(fromDate) && myDate.before(toDate)) {
							return i;
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 标记已读确认框
	 */
	private void showConfirmDialog() {

		SureMarkDailog dialog = new SureMarkDailog(
				KindroidMessengerDialogueActivity.this,
				R.style.Theme_CustomDialog);
		dialog.setMessages(dataList);
		dialog.setAdapter(adapter);
		dialog.showDialog();

		// Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(super.getApplicationContext().getString(
		// R.string.sms_dialog_confirm_mark));
		// //
		// builder.setMessage(super.getApplicationContext().getString(R.string.msg_quit));
		// builder.setPositiveButton(R.string.sms_contact_confirm,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// if (null != dataList && dataList.size() > 0) {
		// for (int i = 0; i < dataList.size(); i++) {
		// SmsMmsMessage msg = dataList.get(i);
		// if (msg.isSelected()) {
		//
		// long threadId = msg.getThreadId();
		// MessengerUtils
		// .setThreadRead(
		// KindroidMessengerDialogueActivity.this,
		// threadId, msg.isSelected());
		// }
		// }
		// }
		// }
		// });
		// builder.setNegativeButton(R.string.sms_contact_cancel,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// /* User clicked Cancel so do some stuff */
		// }
		// });
		// builder.show();
	}

	// 删除一条对话确认框
	private void makeDeleteDialogue(final long threadId, final int position) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(super.getApplicationContext().getString(
				R.string.sms_dialog_delete_thread));
		// builder.setMessage(super.getApplicationContext().getString(R.string.msg_quit));
		builder.setPositiveButton(R.string.sms_contact_confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						MessengerUtils.deleteThread(
								KindroidMessengerDialogueActivity.this,
								threadId, SmsMmsMessage.MESSAGE_TYPE_MESSAGE);
						dataList.remove(position);
						adapter.notifyDataSetChanged();
					}
				});
		builder.setNegativeButton(R.string.sms_contact_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked Cancel so do some stuff */
					}
				});
		builder.show();
	}

	/**
	 * 删除确认框
	 */
	private void showConfirmDeleteDialog() {

		SureDelDialogGroupDailog dialog = new SureDelDialogGroupDailog(
				KindroidMessengerDialogueActivity.this,
				R.style.Theme_CustomDialog);
		dialog.setMessages(dataList);
		dialog.setAdapter(adapter);
		dialog.showDialog();

		// Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(super.getApplicationContext().getString(
		// R.string.sms_dialog_delete_thread));
		// //
		// builder.setMessage(super.getApplicationContext().getString(R.string.msg_quit));
		// builder.setPositiveButton(R.string.sms_contact_confirm,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// if (null != dataList && dataList.size() > 0) {
		// List<SmsMmsMessage> tmpList = new ArrayList<SmsMmsMessage>();
		// for (int i = 0; i < dataList.size(); i++) {
		// SmsMmsMessage msg = dataList.get(i);
		// if (msg.isSelected()) {
		//
		// long threadId = msg.getThreadId();
		// MessengerUtils
		// .deleteThread(
		// KindroidMessengerDialogueActivity.this,
		// threadId,
		// SmsMmsMessage.MESSAGE_TYPE_MESSAGE);
		// tmpList.add(msg);
		// }
		// }
		// if (null != tmpList && tmpList.size() > 0) {
		// for (int i = 0; i < tmpList.size(); i++) {
		// dataList.remove(tmpList.get(i));
		// }
		// }
		//
		// adapter.notifyDataSetChanged();
		// }
		// }
		// });
		// builder.setNegativeButton(R.string.sms_contact_cancel,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// /* User clicked Cancel so do some stuff */
		// }
		// });
		// builder.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// SmsMmsMessage message = contactsList.get(position);
		// long threadId = message.getThreadId();
		// int msgListPos = getDataListPosition(FROM_THREAD, threadId, null,
		// null);
		// messengerListView.setSelection(msgListPos);
		SmsMmsMessage msgInfo = dataList.get(position);
		Intent intent = new Intent(this,
				KindroidMessengerDialogueDetailActivity.class);
		intent.putExtra("messageInfo", msgInfo);
		startActivity(intent);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		//2012-01-31 mod
//		makeContextMenu(position);
		new DialogueOperationDialog(this, R.style.Theme_CustomDialog, position, dataList, adapter).showDialog();
		return true;
	}

	/**
	 * 长按会话上下文对话框
	 * 
	 * @param position
	 *            会话 ListView单击的条目
	 */
	private void makeContextMenu(final int position) {
		final CharSequence[] items = getResources().getStringArray(
				R.array.dialogue_conversition_context_menu);
		final SmsMmsMessage msgInfo = dataList.get(position);
		final long threadId = msgInfo.getThreadId();
		final InterceptDataBase mInterDB = InterceptDataBase
				.get(KindroidMessengerDialogueActivity.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.sms_dialogue_select_operations));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case MENU_DELETE:
					makeDeleteDialogue(threadId, position);
					/*
					 * MessengerUtils.deleteThread(
					 * KindroidMessengerDialogueActivity.this, threadId,
					 * SmsMmsMessage.MESSAGE_TYPE_MESSAGE);
					 * dataList.remove(position);
					 * adapter.notifyDataSetChanged();
					 */
					dialog.dismiss();
					break;
				case MENU_DIAL:
					dial(msgInfo);
					dialog.dismiss();
					break;
				case MENU_SEND_MESSAGE:
					// 跳转到写短信页面
					Intent intent = new Intent(
							KindroidMessengerDialogueActivity.this,
							KindroidMessengerDialogueDetailActivity.class);
					intent.putExtra("messageInfo", msgInfo);
					startActivity(intent);
					dialog.dismiss();
					break;
				case MENU_ADD_TO_BLACK:
					if (!whiteOrBlackIsExists(TYPE_BLACK, msgInfo)) {
						mInterDB.insertBlackWhitList(TYPE_BLACK,
								msgInfo.getContactName(),
								msgInfo.getFromAddress(), true, true, 1);
					} else {
						Toast.makeText(
								KindroidMessengerDialogueActivity.this,
								getResources().getString(
										R.string.all_exist_when_addlist),
								Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
					break;
				case MENU_ADD_TO_WHITE:
					if (!whiteOrBlackIsExists(TYPE_WHITE, msgInfo)) {
						mInterDB.insertBlackWhitList(TYPE_WHITE,
								msgInfo.getContactName(),
								msgInfo.getFromAddress(), true, true, 1);
					} else {
						Toast.makeText(
								KindroidMessengerDialogueActivity.this,
								getResources().getString(
										R.string.all_exist_when_addlist),
								Toast.LENGTH_SHORT).show();
					}

					dialog.dismiss();
					break;
				default:
					break;
				}
			}
		});
		// if (null != mInterDB) {
		// InterceptDataBase.close();
		// }
		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean whiteOrBlackIsExists(int type, SmsMmsMessage msgInfo) {
		NativeCursor nc = new NativeCursor();
		nc.setmRequestType(type);
		nc.setmContactName(msgInfo.getContactName());
		nc.setmPhoneNum(msgInfo.getFromAddress());
		nc = InterceptDataBase.get(KindroidMessengerDialogueActivity.this)
				.selectIsExists(nc);
		return nc.ismIsExists();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isActive = false;

		if (menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;

		}

		// 当点击其它Tab时，隐掉菜单栏
		dialogueFuncLayout.setVisibility(View.GONE);
		// 修改显示标志
		KindroidMessengerTabMain.functionTag = false;
	}

	protected Handler mGlobalHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!isActive)
				return;
			switch (msg.what) {
			case UPGRADE_VERSION:
				showUpgradeDialog();
				break;
			case UPGRADE_VERSION_NO:

				break;
			default:
				break;
			}
		}
	};

	private void showUpgradeDialog() {
		SharedPreferences sp = KindroidMessengerApplication
				.getSharedPrefs(this);
		int mShowUpgradePrompt = sp.getInt(
				com.kindroid.kincent.util.Constant.SHOW_UPGRADE_PROMPT, 1);
		int mOldVersion = sp.getInt(
				com.kindroid.kincent.util.Constant.LAST_UPGRADE_VERSION, -1);
		if ((newVersionCode > mOldVersion) || (mShowUpgradePrompt == 1)) {
			UpgradeDialog upgradeDialog = new UpgradeDialog(this,
					R.style.Theme_CustomDialog, this.upgradeUrl,
					this.releaseNotes, this.newVersionName, this.newVersionCode);
			upgradeDialog.show();
		}
	}

	public boolean hasNewVersion(int oldVersionCode, Context ctx) {
		boolean b = false;
		try {
			String url = null;
			StringBuilder sb = new StringBuilder();
			sb.append(Constant.UPGRADE_URL)
					.append(Constant.MESSENGER_PRODUCT_ID).append('-')
					.append(Config.MARKET_ID).append('-');

			if (Locale.getDefault().getLanguage().startsWith("zh")) {
				sb.append(1);
				url = sb.toString();
			} else {
				sb.append(2);
				url = sb.toString();
			}
			String str = HttpRequestUtil.getData(url);
			JSONObject jobj = new JSONObject(str);
			if (jobj != null) {
				int result = jobj.getInt("result");
				if (result == 0) {

					newVersionCode = jobj.getInt("versioncode");
					if (newVersionCode > oldVersionCode) {
						b = true;
						upgradeUrl = jobj.getString("upgradePath");
						releaseNotes = jobj.getString("releaseNote");
						newVersionName = jobj.getString("versionname");
					}
				}
			}

		} catch (Exception ex) {
			b = false;
		}

		return b;
	}

	class UpdatingThread extends Thread {
		public void run() {
			if (!SafeUtils.checkNetwork(KindroidMessengerDialogueActivity.this)) {
				Log.d("KindroidMessenger", " network error");
				return;
			}
			int versionCode = 0;
			PackageManager manager = getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
				versionCode = info.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (hasNewVersion(versionCode,
					KindroidMessengerDialogueActivity.this)) {
				mGlobalHandle.sendEmptyMessage(UPGRADE_VERSION);
			} else {
				mGlobalHandle.sendEmptyMessage(UPGRADE_VERSION_NO);
			}
		}
	}

	/**
	 * 拨打电话
	 * 
	 * @param msgInfo
	 */
	public void dial(SmsMmsMessage msgInfo) {
		try {
			Intent myIntentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + msgInfo.getFromAddress()));
			startActivity(myIntentDial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// unregisterReceiver(smsBroadCastReceiver);
		super.onDestroy();
	}

}
