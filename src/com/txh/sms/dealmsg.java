package com.txh.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.ijustyce.androidlib.txApplication;
import com.txh.Api.sqlite;

public class dealmsg extends Service {
	String phone, content, dbFile;
	boolean intercept = false, disturbing;
	sqlite api;
	private txApplication tx;
	public static NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		tx = (txApplication) getApplication();
		api = new sqlite();
		dbFile = tx.getDbFile();
		getMsg();
		updateDatabese();
		stopSelfResult(startId);
		return START_NOT_STICKY;
	}

	private void getMsg() {
		SharedPreferences show = getSharedPreferences("new_sms",
				Context.MODE_PRIVATE);
		SharedPreferences myshared = getSharedPreferences(
				"com.txh.sms_preferences", Context.MODE_PRIVATE);
		boolean run = myshared.getBoolean("intercept", true);
		content = show.getString("sms", "No Message now !");
		phone = show.getString("phone", "");

		if (run) {
			intercept = api.exists(dbFile, "intercept", "value", phone)
					|| api.contains(dbFile, "words", "value", content);
		}
	}

	private void updateDatabese() {
		if (!intercept) {
			addData(phone, content + "\n" + tx.getDate("yyyy-MM-dd  HH:mm:ss"),
					"false");

			SharedPreferences myshared = getSharedPreferences(
					"com.txh.sms_preferences", Context.MODE_PRIVATE);
			boolean quickShow = myshared.getBoolean("quickShow", true);
			disturbing = myshared.getBoolean("strangers", false);
			if (quickShow) {
				if (!disturbing
						|| (disturbing && tx.getName(phone)[1].equals("true"))) {
					Intent showMsg = new Intent(this, show.class);
					showMsg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(showMsg);
				}
			}
			remind(tx.getName(phone)[0], content);
		}
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

	public void remind(String name, String msg) {
		SharedPreferences sharedPreferences = getSharedPreferences("read",
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("number", phone);
		editor.commit();

		notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = name + ": " + msg;
		PendingIntent intent1 = PendingIntent.getActivity(this, 0, new Intent(
				this, read.class), 0);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(this, name, msg, intent1);
		Log.i("---justyce---", String.valueOf(disturbing));
		if (!disturbing || (disturbing && tx.getName(phone)[1].equals("true"))) {
			String ringName = "";
			try {
				ringName = RingtoneManager.getActualDefaultRingtoneUri(this,
						RingtoneManager.TYPE_NOTIFICATION).toString();
			}

			catch (Exception e) {
			}

			if (!ringName.equals("")) {
				notification.sound = Uri.parse(ringName);
			}

			notification.vibrate = new long[] { 0, 300, 100, 300 };
		}

		notificationManager.notify(0, notification);
	}
}
