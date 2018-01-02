package Liberty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Filter {
//	public enum isEqual {
//		a,b;
//	}
	
	/*
	 * test Lib's field,
	 * support for: "==","!=","=~","!~"
	 */
	public static Boolean testField(Lib lib,String field,String test,String value) {
		String getvalue=null;
		String method = Util.getFieldMethod(field);
		//Method m=null;
		boolean isequal = false;
		try {
			Method m = Lib.class.getMethod(method, null);
			getvalue = (String) m.invoke(lib);
			//system.out.println(lib.getLibname() + "get value "+getvalue);
			// note: if use getvalue.equals(value)¡¡may throw exception when getvalue=null;
			if (test.equals("==") || test.equals("!=") ) {
				isequal = value.equals(getvalue)?true:false;
			} else if (test.equals("=~") || test.equals("!~")) {
				isequal = getvalue.matches(value)?true:false;
			} else {
				System.out.println("Error: "+test+" is not supported, only support for: == != =~ !~");
				System.exit(1);
			}
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: Lib class has not mothod: "+method+", please check.");
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

		if (test.equals("!=") || test.equals("!~")) {
			isequal = !isequal;
		}

		return isequal;
	}


	/*
	 * support for multi filter expression
	 * eg:
	 * opc_name==ssg_0.9_-40  && vender!=ARM || track!=7
	 */
	public static boolean testExpression(Lib lib, String multiexpressions) {
		boolean multiTestValue;
		List<String> listExpression= new ArrayList<String>();
		// remove blackspace
		multiexpressions= multiexpressions.replaceAll(" ", "");
		String[] subExpressions = multiexpressions.split("[&|]+"); //[opc_name==ssg_0.9_-40, vender!=ARM, track!=7]
		String[] computions = multiexpressions.split("[^&|]+");   //[, &&, ||]
		
		List<Boolean> subTestValue = new ArrayList<Boolean>();
		for (int i=0;i<subExpressions.length;i++){
			List<String> sub = splitFilterExpression(subExpressions[i]);
			subTestValue.add(testField(lib,sub.get(0),sub.get(1),sub.get(2)));
		}
		
		multiTestValue=(boolean) subTestValue.get(0); // only one expression
		if (subExpressions.length>1 ) {
			for (int i=1;i<subExpressions.length;i++) {
				String compution = computions[i];
				if (compution.equals("&&")) {
					multiTestValue=multiTestValue && subTestValue.get(i);
				}else if (computions.equals("||")) {
					multiTestValue=multiTestValue || subTestValue.get(i);
				} else {
					System.out.println("Error: filter expression error, please use \"&&\" and \"||\" only.");
				}
			}
		}
		return multiTestValue;
		
	}
	
	/*
	 * split  one filter expression (string) to: <String,String,String>
	 * return result is for testField args
	 * i.e:
	 * in: opc_name!=ssg_0.9_-40
	 * out: "opc_name","!=","ssg_0.9_-40"
	 */
	public static List<String> splitFilterExpression(String expression) {
		List<String> rst = new ArrayList<String>();
		expression= expression.replaceAll(" ", "");
		String[] a1 = expression.split("[!=~]+"); //[opc_name, ssg_0.9_-40]
		String[] a2 = expression.split("[^!=~]+"); //[, !~]
		if (a1.length==2 && a2.length==2) {
			rst.add(a1[0]);
			rst.add(a2[1]);
			rst.add(a1[1]);
			return rst;
		} else {
			System.out.println("Error: splitFilterExpression parse error, please check.");
			return null;
		}
	}
	
}
