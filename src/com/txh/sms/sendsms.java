/**
 * date:2013-06-02
 * send message
*/
package com.txh.sms;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ijustyce.androidlib.baseclass;
import com.txh.Api.common;
import com.txh.Api.sqlite;
public class sendsms extends baseclass {

	/** Called when the activity is first created. */
	PendingIntent send, receive;
	SmsManager manager;
	String send_status = "SENT_SMS_ACTION";
	String receive_status = "DELIVERED_SMS_ACTION";
	String content,dbFile,phone,SendContent = "" , themeString;
	boolean EndSend , SendFail , SendAgain ,TotalEndSend,check;
	private EditText editText = null;
	public static final int PICK_CONTACT = 3;
	ProgressDialog dialog;
	sqlite api;
	private common txApi;
	private EditText tv1 , tv2;
	
	int year,month,day,hour,minute;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		tv1 = (EditText) findViewById(R.id.name);
		tv2 = (EditText) findViewById(R.id.content);
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null)
		{
			String msg = bundle.getString("sms_body");
			Log.i("---smsContent---", msg);
			tv1.setText(msg);
		}
		
		api = new sqlite();
		themeString = tx.theme(sendsms.this);
		txApi = new common();
		dbFile = tx.getDbFile();	
		setnumber();
		Init();
		myKeyListener();
		IsSelect();
	}
	
	private void Init(){
		
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        
        TextView recent = (TextView)findViewById(R.id.recent_text);
        RelativeLayout.LayoutParams  params = (RelativeLayout.LayoutParams) 
        		recent.getLayoutParams();
        params.setMargins(0, height/18, 0, 0); //left, top, right, bottom
        recent.setLayoutParams(params);
        
        EditText name = (EditText)findViewById(R.id.name);
        name.setWidth(7*width/12);
        params = (RelativeLayout.LayoutParams) name.getLayoutParams();
        params.setMargins(0, height/18, 10, 0); //left, top, right, bottom
        name.setLayoutParams(params);
        
        Button select = (Button)findViewById(R.id.select);
        select.setWidth(width/3);
        select.setHeight(3*height/40);
        params = (RelativeLayout.LayoutParams) select.getLayoutParams();
        params.setMargins(5, height/18, 0, 0); 
        select.setLayoutParams(params);
        
        EditText content = (EditText)findViewById(R.id.content);
        params = (RelativeLayout.LayoutParams) content.getLayoutParams();
        params.setMargins(0, height/18, 0, 0); 
        content.setLayoutParams(params);
        
        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
        	
        	content.setText(bundle.getString("content"));
        }
        
        Button finish = (Button)findViewById(R.id.finish);
        finish.setWidth(2*width/5);
        finish.setHeight(3*height/40);       
        params = (RelativeLayout.LayoutParams) finish.getLayoutParams();
        params.setMargins(0, height/18, width/10, 0);  
        finish.setLayoutParams(params);
        
        Button send = (Button)findViewById(R.id.send);
        send.setWidth(2*width/5);
        send.setHeight(3*height/40);
        params = (RelativeLayout.LayoutParams) send.getLayoutParams();
        params.setMargins(0, height/18, 0, 0); 
        send.setLayoutParams(params);
        
        CheckBox Timing = (CheckBox)findViewById(R.id.Timing);
        Timing.setWidth(2*width/5);
        Timing.setHeight(3*height/40);
        params = (RelativeLayout.LayoutParams) Timing.getLayoutParams();
        params.setMargins(0, height/18, width/10, 0); 
        Timing.setLayoutParams(params);
        
        Button timingSms = (Button)findViewById(R.id.timingSms);
        timingSms.setWidth(2*width/5);
        timingSms.setHeight(3*height/40);
        params = (RelativeLayout.LayoutParams) timingSms.getLayoutParams();
        params.setMargins(0, height/18, 0, 0); 
        timingSms.setLayoutParams(params);
        
        if(themeString.equals("beauty")){
        	
        	select.setAlpha((float)0.4);
        	select.setBackgroundResource(R.drawable.newmessage);
        	
        	finish.setAlpha((float)0.4);
        	finish.setBackgroundResource(R.drawable.newmessage);
        	
        	Timing.setAlpha((float)0.4);
        	Timing.setBackgroundResource(R.drawable.newmessage);
        	
        	send.setAlpha((float)0.4);
        	send.setBackgroundResource(R.drawable.newmessage);
        	
        	timingSms.setAlpha((float)0.4);
        	timingSms.setBackgroundResource(R.drawable.newmessage);
		}
	}

	public void btClick(View v) {

		switch (v.getId()) {
		case R.id.finish:
			clear();
			startActivity(new Intent(this, MainActivity.class));
			anim();
			this.finish();
			break;
		case R.id.send:
			send();
			break;
		case R.id.select:
			Intent intent = new Intent(Intent.ACTION_PICK, 
					ContactsContract.Contacts.CONTENT_URI);  
            startActivityForResult(intent, PICK_CONTACT); 
            anim();
			break;
		case R.id.timingSms:
			if(check){
				addTimingSms();
			}
			else{
				tx.showToast(R.string.Timing_error);
			}
			break;
		}
	}

	 @Override
	public void onActivityResult(int reqCode, int resultCode, Intent data){  
		 
		 super.onActivityResult(reqCode, resultCode, data);  
		 switch(reqCode){  
		 case (PICK_CONTACT):  
			 if (resultCode == Activity.RESULT_OK){  
				 Uri contactData = data.getData();  
	             ContentResolver  c = getContentResolver();  
	             Cursor cursor= c.query(contactData, null, null, null, null);  
	             if (cursor.moveToFirst()){  
	            	 String[] PHONES_PROJECTION = new String[] { 
	            			 "_id","display_name","data1","data3"}; 
	            	 
	                 String contactId = cursor.getString(cursor.
	                		 getColumnIndex(BaseColumns._ID));  	                    
	                 Cursor phone = c.query(ContactsContract.CommonDataKinds.
	                		 Phone.CONTENT_URI,PHONES_PROJECTION, ContactsContract.
	                		 CommonDataKinds.Phone.CONTACT_ID + "=" + 
	                				 contactId, null, null);   
	                 
	                 while (phone.moveToNext()) { 
	                	
	                     String s = phone.getString(2);
	                     if(s.contains("smsto:")){
	            			 s = s.replaceAll("smsto:","");
	            		 }
	                    if(s.contains("%20")){
	            			s = s.replaceAll("%20","");
	            		}
	            		if(s.contains("sms:")){
	            			s = s.replaceAll("sms:","");
	            		}
	            		if(s.contains(" ")){
	            			s = s.replaceAll(" ","");
	            		}			
	            		if(s.contains("-")){
	            			s = s.replaceAll("-","");
		            	}
	                    	
	            		if(tv1.isFocused()){
	                    	tv1.setText(s); 
		                    tv2.requestFocus();
		                    return ;
	                    }
	            		else{
	                    	s = tv2.getText().toString() + 
	                    			tx.getName(s)[0] + ":" + s;
	                    	tv2.setText(s); 
	                    	return ;
	                    }
	                  }   
	              }  
	         } 
	         break;
	    }  
    }  

	private void send() {
		
		EndSend = true;
		SendFail = false;
		SendAgain = true;
		TotalEndSend = false;
	
		phone = tv1.getText().toString();

		EditText tv2 = (EditText) findViewById(R.id.content);
		content = tv2.getText().toString();

		tv1.setText("");
		tv2.setText("");

		if (phone.equals("") || content.equals("")) {
			Toast.makeText(this, R.string.send_error, Toast.LENGTH_LONG).show();
			return;
		}

		else if (PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
			dialog = ProgressDialog.show(sendsms.this, getResources()
					.getString(R.string.send_title),
					getResources().getString(R.string.send_content), true);

			Thread thread = new Thread(null, domywork, "Background");
			thread.start();
		}
		
		boolean isExist = false;
		isExist = api.exists(dbFile, "recent", "phone", phone);
		if(!isExist){
			String[] value = {phone};
			String[] column = {"phone"};
			api.insertData(dbFile, "recent", value, column);
		}
	}

	private Runnable domywork = new Runnable() {

		public void run() {

			sendlongmessage();
			handler.sendEmptyMessage(0);
		}

		/* deal dialog! */
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				/* because there just a message */
				dialog.dismiss();
			}
		};
	};

	private void sendlongmessage() {

		Intent sendIntent = new Intent(send_status);
		Intent receiveIntent = new Intent(receive_status);
		receive = PendingIntent.getBroadcast(this, 0, receiveIntent, 0);
		send = PendingIntent.getBroadcast(this, 0, sendIntent, 0);
		manager = SmsManager.getDefault();

		List<String> all = manager.divideMessage(content);
		Iterator<String> it = all.iterator();

		SharedPreferences myshared = getSharedPreferences(
				"com.txh.sms_preferences", Context.MODE_PRIVATE);
		SendAgain = myshared.getBoolean("sendagain", false);
		while (!TotalEndSend && it.hasNext()) {
			if (EndSend) {
				if (SendAgain && SendFail) {
					SendMessage(SendContent);
				}

				else {
					SendContent = it.next().toString();
					SendMessage(SendContent);
					addData(phone, SendContent + "\n" + tx.getDate("yyyy-MM-dd  HH:mm:ss"), "true");
				}
			}
		}

		while (!it.hasNext()) {
			if (EndSend) {
				if (SendAgain && SendFail) {
					SendMessage(SendContent);
				} else {
					TotalEndSend = true;
					break;
				}
			}
		}

	}
	
	private void addData(String phone, String content, String type){
		
		String[] column = {"total"};
		String[] args = {phone};
		int total = 0;
		String[][] value = api.getData(dbFile, "phone", "select * from phone where phone = ?", args, column);
		if (value.length > 0) {
			total = Integer.parseInt(value[0][0]);
		}
		Log.i("===total===", "total msg of this conversation" + total);
		String[]updateColumn = {"phone","content","ismy" ,"total"};
		String[]updateValue = {phone,content,type , String.valueOf(total+1)};
		if(total > 0){
			String sql = "phone=?";
			api.update(dbFile, "phone", updateValue, updateColumn, args, sql);
		}
		else{
			api.insertData(dbFile, "phone", updateValue, updateColumn);
			total = tx.getPreferencesInt("conversation", "total");
			tx.setPreferencesInt("conversation", total + 1, "total");
		}
		
		String[] smsColumn = {"phone","content","ismy" };
		String[] smsValue = {phone,content,type};
		api.insertData(dbFile, "sms", smsValue, smsColumn);	
		
		boolean isExist = false;
		isExist = api.exists(dbFile, "recent", "phone", phone);
		if(!isExist){
			String[] value2 = {phone};
			String[] column2 = {"phone"};
			api.insertData(dbFile, "recent", value2, column2);
		}
	}

	private void SendMessage(String SendContent) {
		manager.sendTextMessage(phone, null, SendContent, send, receive);
		EndSend = false;

		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent){
				
				EditText tv1 = (EditText) findViewById(R.id.name);
				tv1.requestFocus();
				switch (getResultCode()) {
				case Activity.RESULT_OK: {
					Toast.makeText(context,
					getResources().getString(R.string.send_success),Toast.LENGTH_LONG).show();
					EndSend = true;
					SendFail = false;
					unregisterReceiver(this);
					break;
				}

				default: {
					Toast.makeText(context,
							getResources().getString(R.string.send_fail),
							Toast.LENGTH_LONG).show();

					EndSend = true;
					SendFail = true;
					break;
				}
				}
			}

		}, new IntentFilter(send_status));
	}
	
	private void setnumber() {

		SharedPreferences my_shared = getSharedPreferences("edit_new",
				Context.MODE_PRIVATE);
		String number = my_shared.getString("number", "");
		tv1.setText(number);
		
		Intent intent = getIntent();
		String action = intent.getAction();
		
		if("android.intent.action.SENDTO".equals(action)){
			String s = intent.getDataString();
			if(s.contains("%20")){
				s = s.replaceAll("%20","");
			}
			if(s.contains("sms:")){
				s = s.replaceAll("sms:","");
			}
			if(s.contains("smsto:")){
				s = s.replaceAll("smsto:","");
			}
			if(s.contains(" ")){
				s = s.replaceAll(" ","");
			}			
			if(s.contains("-")){
				s = s.replaceAll("-","");
			}
			tv1.setText(s);
		}

		if (!tv1.getText().toString().equals("")) {
			tv2.requestFocus();
		}
		
		setRecent();
	}
	
	private void setRecent(){
		
		String[] column = {"phone"};
		String[][] value = api.getData(dbFile, "recent", "select * from recent", null, column);
		
        LinearLayout Layout = new LinearLayout(this);
       
		for(int i = 0;i<value.length;i++){
			Log.i("---recent---", "start");
			phone = value[i][0];
			final String temp = phone;	
			Button bt = new Button(this);
			bt.setTextSize(12);
			bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bkcolor));
			bt.setGravity(Gravity.CENTER);
			bt.setTextColor(android.graphics.Color.BLACK);
			bt.setText(tx.getName(phone)[0]);
			bt.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0){
					tv1.setText(temp);
					tv2.requestFocus();
				}
			});
			bt.setOnLongClickListener(new OnLongClickListener(){
				public boolean  onLongClick(View args){
					delete(temp);
					return false;
				}
			});
			
			if(themeString.equals("beauty")){
			    bt.setAlpha((float)0.4);
				bt.setBackgroundResource(R.drawable.newmessage);
			}
			
			TextView t = new TextView(this);
			t.setWidth(8);
				
			Layout.addView(bt);
			Layout.addView(t);			
		}
		HorizontalScrollView s = (HorizontalScrollView)findViewById(R.id.scrollView);
		s.addView(Layout);
	}
	
	private void delete(final String phone){
		new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.ask))
		.setMessage(getResources().getString(R.string.ask_content))
		.setNegativeButton(getResources().getString(R.string.no),
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
								int which)
			{
			}
		})
		.setPositiveButton(getResources().getString(R.string.yes),
		new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
								int whichButton)
			{
				del(phone);
			}
		}).show();
	}
	
	private void del(String arg){
		String [] args = {arg};
		api.delete(dbFile, "recent", "phone=?", args);
		
		Intent intent = new Intent(this, sendsms.class);
		startActivity(intent);
		anim();
		this.finish();
	}

	public void myKeyListener() {

		SharedPreferences myshared = getSharedPreferences(
				"little.sms_preferences", Context.MODE_PRIVATE);
		boolean enter_send = myshared.getBoolean("enter_send", true);
		if (enter_send) {

			editText = (EditText) findViewById(R.id.content);
			editText.setOnKeyListener(new OnKeyListener() {

				public boolean onKey(View v, int keyCode, KeyEvent event) {

					if (event.getAction() == KeyEvent.ACTION_DOWN
							&& keyCode == KeyEvent.KEYCODE_ENTER) {
						send();
						return true;
					}
					return false;

				}
			});
		}
	}

	private void clear() {
		SharedPreferences my_shared = getSharedPreferences("edit_new",
				Context.MODE_PRIVATE);
		my_shared.edit().clear().commit();
	}
	
	/**
	 * check box
	 */
	private void IsSelect(){
		CheckBox cb = (CheckBox) this.findViewById(R.id.Timing);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				checkSelect(arg1);
				check = true;
			}
		});
	}
	
	private void checkSelect(boolean select){
		if(select){
			selectTime();
			selectDate();
		}
	}
	
	private void selectDate(){
		Calendar c = null;
		Dialog dialog = null;
		c = Calendar.getInstance();
		dialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener(){
			public void onDateSet(DatePicker dp, int y,int m, int dayOfMonth) {
				year = y;
				month = m+1;
				day = dayOfMonth;
			}
         }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}
	
	private void selectTime(){
		Calendar c = null;
		Dialog dialog = null;
		c=Calendar.getInstance();
        dialog=new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener(){
                 public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay){
                	 hour = hourOfDay;
                	 minute = minuteOfDay;
                 }
               },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false);
        dialog.show();
	}
	
	private void addTimingSms() {

		phone = tv1.getText().toString();

		content = tv2.getText().toString();

		if (phone.equals("") || content.equals("")) {
			Toast.makeText(this, R.string.send_error, Toast.LENGTH_LONG).show();
			return;
		}

		String date1 = String.valueOf(year) + "/" + month + "/" + day + "/"
				+ hour + "/" + minute;

		String date2 = "1970/01/01/00/00";
		long date = txApi.getQuot(date1, date2);
		
		sqlite sql = new sqlite();
		String[] temp = {"value"};
		String timing[][] = sql.getData(dbFile, 
				"timing","select * from timing", null , temp);
	
		long[] dateArray = new long[timing.length]; 
		for(int i = 0 ; i<timing.length; i++){
			
			dateArray[i] = Long.parseLong(timing[i][0]);
		}
		
		if(timing.length>1){
			Arrays.sort(dateArray);
		}
		
		if(timing.length<1 || date < dateArray[0]){
			
			String date3 = txApi.getDate();
			brocast(txApi.getQuot(date1,date3));
			
		}

		String[] value = {phone , content , date1 ,String.valueOf(date)};
		String[] column = {"phone" , "content" , "time" , "value"};
		
		
		sql.insertData(dbFile, "timing", value, column);

		clear();
		startActivity(new Intent(this, MainActivity.class));
		anim();
		this.finish();
	}
	
	public void brocast(long date){
		
		Intent intent =new Intent(sendsms.this, Timing.class);
		intent.setAction("com.tx.sms.timing");
        PendingIntent sender = PendingIntent.getBroadcast(sendsms.this, 0,intent , 0); 
        
        AlarmManager alarm=(AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + date , sender);
	}
}
