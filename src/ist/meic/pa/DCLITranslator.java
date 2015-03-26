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
import ist.meic.pa.DebuggerCLI;

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
			//Para cada metodo editar todas as methods call
			ctConstructor.instrument(new ExprEditor(){
				public void edit(MethodCall m) throws CannotCompileException {
					
					String methodClassName = ctClass.getName();

					
					if(!methodClassName.contains("DebuggerCLI") && !methodClassName.contains("javassist")){
						
						String oldMethodName = m.getMethodName();
						
						String methodReturnType = "";
						
						try {
							methodReturnType = m.getMethod().getReturnType().getName();
						} catch (NotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						m.replace(" $_ = ($r) ist.meic.pa.DebuggerCLI.initCommandLine(\"" + m.getClassName() + "\" , $0 , \"" + methodReturnType + "\" , \"" + oldMethodName + "\" , $args  );");
						
					}
										
				}
			});
		}
		
		// Edit all the remaining methods
		for(final CtMethod ctMethod: ctClass.getDeclaredMethods()) {
			
			//Para cada metodo editar todas as methods call
			ctMethod.instrument(new ExprEditor(){
				public void edit(MethodCall m) throws CannotCompileException {
					
					String methodClassName = ctClass.getName();
					
					if(!methodClassName.contains("DebuggerCLI")&&!methodClassName.contains("javassist")){
						


						String oldMethodName = m.getMethodName();
						String methodReturnType = "";
						try {
							methodReturnType = m.getMethod().getReturnType().getName();
						} catch (NotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						m.replace(" $_ = ($r) ist.meic.pa.DebuggerCLI.initCommandLine(\"" + m.getClassName() + "\" , $0 , \"" + methodReturnType + "\" , \"" + oldMethodName + "\" , $args  );");
						
					}

										
				}
			});
		}
	}	

}
