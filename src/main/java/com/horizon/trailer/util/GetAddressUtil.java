package com.horizon.trailer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class GetAddressUtil {
	private HashMap<String, ArrayList<String>> cityMap = new HashMap<String, ArrayList<String>>();// 城市map
	private HashMap<String, String> codeMap = new HashMap<String, String>();// 编号map，根据城市获�?
	private Activity activity;

	public GetAddressUtil(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 从assets中读取地区数�?,保存为list类型
	 */
	public StringBuffer getData() {
		StringBuffer json = new StringBuffer();
		try {
			InputStreamReader isr = new InputStreamReader(activity
					.getResources().getAssets().open("city.json"));
			BufferedReader br = new BufferedReader(isr);
			char[] tempchars = new char[1024];
			int charread = 0;
			while ((charread = br.read(tempchars)) != -1) {
				json.append(tempchars);
			}
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 省份与城市相对应
	 * 
	 * @return 省份
	 */
	public ArrayList<String> getProvinceList() {
		ArrayList<String> provinceList = new ArrayList<String>();// 省份
		String json = getData().toString();
		try {
			JSONArray data = new JSONArray(json);
			for (int i = 0; i < data.length(); i++) {
				JSONObject provinceObj = data.getJSONObject(i);
				String province = provinceObj.getString("name");
				provinceList.add(province);
				JSONArray citys = provinceObj.getJSONArray("citys");
				ArrayList<String> cityList = new ArrayList<String>();// 相应的城市列�?
				for (int j = 0; j < citys.length(); j++) {
					JSONObject cityObj = citys.getJSONObject(j);
					Iterator<String> keys = cityObj.keys();
					while (keys.hasNext()) {
						String key = keys.next();
						String city = cityObj.getString(key);
						cityList.add(city);
						codeMap.put(city, key);
					}
					if (j == citys.length() - 1) {
						cityMap.put(province, cityList);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return provinceList;
	}

	/**
	 * 获得省份相对应的城市列表
	 * 
	 * @param province
	 *            省份
	 * @return 城市列表
	 */
	public ArrayList<String> getCityList(String province) {
		if("未选择".equals(province)){
			ArrayList a = new ArrayList<String>();
			a.add("未选择");   
			return a;
		}
		return cityMap.get(province);
	}

	/**
	 * 通过code获取city
	 * */
	public String getCity(String code) {
		getProvinceList();
		String city = (String) keyString(codeMap, code);
		return city;
	}

	/**
	 * 通过city获取province
	 */
	public String getProvince(String city) {
		Iterator<String> it = cityMap.keySet().iterator();
		while (it.hasNext()) {
			String province = it.next();
			List<String> cityList = cityMap.get(province);
			for (int i = 0; i < cityList.size(); i++) {
				if (cityList.get(i).equals(city)) {
					return province;
				}
			}
		}
		return null;
	}

	/**
	 * 通过city获取code
	 * 
	 */
	public String getCode(String city) {
		return codeMap.get(city);
	}

	public static Object keyString(HashMap<String, String> map, String o) {
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			Object keyString = it.next();
			if (map.get(keyString).equals(o))
				return keyString;
		}
		return null;
	}

	public List<String> getProvinceListWithProvince() {
		ArrayList<String> provinceList = new ArrayList<String>();// 省份
		String json = getData().toString();
		try {
			JSONArray data = new JSONArray(json);
			for (int i = 0; i < data.length(); i++) {
				JSONObject provinceObj = data.getJSONObject(i);
				String province = provinceObj.getString("name");
				provinceList.add(province);
				JSONArray citys = provinceObj.getJSONArray("citys");
				ArrayList<String> cityList = new ArrayList<String>();// 相应的城市列�?
				if (citys.length() > 1) {// 非直辖市 添加第一个元素 为 省
					cityList.add(province);
				}
				for (int j = 0; j < citys.length(); j++) {
					JSONObject cityObj = citys.getJSONObject(j);
					Iterator<String> keys = cityObj.keys();
					while (keys.hasNext()) {
						String key = keys.next();
						String city = cityObj.getString(key);
						cityList.add(city);
						codeMap.put(city, key);
					}
					if (j == citys.length() - 1) {
						cityMap.put(province, cityList);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return provinceList;
	}
}
