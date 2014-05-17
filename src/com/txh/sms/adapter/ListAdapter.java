package com.txh.sms.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.txh.model.ListModel;
import com.txh.sms.R;

public class ListAdapter extends BaseAdapter{

	List<ListModel> list;
	private LayoutInflater mInflater = null;
	private OnClickListener listener = null;
	private OnLongClickListener longClick = null;
	public ListAdapter(Context context , List<ListModel> list){
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public void setClickListener(OnClickListener listener){
		this.listener = listener;
	}
	
	public void setOnLongClickListener(OnLongClickListener listener){
		this.longClick = listener;
	}
	
	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_item, null);
			holder.button = (Button) convertView.findViewById(R.id.adapter_buttom);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		ListModel model = list.get(position);
		holder.button.setText(model.getContent());
		holder.button.setId(position);
		if (listener!=null) {
			holder.button.setOnClickListener(listener);
		}if (longClick!=null) {
			holder.button.setOnLongClickListener(longClick);
		} 
		if (model.isMy()) {
			holder.button.setBackgroundResource(R.drawable.chat_send);
			holder.button.setTextColor(Color.WHITE);
		}else {
			holder.button.setBackgroundResource(R.drawable.chat_received);
			holder.button.setTextColor(Color.BLACK);
		}
		return convertView;
	}
	
	static class ViewHolder{
		Button button;
	}
}
