package Liberty;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author CN0748
 * A lib includes:
 *  libname: 
 *  opc:
 *  voltage_map:
 *  stdcell: track,channel,vender
 */
public class Lib {
	private String libname;
	private String libfile;
	private String dbfile;
	private String libpath;
	private String dbpath;
	private String orignal_opc_name;
	private Opc opc; //normalized operating condition
	private Map v_map=new HashMap<String,Double>();
	//stdcell field
	private String node;
	private String track;
	private String channel;
	private String vt;
	private String vender;
	
	public Lib(String libpath) {
	this.libpath = libpath;
	parse();
	parsedb();
	checkNullField();
	}
	
	public Lib(String libpath,String vender) {
		// a stdcell
		this(libpath);
		this.vender=vender;
		try {
			Vender v = (Vender) Class.forName("Liberty."+vender).newInstance();
			String[] result = v.parselibname(getLibname()); //node,track,channel,vt
			setNode(result[0]);
			setTrack(result[1]);
			setChannel(result[2]);
			setVt(result[3]);
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		checkNullField();
	}
	
	public String getLibpath() {
		return libpath;
	}


	public void setLibpath(String libpath) {
		this.libpath = libpath;
	}


	public String getLibname() {
		return libname;
	}


	public void setLibname(String libname) {
		this.libname = libname;
	}


	public String getOrignal_opc_name() {
		return orignal_opc_name;
	}


	public void setOrignal_opc_name(String orignal_opc_name) {
		this.orignal_opc_name = orignal_opc_name;
	}


	public Opc getOpc() {
		return opc;
	}


	public void setOpc(Opc opc) {
		this.opc = opc;
	}

	public String getLibfile() {
		return libfile;
	}

	public void setLibfile(String libfile) {
		this.libfile = libfile;
	}

	public String getDbpath() {
		return dbpath;
	}

	public void setDbpath(String dbpath) {
		this.dbpath = dbpath;
	}

	public String getDbfile() {
		return dbfile;
	}

	public void setDbfile(String dbfile) {
		this.dbfile = dbfile;
	}

	public Map getV_map() {
		return v_map;
	}


	public void setV_map(Map v_map) {
		this.v_map = v_map;
	}

	
	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getVt() {
		return vt;
	}

	public void setVt(String vt) {
		this.vt = vt;
	}

	public String getVender() {
		return vender;
	}

	public void setVender(String vender) {
		this.vender = vender;
	}

	public String getOpc_name() {
		return opc.getOpc_name();
	}
	/*
	 * parse lib contents to get:
	 * libname
	 * opc --> lib_opc will be changed to normalized opc
	 * rail(may have multi rail)
	 */
	public void parse() {
		Pattern p;
		Matcher m;
		
		try {
			// only need the head dozens of lines, put them in a StringBuilder
			BufferedReader in = new BufferedReader(new FileReader(libpath));
			StringBuilder sb = new StringBuilder();
			String s;
			
			//2017/12/21: some ip may have qoute"" in name,remove.
			while ((s = in.readLine()) != null) {
//				System.out.println(s);
				if (s.matches("^\\s*cell\\s*\\(.*")) {
					break;
				}
				s=s.replaceAll("\"","");
				//voltage map : saved in hashmap
				p = Pattern.compile("voltage_map\\s*\\((\\w+)\\s*,\\s*([\\d\\.]+)\\s*\\)\\s*;");
				m = p.matcher(s);
				if (m.find()) {
					v_map.put(m.group(1), m.group(2));
				}
				sb.append(s + "\n");
			}
			in.close();
			/*
			 * in lib,lib_name/PVT/opc_name always show in order, so put them together 
			 * 2017/12/21:NO! some fucking stupid guy may change the PVT order in IP lib
			 * 
			//lib name
			Pattern p_libname = Pattern.compile("library\\s*\\(([^)]+)\\)");
			// PVT
			Pattern p_process = Pattern.compile("nom_process\\s*:\\s*([\\d\\.]+)");
			Pattern p_temp = Pattern.compile("nom_temperature\\s*:\\s*([-\\d\\.]+)");
			Pattern p_voltage = Pattern.compile("nom_voltage\\s*:\\s*([\\d\\.]+)");
			// orignal opc name
			Pattern p_opc = Pattern.compile("default_operating_conditions\\s*:\\s*([\\w\\.]+)");
			*/
			String p_libname = "library\\s*\\(([^)]+)\\)";
			String p_process = "nom_process\\s*:\\s*([\\d\\.]+)";
			String p_temp = "nom_temperature\\s*:\\s*([-\\d\\.]+)";
			String p_voltage = "nom_voltage\\s*:\\s*([\\d\\.]+)";
			String p_opc = "default_operating_conditions\\s*:\\s*([\\w\\.\"]+)"; // some may have " in opc_name,stupid.
			String all = p_libname+".*"+p_process+".*"+p_temp+".*"+p_voltage+".*"+p_opc;
			p = Pattern.compile(all,Pattern.DOTALL);
			m = p.matcher(sb);
			if(m.find()) {
				this.libname=m.group(1);
				// in lib: process = 1 
				//opc.setProcess(m.group(2));
				// temp may be a double(eg:-40.000000), so cast string-->double-->int.
				//opc.setTemperture((int) Double.parseDouble(m.group(3)));
				//opc.setVoltage(Double.parseDouble(m.group(4)));
				this.orignal_opc_name = m.group(5);
				//opc.setProcess(Util.getProcessFromString(orignal_opc_name));
				opc= new Opc(Util.getProcessFromString(orignal_opc_name),
						Double.parseDouble(m.group(4)),
						(int) Double.parseDouble(m.group(3))
						);
			} else {
				all = p_libname+".*"+p_process+".*"+p_voltage+".*"+p_temp+".*"+p_opc; // change PVT order in case some stupid case.
				p = Pattern.compile(all,Pattern.DOTALL);
				m = p.matcher(sb);
				if(m.find()) {
					this.libname=m.group(1);
					this.orignal_opc_name = m.group(5);
					//opc.setProcess(Util.getProcessFromString(orignal_opc_name));
					opc= new Opc(Util.getProcessFromString(orignal_opc_name),
							Double.parseDouble(m.group(3)),
							(int) Double.parseDouble(m.group(4))
							);
				}
			}
						
		} catch (FileNotFoundException e) {
			System.out.println(libpath + " cannot be found.");
			// return null;
		} catch (IOException e) {
			e.printStackTrace();
			// return null;
		}
	}
	
	/*
	 * based config file, get related db file
	 * libpath: -->
	 * libfile, dbfile, dbpath
	 */
	public void parsedb() {
		//libfile & dbfile name
		this.libfile = Paths.get(libpath).getFileName().toString();
		this.dbfile = libfile.replaceAll("lib","db");
		//dbpath: change latest 2 level
		String lib_dir = Paths.get(libpath,"..").normalize().getFileName().toString();
		String db_dir = lib_dir.replaceAll("lib", "db");
		Path p_db = Paths.get(libpath,"../..",db_dir,dbfile).normalize();
		if (Files.exists(p_db,new LinkOption[]{})) {
			dbpath=p_db.toString();
		} 		
	}
	
	/*
	 * check a lib's null attribute,
	 * write to a warning file.
	 */
	public void checkNullField() {
		String[] libFields = {"libname","libfile","dbfile","dbpath","orignal_opc_name","opc","v_map"};
		String[] stdFields = {"libname","libfile","dbfile","dbpath","orignal_opc_name","opc","v_map","node","track","channel","vt","vender"};
		String[] checkFields;
		if (this.getVender()==null) {
			checkFields = libFields;
		} else {
			checkFields = stdFields;
		}
		List<String> nullFields = new ArrayList<String>();
		for (String field: checkFields) {
			String getFieldMethod = Util.getFieldMethod(field);
			Method m;
			try {
				m = Lib.class.getMethod(getFieldMethod, null);
				if (m.invoke(this) == null) {
					nullFields.add(field);
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if (nullFields.size()> 0) {
			System.out.println("Warning: "+getLibfile()+" has null attributes: "+nullFields);
		}
	}
	@Override
	public String toString() {
		return "libname: "+libname+"\n"
				+ "lib_path: "+libpath +"\n"
				+ "lib_opc: "+orignal_opc_name +"\n"
				+ "opc: "+opc +"\n"
				+ "rail: "+v_map +"\n"
				+ "node: "+node +"\n"
				+ "track: "+track +"\n"
				+ "channel: "+channel +"\n"
				+ "vt: "+vt +"\n"
				+ "vender: "+vender +"\n";
				
	}

	public static void main(String[] args) {
		
//		Lib l2 = new Lib("tsmc.txt","TSMC");
//		System.out.println(l2);
	
	}
}
