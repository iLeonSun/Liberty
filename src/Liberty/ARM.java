package Liberty;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author CN0748
 * parse ARM lib name
 * ARM lib example:
 * sc7mc_cmos28slp_base_hvt_c38_ff_nominal_min_1p10v_m40c
 *   ^track num          ^ vt info 
 */
public class ARM implements Vender {

	@Override
	//node,track,channel,vt
	public String[] parselibname(String name) {
		Pattern p = Pattern.compile("sc(\\d+)[^_]+_\\D+([^_]+)\\w*?([slh]vt)_c(\\d+)");
		Matcher m =p.matcher(name);
		if (m.find()) {
			String[] result = {m.group(2),m.group(1),m.group(4),m.group(3)};
			return result;
		} else {
			System.out.println("name not match track & vt");
			return null;
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	public static void main(String[] args) {
		System.out.println(Arrays.asList(new ARM().parselibname("sc9mc_cln28hpm_base_svt_c38")));
	}
	
}
