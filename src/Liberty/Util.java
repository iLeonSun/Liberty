package Liberty;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public final class Util {
	/*
	 *  get process from a operction condition name(not normailzed)
	 *  eg:
	 *  ssg0p81vm40c  ss0p81vm40c
	 *  ssg_0p81v_m40c ss_0p81v_m40c
	 */
	public static String getProcessFromString(String opc_name) {
		String process = "";
		Pattern p = Pattern.compile("[sft]{2}g?",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(opc_name);
		if (m.find()) {
			process = m.group(0);
		}
		return process.toLowerCase();
	}
	
	public static String getFieldMethod(String field) {
		return "get"+field.substring(0, 1).toUpperCase()+field.substring(1);
	}
	/*
	 *  get standard cell track/channel/vt info from stdcell lib name
	 *  return a list:
	 *  ArrayList<int,int,String>
	 */
	/*
	 * abandon,put in vender interface
	public static List getStdcellInfoFromString(String stdlib_name) {
		List stdinfo = new ArrayList();
		
		return stdinfo;
	}
	*/
	
	
	public static void main(String[] args) {
		System.out.println(
				Util.getProcessFromString("ssg0p81vm40c") + "\n" +
						Util.getProcessFromString("ss_0p81vm40c")+ "\n" +
						Util.getProcessFromString("SS0P81vm40c") + "\n" +
						Util.getProcessFromString("ssg0p81v0p9vm40c"));

	}
}
