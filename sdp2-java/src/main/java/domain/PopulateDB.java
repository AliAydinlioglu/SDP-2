package domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import enums.MachineStatus;
import enums.OnderhoudStatus;
import enums.ProductionStatus;
import enums.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import repository.GebruikerDaoJpa;
import repository.MachineDaoJpa;
import repository.OnderhoudDaoJpa;
import repository.SiteDaoJpa;

import java.util.ArrayList;
import java.util.List;

public class PopulateDB {
    private GebruikerDaoJpa gebruikerdao;
    private SiteDaoJpa sitedao;
    private MachineDaoJpa machinedao;
    private OnderhoudDaoJpa onderhouddao;

    private List<Gebruiker> gebruikers = new ArrayList<>();
    private List<Site> sites = new ArrayList<>();
    private List<Machine> machines = new ArrayList<>();

    private String jdbcUrl = "jdbc:mysql://localhost:3306";
    private String jdbcUser = "root";
    private String jdbcPassword = "root";
    private String schemaName = "local_sdp2";
    private String persistenceUnitName = "sdp2";

    public void run() {
        if (!resetAndCreateSchema()) {
            System.err.println("Database schema reset failed. Aborting seeding process.");
            return;
        }
        initializeDAOs();
        createGebruikers();
        createSites();
        createMachines();
        createOnderhoud();
        System.out.println("Database succesvol gereset en gevuld met vereenvoudigde testgegevens.");
    }

    private boolean resetAndCreateSchema() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.schema-generation.database.action", "drop-and-create");

        EntityManagerFactory tempEmf = null;
        EntityManager tempEm = null;

        System.out.println("Attempting to drop and recreate schema '" + schemaName + "' using persistence unit '"
                + persistenceUnitName + "'...");
        try {
            tempEmf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
            tempEm = tempEmf.createEntityManager();
            tempEm.getTransaction().begin();
            tempEm.getTransaction().commit();
            System.out.println("Schema '" + schemaName + "' dropped and recreated successfully via JPA.");
            return true;
        } catch (Exception e) {
            System.err.println("Error during explicit schema drop-and-create via JPA: " + e.getMessage());
            e.printStackTrace();
            if (tempEm != null && tempEm.getTransaction().isActive()) {
                tempEm.getTransaction().rollback();
            }
            System.out.println("Attempting to create schema '" + schemaName + "' using direct JDBC as a fallback...");
            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
                    java.sql.Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
                System.out.println("Schema '" + schemaName + "' successfully ensured/created via JDBC.");
                return true;
            } catch (SQLException se) {
                System.err.println("Fallback JDBC schema creation also failed: " + se.getMessage());
                se.printStackTrace();
                return false;
            }
        } finally {
            if (tempEm != null && tempEm.isOpen()) {
                tempEm.close();
            }
            if (tempEmf != null && tempEmf.isOpen()) {
                tempEmf.close();
            }
        }
    }

    private void initializeDAOs() {
        gebruikerdao = new GebruikerDaoJpa();
        sitedao = new SiteDaoJpa();
        machinedao = new MachineDaoJpa();
        onderhouddao = new OnderhoudDaoJpa();
    }

    private void createGebruikers() {
        this.gebruikers.clear();
        gebruikerdao.startTransaction();
        try {
            Gebruiker admin = new Gebruiker("Admin", "User", LocalDate.of(1990, 1, 1),
                    new Adres("Adminstraat", "1", "9000", "Gent", "België"), "admin@example.com", "0411111111",
                    Rol.ADMINISTRATOR, true);
            this.gebruikers.add(admin);
            gebruikerdao.insert(admin);

            Gebruiker manager = new Gebruiker("Manager", "User", LocalDate.of(1985, 5, 10),
                    new Adres("Managerstraat", "2", "2000", "Antwerpen", "België"), "manager@example.com", "0422222222",
                    Rol.MANAGER, true);
            this.gebruikers.add(manager);
            gebruikerdao.insert(manager);

            Gebruiker technieker = new Gebruiker("Technieker", "User", LocalDate.of(1992, 3, 15),
                    new Adres("Techniekerstraat", "3", "1000", "Brussel", "België"), "technieker@example.com",
                    "0433333333", Rol.TECHNIEKER, true);
            this.gebruikers.add(technieker);
            gebruikerdao.insert(technieker);

            Gebruiker verantwoordelijke = new Gebruiker("Verantwoordelijke", "User", LocalDate.of(1988, 7, 20),
                    new Adres("Verantwstraat", "4", "3000", "Leuven", "België"), "verantwoordelijke@example.com",
                    "0444444444", Rol.VERANTWOORDELIJKE, true);
            this.gebruikers.add(verantwoordelijke);
            gebruikerdao.insert(verantwoordelijke);

            gebruikerdao.commitTransaction();
            System.out.println("gebruikers aangemaakt.");
        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van gebruikers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSites() {
        this.sites.clear();
        sitedao.startTransaction();
        try {
            Gebruiker siteVerantwoordelijke = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.VERANTWOORDELIJKE)
                    .findFirst()
                    .orElse(this.gebruikers.get(0));

            Site site1 = new Site("Hoofdzetel Gent");
            site1.setVerantwoordelijke(siteVerantwoordelijke);
            this.sites.add(site1);
            sitedao.insert(site1);

            Site site2 = new Site("Productie Antwerpen");
            site2.setVerantwoordelijke(siteVerantwoordelijke);
            this.sites.add(site2);
            sitedao.insert(site2);

            sitedao.commitTransaction();
            System.out.println("sites aangemaakt.");
        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van sites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createMachines() {
        this.machines.clear();
        machinedao.startTransaction();
        try {
            if (this.sites.isEmpty() || this.gebruikers.isEmpty()) {
                System.err.println("Geen sites of gebruikers beschikbaar om machines aan te maken.");
                return;
            }
            Site site1 = this.sites.get(0);
            Site site2 = this.sites.size() > 1 ? this.sites.get(1) : this.sites.get(0);

            Gebruiker machineAdmin = this.gebruikers.stream().filter(g -> g.getRol() == Rol.ADMINISTRATOR).findFirst()
                    .orElse(this.gebruikers.get(0));
            Gebruiker machineTechnieker = this.gebruikers.stream().filter(g -> g.getRol() == Rol.TECHNIEKER).findFirst()
                    .orElse(this.gebruikers.get(0));

            Machine machine1 = new Machine("M001", "Productiemachine Alpha", "Hal 1", MachineStatus.DRAAIT,
                    ProductionStatus.IN_ORDE, 10, machineAdmin, 5, LocalDate.now().minusYears(1), site1);
            this.machines.add(machine1);
            machinedao.insert(machine1);

            Machine machine2 = new Machine("M002", "Verpakkingsmachine Beta", "Hal 2", MachineStatus.GESTOPT_MANUEEL,
                    ProductionStatus.NOOD_AAN_ONDERHOUD, 7, machineTechnieker, 3, LocalDate.now().minusMonths(6),
                    site2);
            this.machines.add(machine2);
            machinedao.insert(machine2);

            Machine machine3 = new Machine("M003", "Testmachine Gamma", "Labo", MachineStatus.IN_ONDERHOUD,
                    ProductionStatus.FALEND, 8, machineTechnieker, 4, LocalDate.now().minusMonths(3), site1);
            this.machines.add(machine3);
            machinedao.insert(machine3);

            machinedao.commitTransaction();
            System.out.println("machines aangemaakt.");
        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van machines: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createOnderhoud() {
        onderhouddao.startTransaction();
        try {
            if (this.machines.isEmpty() || this.gebruikers.isEmpty()) {
                System.err.println("Geen machines of gebruikers beschikbaar om onderhoud aan te maken.");
                return;
            }

            Machine machine1 = this.machines.get(0);
            Machine machine2 = this.machines.size() > 1 ? this.machines.get(1) : this.machines.get(0);
            Machine machine3 = this.machines.size() > 2 ? this.machines.get(2) : this.machines.get(0);

            Gebruiker technieker = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.TECHNIEKER && g.getActief())
                    .findFirst()
                    .orElse(this.gebruikers.get(0));

            Onderhoud onderhoud1 = new Onderhoud(LocalDate.now().minusDays(30), LocalTime.of(9, 0), LocalTime.of(11, 0),
                    technieker.getGebruikerID(), "Routinecontrole M1", "Alles OK", "Geen opmerkingen",
                    OnderhoudStatus.VOLTOOID, machine1.getMachineID());
            onderhouddao.insert(onderhoud1);

            Onderhoud onderhoud2 = new Onderhoud(LocalDate.now().plusDays(7), LocalTime.of(14, 0), LocalTime.of(11, 0),
                    technieker.getGebruikerID(), "Inspectie M2", "Gepland", "Controleer lagers",
                    OnderhoudStatus.VOLTOOID, machine2.getMachineID());
            onderhouddao.insert(onderhoud2);

            Onderhoud onderhoud3 = new Onderhoud(LocalDate.now().minusDays(5), LocalTime.of(10, 0), LocalTime.of(15, 0),
                    technieker.getGebruikerID(), "Reparatie M3", "Onderdeel vervangen", "Testen na reparatie",
                    OnderhoudStatus.VOLTOOID, machine3.getMachineID());
            onderhouddao.insert(onderhoud3);

            onderhouddao.commitTransaction();
            System.out.println("onderhoudsrecords aangemaakt.");

        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van onderhoudsrecords: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PopulateDB populator = new PopulateDB();
        populator.run();
    }
}