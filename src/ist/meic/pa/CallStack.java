package ist.meic.pa;

import java.util.ArrayList;
import java.util.Iterator;

public class CallStack {

	private ArrayList<CallStackElement> _callStack;
	private int _size;

	public CallStack(){ _callStack = new ArrayList<CallStackElement>(); _size = 0; }

	public ArrayList<CallStackElement> getElements() { return _callStack; }
	public Iterator<CallStackElement> getIterator() { return _callStack.iterator(); }

	public void push(CallStackElement element) { _callStack.add(element); _size++; }
	public CallStackElement pop() { CallStackElement el = _callStack.remove(_size - 1); _size--; return el;  }

	public CallStackElement getElement(int index) { return _callStack.get(index); }

	public void printStack(){
		
		for(int i = _callStack.size() - 1; i >= 0; i--){
			System.out.println(_callStack.get(i));
		}
		
	}


}
