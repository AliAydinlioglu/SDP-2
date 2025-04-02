package domain;

import java.time.LocalDate;

import enums.Rol;
import repository.GebruikerDaoJpa;
import repository.MachineDaoJpa;

public class PopulateDB {
	public void run() {
		GebruikerDaoJpa gebruikerdao = new GebruikerDaoJpa();
		MachineDaoJpa machinedao = new MachineDaoJpa();

		machinedao.startTransaction();
		gebruikerdao.startTransaction();

		machinedao.insert(new Machine("M1", "Machine 1 locatie", "werkt uitstekent", "Draait", "Gezond", 10, "Steven technician" ,LocalDate.of(2025, 4, 1), 5, LocalDate.of(2025, 4, 5) ));
		gebruikerdao.insert(new Gebruiker("Dornon", "Seppe", LocalDate.of(2004, 4, 1), new Adres("kroonstraat", "42", "9000", "Gent", "Belgie"), "seppe.dornon@student.hogent.be", "04123456", Rol.ADMINISTRATOR, true));

		gebruikerdao.commitTransaction();
		machinedao.commitTransaction();
	}

}
