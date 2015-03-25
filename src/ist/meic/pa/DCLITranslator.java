package ist.meic.pa;

import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class DCLITranslator implements Translator {
	
	private HashMap<String, String> debuggedMethods = new HashMap<String, String>();

	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
	CannotCompileException {

		CtClass ctClass = pool.get(className);

		try {
			insertDebugCode(ctClass);
		} catch(ClassNotFoundException cnfe){
			System.out.println("Error: Class was not found");
			cnfe.printStackTrace();
		}
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
											 CannotCompileException {		
	}

	void insertDebugCode(final CtClass ctClass) throws NotFoundException,
												 CannotCompileException,
												 ClassNotFoundException {
		
		// Edit all the constructors
		for(CtConstructor ctConstructor: ctClass.getConstructors()){
			
		}
		
		// Edit all the remaining methods
		for(final CtMethod ctMethod: ctClass.getDeclaredMethods()) {
			
			//Para cada metodo editar todas as methods call
			ctMethod.instrument(new ExprEditor(){
				public void edit(MethodCall m) throws CannotCompileException {
					
					// Old method name
					String methodName = m.getMethodName();
					System.out.println(methodName);
					
					// New method name
					String newMethodName = methodName + "$debug";
					System.out.println(newMethodName);
					
					
					// Create new method "$debug"
					CtMethod newDebuggedMethod = null;
					
					try {
						newDebuggedMethod = CtNewMethod.copy(m.getMethod(), newMethodName, ctClass, null);
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
					
					//AINDA E PRECISO CONFIRMAR ISTO
					try {
						newDebuggedMethod.setExceptionTypes(ctMethod.getExceptionTypes());
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
					
					newDebuggedMethod.setBody(
							"try {" +
							"	return ($r) "+ methodName + "($$);" +
							"} catch (java.lang.Exception e){"+
							"	Object returnObject;" +
							"	try {" +
							"		returnObject = ist.meic.pa.DebuggerCLI.initCommandLine($0, e); " +
							"		return ($r) returnObject; " +
							"	} catch (ist.meic.pa.DCLIThrowable t){ " +
							"		if(t.getValue() == \"RETRY\"){" + 
							"			" + newMethodName + "($$);" + 
							"		}" + 
							"	}" +
							"}");
					
					ctClass.addMethod(newDebuggedMethod);
					
					// Replace the method call
					// $1: First parameter
					// $_: Resulting value of the method call
					// $proceed: Name of the method originally called
					// $$: all the arguments
					m.replace("{ $_ = " + newMethodName + "($$); }");
					
					// Add to the map
					debuggedMethods.put(methodName, "");

					
				}
			});
		}
	}	

}
