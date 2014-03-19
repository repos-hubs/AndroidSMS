package com.kindroid.android.util.dialog;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextPaint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.model.CategorySmsListItem;
import com.kindroid.android.model.CollectCategory;
import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.kincent.adapter.DialogueMessageDetailAdapter;
import com.kindroid.kincent.adapter.DialogueMessengerListAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueActivity;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.ui.KindroidMessengerWriteMessageActivity;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.kincent.util.PhoneUtils;
import com.kindroid.security.util.CollectionDataBase;
import com.kindroid.security.util.DateTimeUtil;
import com.kindroid.security.util.InterceptDataBase;

public class DialogueDetailOperationDialog extends Dialog implements View.OnClickListener {
	private TextView mTitleTv;
	private LinearLayout delLayout;
	private LinearLayout copyLayout;
	private LinearLayout collectLayout;
	private LinearLayout forwardLayout;

	private static final String LAYOUT_FILE = "sms_messengers_dialogue_detail_operation_dialog";
	
	private List<SmsMmsMessage> list;
	private int position;
	private Context ctx;
	private DialogueMessageDetailAdapter adapter;
	private final static int TYPE_BLACK = 1;
	private final static int TYPE_WHITE = 2;
	private ClipboardManager clipboard; // 剪切板
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContentViews();
	}
	
	private void initContentViews(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			setContentView(R.layout.sms_messengers_dialogue_detail_operation_dialog);
		} else {
			setContentView(contentView);
		}
		clipboard = (ClipboardManager) ctx.getSystemService(ctx.CLIPBOARD_SERVICE);
		findViews();
	}

	private void findViews() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);

		TextPaint tp = mTitleTv.getPaint();
		tp.setFakeBoldText(true);
		tp.setColor(R.color.white);
		mTitleTv.setText(getContext().getString(R.string.sms_dialogue_select_operations));
		
		findViewById(R.id.button_close).setOnClickListener(this);
		
		delLayout = (LinearLayout) findViewById(R.id.delLayout);
		delLayout.setOnClickListener(this);
		copyLayout = (LinearLayout) findViewById(R.id.copyLayout);
		copyLayout.setOnClickListener(this);
		collectLayout = (LinearLayout) findViewById(R.id.collectLayout);
		collectLayout.setOnClickListener(this);
		forwardLayout = (LinearLayout) findViewById(R.id.forwardLayout);
		forwardLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		final SmsMmsMessage msgInfo = list.get(position);
		final long threadId = msgInfo.getThreadId();
		final long messageId = msgInfo.getMessageId();
		final InterceptDataBase mInterDB = InterceptDataBase.get(ctx);
		switch (view.getId()) {
		case R.id.delLayout:
			int result = MessengerUtils.deleteMessage(
					ctx,
					messageId, threadId,
					SmsMmsMessage.MESSAGE_TYPE_SMS);
			if (result > 0) {
				list.remove(position);
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.copyLayout:
			clipboard.setText(msgInfo.getMessageBody());
			break;
		case R.id.sendSmsLayout:
			// 跳转到写短信页面
			Intent intent = new Intent(
					ctx,
					KindroidMessengerDialogueDetailActivity.class);
			intent.putExtra("messageInfo", msgInfo);
			ctx.startActivity(intent);
			break;
		case R.id.collectLayout:
			// 插入一条记录到收藏短信数据库表
//			showCollectionCategoryDialog(position);
			new DialogueDetailOperationCollectDialog(ctx, R.style.Theme_CustomDialog, position, list).showDialog();
			break;
		case R.id.forwardLayout:
			Intent intents = new Intent(
					ctx,
					KindroidMessengerWriteMessageActivity.class);
			intents.putExtra(
					KindroidMessengerWriteMessageActivity.FORWARD_MESSAGE_KEY,
					msgInfo.getMessageBody());
			ctx.startActivity(intents);
			break;
		}
		dismiss();
	}

	/**
	 * nc 必填选项
	 * */
	public DialogueDetailOperationDialog(Context context, int theme, int pos, List<SmsMmsMessage> dataList,
			DialogueMessageDetailAdapter adpt) {
		super(context, theme);
		this.ctx = context;
		this.position = pos;
		this.list = dataList;
		this.adapter = adpt;
	}

	public void showDialog() {
		this.show();

	}
	
	private void showCollectionCategoryDialog(final int pos) {
		final List<CollectCategory> categoryList = CollectionDataBase.get(ctx).getCategorys();
		if (null != categoryList && categoryList.size() > 0) {
			CharSequence[] items = PhoneUtils.formatCategoryList(categoryList).split(",");
			final SmsMmsMessage msgInfo = list.get(pos);
			final long messageId = msgInfo.getMessageId();
			final long threadId = msgInfo.getThreadId();
			final String messageBody = msgInfo.getMessageBody();
			final long timestamp = msgInfo.getTimestamp();
			final String addressStr = msgInfo.getFromAddress();
			final String dateStr = DateTimeUtil.long2String(timestamp, "MM/dd");
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle(ctx.getResources().getString(R.string.sms_dialogue_select_category));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					CollectCategory category = categoryList.get(item);
					CategorySmsListItem itemInfo = new CategorySmsListItem();
					itemInfo.setInsertTime(dateStr);
					itemInfo.setmAddress(addressStr);
					itemInfo.setmBody(messageBody);
					itemInfo.setmCategoryId(category.getmId());

					boolean ret = CollectionDataBase.get(ctx).insertCategorySmsItem(itemInfo);
					if (!ret) {
						Toast.makeText(ctx, R.string.collect_fail, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ctx, R.string.collect_success, Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			Toast.makeText(
					ctx,
					ctx.getResources().getString(
							R.string.sms_dialogue_detail_add_collection_first),
					Toast.LENGTH_SHORT).show();
			return;
		}

	}
	

}

