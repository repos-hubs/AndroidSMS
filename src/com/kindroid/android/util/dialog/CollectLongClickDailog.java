package com.kindroid.android.util.dialog;

import java.text.ParseException;

import com.kindroid.android.model.CollectCategory;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.InterceptHistoryAdapter;
import com.kindroid.kincent.adapter.ListTextGeneralAdapter;
import com.kindroid.kincent.adapter.MoreBlackWhiteDialogAdapter;
import com.kindroid.kincent.adapter.MoreHistoryDialogAdapter;
import com.kindroid.kincent.ui.KindroidMessengerCollectionActivity;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.ui.PrivacyDialogueDetailActivity;
import com.kindroid.kincent.R;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.HistoryNativeCursor;
import com.kindroid.security.util.InterceptDataBase;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CollectLongClickDailog extends Dialog implements
		View.OnClickListener, OnItemClickListener {
	private TextView mTitleTv;
	private ListView mListView;
	private HistoryNativeCursor hnc;
	private InterceptHistoryAdapter mAdapter;

	private GenealDailog mGeneralDialog;
	
	private CollectCategory  category;

	public CollectCategory getCategory() {
		return category;
	}

	public void setCategory(CollectCategory category) {
		this.category = category;
	}

	private static final String LAYOUT_FILE = "more_balck_whitelist_dialog";

	public HistoryNativeCursor getHnc() {
		return hnc;
	}

	public void setHnc(HistoryNativeCursor hnc) {
		this.hnc = hnc;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentViews();
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
			setContentView(R.layout.more_balck_whitelist_dialog);
		} else {
			setContentView(contentView);
		}

		findView();
	}

	private void findView() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);

		findViewById(R.id.button_close).setOnClickListener(this);
		TextPaint tp = mTitleTv.getPaint();
		tp.setFakeBoldText(true);
		mTitleTv.setText(getContext().getString(R.string.sms_remind));
		mListView = (ListView) findViewById(R.id.listproc);
		mListView.setOnItemClickListener(this);
		mGeneralDialog = new GenealDailog(getContext(),
				R.style.Theme_CustomDialog);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_close:
			dismiss();
			break;

		}
	}

	/**
	 * nc 必填选项
	 * */
	public CollectLongClickDailog(Context context, int theme
			) {
		super(context, theme);
		
	}

	public void showDialog() {
		this.show();
		ListTextGeneralAdapter adapter = null;

		int stringId[] = new int[] { R.string.privacy_auto_msg_list_edit_text,
				R.string.privacy_auto_msg_list_delete_text };

		adapter = new ListTextGeneralAdapter(getContext(), stringId, null);
		mListView.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		dismiss();
		switch (arg2) {
		case 0:
			EditCollectCategoryDailog dialoget=new EditCollectCategoryDailog(getContext(), R.style.Theme_CustomDialog);
			dialoget.setId(category.getmId());
			dialoget.setContent(category.getmName());
			dialoget.showDialog();
			break;
		case 1:
			SureDelCategoryDailog dialogDel=new SureDelCategoryDailog(getContext(), R.style.Theme_CustomDialog);
			dialogDel.setId(category.getmId());
			dialogDel.showDialog();
	
			break;

		}
	}

	private boolean numberExistInContacts(String num) {
		return false;
	}

	class AsyRestoreSms extends AsyncTask<Void, Void, Void> {

		private NativeCursor nc;

		public AsyRestoreSms(NativeCursor nc) {
			this.nc = nc;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			CollectLongClickDailog.this.cancel();
			mGeneralDialog.showDialog();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Cursor c = InterceptDataBase.get(getContext())
					.selectAllInterceptHistoryListByNum(nc.getmPhoneNum());
			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {

					int id = c.getInt(c.getColumnIndex(InterceptDataBase.ID));
					String address = c.getString(c
							.getColumnIndex(InterceptDataBase.ADDRESS));
					String date = c.getString(c
							.getColumnIndex(InterceptDataBase.DATE));
					String body = c.getString(c
							.getColumnIndex(InterceptDataBase.BODY));
					int read = c.getInt(c
							.getColumnIndex(InterceptDataBase.READ));
					HistoryNativeCursor hnc = new HistoryNativeCursor();
					hnc.setmId(id);
					hnc.setmAddress(address);
					hnc.setmOriginDate(date);
					try {
						date = InterceptDataBase.DF_DATE_MD
								.format(InterceptDataBase.DF_DATE.parse(date));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					hnc.setmDate(date);
					hnc.setmBody(body);
					hnc.setmRead(read);
					hnc.setmRequestType(3);
					SureRestorySmsDailog.InsertSmstoBox(getContext(), hnc);
					InterceptDataBase.get(getContext()).DelHistory(hnc);
				}
			}

			// String contactName = mRemarkEt.getText().toString().trim();

			InterceptDataBase.get(getContext()).insertBlackWhitList(
					nc.getmRequestType(), "", nc.getmPhoneNum(), true, true, 1);
			if (c != null) {
				c.close();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// getContext().sendBroadcast(
			// new Intent(Constant.BROACT_INTERCEPT_BALCKWHITE_LIST));

			Intent intent = new Intent(Constant.BROACT_UPDATE_INTERCEPT_HISTORY);
			intent.putExtra("sms_or_phone", 3);
			getContext().sendBroadcast(intent);

			mGeneralDialog.disMisDialog();
			Toast.makeText(getContext(), R.string.add_list_complete,
					Toast.LENGTH_LONG).show();

		}

		// private void InsertSmstoBox(Context context, HistoryNativeCursor hnc)
		// {
		//
		// final String ADDRESS = "address";
		// final String DATE = "date";
		// final String READ = "read";
		// final String STATUS = "status";
		// final String TYPE = "type";
		// final String BODY = "body";
		// ContentValues values = new ContentValues();
		// /* 手机号 */
		// values.put(ADDRESS, hnc.getmAddress());
		// /* 时间 */
		// if (hnc.getmDate() != null && hnc.getmDate().length() > 0) {
		// try {
		// long time = InterceptDataBase.DF_DATE_MD.parse(
		// hnc.getmDate()).getTime();
		// values.put(DATE, time);
		// } catch (java.text.ParseException pe) {
		// pe.printStackTrace();
		// }
		//
		// }
		// values.put(READ, 1);
		// values.put(STATUS, -1);
		// /* 类型1为收件箱，2为发件箱 */
		// values.put(TYPE, 1);
		// /* 短信体内容 */
		// values.put(BODY, hnc.getmBody());
		// /* 插入数据库操作 */
		// context.getContentResolver().insert(Uri.parse("content://sms"),
		// values);
		//
		// }

	}

}
