package ist.meic.pa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod; 
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class BoxingProfiler {
	
	/*	TreeMap Key:Counter name	Value:Method on which the counter was inserted
	*	the value method is used to complement the information in the final print
	*/
	static final TreeMap<String, CtMethod> counterList = new TreeMap<String, CtMethod>();
	static CtClass ctClass;
	
	/*	Creates unique counter id to insert in profiling Class
	 * 	counter composed by method name + method type + action(boxed/unboxed)
	 * 	returns null if the method call is not relevant
	 */
	static String counterCreator(String classmethod, String methodname, String mainmethod) {
		if(classmethod.startsWith("java.lang.")) {			
			if(methodname.equals("valueOf")) {
				String[] type = classmethod.split("[.]");
				String counter = mainmethod+ "_" + type[2] + "_" + "boxed";
				return counter;		
			} else if(methodname.endsWith("Value")) {
				if(methodname.startsWith("int") && classmethod.endsWith("Integer")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("boolean") && classmethod.endsWith("Boolean")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("byte") && classmethod.endsWith("Byte")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("char") && classmethod.endsWith("Character")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("float") && classmethod.endsWith("Float")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("long") && classmethod.endsWith("Long")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("short") && classmethod.endsWith("Short")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
				else if(methodname.startsWith("double") && classmethod.endsWith("Double")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					return counter;
				}
			} else return null;
		} else return null;
		return null;
	}
	
	/*	inserts counter increment and counter initialization in the given class
	 * 	counter incremented is inserted where there is a boxing or unboxing of a type
	 * 	counter are global and are initialized with zero
	 */
	static void insertCounter(String counter, CtMethod method, MethodCall m) {
		try {
			if(counter!=null){
				if(!counterList.containsKey(counter)) {
					counterList.put(counter, method);
					CtField ctField = CtField.make("public static int " + counter + " =0;", ctClass);
					ctClass.addField(ctField);
				}
				m.replace("$_ = $proceed($$);;" + counter+"++;");
			}
		} catch (CannotCompileException e) {
			System.err.println("BoxingProfiler.insertInc error");
		}
	}
	
	/*	Goes through all the counters created and inserts a print command in the given class
	 * 	Only the counters that are incremented are printed in the end by the given class
	 */
	static void insertCounterPrints () {
		String[] tokens;
		String type;
		String action;
		String methodFullName;
		String space=" ";			
		
		for(String c : counterList.keySet()) {
			
			tokens = c.split("[_]");
			type = "java.lang."+tokens[1];
			action = tokens[2];
			methodFullName=counterList.get(c).getLongName();
			
			try {
				ctClass.getDeclaredMethod("main").insertAfter(
						"if("+ c +" != 0 ){"+
						"System.err.println(\""+ methodFullName + space + action + space + "\" + "+ c +" + \""+space+type+"\");}");
			} catch (CannotCompileException | NotFoundException e) {
				System.out.println("BoxingProfiler.insertCounterPrints error");
			}	
		}
	}
	
	//Gets class to profile, calls functions to provide profiling and calls main function of given class
	public static void main (String[] args) {
		if (args.length < 1) {
			System.err.println("Missing Class to profile");
			System.exit(1);
		} else {
			
			ClassPool pool = ClassPool.getDefault();
			
			try {
				ctClass = pool.getCtClass(args[0]);
				
				for(final CtMethod method: ctClass.getDeclaredMethods()) {
					final String pmethod = method.getName();
								
					method.instrument(
				        new ExprEditor() {
				            public void edit(MethodCall m)
				            {
				            	String counter = counterCreator(m.getClassName(), m.getMethodName(), pmethod);
				            	insertCounter(counter, method, m);
				            }            
				        });
				}
				
				insertCounterPrints();
				Class<?> rtClass = ctClass.toClass();
				Method main = rtClass.getMethod("main", args.getClass());
				main.invoke(null, new Object[] { args });
			 				
			} catch (NotFoundException e) {
				System.err.println("BoxingProfiler.main: "+args[0]+"is not a class.");
			} catch (CannotCompileException e) {
				System.err.println("BoxingProfiler.main: Could not apply instrument to method");
			} catch (NoSuchMethodException e) {
				System.err.println("BoxingProfiler.main: method does not exist");
			} catch (SecurityException e) {
				System.err.println("BoxingProfiler.main: security violation");
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("BoxingProfiler.main: Problem in main.invoke");
			}	
		}
	}
}