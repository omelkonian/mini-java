class test17{
    public static void main(String[] a){
	System.out.println(new Test2().start());
    }
}

class Test2{

    int i;

    public int start(){

	Test2 test;

	test = new Test2();

	i = 10;

	i = i + ((test.first(this)).second());

	return i;
    }

    public Test2 first(Test2 test2){

	Test2 test3;

	test3 = test2;

	return test3;
    }

    public int second(){

	i = i + 10;

	return i;
    }
}
