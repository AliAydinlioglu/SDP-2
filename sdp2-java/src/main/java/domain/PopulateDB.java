package domain;

import java.time.LocalDate;

import enums.Rol;
import repository.GebruikerDaoJpa;

public class PopulateDB {
	public void run() {
		GebruikerDaoJpa gebruikerdao = new GebruikerDaoJpa();
		gebruikerdao.startTransaction();
		
		gebruikerdao.insert(new Gebruiker("Dornon", "Seppe", LocalDate.of(2004, 4, 1), new Adres("kroonstraat", "42", "9000", "Gent", "Belgie"), "seppe.dornon@student.hogent.be", "04123456", Rol.ADMINISTRATOR));
	
		gebruikerdao.commitTransaction();
	}

}
