package ist.meic.pa;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod; 
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class BoxingProfiler {
	
	//the name of another Java program and 
	//the arguments that should be provided 
	//to that program
	
	static String counterCreator(String classmethod, String methodname, String mainmethod) {
		if(classmethod.startsWith("java.lang.")) {			
			if(methodname.equals("valueOf")) {
				String[] type = classmethod.split("[.]");
				String counter = mainmethod+ "_" + type[2] + "_" + "boxed";
				//System.out.println(counter);
				return counter;
				//return "boxed "+ classmethod;			
			} else if(methodname.endsWith("Value")) {
				if(methodname.startsWith("int") && classmethod.endsWith("Integer")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("boolean") && classmethod.endsWith("Boolean")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("byte") && classmethod.endsWith("Byte")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("char") && classmethod.endsWith("Character")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("float") && classmethod.endsWith("Float")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("long") && classmethod.endsWith("Long")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("short") && classmethod.endsWith("Short")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
				else if(methodname.startsWith("double") && classmethod.endsWith("Double")){
					String[] type = classmethod.split("[.]");
					String counter = mainmethod + "_" + type[2] + "_" + "unboxed";
					//System.out.println(counter);
					return counter;
					//return "unboxed " + classmethod;
				}
			} else return null;
		} else return null;
		return null;
	}
	
	public static void main (String[] args)throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		if (args.length < 1) {
			System.err.println("Usage: java Memoize <class>");
			System.exit(1);
		} else {
			
			ClassPool pool = ClassPool.getDefault();
			final CtClass ctClass = pool.getCtClass(args[0]);
			
			for(final CtMethod methods: ctClass.getDeclaredMethods()) {
				final String pmethod = methods.getName();
				final ArrayList<String> counterList = new ArrayList<String>();
				Map<String, String> hashMap = new HashMap<String, String>();
				
				methods.instrument(
			        new ExprEditor() {
			            public void edit(MethodCall m)
			                          throws CannotCompileException
			            {
			            	String counter = counterCreator(m.getClassName(), m.getMethodName(), pmethod);
			            	if(counter!=null){
			            		//System.out.println(pmethod + " " + m.getClassName() + "." + m.getMethodName() + " ");
			            		//System.out.println(pmethod + " " + autoBoxing);
			            		if(!counterList.contains(counter)) {
			            			counterList.add(counter);
			            			CtField ctField = CtField.make("public static int " + counter + " =0;", ctClass);
			    					ctClass.addField(ctField);
			            		}		
			            		//System.out.println("Line Number: " + m.getLineNumber());
			            		
			            		//methods.insertAt(m.getLineNumber() + 1, counter+"++;");
			            		m.replace("$_ = $proceed($$);;" + counter+"++;");
			            		
			            		}
			            		

			            		//int index = m.getLineNumber();
			            		//methods.insertAt(index, counter+"++;");
			            		//System.out.println("#######################");
			            	}            
			            });
				
				String[] tokens;
				String method;
				String type;
				String action;
				String methodFullName;
				String space=" ";
				String key;
				String insert1;
				String insert2;
				
				
				for(String c : counterList) {
					//methods.insertBefore("int " + c + "=0;"+"System.out.println(\""+ c +": \" + "+ c +");");
					//CtField cc = new CtField(CtClass.intType, c, ctClass);
					//ctClass.addField(cc, "0");
					
					tokens = c.split("[_]");
					method = tokens[0];
					type = "java.lang."+tokens[1];
					action = tokens[2];
					methodFullName=ctClass.getDeclaredMethod(method).getLongName();
					//sortby method, sortby type, sortby action
					
					key= methodFullName+space+type+space+action;
					insert1= methodFullName + space + action + space;
					insert2=space+type;
					hashMap.put(key, insert1 + insert2);
					ctClass.getDeclaredMethod("main").insertAfter(
							"if("+ c +" != 0 ){"+
							"System.out.println(\""+ methodFullName + space + action + space + "\" + "+ c +" + \""+space+type+"\");}");	
				}
				//DOESNT ORDER MY ALPHABETICAL ORDER
				//Map reverseOrderedMap = new TreeMap(Collections.reverseOrder());
				Map<String, String> treeMap = new TreeMap<String, String>(hashMap);
				
				
				for (Map.Entry<String, String> entry : treeMap.entrySet()) {
					System.out.println(entry.getValue());
				}
			}
			
			Class<?> rtClass = ctClass.toClass();
			Method main = rtClass.getMethod("main", args.getClass());
			main.invoke(null, new Object[] { args });
			
			/*FIXME*/ 
			//memoize(ctClass, ctClass.getMethods());
			
			/*Class<?> rtClass = ctClass.toClass();
			Method main = rtClass.getMethod(args[1], args.getClass());*/
			/*String[] restArgs = new String[args.length - 2];
			System.arraycopy(args, 2, restArgs, 0, restArgs.length);*/
			//main.invoke(null, new Object[] { restArgs });
			
			/*ClassPool pool = ClassPool.getDefault();
			
			CtClass ctClass = pool.get(args[0]);
			
			memoize(ctClass, ctClass.getDeclaredMethods());
			
			Class<?> rtClass = ctClass.toClass();
			Method main = rtClass.getMethod("main", args.getClass());
			String[] restArgs = new String[args.length - 2];
			System.arraycopy(args, 2, restArgs, 0, restArgs.length);
			main.invoke(null, new Object[] { restArgs });*/
		}
	}
	
}