package com.txh.sms;

import java.io.File;

import com.ijustyce.androidlib.baseclass;
import com.txh.Api.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class backup extends baseclass {

	private common txApi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup);

		txApi = new common();
		initDatabase();
		
		initBt();
	}

	private void initDatabase() {
		File filePath = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/.txh");

		if (!filePath.exists()) {
			filePath.mkdir();
		}
		
		filePath = new File(Environment.getDataDirectory() + "/data/" 
		+ this.getPackageName() + "/shared_prefs");

		if (!filePath.exists()) {
			filePath.mkdir();
		}
	}
	
	private void initBt(){
		
		String themeString = tx.theme(backup.this);
		
		Button backupButton = (Button)findViewById(R.id.BackupBt);
		Button restoreButton = (Button)findViewById(R.id.RestoreBt);
		Button checkButton = (Button)findViewById(R.id.checkSignBt);
		
		if(themeString.equals("beauty")){
		    
			backupButton.setAlpha((float)0.4);
			backupButton.setBackgroundResource(R.drawable.newmessage);
			
			restoreButton.setAlpha((float)0.4);
			restoreButton.setBackgroundResource(R.drawable.newmessage);
			
			checkButton.setAlpha((float)0.4);
			checkButton.setBackgroundResource(R.drawable.newmessage);
		}
	}

	public void BtClick(View v) {
		switch (v.getId()) {
		case R.id.BackupBt:
			BackupPre();
			break;

		case R.id.RestoreBt:
			RestorePre();
			break;
		case R.id.checkSignBt:
			checkSignature();
			break;
		default:
			break;
		}
	}
	
	private void checkSignature(){
		String[] result = getSignInfo();
		if(result[0].equals("false")){
			Uri packageURI=Uri.parse("package:"+this.getPackageName());
			 Intent intent=new Intent(Intent.ACTION_DELETE,packageURI);
			 startActivity(intent);
		}
		else{
			Toast.makeText(this, getResources().getString(R.string.sign_success)
					.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private void BackupPre() {
		String title = getResources().getString(R.string.backup);
		String msg = getResources().getString(R.string.warn);
		clickOk(title,msg,"Backup");
	}

	private void Backup() {
		File dst = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/.txh/backup_sms.db");
		File src = new File(tx.getDbFile());
		if(!src.exists()){
			return ;
		}
		if (dst.exists()) {
			dst.delete();
		}
		txApi.copy(src, dst);
		
		dst = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/.txh/total.xml");
		src = new File(Environment.getDataDirectory() + "/data/" 
				+ this.getPackageName() + "/shared_prefs/total.xml");
		if(!src.exists()){
			return ;
		}
		if (dst.exists()) {
			dst.delete();
		}
		txApi.copy(src, dst);
		
	}
	
	private void RestorePre() {
		String title = getResources().getString(R.string.restore);
		String msg = getResources().getString(R.string.warn);
		
		clickOk(title, msg,"Restore");
	}


	private void Restore() {
		File src = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/.txh/backup_sms.db");
		File dst = new File(tx.getDbFile());
		if(!src.exists()){
			return ;
		}
		if (dst.exists()) {
			dst.delete();
		}
		txApi.copy(src, dst);
		
		src = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/.txh/total.xml");
		dst = new File(Environment.getDataDirectory() + "/data/" 
				+ this.getPackageName() + "/shared_prefs/total.xml");
		if(!src.exists()){
			return ;
		}
		if (dst.exists()) {
			dst.delete();
		}
		txApi.copy(src, dst);
	}

	private void clickOk(String title, String msg,final String action) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(msg)
				.setNegativeButton(getResources().getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if(action.equals("Backup")){
									Backup();
								}
								else if(action.equals("Restore")){
									Restore();
								}
							}
						}).show();
	}
	
}
