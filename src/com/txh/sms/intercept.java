/**
 * date:2013-06-02
 * 鐢ㄤ簬鏄剧ず楠氭壈鎷︽埅鍙风爜锛� * 琚媺榛戠殑鍙风爜鍙戞潵鐨勭煭淇′細琚拷鐣ワ紒
 */

package com.txh.sms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ijustyce.androidlib.baseclass;
import com.txh.Api.sqlite;

public class intercept extends baseclass{
	String next = "",dbFile;
	int total;
	sqlite api;
	String table;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_intercept);
		
		api = new sqlite();
		dbFile = tx.getDbFile();
		
		settext("words");
		
		TextView words = new TextView(this);
		words.setText(getResources().getString(R.string.intercept_words).toString());
		words.setTextSize(21);
		words.setGravity(Gravity.CENTER);
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.intercept);
		linearLayout.addView(words, 0);

		TextView textview = new TextView(this);
		textview.setHeight(8);
		linearLayout.addView(textview, 0);
		
		settext("intercept");
		
		TextView phone = new TextView(this);
		phone.setText(getResources().getString(R.string.intercept_phone).toString());
		phone.setTextSize(21);
		phone.setGravity(Gravity.CENTER);
		
		linearLayout.addView(phone, 0);

		textview = new TextView(this);
		textview.setHeight(8);
		linearLayout.addView(textview, 0);
	}
	
	
	private void settext(String t){	
		int i,j;
		table = t;
		String[]column={"value"};
		String [][]value = api.getData(dbFile, table, 
				"select * from "+table, null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				ShowIntercept(value[i][j],t);
			}
		}
	}

	private void ShowIntercept(String phone,final String t){
		
		String theme = tx.theme(intercept.this);

		WindowManager wm = getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();

		Button bt = new Button(this);
		bt.setTextSize(18);
		bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bkcolor));
		bt.setTextColor(android.graphics.Color.BLACK);
		bt.setText(phone);
		bt.setContentDescription(phone);
		bt.setLayoutParams(new LinearLayout.LayoutParams(7 * width / 8,
														 LayoutParams.WRAP_CONTENT));
		final String temp = phone;
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0){
					next = temp;
					table = t;
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
		api.delete(dbFile, table, "value=?", args);

		Intent intent = new Intent(this, intercept.class);
		startActivity(intent);
		anim();
		this.finish();
	}
}
