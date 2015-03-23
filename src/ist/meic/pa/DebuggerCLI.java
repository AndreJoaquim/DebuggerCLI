package ist.meic.pa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DebuggerCLI {

	public static void main(String[] args) {

		String programToDebug = args[0];		

		try {

			
			// TODO: ir buscar class 'a classpool
			
			// Get class of the program to debug
			Class<?> program = Class.forName(programToDebug);
			// Get the method 'main' of the program to debug
			Method main = program.getMethod("main", String[].class);

			// main_args: arguments of the program to debug
			String[] main_args = new String[args.length - 1];

			// Get the arguments
			for(int i = 1; i < args.length; i++){
				main_args[i - 1] = args[i];
			}

			// Invoke the main method with the respective arguments
			main.invoke(null, (Object) main_args);			


		} catch (InvocationTargetException ite){

			System.out.print("DebuggerCLI:> ");
			String input = System.console().readLine();
			System.out.println("Command: " + input);
			
			String[] split_input = input.split(" ");
			String command = split_input[0];
			
			// Abort:
			// 		Terminates the execution of the application
			if (command.equals("Abort")){
				return;
				
			// Info:
			// 		Presents detailed information about the called object, its fields,
			// 		and the call stack. The presented information follows the format
			// 		described in the next section. 	
			} else if (command.equals("Info")){
				
			// Throw:
			// 		Re-throws the exception, so that it may be handled by the next handler.	
			} else if (command.equals("Throw")){
			
			// Return <value>:
			// 		Ignores the exception and continues the execution of the application
			//		assuming that the current method call returned <value>. For calls to methods
			//		returning void the <value> is ignored. Note that <value> should be of a
			//		primitive type.
			} else if (command.equals("Return")) {
				
				String value = split_input[1];
				
			} else if(command.equals("Get")){
				
			} else if(command.equals("Set")){

			} else if(command.equals("Retry")){
				
			} else {				
				System.out.println("Unrecognized command");
				return;				
			}

		} catch(ArrayIndexOutOfBoundsException aiobe){

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}	

	}

}
