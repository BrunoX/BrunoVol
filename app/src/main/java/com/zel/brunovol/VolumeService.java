package com.zel.brunovol;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Created by zel on 03/11/15.
 */
public class VolumeService
	extends Service
{
	private static final String TAG = VolumeService.class.getSimpleName();

	public static final String VOL_DOWN_ACTION = "com.example.brx.teststuff.VOL_DOWN";
	public static final String VOL_UP_ACTION   = "com.example.brx.teststuff.VOL_UP";
	public static final String VOL_MUTE_ACTION = "com.example.brx.teststuff.VOL_MUTE";
	public static final String SETTINGS_ACTION = "com.example.brx.teststuff.SETTINGS";

	public static final String VOL_DOWN = "voldown";
	public static final String VOL_UP   = "volup";


	private AdjustVolumeThread mAdjustVolThread;
	private MuteVolumeThread   mMuteVolumeThread;

	private IntentFilter mIntentFilter = new IntentFilter()
	{
		{
			addAction(VOL_DOWN_ACTION);
			addAction(VOL_UP_ACTION);
			addAction(VOL_MUTE_ACTION);
			addAction(SETTINGS_ACTION);
		}
	};

	@Override
	public void onCreate()
	{
		super.onCreate();
		registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if(action.equals(VOL_DOWN_ACTION)){
				mAdjustVolThread = new AdjustVolumeThread(VOL_DOWN);
				mAdjustVolThread.start();
			}
			else if(action.equals(VOL_UP_ACTION)){
				mAdjustVolThread = new AdjustVolumeThread(VOL_UP);
				mAdjustVolThread.start();
			}
			else if(action.equals(VOL_MUTE_ACTION)){
				mMuteVolumeThread = new MuteVolumeThread();
				mMuteVolumeThread.start();
			}
			else if(action.equals(SETTINGS_ACTION)){
				Intent settingsActivityIntent = new Intent(context, MainActivity.class);
				settingsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(settingsActivityIntent);
			}
		}
	};


	private class AdjustVolumeThread extends Thread
	{
		private volatile boolean mQuit = false;

		private String command;
		private String mBrunoSrvrIp;

		public AdjustVolumeThread(String commandStr)
		{
			super("AdjustVolumeThread");
			command = commandStr;
			mBrunoSrvrIp = BrunoApp.getAPP().PREFS.getString(BrunoApp.PREF_IP, "");
			if(mBrunoSrvrIp.equals("")){
				quit();
			}
		}

		public void quit()
		{
			Log.d(TAG, "quit()");
			mQuit = true;
			interrupt();
		}

		@Override
		public void run()
		{
			DatagramSocket ds = null;

			try{

				ds = new DatagramSocket();
				InetAddress serverAddr = InetAddress.getByName(mBrunoSrvrIp);

				DatagramPacket dp;
				dp = new DatagramPacket(command.getBytes(), command.length(), serverAddr, 1025);
				ds.send(dp);
			}
			catch(SocketException e){
				e.printStackTrace();
			}
			catch(UnknownHostException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				if(ds != null){
					ds.close();
				}
			}
			Log.d(TAG, "...sent " + command + " command");
		}
	}


	private class MuteVolumeThread extends Thread
	{
		private static final String COMMAND_MUTE = "mute";

		private volatile boolean mQuit = false;
		private String mBrunoSrvrIp;

		public MuteVolumeThread()
		{
			super("MuteVolumeThread");
			mBrunoSrvrIp = BrunoApp.getAPP().PREFS.getString(BrunoApp.PREF_IP, "");
			if(mBrunoSrvrIp.equals("")){
				quit();
			}
		}

		public void quit()
		{
			Log.d(TAG, "quit()");
			mQuit = true;
			interrupt();
		}

		@Override
		public void run()
		{
			DatagramSocket ds = null;

			try{

				ds = new DatagramSocket();
				InetAddress serverAddr = InetAddress.getByName(mBrunoSrvrIp);

				DatagramPacket dp;
				dp = new DatagramPacket(COMMAND_MUTE.getBytes(), COMMAND_MUTE.length(), serverAddr, 1025);
				ds.send(dp);
			}
			catch(SocketException e){
				e.printStackTrace();
			}
			catch(UnknownHostException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				if(ds != null){
					ds.close();
				}
			}
			Log.d(TAG, "...sent " + COMMAND_MUTE + " command");
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		unregisterReceiver(mBroadcastReceiver);
	}
}
