/**
 * date:2013-06-02
 * 鐢ㄤ簬鏄剧ず楠氭壈鎷︽埅鍙风爜锛� * 琚媺榛戠殑鍙风爜鍙戞潵鐨勭煭淇′細琚拷鐣ワ紒
 */

package com.txh.sms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ijustyce.androidlib.baseclass;
import com.txh.Api.sqlite;

public class timingList extends baseclass{
	String next = "",dbFile;
	int total;
	sqlite api;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_intercept);
		api = new sqlite();
		dbFile = tx.getDbFile();
		settext();
	}
	
	private void settext() {

		int i, j;
		String[] column = {"_id", "phone", "content", "time"};
		String[][] value = api.getData(dbFile, "timing",
				"select * from timing", null, column);
		for (i = 0; i < value.length; i++) {
			String temp = tx.getName(value[i][1])[0] + "-";;
			for (j = 0; j < value[i].length; j++) {
				if(j>1){
					temp = temp + value[i][j] + "-";
				}
			}
			ShowIntercept(temp.substring(0, temp.length()-1), value[i][0]);
		}
	}

	private void ShowIntercept(String phone,String id){
		
		String theme = tx.theme(timingList.this);

		WindowManager wm = getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();

		Button bt = new Button(this);
		bt.setTextSize(14);
		bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bkcolor));
		bt.setTextColor(android.graphics.Color.BLACK);
		bt.setText(phone);
		bt.setContentDescription(phone);
		bt.setLayoutParams(new LinearLayout.LayoutParams(7 * width / 8,
														 LayoutParams.WRAP_CONTENT));
		final String temp = id;
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0){
					next = temp;
					ict();
				}

		});
		
		if(theme.equals("beauty")){
		    bt.setAlpha((float)0.4);
			bt.setBackgroundResource(R.drawable.newmessage);
		}
		final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.intercept);
		linearLayout.addView(bt, 0);

		TextView textview = new TextView(this);
		textview.setHeight(8);
		linearLayout.addView(textview, 0);
	}

	private void ict(){
		new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.ask))
		.setMessage(getResources().getString(R.string.ask_content))
		.setNegativeButton(getResources().getString(R.string.no),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which){}
		})
		.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton){
					del();
				}
		}).show();
	}

	private void del(){
		String [] args = {next};
		api.delete(dbFile, "timing", "_id=?", args);

		Intent intent = new Intent(this, timingList.class);
		startActivity(intent);
		anim();
		this.finish();
	}
}
