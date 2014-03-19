/*
 * Copyright (C)  Kindroid.com, 2011-2012
 * File:
 * Author:heli.zhao
 * Date:2011.12
 * Description:
 */
package com.kindroid.kincent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.kindroid.kincent.AutoLockActivityInterface;
import com.kindroid.kincent.KindroidMessengerApplication;
import com.kindroid.kincent.adapter.PrivacyContactsListAdapter;
import com.kindroid.kincent.data.PrivacyContactDataItem;
import com.kindroid.kincent.util.Constant;
import com.kindroid.kincent.util.PrivacyDBUtils;
import com.kindroid.kincent.R;

import java.util.ArrayList;
import java.util.List;

public class PrivacyContactsActivity extends ListActivity implements AutoLockActivityInterface{
	private PrivacyContactsListAdapter mAdapter;
	
	private boolean menuShowed = false;
	private PopupWindow menuWindow;
	public int menuShowedLinear;
	private View menuTopLinear;
	private View menuBottomLinear;

	public static List<PrivacyContactDataItem> sPrivacyContactsListItems;
	
	private static final String LAYOUT_FILE = "privacy_contacts_list_activity";
	private static final String MENU_LAYOUT_FILE = "privacy_contacts_menu";
	
	private Dialog mCurrentDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContentView();
		menuShowedLinear = 0;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mAdapter != null){
			for(int i = 0; i < mAdapter.getCount(); i++){
				PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
				item.setSelected(false);
				item.setShowSelect(false);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	private void initContentView(){
		View contentView = null;
		try{
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(LAYOUT_FILE);
		}catch(Exception e){
			contentView = null;
		}
		if(contentView == null){
			setContentView(R.layout.privacy_contacts_list_activity);
		}else{
			setContentView(contentView);
		}		
		
		if(sPrivacyContactsListItems == null){
			PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(this);
			sPrivacyContactsListItems = pdbUtils.getAllContacts(this);
		}
		mAdapter = new PrivacyContactsListAdapter(this, sPrivacyContactsListItems);
		setListAdapter(mAdapter);
		setupMenu();
	}
	private View initMenuView(){
		View contentView = null;
		try {
			contentView = KindroidMessengerApplication.mThemeRegistry.inflate(MENU_LAYOUT_FILE);
		} catch (Exception e) {
			contentView = null;
		}
		if (contentView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			contentView = layoutInflater.inflate(R.layout.privacy_contacts_menu, null);
		} 
		return contentView;
	}
	private void setupMenu() {		
		View layout = initMenuView();
		menuTopLinear = layout.findViewById(R.id.privacy_contact_menu_top_linear);
		menuBottomLinear = layout.findViewById(R.id.privacy_contact_menu_bottom_linear);
		if(menuShowedLinear == 0){
			menuTopLinear.setVisibility(View.VISIBLE);
			menuBottomLinear.setVisibility(View.GONE);
		}else{
			menuTopLinear.setVisibility(View.GONE);
			menuBottomLinear.setVisibility(View.VISIBLE);
		}
		menuWindow = new PopupWindow(layout, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.findViewById(R.id.privacy_contact_menu_add).setOnClickListener(
				menuListener);
		layout.findViewById(R.id.privacy_contact_menu_import).setOnClickListener(
				menuListener);
		layout.findViewById(R.id.privacy_contact_menu_delete_select).setOnClickListener(
				menuListener);
		layout.findViewById(R.id.privacy_contact_menu_select_all).setOnClickListener(
				menuListener);
		layout.findViewById(R.id.privacy_contact_menu_unselect_all).setOnClickListener(
				menuListener);
		layout.findViewById(R.id.privacy_contact_menu_delete_action).setOnClickListener(
				menuListener);

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(menuShowedLinear == 0){
			PrivacyContactListItemClickDialog clickDialog = new PrivacyContactListItemClickDialog(this.getParent(), R.style.Theme_CustomDialog, mAdapter, (PrivacyContactDataItem)mAdapter.getItem(position));
			clickDialog.show();
			mCurrentDialog = clickDialog;
		}else{
			CheckBox cb = (CheckBox)v.findViewById(R.id.privacy_contact_select_cb);
			if(cb != null){
				PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(position);
				item.setSelected(!item.isSelected());
				cb.setChecked(item.isSelected());
			}
		}
		
		super.onListItemClick(l, v, position, id);		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
			return true;
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && menuShowedLinear == 1){
			menuShowedLinear = 0;
			menuTopLinear.setVisibility(View.VISIBLE);
			menuBottomLinear.setVisibility(View.GONE);
			
			for(int i = 0; i < mAdapter.getCount(); i++){
				PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
				item.setShowSelect(false);
			}
			mAdapter.notifyDataSetChanged();
			return true;
		}
		if (event.getAction() != KeyEvent.ACTION_UP){
			return super.dispatchKeyEvent(event);
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			if (!menuShowed) {
				if (menuWindow == null){
					setupMenu();
				}else{
					menuWindow.showAtLocation(this.findViewById(R.id.privacy_contacts_list_activity_bottom_linear),
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
	private void startAddPrivacyContactDialog(){
		PrivacyContactAddDialog mPrivacyContactAddDialog = new PrivacyContactAddDialog(this.getParent(), R.style.Theme_CustomDialog, mAdapter, null);
		mPrivacyContactAddDialog.show();
		mCurrentDialog = mPrivacyContactAddDialog;
	}
	private OnClickListener menuListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.privacy_contact_menu_add:
				startAddPrivacyContactDialog();				
				break;
			case R.id.privacy_contact_menu_import:
				PrivacyContactImportDialog importDialog = new PrivacyContactImportDialog(PrivacyContactsActivity.this.getParent(), R.style.Theme_CustomDialog, PrivacyContactsActivity.this);
				importDialog.show();
				mCurrentDialog = importDialog;
				break;
			case R.id.privacy_contact_menu_delete_select:
				if(mAdapter.getCount() > 0){
					menuShowedLinear = 1;
					for(int i = 0; i < mAdapter.getCount(); i++){
						PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
						item.setShowSelect(true);
					}
					mAdapter.notifyDataSetChanged();
					menuTopLinear.setVisibility(View.GONE);
					menuBottomLinear.setVisibility(View.VISIBLE);
					return;
				}
				break;
			case R.id.privacy_contact_menu_select_all:
				for(int i = 0; i < mAdapter.getCount(); i++){
					PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
					item.setSelected(true);
				}
				mAdapter.notifyDataSetChanged();
				break;
			case R.id.privacy_contact_menu_unselect_all:
				for(int i = 0; i < mAdapter.getCount(); i++){
					PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
					item.setSelected(false);
				}
				mAdapter.notifyDataSetChanged();
				break;
			case R.id.privacy_contact_menu_delete_action:
//				List<PrivacyContactDataItem> listForDelete = new ArrayList<PrivacyContactDataItem>();
//				
//				for(int i = 0; i < mAdapter.getCount(); i++){
//					PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
//					if(item.isSelected()){
//						PrivacyDBUtils pdbUtils = PrivacyDBUtils.getInstance(PrivacyContactsActivity.this);
//						pdbUtils.delContact(PrivacyContactsActivity.this, item);
//						listForDelete.add(item);
//					}
//					item.setShowSelect(false);
//				}
//				for(int i = 0; i < listForDelete.size(); i++){
//					mAdapter.delItem(listForDelete.get(i));
//				}
//				mAdapter.notifyDataSetChanged();
				PrivacyDelContactPromptDialog promptDialog = new PrivacyDelContactPromptDialog(PrivacyContactsActivity.this.getParent(), R.style.Theme_CustomDialog, mAdapter.getItemsAll(), mAdapter);
				promptDialog.show();
				mCurrentDialog = promptDialog;
				
//				menuShowedLinear = 0;
				menuTopLinear.setVisibility(View.VISIBLE);
				menuBottomLinear.setVisibility(View.GONE);
				break;
			}
			menuWindow.dismiss();
			menuShowed = false;

		}
	};
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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
		
		}
	}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		System.out.println("requestCode :" + requestCode + "; resultCode :" + resultCode);

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLock() {
		// TODO Auto-generated method stub
		if(mCurrentDialog != null && mCurrentDialog.isShowing()){
			mCurrentDialog.dismiss();
		}
		if (menuShowed) {
			menuWindow.dismiss();
			menuShowed = false;
		
		}
		if(mAdapter != null){
			for(int i = 0; i < mAdapter.getCount(); i++){
				PrivacyContactDataItem item = (PrivacyContactDataItem)mAdapter.getItem(i);
				item.setSelected(false);
				item.setShowSelect(false);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
}
