package ist.meic.pa.test;

public class A {

	int a = 1;

	public Double foo(B b, char c) {
		System.out.println("Inside A.foo ");
		if (a == 1) {
			return b.bar(0);
		} else {
			return b.baz(null);
		}
	}
}
