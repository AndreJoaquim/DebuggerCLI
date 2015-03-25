package ist.meic.pa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

public class DebuggerCLI {

	public static Object initCommandLine(Object ct, Exception e) throws Exception, DCLIThrowable{
		
		System.out.print("DebuggerCLI:> ");
		String input = System.console().readLine();
		System.out.println("Command: " + input);
		
		String[] split_input = input.split(" ");
		String command = split_input[0];
		
		// Abort:
		// 		Terminates the execution of the application
		if (command.equals("Abort")){
			System.exit(1);
			
		// Info:
		// 		Presents detailed information about the called object, its fields,
		// 		and the call stack. The presented information follows the format
		// 		described in the next section. 	
		} else if (command.equals("Info")){
			
			//TODO:
			
		// Throw:
		// 		Re-throws the exception, so that it may be handled by the next handler.	
		} else if (command.equals("Throw")){
		
			throw e;
			
		// Return <value>:
		// 		Ignores the exception and continues the execution of the application
		//		assuming that the current method call returned <value>. For calls to methods
		//		returning void the <value> is ignored. Note that <value> should be of a
		//		primitive type.
		} else if (command.equals("Return")) {
			
			String value = split_input[1];
			
			return value;
			
		} else if(command.equals("Get")){
			
		} else if(command.equals("Set")){

		} else if(command.equals("Retry")){
			
			throw new DCLIThrowable("RETRY");
			
		} else {
			
			System.out.println("Unrecognized command");
			return null;
			
		}
		
		return null;
		
	}
	
	public static void main(String[] args) {

		try {
			
			if(args.length < 1){
				System.out.println("Usage: java ist.meic.pa.DebuggerCLI <programToDebug> [<args>]");
				System.exit(1);
			}
			
			// DebuggerCLI Translator
			Translator translator = new DCLITranslator();
			
			ClassPool classPool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			
			classLoader.addTranslator(classPool, translator);
			
			// Get the programToDebug arguments
			String[] restArgs = new String[args.length - 1];
			System.arraycopy(args, 1, restArgs, 0, restArgs.length);
			
			classLoader.run(args[0], restArgs);	


		} catch(ArrayIndexOutOfBoundsException aiobe){

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println("classLoader.run throwed an exception");
			e.printStackTrace();
		}	

	}

	private static void injectDebugCode(CtClass ctClass) {
		// TODO Auto-generated method stub
		
	}

}
