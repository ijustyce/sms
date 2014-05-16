/**
 * date:2013-06-02
 * receiver message then call dealmsg
 */

package com.txh.sms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsActivity extends BroadcastReceiver {

	String body = "";
	String number = "";
	String time = "";

	@Override
	public void onReceive(Context context, Intent intent) {
		this.abortBroadcast();

		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < pdus.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			}

			SmsMessage sms = messages[0];
			try {
				if (messages.length == 1 || sms.isReplace()) {
					body = sms.getDisplayMessageBody();
					Log.d("---sms---", body); 
				} else {
					StringBuilder bodyText = new StringBuilder();
					for (int i = 0; i < messages.length; i++) {
						bodyText.append(messages[i].getMessageBody());
					}
					body = bodyText.toString();
				}
			} catch (Exception e) {

			}

			Date date = new Date(messages[0].getTimestampMillis());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.CHINA);
			String sendtime = format.format(date);
			number = messages[0].getOriginatingAddress();
			if (number.startsWith("+86")) {
				number = number.replace("+86", "");
			}

			SharedPreferences new_sms = context.getSharedPreferences("new_sms",
					Context.MODE_PRIVATE);
			Editor new_edit = new_sms.edit();
			new_edit.putString("sms", body);
			new_edit.putString("phone", number);
			new_edit.putString("date", sendtime);
			new_edit.commit();

			Intent showMsg = new Intent(context, dealmsg.class);
			showMsg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(showMsg);

		}
	}
}
