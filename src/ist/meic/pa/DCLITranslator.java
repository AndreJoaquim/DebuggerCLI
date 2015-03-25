package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;

public class DCLITranslator implements Translator {

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

	void insertDebugCode(CtClass ctClass) throws NotFoundException, CannotCompileException, 	ClassNotFoundException{
		for(CtMethod ctMethod: ctClass.getDeclaredMethods()) {
			//TODO: Edit Methods
		}
	}	

}
