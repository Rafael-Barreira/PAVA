package ist.meic.pa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BoxingProfiler {
	
	//the name of another Java program and 
	//the arguments that should be provided 
	//to that program
	public static void main (String[] args) {
		//Receives class name and arguments for function
		System.out.println("I'm in!");
		String[] func_args = new String[args.length - 1];
		
		//loads func_args with arguments to be ran with the program
		for(int i=1; i <= args.length; i++) {
			func_args[i-1]=args[i];
		}
		//args[0]->NameOfFunction args[>0]->ArgsOfTheFunction
		
		try {
			
			Class c = Class.forName("ist.meic.pa"+args[0]);
			Object func = c.newInstance();
			Class[] argTypes = new Class[] { String[].class };
			Method m = c.getMethod("ist.meic.pa"+args[0], argTypes);
			m.invoke(func, args[1]);
			
		} catch(ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
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
}