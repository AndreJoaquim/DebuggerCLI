package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

public class DebuggerCLI {
	
	public static Object initCommandLine(String invocationTargetClassName,
			Object invocationTarget, String invocationTargetReturnType,
			String invocationTargetMethodName,
			Object[] invocationTargetMethodParams) throws Throwable {

		Method methodToInvoke;
		Object invocationTargetReturn = null;
		
		// System.out.println("[invocationTargetClassName] " +
		// invocationTargetClassName);

		ArrayList<Class<?>> invocationTargetParameterTypes = new ArrayList<Class<?>>();
		
		// Check for primitive types and convert
		for (Object param : invocationTargetMethodParams) {
			// System.out.println("[ParameterType]" +
			// param.getClass().getName());
			// if(param.getClass().isPrimitive()){
			// System.out.println("PRIMITIVE" + param.getClass().getName());
			
			if (param.getClass().getName().equals("java.lang.Integer")) {
				invocationTargetParameterTypes.add(int.class);
			} else if (param.getClass().getName().equals("java.lang.Byte")) {
				invocationTargetParameterTypes.add(byte.class);
			} else if (param.getClass().getName().equals("java.lang.Long")) {
				invocationTargetParameterTypes.add(long.class);
			} else if (param.getClass().getName().equals("java.lang.Short")) {
				invocationTargetParameterTypes.add(short.class);
			} else if (param.getClass().getName().equals("java.lang.Double")) {
				invocationTargetParameterTypes.add(double.class);
			} else if (param.getClass().getName().equals("java.lang.Float")) {
				invocationTargetParameterTypes.add(float.class);
			} else if (param.getClass().getName().equals("java.lang.Boolean")) {
				invocationTargetParameterTypes.add(boolean.class);
			} else if (param.getClass().getName().equals("java.lang.Character")) {
				invocationTargetParameterTypes.add(char.class);
				// }
			} else {
				invocationTargetParameterTypes.add(param.getClass());
			}
		}

		Class<?> invocationTargetClass = null;
		
		try {
			// System.out.println("[invocationTargetMethodName]" +
			// invocationTargetMethodName);

			invocationTargetClass = Class.forName(invocationTargetClassName);

			Class<?>[] invocationTargetMethodArguments = invocationTargetParameterTypes.toArray(new Class<?>[invocationTargetParameterTypes.size()]);

			methodToInvoke = invocationTargetClass.getDeclaredMethod(invocationTargetMethodName, invocationTargetMethodArguments);
			
			invocationTargetReturn = methodToInvoke.invoke(invocationTarget, invocationTargetMethodParams);

		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {

			System.out.println("[" + e.getTargetException().getClass().getName() + "]: "
					+ e.getTargetException().getMessage() + "." + invocationTargetMethodName);
			
			while (true) {
				
				System.out.print("DebuggerCLI:> ");

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input = "";

				try {
					input = br.readLine();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				
				System.out.println("Command: " + input);

				String[] split_input = input.split(" ");
				String command = split_input[0];

				// Abort:
				// Terminates the execution of the application
				if (command.equals("Abort")) {
					System.out.println("Exiting DebuggerCLI...");
					System.exit(1);

				// Info:
					// Presents detailed information about the called object,
					// its fields,
					// and the call stack. The presented information follows the
					// format
					// described in the next section.
				} else if (command.equals("Info")) {

					// TODO:

				// Throw:
					// Re-throws the exception, so that it may be handled by the
					// next handler.
				} else if (command.equals("Throw")) {
					
					throw e.getTargetException();

				// Return <value>:
					// Ignores the exception and continues the execution of the
					// application
					// assuming that the current method call returned <value>.
					// For calls to methods
					// returning void the <value> is ignored. Note that <value>
					// should be of a
					// primitive type.
				} else if (command.equals("Return")) {

					String value = split_input[1];

					// Convert the input in the return type
					if (invocationTargetReturnType.equals("int")) {
						return Integer.parseInt(value);
					} else if (invocationTargetReturnType.equals("byte")) {
						return Byte.parseByte(value);
					} else if (invocationTargetReturnType.equals("long")) {
						return Long.parseLong(value);
					} else if (invocationTargetReturnType.equals("short")) {
						return Short.parseShort(value);
					} else if (invocationTargetReturnType.equals("double")) {
						return Double.parseDouble(value);
					} else if (invocationTargetReturnType.equals("float")) {
						return Float.parseFloat(value);
					} else if (invocationTargetReturnType.equals("boolean")) {
						return Boolean.parseBoolean(value);
					} else if (invocationTargetReturnType.equals("char")) {
						return value.charAt(0);
					} else {
						return value;
						// TODO; Add Extension to handle non-primitive classes;
					}

				// Get <field name>
				//		Reads the field named <field name> of the called object.
				} else if (command.equals("Get")) {

					if(split_input.length != 2){
						System.out.println("Invalid command syntax");
						System.out.println("Usage: Get <field name> . For more info, use Help command");
						continue;
					}
					
					String field_name = split_input[1];
					
					Field field = invocationTargetClass.getDeclaredField(field_name);										
					field.setAccessible(true);				
					Object value = field.get(invocationTarget);
					System.out.println(value);					
					
				// Set <field name> <new value>
				//		Writes the field named <field name> of the called object, assigning it the <new value>.
				} else if (command.equals("Set")) {
					
					if(split_input.length != 3){
						System.out.println("Invalid command syntax");
						System.out.println("Usage: Set <field name> <new value>. For more info, use Help command");
					}
					
					String field_name = split_input[1];
					String new_value = split_input[2];
										
					Field field = invocationTargetClass.getDeclaredField(field_name);										
					field.setAccessible(true);				
					String field_type = field.getType().getName();
					
					Object value = null;
					
					// Convert the input in the return type
					if (field_type.equals("int")) {
						value = Integer.parseInt(new_value);
					} else if (field_type.equals("byte")) {
						value = Byte.parseByte(new_value);
					} else if (field_type.equals("long")) {
						value = Long.parseLong(new_value);
					} else if (field_type.equals("short")) {
						value = Short.parseShort(new_value);
					} else if (field_type.equals("double")) {
						value = Double.parseDouble(new_value);
					} else if (field_type.equals("float")) {
						value = Float.parseFloat(new_value);
					} else if (field_type.equals("boolean")) {
						value = Boolean.parseBoolean(new_value);
					} else if (field_type.equals("char")) {
						value = new_value.charAt(0);
					} else {
						value = new_value;
						// TODO; Add Extension to handle non-primitive classes;
					}
					
					field.set(invocationTarget, value);

				} else if (command.equals("Retry")) {

					return initCommandLine(invocationTargetClassName,
							invocationTarget, invocationTargetReturnType,
							invocationTargetMethodName,
							invocationTargetMethodParams);
					
				} else if (command.equals("Help")){
					
					System.out.println("Abort:	Terminates the execution of the application.");
					System.out.println("Info:	Presents detailed information about the called object,"
							+ "its fields, and the call stack. The presented information "
							+ "follows the format described in the next section.");
					System.out.println("Throw:	Re-throws the exception, so that it may be"
							+ "handled by the next handler.");
					System.out.println("Return <value>:	Ignores the exception and continues the execution of the application assuming that the"
							+ "current method call returned <value>. For calls to methods returning void the <value> is ignored. Note"
							+ "that <value> should be of a primitive type.");
					System.out.println("Get <field name>:	Reads the field named <field name> of the called object.");
					System.out.println("Set <field name> <new value>:	Writes the field named <field name> of the called"
							+ "object, assigning it the <new value>.");
					System.out.println("Retry:	Repeats the method call that was interrupted.");
					
				} else {

					System.out.println("Unrecognized command. For help, type Help");

				}
			}
		}

		return invocationTargetReturn;

	}

	public static void main(String[] args) {

		try {

			if (args.length < 1) {
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

		} catch (ArrayIndexOutOfBoundsException aiobe) {
			aiobe.printStackTrace();
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
		} catch (SecurityException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (NotFoundException e) {
			// e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println("[Exception thrown] classLoader.run throwed an exception");
			System.out.println("[Class Name]:" + e.getClass().getName());
			e.printStackTrace();
		}

	}

}
