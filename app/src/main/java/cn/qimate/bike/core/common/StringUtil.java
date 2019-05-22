package cn.qimate.bike.core.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	/**
	 * 校验手机号
	 */

	private final static Pattern phoner = Pattern.compile("1\\d{10}");
	
	/**
	 * 校验邮箱
	 * */
	private final static Pattern emailer = Pattern.compile(
			"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
	/**
	 * 校验一个字符串是否为整数
	 */
	private final static Pattern integer = Pattern.compile("^[1-9]\\d*$");

	/**
	 * 校验一个字符串是否含有数字
	 */

	private final static Pattern hasDigit = Pattern.compile(".*\\d+.*");

	/**
	 * 校验一个字符串是否有1-2位小数
	 */

	private final static Pattern decimals = Pattern.compile("^[0-9]+(.[0-9]{1,2})?$");

	/**
	 * 返回小数位数
	 * 
	 */
	public static int decimalsLen(String str, int len) {
		int index = str.lastIndexOf(".");// 寻找小数点的索引位置，若不是小数，则为-1
		if (index > -1) {
			len = str.substring(index + 1).length();// 取得小数点后的数值，不包括小数点
		} else {
			len = 0;
		}
		return len;
	}

	/**
	 * 
	 * 判断一个字符串是否为整数
	 * 
	 */

	public static boolean isInteger(String str) {

		if (str == null || "".equals(str))
			return false;
		return integer.matcher(str).matches();
	}

	/**
	 * 将字符串转化为double类型
	 * 
	 * @param value
	 * 
	 * @return
	 */
	public static double StringToDouble(String value) {
		if (value == null || "".equals(value))
			return 0.00;
		return Double.parseDouble(value);
	}

	/**
	 * 判断是不是一个合法的手机号码
	 * 
	 * @param card
	 * @return
	 */
	public static boolean isPhoner(String phone) {
		if (phone == null || "".equals(phone))
			return false;
		return phoner.matcher(phone).matches();
	}

	// 判断一个字符串是否含有数字
	public static boolean hasDigit(String content) {

		boolean flag = false;

		if (content == null || "".equals(content))
			return false;

		if (hasDigit.matcher(content).matches()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 校验一个字符串是否有1-2位小数
	 */
	public static boolean hasDecimals(String str) {

		if (str == null || "".equals(str))
			return false;

		return decimals.matcher(str).matches();
	}

	public static boolean isCompanyPhone(String phone) {
		if(phone.contains(" ")){
			return false;
		}
		if (phone.startsWith("1")) {
            return phone.length() == 11;
        }
		if (!phone.startsWith("0")) {
			return false;
		}
		if (!phone.contains("-")) {
			return false;
		}
		int startLen = phone.substring(0, phone.indexOf("-")).length();
		if (startLen != 3 && startLen != 4) {
			return false;
		}
		int endLen = phone.substring(phone.indexOf("-")+1).length();
        return endLen == 7 || endLen == 8;
    }
	
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;

		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;

		Pattern pattern = Pattern.compile(expression);

		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches()) {
			isValid = true;
		}

		return isValid;

	}
	/**
	 * 检验邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}
}
