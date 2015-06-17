class Main {
	public static void main(String[] a) {
		A a;
		B b;
		boolean bool;

		a = new A();
		b = new B();

		a = b;

		b = b.complex((a.subtype(b)).only(b));
		System.out.println(b.getBoolean());
	}
}


class A extends Main {
	int a;
	int[] b;
	boolean c;
	
	public boolean only(B b) { 
		return b.getBoolean();
	}

	public int a(int a1, Main a2) {
		return (a1 + a);
	}

	public A subtype(A general) {
		B temp;
		boolean bool;
		int result;


		temp = new B();
		bool = temp.only(temp);
		result = general.getInt();

		return temp;
	}

	public boolean getBoolean() { 
		return c;
	} 

	public int getInt() { 
		return a;
	}

	public A getSelf() {
		return this;
	}
}

class B extends A {
	int a;
	A parent;
	
	public int a(int a1, Main a2) {
		int t1;
		int t2;
		int t3;
		boolean bool;

		t1 = parent.a();
		t2 = this.a();

		t3 = a + a1;

		bool = c && true;

		return (15 + a);
	}

	public B complex(boolean bool) {
		int[] temp;
		B ret;
		if (bool) {
			ret = new B();
			temp[0] = 1;
			temp[2] = 2;
			temp[ret.a(temp[0], new Main())] = new A().a(temp[10], new Main());
			a = temp[15];
		}
		else
			ret = this;

		return ret;
	}
}
