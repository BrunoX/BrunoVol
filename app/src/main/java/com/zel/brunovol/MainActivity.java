package com.zel.brunovol;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.zel.brunovol.teststuff.R;


/**
 * Created by zel on 03/11/15.
 */
public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "MainActivity";

	private EditText            mServerIp;
	private Button              mSave;
	private SwitchCompat        mSwitchCompat;
	private NotificationManager mNotificationManager;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		final View snackbarView = findViewById(R.id.snackbarPosition);

		mServerIp = (EditText) findViewById(R.id.server_ip);
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String userInput = mServerIp.getText()
					.toString();
				IpValidateHelper ipValidateHelper = new IpValidateHelper();
				if(ipValidateHelper.validate(userInput)){
					BrunoApp.PREFS.edit()
						.putString(BrunoApp.PREF_IP, userInput)
						.apply();
					Snackbar.make(snackbarView, R.string.saved, Snackbar.LENGTH_LONG)
						.show();
				}
				else{
					Snackbar.make(snackbarView, R.string.ip_correct, Snackbar.LENGTH_LONG)
						.show();
				}
			}
		});

		mSwitchCompat = (SwitchCompat) findViewById(R.id.active);
		mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				Intent intent = new Intent(MainActivity.this, VolumeService.class);
				if(isChecked){
					showNotification();
					startService(intent);
					BrunoApp.PREFS.edit()
						.putBoolean(BrunoApp.PREF_ACTIVE, true)
						.apply();
					Snackbar.make(snackbarView, R.string.start_brunovol, Snackbar.LENGTH_LONG)
						.show();
				}
				else{
					stopService(intent);
					BrunoApp.PREFS.edit()
						.putBoolean(BrunoApp.PREF_ACTIVE, false)
						.apply();
					mNotificationManager.cancel(1);
					Snackbar.make(snackbarView, R.string.stopped_brunovol, Snackbar.LENGTH_LONG)
						.show();
				}
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		mServerIp.setText(BrunoApp.PREFS.getString(BrunoApp.PREF_IP, ""));
		mSwitchCompat.setChecked(BrunoApp.PREFS.getBoolean(BrunoApp.PREF_ACTIVE, false));
	}

	private void showNotification()
	{
		RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);

		remoteViews.setOnClickPendingIntent(R.id.voldown,
			getPendingIntent(this, VolumeService.VOL_DOWN_ACTION));

		remoteViews.setOnClickPendingIntent(R.id.volup,
			getPendingIntent(this, VolumeService.VOL_UP_ACTION));

		remoteViews.setOnClickPendingIntent(R.id.volmute,
			getPendingIntent(this, VolumeService.VOL_MUTE_ACTION));

		remoteViews.setOnClickPendingIntent(R.id.settings,
			getPendingIntent(this, VolumeService.SETTINGS_ACTION));

		Notification notification = new Notification.Builder(this).setContentTitle(getString(R.string.app_name))
			.setContentText(getString(R.string.app_name))
			.setAutoCancel(false)
			.setOngoing(true)
			.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bruno_silhoutee_icon))
			.setSmallIcon(getNotificationIcon())
			.setContent(remoteViews)
			.build();

		mNotificationManager.notify(1, notification);
	}

	public PendingIntent getPendingIntent(Context context, String action)
	{
		Intent intent = new Intent(action);
		intent.setAction(action);
		intent.putExtra("notificationId", 1);
		Log.e(TAG, "set action : " + action);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	private int getNotificationIcon() {
		boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
		return whiteIcon ? R.drawable.bruno_silhoutee_icon : R.drawable.bruno_silhoutee_icon;
	}
}
