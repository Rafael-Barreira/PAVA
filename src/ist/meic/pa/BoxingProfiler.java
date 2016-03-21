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
		CtMethod vInteger = ctMethod[0];
		for(int i = 0; i < ctMethod.length; i++) {
			//ctMethod[i].get
		}
	}
	
	public static void main (String[] args)throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		System.out.println("I'm in!");
		if (args.length < 2) {
			System.err.println("Usage: java Memoize <class> <method>");
			System.exit(1);
		} else {
			
			ClassPool pool = ClassPool.getDefault();
			CtClass ctClass = pool.getCtClass(args[0]);
			//memoize(ctClass, ctClass.getMethods());
			
			/*Class<?> rtClass = ctClass.toClass();
			Method main = rtClass.getMethod(args[1], args.getClass());*/
			CtMethod method = ctClass.getDeclaredMethod("lel");
			method.instrument(
			        new ExprEditor() {
			            public void edit(MethodCall m)
			                          throws CannotCompileException
			            {
			                System.out.println(m.getClassName() + "." + m.getMethodName() + " " + m.getSignature());
			            }
			        });
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