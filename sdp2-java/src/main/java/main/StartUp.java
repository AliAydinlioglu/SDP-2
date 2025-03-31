package main;

import domain.DomeinController;

public class StartUp {

	public static void main(String[] args) {
		DomeinController dc = new DomeinController();
		
		System.out.println(dc.getGebruiker(1).toString());
		
		System.out.println(dc.getAll().toString());
		
		

	}

}
