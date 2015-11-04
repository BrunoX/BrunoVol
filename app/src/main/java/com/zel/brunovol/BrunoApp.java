package com.zel.brunovol;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by zel on 03/11/15.
 */
public class BrunoApp extends Application
{
	private static volatile BrunoApp sAPP;

	public static SharedPreferences PREFS;
	public static final String PREF_IP = "pref_ip";
	public static final String PREF_ACTIVE = "pref_active";

	@Override
	public void onCreate()
	{
		super.onCreate();

		sAPP = this;
		PREFS = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public static BrunoApp getAPP()
	{
		return sAPP;
	}
}
