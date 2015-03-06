package com.horizon.trailer.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class AsyncHttpCilentUtil {

	private static AsyncHttpClient client;

	public static AsyncHttpClient getInstance(Context paramContext) {
		if (client == null) {
			client = new AsyncHttpClient();
			PersistentCookieStore myCookieStore = new PersistentCookieStore(paramContext);
			client.setCookieStore(myCookieStore);
			client.setTimeout(10000);
		}
		return client;
	}
}
