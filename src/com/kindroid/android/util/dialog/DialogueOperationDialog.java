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
import android.text.TextPaint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kindroid.android.model.NativeCursor;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.R;
import com.kindroid.kincent.adapter.DialogueMessengerListAdapter;
import com.kindroid.kincent.data.SmsMmsMessage;
import com.kindroid.kincent.ui.KindroidMessengerDialogueActivity;
import com.kindroid.kincent.ui.KindroidMessengerDialogueDetailActivity;
import com.kindroid.kincent.util.MessengerUtils;
import com.kindroid.security.util.InterceptDataBase;

public class DialogueOperationDialog extends Dialog implements View.OnClickListener {
	private TextView mTitleTv;
	private LinearLayout delLayout;
	private LinearLayout dialLayout;
	private LinearLayout sendLayout;
	private LinearLayout addBlackLayout;
	private LinearLayout addWhiteLayout;

	private static final String LAYOUT_FILE = "sms_messengers_dialogue_operation_dialog";
	
	private List<SmsMmsMessage> list;
	private int position;
	private Context ctx;
	private DialogueMessengerListAdapter adapter;
	private final static int TYPE_BLACK = 1;
	private final static int TYPE_WHITE = 2;
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
			setContentView(R.layout.sms_messengers_dialogue_operation_dialog);
		} else {
			setContentView(contentView);
		}
		
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
		dialLayout = (LinearLayout) findViewById(R.id.dialLayout);
		dialLayout.setOnClickListener(this);
		sendLayout = (LinearLayout) findViewById(R.id.sendSmsLayout);
		sendLayout.setOnClickListener(this);
		addBlackLayout = (LinearLayout) findViewById(R.id.addToBlackLayout);
		addBlackLayout.setOnClickListener(this);
		addWhiteLayout = (LinearLayout) findViewById(R.id.addToWhiteLayout);
		addWhiteLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		final SmsMmsMessage msgInfo = list.get(position);
		final long threadId = msgInfo.getThreadId();
		final InterceptDataBase mInterDB = InterceptDataBase.get(ctx);
		switch (view.getId()) {
		case R.id.delLayout:
			makeDeleteDialogue(threadId, position);
			break;
		case R.id.dialLayout:
			dial(msgInfo);
			break;
		case R.id.sendSmsLayout:
			// 跳转到写短信页面
			Intent intent = new Intent(
					ctx,
					KindroidMessengerDialogueDetailActivity.class);
			intent.putExtra("messageInfo", msgInfo);
			ctx.startActivity(intent);
			break;
		case R.id.addToBlackLayout:
			if (!whiteOrBlackIsExists(TYPE_WHITE, msgInfo)) {
				mInterDB.insertBlackWhitList(TYPE_WHITE,
						msgInfo.getContactName(),
						msgInfo.getFromAddress(), true, true, 1);
			} else {
				Toast.makeText(
						ctx,
						ctx.getResources().getString(R.string.all_exist_when_addlist),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.addToWhiteLayout:
			if (!whiteOrBlackIsExists(TYPE_WHITE, msgInfo)) {
				mInterDB.insertBlackWhitList(TYPE_WHITE,
						msgInfo.getContactName(),
						msgInfo.getFromAddress(), true, true, 1);
			} else {
				Toast.makeText(
						ctx,
						ctx.getResources().getString(R.string.all_exist_when_addlist), Toast.LENGTH_SHORT).show();
			}
			break;
		}
		dismiss();
	}

	/**
	 * nc 必填选项
	 * */
	public DialogueOperationDialog(Context context, int theme, int pos, List<SmsMmsMessage> dataList,
			DialogueMessengerListAdapter adpt) {
		super(context, theme);
		this.ctx = context;
		this.position = pos;
		this.list = dataList;
		this.adapter = adpt;
	}

	public void showDialog() {
		this.show();

	}
	
	// 删除一条对话确认框
	private void makeDeleteDialogue(final long threadId, final int position) {
		Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(ctx.getApplicationContext().getString(
				R.string.sms_dialog_delete_thread));
		builder.setPositiveButton(R.string.sms_contact_confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						MessengerUtils.deleteThread(
								ctx,
								threadId, SmsMmsMessage.MESSAGE_TYPE_MESSAGE);
						list.remove(position);
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
	 * 拨打电话
	 * 
	 * @param msgInfo
	 */
	public void dial(SmsMmsMessage msgInfo) {
		try {
			Intent myIntentDial = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + msgInfo.getFromAddress()));
			ctx.startActivity(myIntentDial);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean whiteOrBlackIsExists(int type, SmsMmsMessage msgInfo) {
		NativeCursor nc = new NativeCursor();
		nc.setmRequestType(type);
		nc.setmContactName(msgInfo.getContactName());
		nc.setmPhoneNum(msgInfo.getFromAddress());
		nc = InterceptDataBase.get(ctx)
				.selectIsExists(nc);
		return nc.ismIsExists();
	}

}

