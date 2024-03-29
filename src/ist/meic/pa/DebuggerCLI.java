package ist.meic.pa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

public class DebuggerCLI {

	private static CallStack callStack = new CallStack();

	private static void updateCallStack(String invocationTargetClassName,
			String invocationTargetMethodName,
			Object[] invocationTargetMethodParams) {

		CallStackElement newElement = new CallStackElement(	invocationTargetClassName, invocationTargetMethodName);

		for (int i = 0; i < invocationTargetMethodParams.length; i++) {

			if (invocationTargetMethodName == "main") {
				String parameterString = "";

				ArrayList<String> parameterStringArray = new ArrayList<String>(
						Arrays.asList((String[]) invocationTargetMethodParams[i]));

				for (int j = 0; j < parameterStringArray.size(); j++) {
					parameterString += parameterStringArray.get(j);

					if (j != parameterStringArray.size() - 1) {
						parameterString += ", ";
					}

				}

				newElement.addArgument(parameterString);
			} else
				newElement.addArgument(invocationTargetMethodParams[i]);

		}

		// Push new call to the stack
		callStack.push(newElement);

	}

	public static Object initCommandLine(String invocationTargetClassName,
			Object invocationTarget, String invocationTargetReturnType,
			String invocationTargetMethodName,
			Object[] invocationTargetMethodParams) throws Throwable {

		Boolean isMain = false;
		
		if (invocationTargetMethodName.equals("main$debug")) {
			updateCallStack(invocationTargetClassName, "main", invocationTargetMethodParams);
			isMain = true;
		}else{
			updateCallStack(invocationTargetClassName, invocationTargetMethodName, invocationTargetMethodParams);
		}

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


			if (param == null) {
				invocationTargetParameterTypes.add(Object.class);
			} else if (param.getClass().getName().equals("java.lang.Integer")) {
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
			} else {
				invocationTargetParameterTypes.add(param.getClass());
			}

		}

		Class<?> invocationTargetClass = null;

		try {
			// System.out.println("[invocationTargetMethodName]" +
			// invocationTargetMethodName);

			invocationTargetClass = Class.forName(invocationTargetClassName);

			Class<?>[] invocationTargetMethodArguments = invocationTargetParameterTypes
					.toArray(new Class<?>[invocationTargetParameterTypes.size()]);

			methodToInvoke = invocationTargetClass
					.getDeclaredMethod(invocationTargetMethodName,
							invocationTargetMethodArguments);

			try {
				
				invocationTargetReturn = methodToInvoke.invoke(invocationTarget, invocationTargetMethodParams);
				
			} catch (NullPointerException e) {
				
				if(!isMain)
					callStack.pop();

				throw e;
			}
			
			
		} catch (InvocationTargetException e) {

			String message = e.getTargetException().getClass().getName();

			// If there is a message from the TargetException, include it in the
			// message to be printed
			if (e.getTargetException().getMessage() != null)
				message += ": " + e.getTargetException().getMessage();

			//e.getTargetException().printStackTrace();

			System.out.println(message);

			while (true) {
								
				System.out.print("DebuggerCLI:> ");

				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				String input = "";

				try {
					input = br.readLine();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

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

					// Syntax:
					//
					// Called Object: <called object or null if static>
					// Fields: <field1> ... <fieldN>
					// Call stack:
					// <called class>.<called method>(<arg1>,...,<argN>)
					// <called class>.<called method>(<arg1>,...,<argN>)

					System.out.print("Called Object: ");

					if (invocationTarget != null)
						System.out.print(invocationTarget + "\n");
					else
						System.out.println("null");

					
					Class<?> invocationTargetClassToPrint = Class.forName(invocationTargetClassName);
					
					Field[] declaredFields = invocationTargetClassToPrint.getDeclaredFields();
					
					System.out.print("\tFields:");
					
					for (Field field : declaredFields) {
						System.out.print(" " + field.getName());
					}
					System.out.print("\n");

					System.out.println("Call stack:");

					
					callStack.printStack();
				

					// Throw:
					// Re-throws the exception, so that it may be handled by the
					// next handler.
				} else if (command.equals("Throw")) {

					if(!isMain)
						callStack.pop();
					
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

					if(invocationTargetReturnType == "void"){
						if(!isMain)
							callStack.pop();
						
						return null;
					}
					
					if (split_input.length != 2) {
						System.out.println("Invalid command syntax");
						System.out
								.println("Usage: Return <value> . For more info, use Help command");
						continue;
					}
					
					String value = split_input[1];
					
					if(!isMain)
						callStack.pop();
					
					// Convert the input in the return type
					if (invocationTargetReturnType.equals("int") || invocationTargetReturnType.equals("java.lang.Integer")) {
						return Integer.parseInt(value);
					} else if (invocationTargetReturnType.equals("byte") || invocationTargetReturnType.equals("java.lang.Byte")) {
						return Byte.parseByte(value);
					} else if (invocationTargetReturnType.equals("long") || invocationTargetReturnType.equals("java.lang.Long")) {
						return Long.parseLong(value);
					} else if (invocationTargetReturnType.equals("short") || invocationTargetReturnType.equals("java.lang.Short")) {
						return Short.parseShort(value);
					} else if (invocationTargetReturnType.equals("double") || invocationTargetReturnType.equals("java.lang.Double")) {
						return Double.parseDouble(value);
					} else if (invocationTargetReturnType.equals("float") || invocationTargetReturnType.equals("java.lang.Float")) {
						return Float.parseFloat(value);
					} else if (invocationTargetReturnType.equals("boolean") || invocationTargetReturnType.equals("java.lang.Boolean")) {
						return Boolean.parseBoolean(value);
					} else if (invocationTargetReturnType.equals("char") || invocationTargetReturnType.equals("java.lang.Charater")) {
						return value.charAt(0);
					} else {
						return value;
						// TODO; Add Extension to handle non-primitive classes;
					}

					// Get <field name>
					// Reads the field named <field name> of the called object.
				} else if (command.equals("Get")) {

					if (split_input.length != 2) {
						System.out.println("Invalid command syntax");
						System.out
								.println("Usage: Get <field name> . For more info, use Help command");
						continue;
					}

					String field_name = split_input[1];

					Field field = invocationTargetClass.getDeclaredField(field_name);
					field.setAccessible(true);
					Object value = field.get(invocationTarget);
					System.out.println(value);

					// Set <field name> <new value>
					// Writes the field named <field name> of the called object,
					// assigning it the <new value>.
				} else if (command.equals("Set")) {

					if (split_input.length != 3) {
						System.out.println("Invalid command syntax");
						System.out
								.println("Usage: Set <field name> <new value>. For more info, use Help command");
						continue;
					}

					String field_name = split_input[1];
					String new_value = split_input[2];

					Field field = null;

					try {
						field = invocationTargetClass
								.getDeclaredField(field_name);
					} catch (NoSuchFieldException nsfe) {
						System.out.println("The field " + field_name + " does not exist in " + invocationTarget.getClass().getName());
						continue;
					}

					field.setAccessible(true);
					String field_type = field.getType().getName();

					Object new_value_obj = null;

					// Convert the input in the return type
					if (field_type.equals("int") || field_type.equals("java.lang.Integer")) {
						new_value_obj = Integer.parseInt(new_value);
					} else if (field_type.equals("byte") || field_type.equals("java.lang.Byte")) {
						new_value_obj = Byte.parseByte(new_value);
					} else if (field_type.equals("long") || field_type.equals("java.lang.Long")) {
						new_value_obj = Long.parseLong(new_value);
					} else if (field_type.equals("short") || field_type.equals("java.lang.Short")) {
						new_value_obj = Short.parseShort(new_value);
					} else if (field_type.equals("double") || field_type.equals("java.lang.Double")) {
						new_value_obj = Double.parseDouble(new_value);
					} else if (field_type.equals("float") || field_type.equals("java.lang.Float")) {
						new_value_obj = Float.parseFloat(new_value);
					} else if (field_type.equals("boolean") || field_type.equals("java.lang.Boolean")) {
						new_value_obj = Boolean.parseBoolean(new_value);
					} else if (field_type.equals("char") || field_type.equals("java.lang.Charater")) {
						new_value_obj = new_value.charAt(0);
					} else {
						new_value_obj = new_value;
						// TODO; Add Extension to handle non-primitive classes;
					}

					field.set(invocationTarget, new_value_obj);

				} else if (command.equals("Retry")) {
					
					if(!isMain)
						callStack.pop();
					
					return initCommandLine(invocationTargetClassName,
							invocationTarget, invocationTargetReturnType,
							invocationTargetMethodName,
							invocationTargetMethodParams);

				} else if (command.equals("Help")) {

					System.out
							.println("Abort:				Terminates the execution of the application.");
					System.out
							.println("Info:				Presents detailed information about the called object,"
									+ "its fields, and the call stack. The presented information "
									+ "follows the format described in the next section.");
					System.out
							.println("Throw:				Re-throws the exception, so that it may be"
									+ "handled by the next handler.");
					System.out
							.println("Return <value>:			Ignores the exception and continues the execution of the application assuming that the"
									+ "current method call returned <value>. For calls to methods returning void the <value> is ignored. Note"
									+ "that <value> should be of a primitive type.");
					System.out
							.println("Get <field name>:		Reads the field named <field name> of the called object.");
					System.out
							.println("Set <field name> <new value>:	Writes the field named <field name> of the called"
									+ "object, assigning it the <new value>.");
					System.out
							.println("Retry:				Repeats the method call that was interrupted.");

				} else {

					System.out
							.println("Unrecognized command. For help, type Help");

				}
			}

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

		} finally {

			//System.out.println("name:" + invocationTargetMethodName);
			//callStack.printStack();

		}
		
		if(!isMain)
			callStack.pop();

		
		return invocationTargetReturn;

	}

	public static void main(String[] args) {

		try {

			if (args.length < 1) {
				System.out
						.println("Usage: java ist.meic.pa.DebuggerCLI <programToDebug> [<args>]");
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

		
			// Putting the main info on the CallStack
			// We need this because when we load the new class, the variables
			// are garbage collected and we lose them.
			CtClass mainClass = classPool.get(args[0]);
			CtMethod mainMethod = mainClass.getDeclaredMethod("main");

			// Create a new main$debug method that contains the main code
			CtMethod newMain = CtNewMethod.copy(mainMethod, "main$debug", mainClass, null);
			mainClass.addMethod(newMain);

			// Change the body of the main to call our main$debug that will call
			// our debugger's code
			// just as any other method call
			mainMethod.setBody("{ main$debug($$); }");

			classLoader.run(args[0], restArgs);

		} catch (ArrayIndexOutOfBoundsException aiobe) {
			System.out.println("sdasfd");
			aiobe.printStackTrace();
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
			System.out
					.println("[Exception thrown] classLoader.run throwed an exception");
			System.out.println("[Class Name]:" + e.getClass().getName());
			e.printStackTrace();
		}

	}

}
