package ist.meic.pa;

public class DCLIThrowable extends Throwable {

	private String value;
	
	public DCLIThrowable(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
}
