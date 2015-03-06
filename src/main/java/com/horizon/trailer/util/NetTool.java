package com.horizon.trailer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class NetTool {

	/**
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.i("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == State.CONNECTED) {
						Log.i("NetWorkState", "Availabel");
//						if(State.CONNECTED == connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//								.getState() ){
//							WifiManager mWifiManager = (WifiManager) context
//									.getSystemService(Context.WIFI_SERVICE);
//							
//							boolean b = mWifiManager.pingSupplicant();// ping�����Ƿ��ܹ���ͨ��
//							Log.i("network", "��ǰ�����Ƿ�������������ҳ��" + b);
//							return b;
//							
//						}
						return true;
					}
				}
			}
		}
		return false;
	} 

}
