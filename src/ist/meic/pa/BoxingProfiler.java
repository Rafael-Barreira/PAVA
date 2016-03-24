package ist.meic.pa;

import java.io.IOException;
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
	
	//the name of another Java program and 
	//the arguments that should be provided 
	//to that program
	
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
	
	public static void main (String[] args)throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		if (args.length < 1) {
			System.err.println("Usage: java Memoize <class>");
			System.exit(1);
		} else {
			
			ClassPool pool = ClassPool.getDefault();
			final CtClass ctClass = pool.getCtClass(args[0]);
			final TreeMap<String, CtMethod> counterList = new TreeMap<String, CtMethod>();
			
			for(final CtMethod methods: ctClass.getDeclaredMethods()) {
				final String pmethod = methods.getName();
				//final CtClass[] pmethodType=methods.getParameterTypes();			
				//System.out.println(pmethodType[0].getName());
							
				methods.instrument(
			        new ExprEditor() {
			            public void edit(MethodCall m)
			                          throws CannotCompileException
			            {
			            	String counter = counterCreator(m.getClassName(), m.getMethodName(), pmethod);
			            	if(counter!=null){
			            		if(!counterList.containsKey(counter)) {
			            			counterList.put(counter, methods);
			            			CtField ctField = CtField.make("public static int " + counter + " =0;", ctClass);
			    					ctClass.addField(ctField);
			            		}
			            		m.replace("$_ = $proceed($$);;" + counter+"++;");
			            		
			            	}
			            }            
			        });
			}
			
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
				
				ctClass.getDeclaredMethod("main").insertAfter(
						"if("+ c +" != 0 ){"+
						"System.err.println(\""+ methodFullName + space + action + space + "\" + "+ c +" + \""+space+type+"\");}");	
			}
			
			Class<?> rtClass = ctClass.toClass();
			Method main = rtClass.getMethod("main", args.getClass());
			main.invoke(null, new Object[] { args });
		}
	}
	
}