package ist.meic.pa;

import java.util.ArrayList;

public class CallStackElement {

	private String _className;
	private String _methodName;
	private ArrayList<Object> _arguments;
	
	public CallStackElement(String className, String methodName){
		_className = className;
		_methodName = methodName;
		_arguments = new ArrayList<Object>();
	}
	
	public String getClassName(){ return _className; }
	public String getMethodName(){ return _methodName; }
	public ArrayList<Object> getArguments(){ return _arguments; }
	
	public void addArgument(Object arg){ _arguments.add(arg); }
	
	@Override
	public String toString(){
		
		// Syntax:
		// 		<class>.<method>(<arg1>,...,<argN>)
		
		String description = _className + "." + _methodName;
		
		if(_arguments.size() != 0){
			
			description += "(";
			
			for(int i = 0; i <_arguments.size(); i++){
				if(i >= _arguments.size() - 1){
					description += _arguments.get(i).toString();
				} else {
					description += _arguments.get(i).toString() + ",";
				}
			}
			
			description += ")";
			
		}
		
		return description;
	}

}
