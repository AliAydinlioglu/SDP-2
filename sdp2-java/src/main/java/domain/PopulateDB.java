package domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import enums.MachineStatus;
import enums.OnderhoudStatus;
import enums.ProductionStatus;
import enums.Rol;
import repository.GebruikerDaoJpa;
import repository.MachineDaoJpa;
import repository.OnderhoudDaoJpa;
import repository.SiteDaoJpa;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void run() {
        ensureSchemaExists();
        initializeDAOs();
        createGebruikers();
        createSites();
        createMachines();
        createOnderhoud();
        System.out.println("Database succesvol gevuld met testgegevens.");
    }

    private void ensureSchemaExists() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            System.out.println("Schema " + schemaName + " succesvol gecontroleerd/aangemaakt.");

        } catch (SQLException e) {
            System.err.println("Fout bij het aanmaken van schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDAOs() {
        gebruikerdao = new GebruikerDaoJpa();
        sitedao = new SiteDaoJpa();
        machinedao = new MachineDaoJpa();
        onderhouddao = new OnderhoudDaoJpa();
    }

    private boolean gebruikerExists(String email) {
        try {
            Gebruiker gebruiker = gebruikerdao.getGebruikerByEmail(email);
            return gebruiker != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void createGebruikers() {
        gebruikerdao.startTransaction();
        try {
            if (!gebruikerExists("seppe.dornon@student.hogent.be")) {
                Gebruiker admin = new Gebruiker(
                        "Dornon", "Seppe",
                        LocalDate.of(2004, 4, 1),
                        new Adres("Kroonstraat", "42", "9000", "Gent", "België"),
                        "seppe.dornon@student.hogent.be",
                        "04123456",
                        Rol.ADMINISTRATOR,
                        true
                );
                gebruikers.add(admin);
            }

            if (!gebruikerExists("jan.janssens@example.com")) {
                Gebruiker technicus = new Gebruiker(
                        "Janssens", "Jan",
                        LocalDate.of(1985, 7, 15),
                        new Adres("Techniekerstraat", "10", "9000", "Gent", "België"),
                        "jan.janssens@example.com",
                        "0478123456",
                        Rol.TECHNIEKER,
                        true
                );
                gebruikers.add(technicus);
            }

            if (!gebruikerExists("thomas.vandenberghe@example.com")) {
                Gebruiker manager = new Gebruiker(
                        "Vandenberghe", "Thomas",
                        LocalDate.of(1982, 8, 12),
                        new Adres("Managerslaan", "25", "9000", "Gent", "België"),
                        "thomas.vandenberghe@example.com",
                        "0492345678",
                        Rol.MANAGER,
                        true
                );
                gebruikers.add(manager);
            }

            if (!gebruikerExists("sara.vermeulen@example.com")) {
                Gebruiker verantwoordelijke = new Gebruiker(
                        "Vermeulen", "Sara",
                        LocalDate.of(1988, 11, 5),
                        new Adres("Verantwoordelijkheidstraat", "7", "2000", "Antwerpen", "België"),
                        "sara.vermeulen@example.com",
                        "0498765432",
                        Rol.VERANTWOORDELIJKE,
                        true
                );
                gebruikers.add(verantwoordelijke);
            }

            if (!gebruikerExists("lucas.maertens@example.com")) {
                Gebruiker inactief = new Gebruiker(
                        "Maertens", "Lucas",
                        LocalDate.of(1975, 9, 30),
                        new Adres("Dorpstraat", "15", "8500", "Kortrijk", "België"),
                        "lucas.maertens@example.com",
                        "0467891234",
                        Rol.TECHNIEKER,
                        false
                );
                gebruikers.add(inactief);
            }

            if (!gebruikerExists("pieter.devos@example.com")) {
                Gebruiker verantwoordelijke2 = new Gebruiker(
                        "Devos", "Pieter",
                        LocalDate.of(1988, 3, 22),
                        new Adres("Reparatiestraat", "7", "9000", "Gent", "België"),
                        "pieter.devos@example.com",
                        "0478987654",
                        Rol.VERANTWOORDELIJKE,
                        true
                );
                gebruikers.add(verantwoordelijke2);
            }

            if (!gebruikerExists("sofia.vandam@example.com")) {
                Gebruiker verantwoordelijke3 = new Gebruiker(
                        "Van Dam", "Sofia",
                        LocalDate.of(1991, 6, 18),
                        new Adres("Monteurplein", "12", "2000", "Antwerpen", "België"),
                        "sofia.vandam@example.com",
                        "0476543210",
                        Rol.VERANTWOORDELIJKE,
                        true
                );
                gebruikers.add(verantwoordelijke3);
            }

            if (!gebruikerExists("david.claes@example.com")) {
                Gebruiker verantwoordelijke4 = new Gebruiker(
                        "Claes", "David",
                        LocalDate.of(1983, 11, 5),
                        new Adres("Techniekerslaan", "23", "3000", "Leuven", "België"),
                        "david.claes@example.com",
                        "0472345678",
                        Rol.VERANTWOORDELIJKE,
                        true
                );
                gebruikers.add(verantwoordelijke4);
            }

            if (!gebruikerExists("geralt@gmail.com")) {
                Gebruiker geralt = new Gebruiker(
                        "van Rivia", "Geralt",
                        LocalDate.of(1972, 5, 15),
                        new Adres("Kaer Morhen", "1", "1000", "Brussel", "België"),
                        "geralt@gmail.com",
                        "0455667788",
                        Rol.ADMINISTRATOR,
                        true
                );
                gebruikers.add(geralt);
            }

            if (!gebruikers.isEmpty()) {
                for (Gebruiker gebruiker : gebruikers) {
                    gebruikerdao.insert(gebruiker);
                }
                gebruikerdao.commitTransaction();
                System.out.println(gebruikers.size() + " nieuwe gebruikers toegevoegd aan de database");
            } else {
                gebruikerdao.rollbackTransaction();
                System.out.println("Alle gebruikers bestaan al in de database");
            }
        } catch (Exception e) {
            gebruikerdao.rollbackTransaction();
            System.out.println("Fout bij het aanmaken van gebruikers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean siteExists(String naam) {
        try {
            List<Site> existingSites = sitedao.findAll();
            return existingSites.stream().anyMatch(site -> site.getNaam().equals(naam));
        } catch (Exception e) {
            return false;
        }
    }

    private void createSites() {
        List<Gebruiker> verantwoordelijken = new ArrayList<>();
        if (gebruikers.isEmpty()) {
            gebruikers = gebruikerdao.findAll();
        }

        List<Gebruiker> allVerantwoordelijken = gebruikers.stream()
                .filter(g -> g.getRol() == Rol.VERANTWOORDELIJKE && g.getActief())
                .collect(Collectors.toList());

        if (allVerantwoordelijken.size() < 4) {
            gebruikerdao.startTransaction();
            try {
                if (allVerantwoordelijken.isEmpty()) {
                    if (!gebruikerExists("verantw1@example.com")) {
                        Gebruiker verantw1 = new Gebruiker(
                                "Verantwoordelijke", "Een",
                                LocalDate.of(1980, 5, 15),
                                new Adres("Verantwoordelijkheidsstraat", "1", "9000", "Gent", "België"),
                                "verantw1@example.com",
                                "0471112233",
                                Rol.VERANTWOORDELIJKE,
                                true
                        );
                        gebruikerdao.insert(verantw1);
                        verantwoordelijken.add(verantw1);
                        gebruikers.add(verantw1);
                    }
                }

                if (allVerantwoordelijken.size() < 2) {
                    if (!gebruikerExists("verantw2@example.com")) {
                        Gebruiker verantw2 = new Gebruiker(
                                "Verantwoordelijke", "Twee",
                                LocalDate.of(1982, 6, 20),
                                new Adres("Verantwoordelijkheidsstraat", "2", "2000", "Antwerpen", "België"),
                                "verantw2@example.com",
                                "0472223344",
                                Rol.VERANTWOORDELIJKE,
                                true
                        );
                        gebruikerdao.insert(verantw2);
                        verantwoordelijken.add(verantw2);
                        gebruikers.add(verantw2);
                    }
                }

                if (allVerantwoordelijken.size() < 3) {
                    if (!gebruikerExists("verantw3@example.com")) {
                        Gebruiker verantw3 = new Gebruiker(
                                "Verantwoordelijke", "Drie",
                                LocalDate.of(1984, 7, 25),
                                new Adres("Verantwoordelijkheidsstraat", "3", "1000", "Brussel", "België"),
                                "verantw3@example.com",
                                "0473334455",
                                Rol.VERANTWOORDELIJKE,
                                true
                        );
                        gebruikerdao.insert(verantw3);
                        verantwoordelijken.add(verantw3);
                        gebruikers.add(verantw3);
                    }
                }

                if (allVerantwoordelijken.size() < 4) {
                    if (!gebruikerExists("verantw4@example.com")) {
                        Gebruiker verantw4 = new Gebruiker(
                                "Verantwoordelijke", "Vier",
                                LocalDate.of(1986, 8, 30),
                                new Adres("Verantwoordelijkheidsstraat", "4", "8500", "Kortrijk", "België"),
                                "verantw4@example.com",
                                "0474445566",
                                Rol.VERANTWOORDELIJKE,
                                true
                        );
                        gebruikerdao.insert(verantw4);
                        verantwoordelijken.add(verantw4);
                        gebruikers.add(verantw4);
                    }
                }

                gebruikerdao.commitTransaction();
                System.out.println(verantwoordelijken.size() + " nieuwe verantwoordelijken toegevoegd aan de database");
            } catch (Exception e) {
                gebruikerdao.rollbackTransaction();
                System.out.println("Fout bij het aanmaken van verantwoordelijken: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else {
            verantwoordelijken = allVerantwoordelijken.subList(0, 4);
        }

        sitedao.startTransaction();
        try {
            Gebruiker verantwoordelijke1 = verantwoordelijken.get(0);
            Gebruiker verantwoordelijke2 = verantwoordelijken.get(1);
            Gebruiker verantwoordelijke3 = verantwoordelijken.get(2);
            Gebruiker verantwoordelijke4 = verantwoordelijken.get(3);

            if (!siteExists("Gent Hoofdkantoor")) {
                Site site1 = new Site("Gent Hoofdkantoor");
                site1.setVerantwoordelijke(verantwoordelijke1);
                sites.add(site1);
            }

            if (!siteExists("Antwerpen Lokaal")) {
                Site site2 = new Site("Antwerpen Lokaal");
                site2.setVerantwoordelijke(verantwoordelijke2);
                sites.add(site2);
            }

            if (!siteExists("Brussel Centrum")) {
                Site site3 = new Site("Brussel Centrum");
                site3.setVerantwoordelijke(verantwoordelijke3);
                sites.add(site3);
            }

            if (!siteExists("Kortrijk Fabriek")) {
                Site site4 = new Site("Kortrijk Fabriek");
                site4.setVerantwoordelijke(verantwoordelijke4);
                sites.add(site4);
            }

            if (!sites.isEmpty()) {
                for (Site site : sites) {
                    sitedao.insert(site);
                }
                sitedao.commitTransaction();
                System.out.println(sites.size() + " nieuwe sites toegevoegd aan de database met verantwoordelijken");
            } else {
                sitedao.rollbackTransaction();
                System.out.println("Alle sites bestaan al in de database");
            }
        } catch (Exception e) {
            sitedao.rollbackTransaction();
            System.out.println("Fout bij het aanmaken van sites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean machineExists(String naam) {
        try {
            List<Machine> existingMachines = machinedao.findAll();
            return existingMachines.stream().anyMatch(machine -> machine.getNaam().equals(naam));
        } catch (Exception e) {
            return false;
        }
    }

    private void createMachines() {
        if (gebruikers.isEmpty()) {
            gebruikers = gebruikerdao.findAll();
        }

        if (sites.isEmpty()) {
            sites = sitedao.findAll();
        }

        if (gebruikers.isEmpty() || sites.isEmpty()) {
            System.out.println("Kan geen machines aanmaken: gebruikers of sites niet gevonden in de database");
            return;
        }

        Gebruiker admin = gebruikers.stream()
                .filter(g -> g.getRol() == Rol.ADMINISTRATOR)
                .findFirst()
                .orElse(null);

        Gebruiker technicus = gebruikers.stream()
                .filter(g -> g.getRol() == Rol.TECHNIEKER && g.getActief())
                .findFirst()
                .orElse(null);

        Gebruiker manager = gebruikers.stream()
                .filter(g -> g.getRol() == Rol.MANAGER)
                .findFirst()
                .orElse(null);


        machinedao.startTransaction();
        try {
            if (!machineExists("M001")) {
                Machine machine1 = new Machine(
                        "M001",
                        "Werkt uitstekend, regulier onderhoud uitgevoerd",
                        "Productiehal A, Zone 1",
                        MachineStatus.DRAAIT,
                        ProductionStatus.IN_ORDE,
                        10,
                        admin,
                        5,
                        LocalDate.of(2025, 4, 5),
                        sites.get(0)
                );
                machines.add(machine1);
            }

            if (!machineExists("M002")) {
                Machine machine2 = new Machine(
                        "M002",
                        "Machine maakt abnormaal geluid, inspectie nodig",
                        "Productiehal B, Zone 3",
                        MachineStatus.GESTOPT_AUTO,
                        ProductionStatus.NOOD_AAN_ONDERHOUD,
                        8,
                        technicus,
                        4,
                        LocalDate.of(2023, 12, 15),
                        sites.get(0)
                );
                machines.add(machine2);
            }

            if (!machineExists("M003")) {
                Machine machine3 = new Machine(
                        "M003",
                        "Bezig met periodiek onderhoud",
                        "Kelder, Kamer 2",
                        MachineStatus.IN_ONDERHOUD,
                        ProductionStatus.FALEND,
                        6,
                        technicus,
                        3,
                        LocalDate.of(2024, 8, 20),
                        sites.get(1)
                );
                machines.add(machine3);
            }

            if (!machineExists("M004")) {
                Machine machine4 = new Machine(
                        "M004",
                        "Defecte motor, vervangonderdelen besteld",
                        "Productiehal C, Zone 5",
                        MachineStatus.GESTOPT_MANUEEL,
                        ProductionStatus.FALEND,
                        3,
                        technicus,
                        0,
                        LocalDate.of(2024, 5, 10),
                        sites.get(2)
                );
                machines.add(machine4);
            }

            if (!machineExists("M005")) {
                Machine machine5 = new Machine(
                        "M005",
                        "Kritieke productiemachine, dagelijkse controle",
                        "Productiehal A, Zone 2",
                        MachineStatus.DRAAIT,
                        ProductionStatus.IN_ORDE,
                        9,
                        manager,
                        5,
                        LocalDate.of(2024, 11, 30),
                        sites.get(3)
                );
                machines.add(machine5);
            }

            if (!machines.isEmpty()) {
                for (Machine machine : machines) {
                    machinedao.insert(machine);
                }
                machinedao.commitTransaction();
                System.out.println(machines.size() + " nieuwe machines toegevoegd aan de database");
            } else {
                machinedao.rollbackTransaction();
                System.out.println("Alle machines bestaan al in de database");
            }
        } catch (Exception e) {
            machinedao.rollbackTransaction();
            System.out.println("Fout bij het aanmaken van machines: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean onderhoudExists(int machineId, LocalDate datum) {
        try {
            List<Onderhoud> existingOnderhouden = onderhouddao.findLaatsteVoltooidePerMachine();
            return existingOnderhouden.stream()
                    .anyMatch(onderhoud -> onderhoud.getDatum().equals(datum));
        } catch (Exception e) {
            return false;
        }
    }

    private void createOnderhoud() {
        if (gebruikers.isEmpty() || machines.isEmpty()) {
            System.out.println("Kan geen onderhoudsrecords aanmaken: gebruikers of machines niet gevonden in de database");
            return;
        }

        onderhouddao.startTransaction();
        try {
            Gebruiker technicus = gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.TECHNIEKER && g.getActief())
                    .findFirst()
                    .orElse(null);

            Gebruiker manager = gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.MANAGER)
                    .findFirst()
                    .orElse(null);

            if (technicus == null || manager == null) {
                System.out.println("Kan geen onderhoudsrecords aanmaken: vereiste gebruikersrollen niet gevonden");
                onderhouddao.rollbackTransaction();
                return;
            }

            Onderhoud onderhoud1 = new Onderhoud(
                    LocalDate.now().minusMonths(2),
                    LocalTime.of(8, 0),
                    LocalTime.of(10, 30),
                    technicus.getGebruikerID(),
                    "Regulier onderhoud",
                    "Filters vervangen en olie ververst. Alle systemen werken naar behoren.",
                    "Volgende controle over 3 maanden aanbevolen",
                    OnderhoudStatus.VOLTOOID,
                    machines.get(0).getMachineID()
            );

            Onderhoud onderhoud2 = new Onderhoud(
                    LocalDate.now().minusWeeks(2),
                    LocalTime.of(9, 15),
                    LocalTime.of(12, 0),
                    technicus.getGebruikerID(),
                    "Inspectie abnormaal geluid",
                    "Losse aandrijfriem geïdentificeerd en vervangen. Testrun succesvol uitgevoerd.",
                    "Riem was versleten, mogelijk door verkeerde uitlijning",
                    OnderhoudStatus.VOLTOOID,
                    machines.get(1).getMachineID()
            );

            Onderhoud onderhoud3 = new Onderhoud(
                    LocalDate.now(),
                    LocalTime.of(8, 30),
                    LocalTime.of(16, 0),
                    technicus.getGebruikerID(),
                    "Periodiek onderhoud",
                    "Volledige inspectie van alle componenten. Vervanging van slijtageonderdelen.",
                    "Machine wordt volledig gedemonteerd voor grondig onderhoud",
                    OnderhoudStatus.IN_UITVOERING,
                    machines.get(2).getMachineID()
            );

            Connection connection = null;
            try {
                String dbUrl = jdbcUrl + "/" + schemaName;

                connection = DriverManager.getConnection(dbUrl, jdbcUser, jdbcPassword);

                String sql1 = "INSERT INTO onderhouden (datum, starttijd, eindtijd, technieker_id, reden, rapport, opmerkingen, status, machine_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(sql1)) {
                    statement.setObject(1, LocalDate.now().plusWeeks(1));
                    statement.setObject(2, LocalTime.of(9, 0));
                    statement.setObject(3, LocalTime.of(17, 0));
                    statement.setInt(4, manager.getGebruikerID());
                    statement.setString(5, "Vervanging defecte motor");
                    statement.setString(6, "Nieuwe motor is besteld en zal geïnstalleerd worden");
                    statement.setString(7, "Uitschakeling van 8 uur is ingepland, klanten zijn op de hoogte gebracht");
                    statement.setString(8, OnderhoudStatus.INGEPLAND.name());
                    statement.setInt(9, machines.get(3).getMachineID());

                    statement.executeUpdate();
                    System.out.println("1 gepland onderhoudsrecord toegevoegd aan de database");

                    statement.setObject(1, LocalDate.now().plusMonths(1));
                    statement.setObject(2, LocalTime.of(17, 0));
                    statement.setObject(3, LocalTime.of(19, 0));
                    statement.setInt(4, manager.getGebruikerID());
                    statement.setString(5, "Routine onderhoud");
                    statement.setString(6, "Standaard controle en preventief onderhoud");
                    statement.setString(7, "Wordt na werktijd uitgevoerd om productie niet te verstoren");
                    statement.setString(8, OnderhoudStatus.INGEPLAND.name());
                    statement.setInt(9, machines.get(4).getMachineID());

                    statement.executeUpdate();
                    System.out.println("1 gepland onderhoudsrecord toegevoegd aan de database");
                }
            } catch (SQLException e) {
                System.err.println("Fout bij het invoegen van gepland onderhoud: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        System.err.println("Fout bij het sluiten van de verbinding: " + e.getMessage());
                    }
                }
            }

            Onderhoud onderhoud6 = new Onderhoud(
                    LocalDate.now().minusMonths(5),
                    LocalTime.of(10, 0),
                    LocalTime.of(11, 30),
                    technicus.getGebruikerID(),
                    "Noodonderhoud",
                    "Machine stopte plotseling. Sensor vervangen en software gereset.",
                    "Sensor was defect door overbelasting",
                    OnderhoudStatus.VOLTOOID,
                    machines.get(0).getMachineID()
            );

            onderhouddao.insert(onderhoud1);
            onderhouddao.insert(onderhoud2);
            onderhouddao.insert(onderhoud3);
            onderhouddao.insert(onderhoud6);

            onderhouddao.commitTransaction();
            System.out.println("4 nieuwe onderhoudsrecords toegevoegd aan de database");
        } catch (Exception e) {
            onderhouddao.rollbackTransaction();
            System.out.println("Fout bij het aanmaken van onderhoudsrecords: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
