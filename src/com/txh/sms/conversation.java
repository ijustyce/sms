/**
 * date: 2013-06-02
 * Conversation with friends 
 * */

package com.txh.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ijustyce.androidlib.baseclass;
import com.ijustyce.unit.DateUtils;
import com.ijustyce.unit.toast;
import com.macjay.pulltorefresh.PullToRefreshBase;
import com.macjay.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.macjay.pulltorefresh.PullToRefreshListView;
import com.txh.Api.sqlite;
import com.txh.model.ListModel;
import com.txh.sms.adapter.ListAdapter;

public class conversation extends baseclass{

	private  List<ListModel> list;
	private PullToRefreshListView mPullListView;
	private ListAdapter adapter;
	private int from = 0 , to = 0;
	private int pageCount = 15;
	private int total;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversition);
		init();
		initData();
	}
	
	private void init(){
		
		list = new ArrayList<ListModel>();
		mPullListView = (PullToRefreshListView)findViewById(R.id.lv);
		mPullListView.setScrollLoadEnabled(true);
		ListView lv = mPullListView.getRefreshableView();
		mPullListView.setDividerDrawable(null);
		lv.setDivider(null);
		adapter = new ListAdapter(this, list);
		adapter.setClickListener(listener);
		lv.setAdapter(adapter);
		mPullListView.setOnRefreshListener(refreshListener);
	}
	
	private void initData(){
		list.clear();
		total = tx.getPreferencesInt("conversation", "total");
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
			}else {
				to = total;
				setmessage();
				mPullListView.setHasMoreData(false);
				mPullListView.setScrollLoadEnabled(false);
			}
			mPullListView.onPullUpRefreshComplete();
			mPullListView.setLastUpdatedLabel(DateUtils.getDate("yyyy-MM-dd HH:mm"));
		}
	};
	
	OnClickListener listener = new OnClickListener() {
		
		public void onClick(View view) {
			int position = view.getId();
			ListModel model = list.get(position);
			show(model.getPhone() , model.getTotal());
		}
	};
	
	private void setmessage() {
		
		String dbFile = tx.getDbFile();
		sqlite api = new sqlite();

		String[] column = { "_id", "phone", "content", "ismy" , "total"};
		String[][] value = api.getData(dbFile, "phone", "select * from phone limit " + from + "," + to,
				null, column);
		int size = value.length;
		for (int i = 0; i < size; i++) {
			ListModel model = new ListModel();
			model.setId(value[i][0]);
			model.setPhone(value[i][1]);
			model.setContent(value[i][2]);
			model.isMy(Boolean.parseBoolean(value[i][3]));
			model.setTotal(Integer.parseInt(value[i][4]));
			if (model.isMy()) {
				model.setName(getResources().getString(R.string.me).toString());
			}else {
				model.setName(tx.getName(value[i][1])[0]);
			}
			list.add(model);
			adapter.notifyDataSetChanged();
		}
	}
	
	private void show(String phone , int total){
		
		Bundle bundle = new Bundle();
		bundle.putString("number", phone);
		bundle.putInt("total", total);
		Intent read = new Intent(this, read.class);
		read.putExtras(bundle);
		startActivity(read);
		anim();
		finish();
	}
}
