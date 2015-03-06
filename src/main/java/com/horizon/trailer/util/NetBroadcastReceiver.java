package com.horizon.trailer.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 *���ܹ㲥 
 *����ͨ�� setUiHandler ����UI��Ϣ
 * @author Zheng
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
	
	Handler uiHandler;
 
	/**
	 * msg.what = 1 ���������ѻָ�����
	 * msg.what = 0 ���������������ж�
	 * @param uiHandler
	 */
	public void setUiHandler(Handler uiHandler) {
		this.uiHandler = uiHandler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean success = false;
		// ����������ӷ���
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// State state = connManager.getActiveNetworkInfo().getState();
		if (State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState() || State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState()) { // �ж��Ƿ�����ʹ��WIFI����
			success = true;
//			if(State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//				.getState() ){
//				WifiManager mWifiManager = (WifiManager) context
//						.getSystemService(Context.WIFI_SERVICE);
//				
//				success = mWifiManager.pingSupplicant();// ping�����Ƿ��ܹ���ͨ��
//				if(!success){
//					Toast.makeText(context, "�������粻��������������", Toast.LENGTH_SHORT).show();
//				}
//				Log.i("network", "��ǰ�����Ƿ�������������ҳ��" + success);
//			}
		}else{
			success = false;
		}

		Message msg = new Message(); 
		if (!success) {//�������Ӻ� ������������õ��������֪ͨ �ֻ���Ҫ����2-3s�Ļ����� app �ſ���ʹ������
			msg.what = 0;
			Toast.makeText(context, "���������������ж�", Toast.LENGTH_SHORT).show();
		} else {
			msg.what = 1;
		}
		if(uiHandler!=null){
			uiHandler.dispatchMessage(msg);
		}
	}

}