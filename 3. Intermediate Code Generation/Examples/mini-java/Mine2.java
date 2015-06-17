class Mine2 {
	public static void main(String[] args) {
		A a;
		A a2;
		B b;
		B b2;
		C c;
		C c2;
		
		int aux;

		a = new A();
		b = new B();
		c = new C();
		c2 = new C();
		a2 = new C();
		b2 = new C();

		// Field default values
		aux = c.print();
		System.out.println(111111111);
		
		// Field initialization
		aux = c2.Init3(10);
		aux = c2.print();
		System.out.println(222222222); 
		
		// Complex field initialization
		aux = c.Init33(10);
		aux = c.print();
		System.out.println(333333333); 

		// Complex field initialization & Global Offset
		aux = c.inc3(5);
		aux = c.print();
		System.out.println(444444444); 

		// Subtyping, Overriding
		aux = a2.Init1(10);
		aux = a2.print();
		System.out.println(555555555); 
		aux = b2.Init22(10);
		aux = b2.print();
		System.out.println(555555555); 
		
	}
}

class A {
	int i;
	public int Init1(int num) {
		i = num;
		return 0;
	}
	public int Init11(int num) {
		i = num;
		return 0;
	}
	public int get1() {
		return i;
	}
	public int inc1(int offset) {
		i = i + offset;
		return 0;
	}
	public int print() {		
		int aux;	

		aux = this.get1();
		System.out.println(aux);

		return 0;
	}
}

class B extends A {
	int i;

	public int Init2(int num) {
		int aux;

		i = num;
		aux = this.Init1(num);
		return 0;
	}	
	public int Init22(int num) {
		int aux;

		i = 2*num;
		aux = this.Init11(num);
		return 0;
	}
	public int get2() {
		return i;
	}
	public int inc2(int offset) {
		i = i + offset;
		return 0;
	}
	public int print() {		
		int aux;	

		aux = this.get1();
		System.out.println(aux);
		aux = this.get2();
		System.out.println(aux);

		return 0;
	}
}

class C extends B {
	int i;
	public int Init3(int num) {
		int aux;

		i = num;
		aux = this.Init2(num);
		aux = this.Init1(num);
		return 0;
	}
	public int Init33(int num) {
		int aux;

		i = 3*num;
		aux = this.Init22(num);
		aux = this.Init11(num);
		return 0;
	}
	public int get3() {
		return i;
	}
	public int inc3(int offset) {
		int aux;
		
		i = i + offset;
		aux = this.inc1(offset);
		aux = this.inc2(offset);
		return 0;
	}
	public int print() {		
		int aux;	

		aux = this.get1();
		System.out.println(aux);
		aux = this.get2();
		System.out.println(aux);
		aux = this.get3();
		System.out.println(aux);

		return 0;
	}
}
