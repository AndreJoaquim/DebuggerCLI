package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ExtendedDCLITranslator implements Translator {
	
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
			
			// For each constructor edit all the method calls
			ctConstructor.instrument(new ExprEditor(){
				public void edit(MethodCall m) throws CannotCompileException {
					
					String methodClassName = ctClass.getName();

					// Filter the methods of our Debugger and the javassist
					if(methodClassName.contains("ist.meic.pa.test") && !methodClassName.contains("DebuggerCLI") && !methodClassName.contains("javassist")){
						
						String oldMethodName = m.getMethodName();
						String methodReturnType = "";
						
						try {
							methodReturnType = m.getMethod().getReturnType().getName();
						} catch (NotFoundException e) {
							e.printStackTrace();
						}
						
						m.replace(" $_ = ($r) ist.meic.pa.ExtendedDebuggerCLI.initCommandLine(\"" + m.getClassName() + "\" , $0 , \"" + methodReturnType + "\" , \"" + oldMethodName + "\" , $args  );");
						
					}
										
				}
			});
		}
		
		// Edit all the remaining methods
		for(final CtMethod ctMethod: ctClass.getDeclaredMethods()) {
			
			// For each method edit all the method calls
			ctMethod.instrument(new ExprEditor(){
				public void edit(MethodCall m) throws CannotCompileException {
					
					String methodClassName = ctClass.getName();
					
					// Filter the methods of our Debugger and the javassist
					if(methodClassName.contains("ist.meic.pa.test") && !methodClassName.contains("DebuggerCLI") && !methodClassName.contains("javassist")){
						
						String oldMethodName = m.getMethodName();
						String methodReturnType = "";
						
						try {
							methodReturnType = m.getMethod().getReturnType().getName();
						} catch (NotFoundException e) {
							e.printStackTrace();
						}
						
						
						m.replace(" $_ = ($r) ist.meic.pa.ExtendedDebuggerCLI.initCommandLine(\"" + m.getClassName() + "\" , $0 , \"" + methodReturnType + "\" , \"" + oldMethodName + "\" , $args );");
						
					}

										
				}
			});
		}
	}	

}
