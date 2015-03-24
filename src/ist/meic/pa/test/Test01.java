package ist.meic.pa.test;

public class Test01 {
	
	public static void a() throws ArrayIndexOutOfBoundsException{ b(); }
	
	public static void b() throws ArrayIndexOutOfBoundsException { c(); }
	
	public static void c() { throw new ArrayIndexOutOfBoundsException(); }
	
	public static void main(String[] args){
		
		System.out.println("TEST01");
		// Print arguments
		for(int i = 0; i < args.length; i++){
			
			System.out.println("Argument " + i + ": " + args[i]);
			
		}
		
	}

}
