class Mine3 {
	public static void main(String[] args) {
		A a;
		A a2;
		B b;
		B b2;
		C c;

		a = new A();
		b = new B();
		c = new C();
		a2 = new C();
		b2 = new C(); 

		System.out.println(b2.propagate(a2.propagate(a.propagate(a.propagate(b.propagate(b.propagate(a.propagate(c.propagate(1))))))))); // = 108
	}
}

class A {
	public int propagate(int num) {
		return num;
	}
}

class B extends A {
	public int propagate(int num) {
		return 2*num;
	}
}

class C extends B {
	public int propagate(int num) {
		return 3*num;
	}
}
