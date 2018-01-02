package Liberty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author CN0748
 *	TSMC lib example:
 *	tcbn28hpcbwp7t40p140hvttt0p9v85c_ccs
 *	tcbn28hpcplusbwp7t30p140hvttt0p9v85c_ccs
 *	tcbn28hpcplusbwp7t40p140mb
 *	tcbn28hpcplusbwp7t30p140opphvt
 *		^node.proc  ^t ^c ^p ^s ^vt
 */
public class TSMC implements Vender{

	@Override
	public String[] parselibname(String name) {
		Pattern p = Pattern.compile("tcbn(\\d+\\w+)bwp(\\d+)t(\\d+)p[^(vt)]*?(u?[hl]vt)");
		//																	^non-greedy here
		Matcher m =p.matcher(name);
		Pattern p1 = Pattern.compile("tcbn(\\d+\\w+)bwp(\\d+)t(\\d+)p[^(vt)]*");
		Matcher m1 =p1.matcher(name);
		if (m.find()) {
			//System.out.println(m.group(0));
			String vt = m.group(4)==null?"svt":m.group(4);
			String[] result = {m.group(1),m.group(2),m.group(3),vt};
			return result;
		} else if (m1.find()) {
			//System.out.println(m1.group(0));
			String[] result = {m1.group(1),m1.group(2),m1.group(3),"svt"};
			return result;
		} else {
			return null;
		}
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		System.out.println(Class.forName("Liberty.TSMC"));
		TSMC t = (TSMC) Class.forName("Liberty.TSMC").newInstance();
//		String[] a = new TSMC().parselibname("tcbn28hpcplusbwp7t30p140opp");
		String[] a = t.parselibname("tcbn28hpcplusbwp7t30p140opp");
		System.out.println(Arrays.asList(a));
	}
}
