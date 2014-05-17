/**
 * date:2013-06-02
 * send message background !
 **/

package com.txh.sms;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.ijustyce.androidlib.txApplication;
import com.txh.Api.sqlite;

public class MessageActivity extends Service{

	SmsManager smsManager;
	EditText editText = null;
	SharedPreferences shared;
	String dbFile;
	sqlite api;
	private txApplication tx;
	int i = 0;

	@Override
	public IBinder onBind(Intent arg0){
		return null;
	}

	@Override
	public void onCreate(){
		api = new sqlite();
		tx = (txApplication)getApplication();
		dbFile = tx.getDbFile();
		super.onCreate();
	}

	@Override
	public void onDestroy(){

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		send();
		return START_STICKY;
	}

	private void send(){
		SharedPreferences newmessage = getSharedPreferences("send_new",
															Context.MODE_PRIVATE);
		String content = newmessage.getString("content", "");
		String phone = newmessage.getString("phone", "");
		
		smsManager = SmsManager.getDefault();	

		if (PhoneNumberUtils.isGlobalPhoneNumber(phone)&&!content.equals("")){			
			
			smsManager.sendTextMessage(phone, null, content, null, null);
			addData(phone, content +  "\n" + tx.getDate("yyyy-MM-dd  HH:mm:ss"), "true");			
			newmessage.edit().clear().commit();	
		} 
		else{
			Toast.makeText(this, R.string.send_error, Toast.LENGTH_LONG).show();
		}
		stopSelf();
	}
	
	private void addData(String phone, String content, String type) {

		String[] column = { "total" };
		int total = 0;
		String[][] value = api.getData(dbFile, "phone",
				"select * from phone where phone = ?", new String[] { phone },
				column);
		if (value.length > 0) {
			total = Integer.parseInt(value[0][0]);
		}
		String[] updateColumn = { "phone", "content", "ismy", "total" };
		String[] updateValue = { phone, content, type,
				String.valueOf(total + 1) };
		if (total > 0) {
			String[] args = { phone };
			String sql = "phone=?";
			api.update(dbFile, "phone", updateValue, updateColumn, args, sql);
		} else {
			api.insertData(dbFile, "phone", updateValue, updateColumn);
			total = tx.getPreferencesInt("conversation", "total");
			tx.setPreferencesInt("conversation", total + 1, "total");
			Log.i("===total===", total + "");
		}

		String[] smsColumn = { "phone", "content", "ismy" };
		String[] smsValue = { phone, content, type };
		api.insertData(dbFile, "sms", smsValue, smsColumn);

		boolean isExist = false;
		isExist = api.exists(dbFile, "recent", "phone", phone);
		if (!isExist) {
			String[] value2 = { phone };
			String[] column2 = { "phone" };
			api.insertData(dbFile, "recent", value2, column2);
		}if (phone.equals(read.num)) {
			read.total++;
		}
	}
}
