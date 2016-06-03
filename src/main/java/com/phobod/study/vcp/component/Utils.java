package com.phobod.study.vcp.component;

import java.util.regex.Pattern;

public class Utils {
	public static boolean checkPasswordWithRegExp(String password) {
		Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,12}$");
		return p.matcher(password).matches();
	}

	public static boolean checkEmailWithRegExp(String password) {
		Pattern p = Pattern.compile("^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$");
		return p.matcher(password).matches();
	}

	public static boolean checkPhoneNumberWithRegExp(String password) {
		Pattern p = Pattern.compile("(\\+)?(\\()?(\\d+){1,4}(\\))?(-)?(\\d+){1,3}?(-)?(\\d+){1,4}?(-)?(\\d+){1,4}?(-)?(\\d+){1,4}");
		return p.matcher(password).matches();
	}
}
