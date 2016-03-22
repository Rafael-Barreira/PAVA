package ist.meic.pa;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	
	static void memoize(CtClass ctClass, CtMethod[] ctMethod) throws ClassNotFoundException, NotFoundException, CannotCompileException {

	}
	
	static String methodParser(String classmethod, String methodname) {
		
		if(methodname.equals("valueOf")) {
			return "boxed "+ classmethod;			
		} else if(methodname.endsWith("Value")) {
			return "unboxed " + classmethod;
		} else return null;
		
	}
	
	public static void main (String[] args)throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		if (args.length < 1) {
			System.err.println("Usage: java Memoize <class>");
			System.exit(1);
		} else {
			
			ClassPool pool = ClassPool.getDefault();
			CtClass ctClass = pool.getCtClass(args[0]);
			
			for(CtMethod methods: ctClass.getDeclaredMethods()) {
				String pmethod = methods.getLongName();
			
				methods.instrument(
			        new ExprEditor() {
			            public void edit(MethodCall m)
			                          throws CannotCompileException
			            {
			            	String autoBoxing = methodParser(m.getClassName(), m.getMethodName());
			            	if(autoBoxing!=null){
			            		//System.out.println(pmethod + " " + m.getClassName() + "." + m.getMethodName() + " ");
			            		System.out.println(pmethod + " " + autoBoxing);
			            		System.out.println("#######################");
			            	}            
			            }
			        });
			}
			
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