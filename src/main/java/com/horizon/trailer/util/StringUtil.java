package com.horizon.trailer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean checkPno(String mobiles) {
		Pattern p = Pattern.compile("^(13|15|18|14)\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
		// return true;
	} 

}
