package domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import enums.MachineStatus;
import enums.ProductionStatus;
import enums.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import repository.GebruikerDaoJpa;
import repository.MachineDaoJpa;
import repository.SiteDaoJpa;

public class PopulateDB {
    public static void resetAndSeedDatabase() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.schema-generation.database.action", "drop-and-create");

        EntityManagerFactory tempEmf = null;
        EntityManager tempEm = null;

        System.out.println("Attempting to drop and recreate schema...");
        try {
            tempEmf = Persistence.createEntityManagerFactory("sdp2", properties);
            tempEm = tempEmf.createEntityManager();
            tempEm.getTransaction().begin();
            tempEm.getTransaction().commit();
            System.out.println("Schema dropped and recreated successfully.");
        } catch (Exception e) {
            System.err.println("Error during explicit schema drop-and-create: " + e.getMessage());
            e.printStackTrace();
            if (tempEm != null && tempEm.getTransaction().isActive()) {
                tempEm.getTransaction().rollback();
            }
            return;
        } finally {
            if (tempEm != null && tempEm.isOpen()) {
                tempEm.close();
            }
            if (tempEmf != null && tempEmf.isOpen()) {
                tempEmf.close();
            }
        }

        System.out.println("Starting database seeding...");

        GebruikerDaoJpa gebruikerdao = new GebruikerDaoJpa();
        SiteDaoJpa sitedao = new SiteDaoJpa();
        MachineDaoJpa machinedao = new MachineDaoJpa();

        try {
            gebruikerdao.startTransaction();
            Gebruiker gebruiker = new Gebruiker("Dornon", "Seppe", LocalDate.of(2004, 4, 1),
                    new Adres("kroonstraat", "42", "9000", "Gent", "Belgie"), "seppe.dornon@student.hogent.be",
                    "04123456",
                    Rol.ADMINISTRATOR, true);
            gebruikerdao.insert(gebruiker);
            gebruikerdao.commitTransaction();

            sitedao.startTransaction();
            Site site = new Site("site1");
            sitedao.insert(site);
            sitedao.commitTransaction();

            machinedao.startTransaction();
            machinedao.insert(new Machine("M1", "Machine 1 locatie", "werkt uitstekent", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 10, gebruiker, 5, LocalDate.of(2025, 4, 5), site));
            machinedao.commitTransaction();
            System.out.println("Database seeding completed successfully.");
        } catch (Exception e) {
            System.out.println("PopulateDB: " + e.getMessage());
        }
    }

}
