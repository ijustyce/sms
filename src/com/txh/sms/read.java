/**
 * date: 2013-06-02
 * read message !
 */

package com.txh.sms;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ijustyce.androidlib.baseclass;
import com.ijustyce.unit.DateUtils;
import com.ijustyce.unit.LogCat;
import com.ijustyce.unit.toast;
import com.macjay.pulltorefresh.PullToRefreshBase;
import com.macjay.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.macjay.pulltorefresh.PullToRefreshListView;
import com.txh.Api.sqlite;
import com.txh.model.ListModel;
import com.txh.sms.adapter.ListAdapter;

public class read extends baseclass{

	private List<ListModel> list;
	private ListAdapter adapter;
	private PullToRefreshListView mPullListView;
	private String dbFile;
	private sqlite api;
	private String content , id;
	public static String num;
	private int position;
	public static int total;
	private int from = 0 , to = 0;
	private int pageCount = 15;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle!=null) {
			num = bundle.getString("num");
			total = bundle.getInt("total");
		}else{
			SharedPreferences shared = getSharedPreferences("read",
					Context.MODE_PRIVATE);
			num = shared.getString("number", "");
			total = shared.getInt("total", 0);
			shared.edit().clear().commit();
		}
		
		if (total < 1) {
			toast.show(R.string.none_sms, getBaseContext());
			startActivity(new Intent(this , conversation.class));
			this.finish();
		}
		LogCat.i("===read===", "read sms of " + num + " total sms number: " + total);
		api = new sqlite();
		dbFile = tx.getDbFile();
		
		init();
		initData();
	}
	
	OnLongClickListener listener = new OnLongClickListener() {

		public boolean onLongClick(View view) {
			position = view.getId();
			ListModel model = list.get(position);
			content = model.getContent();
			id = model.getId();
			show();
			return false;
		}
	};
	
	OnRefreshListener<ListView> refreshListener = new OnRefreshListener<ListView>(){

		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			initData();
			mPullListView.onPullDownRefreshComplete();
			mPullListView.setLastUpdatedLabel(DateUtils.getDate("yyyy-MM-dd HH:mm"));
		}

		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			from = to;
			to = from + pageCount;
			if (to < total) {
				setmessage();
				mPullListView.setHasMoreData(true);
				mPullListView.setScrollLoadEnabled(true);
				mPullListView.onPullUpRefreshComplete();
				return ;
			}if(to > total) {
				to = total;
				setmessage();
			}
			mPullListView.setHasMoreData(false);
			mPullListView.setScrollLoadEnabled(false);
			mPullListView.setLastUpdatedLabel(DateUtils.getDate("yyyy-MM-dd HH:mm"));
		}
	};
	
	private void init(){
		list = new ArrayList<ListModel>();
		mPullListView = (PullToRefreshListView)findViewById(R.id.lv);
		mPullListView.setScrollLoadEnabled(true);
		ListView lv = mPullListView.getRefreshableView();
		mPullListView.setDividerDrawable(null);
		mPullListView.setOnRefreshListener(refreshListener);
		lv.setDivider(null);
		adapter = new ListAdapter(this, list);
		adapter.setOnLongClickListener(listener);
		lv.setAdapter(adapter);
	}
	
	private void initData(){
		list.clear();
		to = 0;
		from = 0;
		if (total < pageCount) {
			mPullListView.setHasMoreData(false);
			mPullListView.setScrollLoadEnabled(false);
			to = total;
			setmessage();
		}else {
			mPullListView.setHasMoreData(true);
			mPullListView.setScrollLoadEnabled(true);
			to = from + pageCount;
			setmessage();
		}
	}
	
	private void setmessage(){

		String [] phone = {num};

		Button bt = (Button) findViewById(R.id.newnote);
		bt.setText(getResources().getString(R.string.with) + tx.getName(num)[0]
				   + getResources().getString(R.string.main_show));
		bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bkcolor));
		String[] column = {"_id","ismy","content"};
		String[][] value = api.getData(dbFile, "sms", "select * from sms "
				+ "where phone = ? limit " + from + "," + to, phone, column);
		int length = value.length;
		for(int i = 0 ; i< length; i++){
			ListModel model = new ListModel();
			model.setId(value[i][0]);
			model.setPhone(num);
			model.setContent(value[i][2]);
			model.isMy(Boolean.parseBoolean(value[i][1]));
			if (model.isMy()) {
				model.setName(getResources().getString(R.string.me).toString());
			}else {
				model.setName(tx.getName(num)[0]);
			}
			list.add(model);
			adapter.notifyDataSetChanged();	
		}
	}
	
	android.content.DialogInterface.OnClickListener menuListener = 
			new DialogInterface.OnClickListener(){
		
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				delete(id);
				break;
			case 1:
				clickClean();
				break;
			case 2:
				tx.setClipboard(content);
				break;
			case 3:
				transmit(content);
				break;
			case 4:
				call();
				break;
			case 5:
				add();
				break;
			case 6:
				addContacts();
			default:
				break;
			}
			dialog.dismiss();
		}
	};

	private void show(){
		
		String delete = getResources().getString(R.string.delete).toString();
		String delAll = getResources().getString(R.string.del_all).toString();
		String copy = getResources().getString(R.string.copy).toString();
		String call = getResources().getString(R.string.call).toString();
		String transmit = getResources().getString(R.string.transmit).toString();
		String add = getResources().getString(R.string.add_contacts).toString();
		String option = getResources().getString(R.string.option).toString();
		String intecept = getResources().getString(R.string.intecept_num).toString();
		String[] menu = {delete , delAll, copy, transmit, call ,intecept, add};
		if(tx.getName(num)[1].equals("true")){
			menu = new String[]{delete , delAll, copy, transmit, call, intecept};
		}
		new AlertDialog.Builder(read.this)
		.setTitle(option)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setItems(menu, menuListener).show();
	} 
	
	private void call(){
	
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
		startActivity(intent);
		anim();
	}
	
	private void transmit(String content){
		
		Intent intent = new Intent(this , sendsms.class);
		Bundle bundle = new Bundle();
		bundle.putString("sms_body", content);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
		anim();
	}
	
	private void addContacts(){
		
	}

	private void delete(String id){
		
		String [] args = {id};
		api.delete(dbFile, "sms", "_id=?", args);
		total--;
		if(total < 1){
			String [] arg = {num};
			api.delete(dbFile, "phone", "phone=?", arg);
			int total = tx.getPreferencesInt("conversation", "total");
			tx.setPreferencesInt("conversation", total -1 , "total");
			Intent Intent = new Intent(this, conversation.class);
			startActivity(Intent);
			anim();
			this.finish();
		}
		
		else{
			int j;
			String updatephone="",content="",ismy="";
			String [] phone = {num};
			String[] column = {"phone","content","ismy"};
			String[][] value = api.getData(dbFile, "sms", "select * from sms where phone = ?", phone, column);
			for(j=0;j<value[0].length;j++){
				updatephone = value[value.length-1][0];
				content = value[value.length-1][1];
				ismy = value[value.length-1][2];
					
				String[] updateColumn = {"phone","content","ismy","total"};
				String[] updateValue = {updatephone,content,ismy , String.valueOf(total-1)};
				String sql = "phone=?";
				api.update(dbFile, "phone", updateValue, updateColumn, phone, sql);
			}
		}
		list.remove(position);
		adapter.notifyDataSetChanged();
	}

	private void del_total(){
		String [] arg = {num};
		api.delete(dbFile, "sms", "phone=?", arg);	
		api.delete(dbFile, "phone", "phone=?", arg);
		int total = tx.getPreferencesInt("conversation", "total");
		tx.setPreferencesInt("conversation", total -1 , "total");
		Intent Intent = new Intent(this, conversation.class);
		startActivity(Intent);
		anim();
		this.finish();
	}

	public void yc(View v){

		switch (v.getId()){
			case R.id.newnote:
				SharedPreferences edit_new = getSharedPreferences("edit_new",
																  Context.MODE_PRIVATE);
				Editor my_edit = edit_new.edit();
				my_edit.putString("number", num);
				my_edit.commit();
				Intent newnote = new Intent(this, sendsms.class);
				startActivity(newnote);
				anim();
				this.finish();
		}
	}

	private void clickClean(){
		new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.ask))
		.setMessage(getResources().getString(R.string.clickCleanContent))
		.setNegativeButton(getResources().getString(R.string.no),
		new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog,int which){
			}
		})
		.setPositiveButton(getResources().getString(R.string.yes),
		new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog,int whichButton){
				del_total();
			}
		}).show();
	}

	private void add(){
		
		String[]column = {"value"};
		String[]value = {num};
		boolean isExist = api.exists(dbFile, "intercept", "value", num);
		if(isExist){
			toast.show(R.string.add_error, getBaseContext());
			return ;
		}
		else{
			api.insertData(dbFile, "intercept", value, column);
		}
	}
}