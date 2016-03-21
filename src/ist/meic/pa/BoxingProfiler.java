package ist.meic.pa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class BoxingProfiler {
	
	//the name of another Java program and 
	//the arguments that should be provided 
	//to that program
	public static void main (String[] args) throws NotFoundException, CannotCompileException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (args.length != 2) {
			System.err.println("Usage: <Class to Profile> <method>");
			System.exit(1);
			} else {
				ClassPool pool = ClassPool.getDefault();
				CtClass ctClass = pool.get(args[0]);
				Class<?> rtClass = ctClass.toClass();
				Method main = rtClass.getMethod("main", args.getClass());
				String[] restArgs = new String[args.length - 2];
				main.invoke(null, new Object[] { restArgs });
			}
	}
}