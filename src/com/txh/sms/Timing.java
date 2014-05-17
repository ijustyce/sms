/**
 * date:2013-06-02
 * 广播，监听新短信！写进new_sms.xml并启动dealsms.class
 */
package com.txh.sms;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ijustyce.androidlib.txApplication;
import com.txh.Api.common;
import com.txh.Api.sqlite;

public class Timing extends BroadcastReceiver{
	sqlite api;
	private txApplication tx;
	private common txApi;
	String dbFile;
	@Override
	public void onReceive(Context context, Intent intent){
		api = new sqlite();
		tx = (txApplication)context.getApplicationContext();
		txApi = new common();
		dbFile = tx.getDbFile();
		if(intent.getAction().equals("com.tx.sms.timing")){
			sendMsg(context);
        }else{
        	upDate(context);
        }
	}
	
	private void sendMsg(Context context){
		
		String date1 = txApi.getDate();
		int i,j;
		String[]result = new String[8];
		String[]temp = new String[8]; 
		String date2="";;
		String[]column={"phone","content","year","month","day","hour","minute","_id"};
		String [][]value = api.getData(dbFile, "timing","select * from timing", null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				temp[j] = value[i][j];
				if(j>1&&j<7&&Integer.parseInt(temp[j])<10){
					result[j] = "0"+temp[j];
				}
				else{
					result[j] = temp[j];
				}
				
				if(j>1&&j<7){
					if(j<6){
						date2 = date2+result[j]+"/";
					}
					else{
						date2 = date2+result[j];
					}
				}
			}
			long time = txApi.getQuot(date1,date2);
			if(time<0){
				time = 0-time;
			}
			if(time<10000){
				SharedPreferences newmessage = context.getSharedPreferences("send_new",
						Context.MODE_PRIVATE);
				Editor edit = newmessage.edit();
				edit.putString("content", result[1]);
				edit.putString("phone", result[0]);
				edit.commit();
					
				/*do{
					serviceRun = shared.getBoolean("run", true);
				}while(serviceRun);*/

				Intent myIntent = new Intent(context, MessageActivity.class); 
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(myIntent);
					
			    String []del = {result[7]};
			    api.delete(dbFile, "timing", "_id=?", del);
				
			}					
		}
	}
	
	private void upDate(Context context){
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR)); 
		String month = String.valueOf(c.get(Calendar.MONTH)+1); 
		String date = String.valueOf(c.get(Calendar.DATE)); 
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY)); 
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		if(Integer.parseInt(month)<10){
			month = "0"+month;
		}
		
		if(Integer.parseInt(date)<10){
			date = "0"+date;
		}
		
		if(Integer.parseInt(hour)<10){
			hour = "0"+hour;
		}
		
		if(Integer.parseInt(minute)<10){
			minute = "0"+minute;
		}
		String date2 = year+"/"+month+"/"+date+"/"+hour+"/"+minute;
		int i,j;
		boolean serviceRun = false;
		SharedPreferences shared = context.getSharedPreferences("server",
				Context.MODE_PRIVATE);
		String[]result = new String[8];
		String[]temp = new String[8]; 
		String date1="";;
		String[]column={"phone","content","year","month","day","hour","minute","_id"};
		String [][]value = api.getData(dbFile, "timing","select * from timing", null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				temp[j] = value[i][j];
				if(j>1&&j<7&&Integer.parseInt(temp[j])<10){
					result[j] = "0"+temp[j];
				}
				else{
					result[j] = temp[j];
				}
				
				if(j>1&&j<7){
					if(j<6){
						date1 = date1+result[j]+"/";
					}
					else{
						date1 = date1+result[j];
					}
				}
			}
			long time = txApi.getQuot(date1,date2);
			if(time<1){
				SharedPreferences myshared = context.getSharedPreferences(
						"com.txh.sms_preferences", Context.MODE_PRIVATE);
				String send = myshared.getString("TimingOut", "send");
				SharedPreferences newmessage = context.getSharedPreferences("send_new",
						Context.MODE_PRIVATE);
				Editor edit = newmessage.edit();
				edit.putString("content", result[1]);
				edit.putString("phone", result[0]);
				edit.commit();
				if(send.equals("send")){
					do{
						serviceRun = shared.getBoolean("run", true);
					}while(serviceRun);
					
					Intent myIntent = new Intent(context, MessageActivity.class); 
					myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startService(myIntent);
				}
				String []del = {result[7]};
				api.delete(dbFile, "timing", "_id=?", del);
			}
			else{
				Intent intent =new Intent(context, Timing.class);
				intent.setAction("com.tx.sms.timing");
				PendingIntent sender = PendingIntent.getBroadcast(context, 0,intent , 0); 
				AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			    alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+time, sender);
			}
			
		}
	}
}
