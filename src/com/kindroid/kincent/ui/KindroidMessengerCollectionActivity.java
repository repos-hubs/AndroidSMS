package com.kindroid.kincent.ui;

import java.util.ArrayList;
import java.util.List;

import com.kindroid.android.model.BlackWhiteListModel;
import com.kindroid.android.model.CollectCategory;
import com.kindroid.android.util.dialog.AddCollectCategoryDailog;
import com.kindroid.android.util.dialog.CollectLongClickDailog;
import com.kindroid.android.util.dialog.EditCollectCategoryDailog;
import com.kindroid.android.util.dialog.SureDelCategoryDailog;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.CollectionAdapter;
import com.kindroid.kincent.ui.InterceptBlackWhiteList.SectionComposerAdapter;
import com.kindroid.kincent.R;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.Constant;
import com.kindroid.security.util.InterceptDataBase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class KindroidMessengerCollectionActivity extends Activity implements
		OnItemClickListener, OnItemLongClickListener {

	private GridView mGridView;

	private List<CollectCategory> mCategorys = new ArrayList<CollectCategory>();

	private CollectionAdapter mAdapter;

	private CategoryBroact mBroact;

	private static final String LAYOUT_FILE = "sms_collection_layout";

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
			setContentView(R.layout.sms_collection_layout);
		} else {
			setContentView(contentView);
		}

		findView();
	}

	private void findView() {
		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemLongClickListener(this);
		mBroact = new CategoryBroact();
		IntentFilter it = new IntentFilter(
				Constant.BROACT_UPDATE_COLLECT_CATEGORY);
		registerReceiver(mBroact, it);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new AsyLoadRequest().execute();

	}

	class AsyLoadRequest extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			mCategorys = CollectionDataBase.get(
					KindroidMessengerCollectionActivity.this).getCategorys();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mAdapter = new CollectionAdapter(
					KindroidMessengerCollectionActivity.this, mCategorys);
			mGridView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}

	}

	class CategoryBroact extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			new AsyLoadRequest().execute();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg2 == mCategorys.size()) {

			AddCollectCategoryDailog mDialog = new AddCollectCategoryDailog(
					KindroidMessengerCollectionActivity.this,
					R.style.Theme_CustomDialog);
			mDialog.showDialog();

		} else {
			Intent intent = new Intent(
					KindroidMessengerCollectionActivity.this,
					CollectListActivity.class);
			intent.putExtra("categoryId", mCategorys.get(arg2).getmId());
			startActivity(intent);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBroact);

	}

	void showHandleDialog(final int num) {
		CollectLongClickDailog dialog=new CollectLongClickDailog(this, R.style.Theme_CustomDialog);
		dialog.setCategory(mCategorys.get(num));
		dialog.showDialog();
	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		if (arg2 != mCategorys.size()) {
			showHandleDialog(arg2);
		}

		return false;
	}

}
