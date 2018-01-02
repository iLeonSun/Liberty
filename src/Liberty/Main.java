package Liberty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
	public static void main(String[] args)
	{
		/*
		 * Usage:
		 * toJson:
		 * java -jar Lib.jar -libs lib_list_file [-output outputFile] 
		 * filterJson:
		 * java -jar Lib.jar -json jsonFile -filter filterExpression -outputAttr AttrName -outputFile outputFile
		 */
		 String helpInfo = "Usage:\n"
					+"toJson:\n"
					+"java -jar Lib.jar -libs libsListFile [-output outputFile]\n"
					+"filterJson:\n"
					+"java -jar Lib.jar -json jsonFile -filter filterExpression -outputAttr AttrName -outputFile outputFile\n"
					+"Please refer README for more info";			
		if (args[0].equals("-h") || args[0].equals("-help")) {
			System.out.println(helpInfo);
		} else if (args[0].equals("-libs") && args.length==2) {
			// toJson w/o outputFile
			toJson(args[1]);
		} else if (args[0].equals("-libs") && args.length==4) {
			// toJson w outputFile
			toJson(args[1],args[3]);
		} else if (args[0].equals("-json") && args.length==8) {
			filterJson(args[1],args[3],args[5],args[7]);
		} else {
			System.out.println("Error: wrong usage!");
			System.out.println(helpInfo);
		}
	
	}

	public static void toJson(String file,String outputfile) {	
		List<Lib> libs = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			int lineno=0;
			libs = new ArrayList<Lib>();
			while((line=in.readLine()) != null) {
				lineno++;
				String[] words=line.split("\\s+");
				if (words.length==1) {
					//Lib
					libs.add(new Lib(words[0]));
				} else if (words.length == 2) {
					//Stdcel
					libs.add(new Lib(words[0],words[1]));
				} else {
					System.out.println("Error: wrong setting at line "+lineno);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: cannot find file: "+file);
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			Writer out = new FileWriter(new File(outputfile));
//		System.out.println(gson.toJson(libs));
			out.write(gson.toJson(libs));
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Total "+libs.size()+" libs are parsed.");
	}

	/*
	 * if not specify outputfile, use "libs.json" as default
	 */
	public static void toJson(String file) {
		toJson(file,"libs.json");
	}
	
	
	public static void filterJson(String file,String filterExpressions,String outputField,String outputFile) {

		Gson gson = new Gson();
		FileReader in = null;
		try {
			in = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: cannot find file "+file);
			e.printStackTrace();
			System.exit(1);
		}
		Lib[] libs_array =  gson.fromJson(in, Lib[].class);
		List<Lib> filteredLibs = new ArrayList<Lib>();
		List<String> filteredLibsOutputs = new ArrayList<String>();
		/*
		 * Stream only supported since JRE1.8
		 * 
		 * Object[] a = libs.stream().filter(lib -> lib.getOpc().getOpc_name().equals("ss_0.81_-40")).toArray();
		 */
		//output fileld
		String[] outputs = outputField.trim().split("\\s+");
		for (Lib lib:libs_array) {
			String libOutput = "";
			if (Filter.testExpression(lib,filterExpressions)) {
				filteredLibs.add(lib);
				for (String field:outputs) {
					String m = Util.getFieldMethod(field);
					Method method = null;
					try {
						method = Lib.class.getMethod(m, null);
						libOutput+=(String)method.invoke(lib)+" ";
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				filteredLibsOutputs.add(libOutput);
			}
		}
		
		//write result to a file
		Writer out;
		try {
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			for (String s:filteredLibsOutputs) {
				out.append(s+"\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("Error: cannot write to file"+outputFile);
		}

	}
	
}

	
