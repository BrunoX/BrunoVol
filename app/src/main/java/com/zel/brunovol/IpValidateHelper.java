package com.zel.brunovol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by zel on 03/11/15.
 */
public class IpValidateHelper
{
	private Pattern pattern;
	private Matcher matcher;

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public IpValidateHelper()
	{
		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}

	public boolean validate(final String ip)
	{
		matcher = pattern.matcher(ip);
		return matcher.matches();
	}
}
