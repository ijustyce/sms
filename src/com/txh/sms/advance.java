package com.txh.sms;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ijustyce.androidlib.baseclass;
import com.txh.Api.sqlite;

public class advance extends baseclass{
	sqlite api;
	String dbFile;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance);
		api = new sqlite();
		dbFile = tx.getDbFile();
		init();
	}
	
	private void init(){
		
		getSignInfo();
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);        
        int width = metric.widthPixels;
        
        Button bt1 = (Button)findViewById(R.id.bt1);
        bt1.setWidth(2*width/5);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bt1.getLayoutParams();
        params.setMargins(0, 20 , 0, 0); 
        bt1.setLayoutParams(params);
        
        Button bt2 = (Button)findViewById(R.id.bt2);
        bt2.setWidth(2*width/5);
        params = (RelativeLayout.LayoutParams) bt2.getLayoutParams();
        params.setMargins(width/10, 20 , 0, 0); 
        bt2.setLayoutParams(params);
        
        String themeString = tx.theme(advance.this);
        
        if(themeString.equals("beauty")){
        	
			bt1.setAlpha((float)0.4);
			bt1.setBackgroundResource(R.drawable.newmessage);
			
			bt2.setAlpha((float)0.4);
			bt2.setBackgroundResource(R.drawable.newmessage);
		}
	}
	
	public void btClick(View v){
		switch(v.getId()){
		case R.id.bt1:
			add("intercept");
			break;
		case R.id.bt2:
			add("words");
			break;
		}
	}
	
	private String getValue(){
		EditText e = (EditText)findViewById(R.id.value);
		return e.getText().toString();
	}
	
	private void add(String type){
		String number = getValue();
		if(isExist(number,type)){
			showToast(getResources().getString(R.string.add_error));
			return ;
		}
		
		String[]column = {"value"};
		String[]value = {getValue()};
		api.insertData(dbFile, type, value, column);
	}
	
	private boolean isExist(String num,String type){
		int i ;
		String[]column={"value"};
		String [][]value = api.getData(dbFile, type, 
				"select * from "+type, null, column);
		for(i = 0;i<value.length;i++){
				if(num.equals(value[i][0])){
					return true;
			}
		}
		return false;
	}
	
	private void showToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
}
