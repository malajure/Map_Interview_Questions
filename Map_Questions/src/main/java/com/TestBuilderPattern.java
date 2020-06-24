package com;

class Student {
	private int rollNo;
	private String name;

	//
	public Student setRollNo(int rollNO) {
		this.rollNo = rollNO;
		return this;
	}

	public Student setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String toString() {
		return "Student [rollNo=" + rollNo + ", name=" + name + "]";
	}
	
	

}

public class TestBuilderPattern {

	public static void main(String[] args) {

		Student student1 =new Student();
		Student student2 = new Student();
		
		student1.setRollNo(300).setName("shiva");
		student2.setRollNo(400).setName("deva");
		System.out.println(student1);
		System.out.println(student2);
		
		System.out.println(student1.setRollNo(99));
	}

}
