class Mine {
	public static void main(String[] args) {
		int[] array;
		int i;
		int j;
		int aux;
		A a;
		B b;

		a = new A();
		j = a.Init(10);
		// Array testing
		array = new int[10];
		System.out.println(array.length);
		i = 0;
		while (i < (array.length)) {
			System.out.println(array[i]);
			array[i] = i;
			i = i + 1;
		}
		i = 0;
		while (i < (array.length)) {
			System.out.println(array[i]);
			i = i + 1;
		}		
		j = (a.getFld()) - 100;
		System.out.println(j);
		// i = array[j];
		// System.out.println(i);		

		// Inheritance testing
		a = new B();
		b = new B();
		aux = a.Init(10);
		aux = b.Init(100);
		aux = b.Print2();

	}
}


class A {
	int fld;

	public int Init(int num) {
		fld = num;
		return 0;
	}

	public int Init2(int num) {
		fld = num;
		return 0;
	}

	public int getFld() {
		return fld;
	}
}

class B extends A {
	int fld;

	public int Init(int num) {
		int aux;

		fld = 2*num;
		aux = this.Init2(num);
		
		return 0;
	}

	public int getFld2() {
		return fld;
	}

	public int Print2() {
		int aux;
		
		System.out.println(fld);
		aux = this.getFld();
		System.out.println(aux);

		return 0;
	}
}
