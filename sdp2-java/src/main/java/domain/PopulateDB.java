package domain;

import java.time.LocalDate;

import enums.MachineStatus;
import enums.ProductionStatus;
import enums.Rol;
import repository.GebruikerDaoJpa;
import repository.MachineDaoJpa;
import repository.SiteDaoJpa;

public class PopulateDB {
    public void run() {
        GebruikerDaoJpa gebruikerdao = new GebruikerDaoJpa();
        SiteDaoJpa sitedao = new SiteDaoJpa();
        MachineDaoJpa machinedao = new MachineDaoJpa();

        gebruikerdao.startTransaction();
        // sitedao.startTransaction();
        // machinedao.startTransaction();

        Gebruiker gebruiker = new Gebruiker("Dornon", "Seppe", LocalDate.of(2004, 4, 1),
                new Adres("kroonstraat", "42", "9000", "Gent", "Belgie"), "seppe.dornon@student.hogent.be", "04123456",
                Rol.ADMINISTRATOR);
        gebruikerdao.insert(gebruiker);
        Site site = new Site("site1");
        sitedao.insert(site);
        machinedao.insert(new Machine("M1", "Machine 1 locatie", "werkt uitstekent", MachineStatus.DRAAIT,
                ProductionStatus.IN_ORDE, 10, gebruiker, 5, LocalDate.of(2025, 4, 5), site));

        // machinedao.commitTransaction();
        gebruikerdao.commitTransaction();
    }

}
