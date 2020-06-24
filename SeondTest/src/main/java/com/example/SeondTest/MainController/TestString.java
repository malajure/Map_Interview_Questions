package com.example.SeondTest.MainController;

public class TestString {

	public static void main(String[] args) {
		
		String company_ser_id = "2";
		System.out.println(company_ser_id);
		
		if( !( company_ser_id.equalsIgnoreCase("") || company_ser_id.toString() == null)) {
			System.out.println("not Empty");
		}else {
			System.out.println("empty");
		}

	}

}
