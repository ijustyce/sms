/**
 * date:2013-06-02
 * 棰勮绐楀彛锛侀粯璁わ紝闈炶仈绯讳汉娌℃湁棰勮锛屼篃涓嶄細鍝嶉搩锛� */

package com.txh.sms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ijustyce.androidlib.txApplication;

public class show extends Activity{
	String phone="";
	String content="";
	private txApplication tx;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quickshow);
		tx = (txApplication)getApplication();
		setWidth();
		quickShow();
	}

	private void setWidth(){
		EditText et = (EditText)findViewById(R.id.newmessage);
		DisplayMetrics dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHight = dm.heightPixels;
		et.setWidth(screenWidth / 2);
		
		ScrollView layout = (ScrollView)findViewById(R.id.layout);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT , screenHight / 3));
	}

	private void quickShow(){
		SharedPreferences show = getSharedPreferences("new_sms",
													  Context.MODE_PRIVATE);
		content = show.getString("sms", "No Message now !");
		phone = show.getString("phone", "");
    	
    	setTitle(tx.getName(phone)[0]);
	
		TextView tv2 = (TextView)findViewById(R.id.showcontent);
		tv2.setText(content);
		tv2.setTextSize(18);
	}
	public void yc(View v){
		switch (v.getId()){
			case R.id.quickclose:
				onClose();
				break;
			case R.id.quickreply:
				send();
				break;
		}
	}
	private void send(){
		EditText tv = (EditText) findViewById(R.id.newmessage);
		String content = tv.getText().toString();

		SharedPreferences newmessage = getSharedPreferences("send_new",
															Context.MODE_PRIVATE);
		Editor edit = newmessage.edit();
		edit.putString("content", content);
		edit.putString("phone", phone);
		edit.commit();

		Intent myIntent = new Intent(this, MessageActivity.class); 
		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(myIntent);
		onClose();
	}

	private void onClose(){
		dealmsg.notificationManager.cancelAll();
		finish();
	}
}
