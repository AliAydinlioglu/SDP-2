package domain;

import java.util.List;

import repository.GebruikerDaoJpa;

public class DomeinController {

	private GebruikerDaoJpa gebruikerDaoJpa;
	
	public DomeinController() {
		//new PopulateDB().run();
		gebruikerDaoJpa = new GebruikerDaoJpa();
	}
	
	
	public Gebruiker getGebruiker(int id) {
		//return gebruikerDaoJpa.getGebruikerById(id);
		return gebruikerDaoJpa.get(1);
	}
	
	public List<Gebruiker> getAll(){
		return gebruikerDaoJpa.findAll();
	}
}
