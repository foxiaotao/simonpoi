package regextest;

import simon.demo.core.util.Constants;
import simon.demo.core.util.RegexUtil;

public class RegexTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String zhibu = "中共监利县分盐镇某某村支部委员会";
		String zhibu2 = "中共分盐镇某某村支部委员会";
		String zhibu3 = "中共分盐镇某某村委员会";
		
		System.out.println(RegexUtil.isMatches(zhibu, Constants.REX_MATCH_STRING_PARTY_ZHI_BU));
		System.out.println(RegexUtil.isMatches(zhibu2, Constants.REX_MATCH_STRING_PARTY_ZHI_BU));
		System.out.println(RegexUtil.isMatches(zhibu3, Constants.REX_MATCH_STRING_PARTY_ZHI_BU));
	}

}
