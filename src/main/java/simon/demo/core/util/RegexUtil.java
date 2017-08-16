package simon.demo.core.util;

import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * 校验身份证信息
     * @param idNoStr
     * @return
     */
    public static boolean isCard(String idNoStr){
    	Pattern p15 = Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$");
    	//Pattern p18 = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
    	Pattern p18 = Pattern.compile("^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    	return p15.matcher(idNoStr).matches() || p18.matcher(idNoStr).matches();
    }
    /**
     * 电话号码
     * @param idNoStr
     * @return
     */
    public static boolean isPhone(String phoneStr){
    	String phoneReg = "^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,3,6,7,8])|(18[0-9])|(19[8]))\\d{8}$";
    	Pattern phonePattern = Pattern.compile(phoneReg);
    	return phonePattern.matcher(phoneStr).matches();
    }
    /**
     * 座机电话号码
     * @param idNoStr
     * @return
     */
    public static boolean isHomePhone(String phoneStr){
    	//String homePhoneReg = "(\\(?(010|021|022|023|024|025|026|027|028|029|852)?\\)?-?\\d{8})|(\\(?(0[3-9][0-9]{2})?\\)?-?\\d{7,8})";
    	String homePhoneReg = "(\\(?(010|021|022|023|024|025|026|027|028|029|852)?\\)?-?\\d{8})|(\\(?(0[3-9][0-9]{2})?\\)?-?\\d{7})";
    	Pattern phonePattern = Pattern.compile(homePhoneReg);
    	return phonePattern.matcher(phoneStr).matches();
    }
    /**
     * 邮箱验证
     * @param idNoStr
     * @return
     */
    public static boolean isEmail(String emailStr){
    	String emailPatternMatcher = "\\b^['_a-z0-9-\\+]+(\\.['_a-z0-9-\\+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2}|aero|arpa|asia|biz|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|nato|net|org|pro|tel|travel|xxx)$\\b";
    	Pattern phonePattern = Pattern.compile(emailPatternMatcher);
    	return phonePattern.matcher(emailStr).matches();
    }
    /**
     * 日期验证（年月日）
     * @param idNoStr
     * @return
     */
    public static boolean isChnDate(String str){
    	String datePatternMatcher = "\\d{4}年(0?[1-9]|[1][012])月(0?[1-9]|[12][0-9]|3[01])日";
    	Pattern datePattern = Pattern.compile(datePatternMatcher);
    	return datePattern.matcher(str).matches();
    }
}
