package com.kindroid.android.util.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.CollectCategory;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.kincent.adapter.DialogCollectionAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.DateTimeUtil;

public class DialogueDetailOperationCollectDialog extends Dialog implements
		View.OnClickListener, OnItemClickListener {
	private TextView mTitleTv;
	private ListView mListView;
	private Context ctx;
	private List<SmsMmsMessage> list;
	private int position;
	private static final String LAYOUT_FILE = "sms_messengers_dialogue_detail_operation_collect_dialog";
	private List<CollectCategory> categoryList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			setContentView(R.layout.sms_messengers_dialogue_detail_operation_collect_dialog);
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
		mTitleTv.setText(getContext().getString(R.string.sms_dialogue_select_category));
		mListView = (ListView) findViewById(R.id.listproc);
		mListView.setOnItemClickListener(this);

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
	public DialogueDetailOperationCollectDialog(Context context, int theme, int pos, List<SmsMmsMessage> dataList) {
		super(context, theme);
		this.ctx = context;
		this.position = pos;
		this.list = dataList;
	}

	public void showDialog() {
		this.show();
		DialogCollectionAdapter adapter = null;
		categoryList = CollectionDataBase.get(ctx).getCategorys();
		if (null != categoryList && categoryList.size() > 0) {
			CharSequence[] items = PhoneUtils.formatCategoryList(categoryList).split(",");
			adapter = new DialogCollectionAdapter(getContext(), items);
			mListView.setAdapter(adapter);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CollectCategory category = categoryList.get(arg2);
		CategorySmsListItem itemInfo = new CategorySmsListItem();
		final SmsMmsMessage msgInfo = list.get(position);
		final String messageBody = msgInfo.getMessageBody();
		final long timestamp = msgInfo.getTimestamp();
		final String addressStr = msgInfo.getFromAddress();
		final String dateStr = DateTimeUtil.long2String(timestamp, "MM/dd");
		itemInfo.setInsertTime(dateStr);
		itemInfo.setmAddress(addressStr);
		itemInfo.setmBody(messageBody);
		itemInfo.setmCategoryId(category.getmId());

		dismiss();
		boolean ret = CollectionDataBase.get(ctx).insertCategorySmsItem(itemInfo);
		if (!ret) {
			Toast.makeText(ctx, R.string.collect_fail, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ctx, R.string.collect_success, Toast.LENGTH_SHORT).show();
		}
		
	}

}
