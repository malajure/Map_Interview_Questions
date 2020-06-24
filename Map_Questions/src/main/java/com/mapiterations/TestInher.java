package com.mapiterations;

class A {

	void show() {
		System.out.println("In Show");
		validateCompany();
	}

	void validateCompany() {
		System.out.println("From Parent");
	}
}

class B extends A {
	@Override
	void validateCompany() {
		System.out.println("From Child");
	}
}

public class TestInher {

	public static void main(String[] args) {
		A ref = new B();
		ref.show();
	}

}
