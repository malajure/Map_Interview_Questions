package com.builderpattern;

class Student {
	private int id;
	private String name;
	private String address;

	public Student setId(int id) {
		this.id = id;
		return this;
	}

	public Student setName(String name) {
		this.name = name;
		return this;
	}

	public Student setAddress(String address) {
		this.address = address;
		return this;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", address=" + address + "]";
	}

}

class StudentMaker {
	private final Student student = new Student();

	public StudentMaker() {

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				student.setId(1).setName("Ram").setAddress("Noida");
			}
		});

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				student.setId(2).setName("Shyam").setAddress("Delhi");
			}
		});
		t1.start();
		t2.start();
	}

	public Student getStudent() {
		return student;
	}
}

/* Shows the drawback of Method Chaining */
public class TestBuilderPattern2 {

	public static void main(String[] args) {
		
		for (int i = 0; i < 3; i++) {
			StudentMaker sr = new StudentMaker();
			System.out.println(sr.getStudent());
		}
	}

}
