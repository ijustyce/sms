/**
 * date:2013-06-02
 * main
 */

package com.txh.sms;

import com.ijustyce.androidlib.about;
import com.ijustyce.androidlib.baseclass;
import com.ijustyce.androidlib.feedback;
import com.ijustyce.androidlib.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class MainActivity extends baseclass{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tx.setMainActivity(new Intent(this , MainActivity.class));
		init();
	}	
	private void init(){
		
		String themeString = tx.theme(MainActivity.this);
		
		getSignInfo();
		
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        
        int h = 3*height/16;
        int w = 11*width/30;
        
		Button bt9 = (Button)findViewById(R.id.bt9);
		bt9.setBackgroundResource(R.drawable.exit);
		
		Button bt10 = (Button)findViewById(R.id.bt10);
		bt10.setBackgroundResource(R.drawable.setting );
		
		int j = R.id.bt1;
		
		for(int i = j;i<j+8;i++){
			
			Button bt = (Button)findViewById(i);
			
			if(themeString.equals("beauty")){
				
				bt.setAlpha((float)0.4);
				bt.setBackgroundResource(R.drawable.newmessage);
			}
			bt.setHeight(h);
			bt.setWidth(w);
		}
		
		SharedPreferences shared = getSharedPreferences("server",
				Context.MODE_PRIVATE);
		shared.edit().putBoolean("run", false).commit();
	}
	
	public void btClick(View v){
		
		switch(v.getId()){
		case R.id.bt1:
			startActivity(new Intent(this,sendsms.class));
			anim();
			this.finish();
			break;
		case R.id.bt2:
			startActivity(new Intent(this, conversation.class));
			anim();
			this.finish();
			break;
		case R.id.bt3:
			startActivity(new Intent(this,timingList.class));
			anim();
			this.finish();
			break;
		case R.id.bt4:
			startActivity(new Intent(this,intercept.class));
			anim();
			this.finish();
			break;
		case R.id.bt5:
			startActivity(new Intent(this,backup.class));
			anim();
			this.finish();
			break;
		case R.id.bt6:
			startActivity(new Intent(this,advance.class));
			anim();
			this.finish();
			break;
		case R.id.bt7:
			startActivity(new Intent(this,feedback.class));
			anim();
			this.finish();
			break;
		case R.id.bt8:
			startActivity(new Intent(this,about.class));
			anim();
			this.finish();
			break;
		case R.id.bt9:
			exit();
			break;
		case R.id.bt10:
			startActivity(new Intent(this,settings.class));
			anim();
			this.finish();
			break;
		}
	}
	
	private void exit(){
		
		System.gc();
		System.exit(0);
	}
}
