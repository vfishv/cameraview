/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.folderv.timelapse;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;


public final class Utils {
	private static String sAppName = "";

	public static boolean isMainThread() {
		return Looper.getMainLooper() == Looper.myLooper();
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInputFromWindow(Context context,View view)
	{
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	public static File getAppCacheDir(Context context, String subName) {
		if (!sdAvailible()) {
			return null;
		}
		File sd = Environment.getExternalStorageDirectory();
		File dir = new File(sd, "timelapse");
		File sub = new File(dir, subName);
		sub.mkdirs();
		return sub;
	}

	public static boolean sdAvailible() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	public static String encrypt(String str) {
		// TODO: encrypt data.
		return str;
	}

	public static String buildSystemInfo(Context context) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\n");
		buffer.append("#-------system info-------");
		buffer.append("\n");
		buffer.append("version-name:");
		buffer.append(Utils.getVersionName(context));
		buffer.append("\n");
		buffer.append("version-code:");
		buffer.append(Utils.getVersionCode(context));
		buffer.append("\n");
		buffer.append("system-version:");
		buffer.append(Utils.getSystemVersion(context));
		buffer.append("\n");
		buffer.append("model:");
		buffer.append(Utils.getModel(context));
		buffer.append("\n");
		buffer.append("density:");
		buffer.append(Utils.getDensity(context));
		buffer.append("\n");
		buffer.append("imei:");
		buffer.append(Utils.getIMEI(context));
		buffer.append("\n");
		buffer.append("screen-height:");
		buffer.append(Utils.getScreenHeight(context));
		buffer.append("\n");
		buffer.append("screen-width:");
		buffer.append(Utils.getScreenWidth(context));
		buffer.append("\n");
		buffer.append("unique-code:");
		buffer.append(Utils.getUniqueCode(context));
		buffer.append("\n");
		buffer.append("mobile:");
		buffer.append(Utils.getMobile(context));
		buffer.append("\n");
		buffer.append("imsi:");
		buffer.append(Utils.getProvider(context));
		buffer.append("\n");
		buffer.append("isWifi:");
		buffer.append(Utils.isWifi(context));
		buffer.append("\n");
		return buffer.toString();
	}

	public static String getUniqueCode(Context context) {
		if (context == null)
			return null;
		String imei = getIMEI(context);
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String mUniqueCode = imei + "_" + info.getMacAddress();
		return mUniqueCode;
	}

	public static boolean isWifi(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static String getMobile(Context context) {

		if(PackageManager.PERMISSION_DENIED ==PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_SMS)){

		}
		else {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return telephonyManager.getLine1Number();
		}
		return "";
	}

	public static String getProvider(Context context) {
		if(PackageManager.PERMISSION_DENIED ==PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)){

		}
		else {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return telephonyManager.getSubscriberId();
		}
		return "";
	}

	public static final String getIMEI(final Context context) {
		if(PackageManager.PERMISSION_DENIED ==PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)){

		}
		else {
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return manager.getDeviceId();
		}
		return "";
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.heightPixels;
	}

	public static String getSystemVersion(Context context) {
		return Build.VERSION.RELEASE;
	}

	public static String getModel(Context context) {
		return Build.MODEL != null ? Build.MODEL.replace(
				" ", "") : "unknown";
	}

	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static String getVersionName(Context context) {
		try {
			PackageInfo pinfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			return pinfo.versionName;
		} catch (NameNotFoundException e) {
		}

		return "";
	}

	public static String getAppName(Context context) {
		if (TextUtils.isEmpty(sAppName)) {
			sAppName = "com_forlong401_log";
			try {
				PackageInfo pinfo = context.getPackageManager().getPackageInfo(
						context.getPackageName(),
						PackageManager.GET_CONFIGURATIONS);
				String packageName = pinfo.packageName;
				if (!TextUtils.isEmpty(packageName)) {
					sAppName = packageName.replaceAll("\\.", "_");
				}
			} catch (NameNotFoundException e) {
			}
		}

		return sAppName;
	}

	public static int versionCode = 0;

	public static int getVersionCode(Context context) {
		if(context==null){
			context = Timelapse.getInstance();
		}
		if (versionCode != 0) {
			return versionCode;
		}
		try {
			PackageInfo pinfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS);
			versionCode = pinfo.versionCode;
		} catch (NameNotFoundException e) {
		}

		return versionCode;
	}

	static final Point screenSize = new Point();
	public static Point getScreenSize(Context ctt) {
		if (ctt == null) {
			return screenSize;
		}
		WindowManager wm = (WindowManager) ctt.getSystemService(Context.WINDOW_SERVICE);
		if (wm != null) {
			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
			Display diplay = wm.getDefaultDisplay();
			if(diplay!=null)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)// Build.VERSION_CODES.JELLY_BEAN
				{
					diplay.getRealMetrics(mDisplayMetrics);
				}
				else
				{
					diplay.getMetrics(mDisplayMetrics);
				}
				int W = mDisplayMetrics.widthPixels;
				int H = mDisplayMetrics.heightPixels;
				if (W * H > 0 /*&& (W > screenSize.x || H > screenSize.y)*/)
				{
					screenSize.set(W, H);
					//Log.e(TAG, "screen size:" + screenSize.toString());
				}
			}
		}
//		if (MainActivity.DEBUG) {
//			Log.i(TAG, screenSize.toString());
//		}
		return screenSize;
	}

	public static void resizeView(View view,int width, float hwratio){
		ViewGroup.LayoutParams vp = view.getLayoutParams();
		//FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) view.getLayoutParams();
		vp.height = (int)(width *hwratio);
		view.setLayoutParams(vp);
	}

	public static boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
			//System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
}
