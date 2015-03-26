package ist.meic.pa;

import java.util.ArrayList;
import java.util.Iterator;

public class CallStack {

	private ArrayList<CallStackElement> _callStack;

	public CallStack(){ _callStack = new ArrayList<CallStackElement>(); }

	public ArrayList<CallStackElement> getElements() { return _callStack; }
	public Iterator<CallStackElement> getIterator() { return _callStack.iterator(); }

	public void addElement(CallStackElement element) { _callStack.add(element); }
	public CallStackElement getElement(int index) { return _callStack.get(index); }

	public void printStack(){
		for(CallStackElement callStackElement : _callStack){
			System.out.println(callStackElement);
		}
	}

}
