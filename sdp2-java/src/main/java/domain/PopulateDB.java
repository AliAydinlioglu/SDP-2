package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dto.GebruikerDTO;
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

public class PopulateDB {
    private GebruikerDaoJpa gebruikerdao;
    private SiteDaoJpa sitedao;
    private MachineDaoJpa machinedao;
    private OnderhoudDaoJpa onderhouddao;
    private repository.LogDaoJpa logdao;

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
        createLogs();
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
        logdao = new repository.LogDaoJpa();
    }

    private void createGebruikers() {
        this.gebruikers.clear();
        gebruikerdao.startTransaction();
        try {
            // Administrators
            createAndAddGebruiker("Jan", "Janssens", LocalDate.of(1980, 1, 15),
                    "Korenmarkt", "45", "9000", "Gent", "België", "jan.janssens@bedrijf.be", "0471234567", Rol.ADMINISTRATOR, true);
            createAndAddGebruiker("Pieter", "De Smet", LocalDate.of(1985, 3, 21),
                    "Antwerpsesteenweg", "112", "9040", "Sint-Amandsberg", "België", "pieter.desmet@bedrijf.be", "0472345678", Rol.ADMINISTRATOR, true);
            createAndAddGebruiker("Sophie", "Martens", LocalDate.of(1978, 11, 7),
                    "Bosstraat", "18", "8500", "Kortrijk", "België", "sophie.martens@bedrijf.be", "0473456789", Rol.ADMINISTRATOR, false);

            // Managers
            createAndAddGebruiker("Bart", "Peeters", LocalDate.of(1975, 6, 12),
                    "Leiekaai", "27", "9000", "Gent", "België", "bart.peeters@bedrijf.be", "0474567890", Rol.MANAGER, false);
            createAndAddGebruiker("Els", "Vandenberghe", LocalDate.of(1982, 4, 30),
                    "Keizer Karelstraat", "83", "9000", "Gent", "België", "els.vandenberghe@bedrijf.be", "0475678901", Rol.MANAGER, true);
            createAndAddGebruiker("Michel", "Dupont", LocalDate.of(1979, 9, 18),
                    "Avenue Louise", "120", "1050", "Brussel", "België", "michel.dupont@bedrijf.be", "0476789012", Rol.MANAGER, true);
            createAndAddGebruiker("Tom", "Wouters", LocalDate.of(1984, 2, 25),
                    "Meir", "15", "2000", "Antwerpen", "België", "tom.wouters@bedrijf.be", "0477890123", Rol.MANAGER, true);
            createAndAddGebruiker("Karen", "Verhoeven", LocalDate.of(1981, 7, 14),
                    "Bondgenotenlaan", "42", "3000", "Leuven", "België", "karen.verhoeven@bedrijf.be", "0478901234", Rol.MANAGER, true);

            // Verantwoordelijken
            createAndAddGebruiker("Luc", "Vermeulen", LocalDate.of(1977, 8, 8),
                    "Oudburg", "7", "9000", "Gent", "België", "luc.vermeulen@bedrijf.be", "0479012345", Rol.VERANTWOORDELIJKE, false);
            createAndAddGebruiker("Eva", "Jacobs", LocalDate.of(1983, 5, 19),
                    "Vrijdagmarkt", "22", "9000", "Gent", "België", "eva.jacobs@bedrijf.be", "0480123456", Rol.VERANTWOORDELIJKE, true);
            createAndAddGebruiker("Marc", "Devos", LocalDate.of(1976, 12, 3),
                    "Veldstraat", "88", "9000", "Gent", "België", "marc.devos@bedrijf.be", "0481234567", Rol.VERANTWOORDELIJKE, true);
            createAndAddGebruiker("Julie", "Van Damme", LocalDate.of(1986, 10, 27),
                    "Nationalestraat", "35", "2000", "Antwerpen", "België", "julie.vandamme@bedrijf.be", "0482345678", Rol.VERANTWOORDELIJKE, true);
            createAndAddGebruiker("Dirk", "Coppens", LocalDate.of(1973, 9, 9),
                    "Lippenslaan", "102", "8300", "Knokke", "België", "dirk.coppens@bedrijf.be", "0483456789", Rol.VERANTWOORDELIJKE, true);
            createAndAddGebruiker("Annemie", "Peeters", LocalDate.of(1980, 4, 22),
                    "Brusselsesteenweg", "155", "9090", "Melle", "België", "annemie.peeters@bedrijf.be", "0484567890", Rol.VERANTWOORDELIJKE, true);

            // Techniekers
            createAndAddGebruiker("Koen", "Mertens", LocalDate.of(1988, 2, 14),
                    "Dampoortstraat", "63", "9000", "Gent", "België", "koen.mertens@bedrijf.be", "0485678901", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Saskia", "Willems", LocalDate.of(1990, 6, 6),
                    "Hoogstraat", "29", "9000", "Gent", "België", "saskia.willems@bedrijf.be", "0486789012", Rol.TECHNIEKER, true);
            createAndAddGebruiker("David", "Verlinden", LocalDate.of(1985, 11, 23),
                    "Sint-Pietersnieuwstraat", "130", "9000", "Gent", "België", "david.verlinden@bedrijf.be", "0487890123", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Nancy", "Maes", LocalDate.of(1982, 3, 31),
                    "Lange Munt", "18", "9000", "Gent", "België", "nancy.maes@bedrijf.be", "0488901234", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Jeroen", "De Sutter", LocalDate.of(1984, 8, 17),
                    "Noorderlaan", "75", "2030", "Antwerpen", "België", "jeroen.desutter@bedrijf.be", "0489012345", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Tine", "Van Hecke", LocalDate.of(1987, 5, 25),
                    "Zeedijk", "201", "8400", "Oostende", "België", "tine.vanhecke@bedrijf.be", "0490123456", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Thomas", "Janssen", LocalDate.of(1989, 7, 12),
                    "Ambachtenlaan", "34", "3001", "Heverlee", "België", "thomas.janssen@bedrijf.be", "0491234567", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Maaike", "Bastiaens", LocalDate.of(1986, 9, 9),
                    "Tiensestraat", "66", "3000", "Leuven", "België", "maaike.bastiaens@bedrijf.be", "0492345678", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Steven", "De Wolf", LocalDate.of(1983, 12, 5),
                    "Grote Markt", "48", "8900", "Ieper", "België", "steven.dewolf@bedrijf.be", "0493456789", Rol.TECHNIEKER, true);
            createAndAddGebruiker("Liesbeth", "Bosmans", LocalDate.of(1991, 1, 28),
                    "Diestsestraat", "142", "3000", "Leuven", "België", "liesbeth.bosmans@bedrijf.be", "0494567890", Rol.TECHNIEKER, true);

            // Gewone gebruikers
            createAndAddGebruiker("Filip", "Lemmens", LocalDate.of(1980, 7, 11),
                    "Vlaanderenstraat", "54", "9000", "Gent", "België", "filip.lemmens@bedrijf.be", "0495678901", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Nathalie", "Claes", LocalDate.of(1985, 4, 2),
                    "Overpoortstraat", "92", "9000", "Gent", "België", "nathalie.claes@bedrijf.be", "0496789012", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Stef", "Van den Broeck", LocalDate.of(1988, 8, 19),
                    "Kortrijksesteenweg", "302", "9000", "Gent", "België", "stef.vandenbroeck@bedrijf.be", "0497890123", Rol.GEBRUIKER, false);
            createAndAddGebruiker("Heidi", "Van Hove", LocalDate.of(1979, 10, 15),
                    "Kammerstraat", "17", "9000", "Gent", "België", "heidi.vanhove@bedrijf.be", "0498901234", Rol.GEBRUIKER, false);
            createAndAddGebruiker("Maarten", "De Vos", LocalDate.of(1983, 5, 22),
                    "Dampoortstraat", "45", "9000", "Gent", "België", "maarten.devos@bedrijf.be", "0499012345", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Eline", "Verschueren", LocalDate.of(1990, 3, 14),
                    "Brabantdam", "78", "9000", "Gent", "België", "eline.verschueren@bedrijf.be", "0491123456", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Thomas", "Verschaeve", LocalDate.of(1987, 9, 8),
                    "Korenmarkt", "12", "9000", "Gent", "België", "thomas.verschaeve@bedrijf.be", "0492234567", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Lotte", "Verhaeghe", LocalDate.of(1982, 11, 30),
                    "Veldstraat", "97", "9000", "Gent", "België", "lotte.verhaeghe@bedrijf.be", "0493345678", Rol.GEBRUIKER, false);
            createAndAddGebruiker("Michiel", "Coppens", LocalDate.of(1991, 2, 17),
                    "Hoogpoort", "23", "9000", "Gent", "België", "michiel.coppens@bedrijf.be", "0494456789", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Sarah", "Vandenberghe", LocalDate.of(1984, 6, 25),
                    "Langemunt", "35", "9000", "Gent", "België", "sarah.vandenberghe@bedrijf.be", "0495567890", Rol.GEBRUIKER, false);
            createAndAddGebruiker("Jeroen", "Vermeulen", LocalDate.of(1986, 4, 9),
                    "Onderbergen", "68", "9000", "Gent", "België", "jeroen.vermeulen@bedrijf.be", "0496678901", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Elke", "Van Daele", LocalDate.of(1989, 8, 12),
                    "Sint-Pietersplein", "42", "9000", "Gent", "België", "elke.vandaele@bedrijf.be", "0497789012", Rol.GEBRUIKER, true);
            createAndAddGebruiker("Pieter-Jan", "Maertens", LocalDate.of(1981, 12, 5),
                    "Nederkouter", "56", "9000", "Gent", "België", "pieterjan.maertens@bedrijf.be", "0498890123", Rol.GEBRUIKER, false);
            createAndAddGebruiker("Emma", "Declercq", LocalDate.of(1992, 1, 19),
                    "Savaanstraat", "88", "9000", "Gent", "België", "emma.declercq@bedrijf.be", "0499901234", Rol.GEBRUIKER, true);

            gebruikerdao.commitTransaction();
            System.out.println("Alle gebruikers aangemaakt: " + this.gebruikers.size() + " gebruikers in totaal.");
        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van gebruikers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAndAddGebruiker(String voornaam, String familienaam, LocalDate geboortedatum,
                                       String straat, String huisnr, String postcode, String stad, String land,
                                       String email, String telefoon, Rol rol, boolean actief) {
        Gebruiker gebruiker = new Gebruiker(voornaam, familienaam, geboortedatum,
                new Adres(straat, huisnr, postcode, stad, land), email, telefoon, rol, actief);
        this.gebruikers.add(gebruiker);
        gebruikerdao.insert(gebruiker);
    }

    private void createSites() {
        this.sites.clear();
        sitedao.startTransaction();
        try {
            List<Gebruiker> SiteVerantwoordelijken = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.VERANTWOORDELIJKE)
                    .collect(java.util.stream.Collectors.toList());

            if (SiteVerantwoordelijken.isEmpty()) {
                SiteVerantwoordelijken.add(this.gebruikers.get(0));
            }

            createAndAddSite("Hoofdzetel Gent", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Productie Antwerpen", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Distributiecentrum Brussel", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("R&D Centrum Leuven", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Logistiek Centrum Kortrijk", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Productielijn Hasselt", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Assemblage Mechelen", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Verpakkingscentrum Aalst", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Testfaciliteit Brugge", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Onderhoudscentrum Oostende", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Kwaliteitscontrole Genk", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Magazijn Sint-Niklaas", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Buitendienst Amsterdam", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Verkoopkantoor Rotterdam", getRandomVerantwoordelijke(SiteVerantwoordelijken));
            createAndAddSite("Servicepunt Eindhoven", getRandomVerantwoordelijke(SiteVerantwoordelijken));

            sitedao.commitTransaction();
            System.out.println("Alle sites aangemaakt: " + this.sites.size() + " sites in totaal.");
        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van sites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAndAddSite(String naam, Gebruiker verantwoordelijke) {
        Site site = new Site(naam);
        site.setVerantwoordelijke(verantwoordelijke);
        this.sites.add(site);
        sitedao.insert(site);
    }

    private Gebruiker getRandomVerantwoordelijke(List<Gebruiker> verantwoordelijken) {
        int index = (int) (Math.random() * verantwoordelijken.size());
        return verantwoordelijken.get(index);
    }

    private void createMachines() {
        this.machines.clear();
        machinedao.startTransaction();
        try {
            if (this.sites.isEmpty() || this.gebruikers.isEmpty()) {
                System.err.println("Geen sites of gebruikers beschikbaar om machines aan te maken.");
                return;
            }

            List<Gebruiker> admins = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.ADMINISTRATOR)
                    .collect(java.util.stream.Collectors.toList());

            List<Gebruiker> techniekers = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.TECHNIEKER)
                    .collect(java.util.stream.Collectors.toList());

            if (admins.isEmpty()) admins.add(this.gebruikers.get(0));
            if (techniekers.isEmpty()) techniekers.add(this.gebruikers.get(0));

            for (Site site : this.sites) {
                int aantalMachines = determineNumberOfMachinesForSite(site.getNaam());

                generateMachinesForSite(site, aantalMachines, admins, techniekers);
            }

            machinedao.commitTransaction();
            System.out.println("Alle machines aangemaakt: " + this.machines.size() + " machines in totaal.");
        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van machines: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int determineNumberOfMachinesForSite(String siteNaam) {
        if (siteNaam.contains("Hoofdzetel") || siteNaam.contains("Productie")) {
            return 15 + (int) (Math.random() * 10);
        } else if (siteNaam.contains("Distributie") || siteNaam.contains("Logistiek")) {
            return 10 + (int) (Math.random() * 8);
        } else if (siteNaam.contains("R&D") || siteNaam.contains("Test")) {
            return 8 + (int) (Math.random() * 7);
        } else if (siteNaam.contains("Assemblage") || siteNaam.contains("Verpakking")) {
            return 12 + (int) (Math.random() * 6);
        } else if (siteNaam.contains("Onderhoud") || siteNaam.contains("Service")) {
            return 6 + (int) (Math.random() * 5);
        } else {
            return 5 + (int) (Math.random() * 5);
        }
    }

    private void generateMachinesForSite(Site site, int aantalMachines, List<Gebruiker> admins, List<Gebruiker> techniekers) {
        String sitePrefix = generateSitePrefix(site.getNaam());
        List<String> machineTypes = getMachineTypesForSite(site.getNaam());
        List<String> locaties = getLocationsForSite(site.getNaam());

        for (int i = 1; i <= aantalMachines; i++) {
            String machineCode = sitePrefix + String.format("%03d", i);

            String machineType = machineTypes.get((int) (Math.random() * machineTypes.size()));

            String productInfo = generateProductInfo(machineType);

            String locatie = locaties.get((int) (Math.random() * locaties.size()));

            MachineStatus status = generateMachineStatus(site.getNaam());
            ProductionStatus productieStatus = generateProductionStatus(status, site.getNaam());

            int uptime = generateUptime(status, productieStatus);

            Gebruiker verantwoordelijke = chooseMachineResponsible(admins, techniekers, site.getNaam());

            int dagenSindsOnderhoud = generateDaysSinceLastMaintenance(status, productieStatus);
            LocalDate volgendOnderhoud = generateNextMaintenanceDate(status, productieStatus);

            Machine machine = new Machine(machineCode, productInfo, locatie, status, productieStatus, uptime,
                    verantwoordelijke, dagenSindsOnderhoud, volgendOnderhoud, site);
            this.machines.add(machine);
            machinedao.insert(machine);
        }
    }

    private String generateSitePrefix(String siteNaam) {
        if (siteNaam.contains("Hoofdzetel")) return "HG";
        if (siteNaam.contains("Productie") && siteNaam.contains("Antwerpen")) return "PA";
        if (siteNaam.contains("Distributie")) return "DB";
        if (siteNaam.contains("R&D")) return "RD";
        if (siteNaam.contains("Logistiek")) return "LK";
        if (siteNaam.contains("Productielijn") && siteNaam.contains("Hasselt")) return "PH";
        if (siteNaam.contains("Assemblage")) return "AM";
        if (siteNaam.contains("Verpakking")) return "VA";
        if (siteNaam.contains("Test")) return "TB";
        if (siteNaam.contains("Onderhoud")) return "OO";
        if (siteNaam.contains("Kwaliteit")) return "KG";
        if (siteNaam.contains("Magazijn")) return "MS";
        if (siteNaam.contains("Amsterdam")) return "AMS";
        if (siteNaam.contains("Rotterdam")) return "RTD";
        if (siteNaam.contains("Eindhoven")) return "EHV";

        String[] woorden = siteNaam.split(" ");
        StringBuilder prefix = new StringBuilder();
        for (String woord : woorden) {
            if (!woord.isEmpty()) {
                prefix.append(Character.toUpperCase(woord.charAt(0)));
            }
            if (prefix.length() >= 3) break;
        }
        return prefix.toString();
    }

    private List<String> getMachineTypesForSite(String siteNaam) {
        List<String> types = new ArrayList<>();

        types.add("Transportband");
        types.add("Controlecamera");
        types.add("Barcodescanner");

        if (siteNaam.contains("Productie") || siteNaam.contains("Hoofdzetel")) {
            types.add("CNC Freesmachine");
            types.add("Draaibank");
            types.add("Hydraulische Pers");
            types.add("Lasrobot");
            types.add("Industriële 3D-Printer");
            types.add("Spuitgietmachine");
            types.add("Extruder");
            types.add("Walsinstallatie");
            types.add("Lasersnijder");
            types.add("Plasmacutter");
        }

        if (siteNaam.contains("Assemblage")) {
            types.add("Robotarm");
            types.add("Precisiemontagelijn");
            types.add("Componentenplaatser");
            types.add("Schroefautomaat");
            types.add("Soldeerstation");
            types.add("Lijmmachine");
            types.add("Testbench");
        }

        if (siteNaam.contains("Verpakking")) {
            types.add("Dozenvouwer");
            types.add("Vulmachine");
            types.add("Etiketteermachine");
            types.add("Krimpfoliemachine");
            types.add("Palletiseermachine");
            types.add("Wikkelaarmachine");
            types.add("Verpakkingsrobot");
        }

        if (siteNaam.contains("Logistiek") || siteNaam.contains("Distributie") || siteNaam.contains("Magazijn")) {
            types.add("Orderpicker");
            types.add("AGV");
            types.add("Magazijnrobot");
            types.add("Sorteermachine");
            types.add("Hoogbouwkraan");
            types.add("Transportrobot");
            types.add("Vorkheftruck");
        }

        if (siteNaam.contains("R&D") || siteNaam.contains("Test")) {
            types.add("Klimaatkamer");
            types.add("Durometer");
            types.add("Trekbank");
            types.add("Proefmachine");
            types.add("Microscoop");
            types.add("Spectrometer");
            types.add("Prototype Printer");
        }

        if (siteNaam.contains("Kwaliteit")) {
            types.add("Röntgenapparaat");
            types.add("Dichtheidstester");
            types.add("Meetmachine");
            types.add("Materiaaltester");
            types.add("Optisch inspectiestaton");
        }

        if (siteNaam.contains("Onderhoud")) {
            types.add("Kalibratieapparaat");
            types.add("Diagnosestation");
            types.add("Hydrometer");
            types.add("Testsetup");
            types.add("Voltmeter");
        }

        if (types.size() < 5) {
            types.add("Multi-tool");
            types.add("Besturingssysteem");
            types.add("Controlepaneel");
            types.add("Universele Testapparatuur");
        }

        return types;
    }

    private List<String> getLocationsForSite(String siteNaam) {
        List<String> locaties = new ArrayList<>();

        locaties.add("Begane Grond");
        locaties.add("Eerste Verdieping");

        if (siteNaam.contains("Productie") || siteNaam.contains("Hoofdzetel")) {
            locaties.add("Hal A");
            locaties.add("Hal B");
            locaties.add("Hal C");
            locaties.add("Machineruimte Noord");
            locaties.add("Machineruimte Zuid");
            locaties.add("Productievloer");
            locaties.add("Werkvloer Sectie 1");
            locaties.add("Werkvloer Sectie 2");
            locaties.add("Werkvloer Sectie 3");
        }

        if (siteNaam.contains("Assemblage")) {
            locaties.add("Montagelijn 1");
            locaties.add("Montagelijn 2");
            locaties.add("Precisiezone");
            locaties.add("Assemblagehal");
            locaties.add("Eindassemblage");
        }

        if (siteNaam.contains("Verpakking")) {
            locaties.add("Inpakhal");
            locaties.add("Verpakkingsafdeling");
            locaties.add("Verzendingsruimte");
            locaties.add("Etikettering");
        }

        if (siteNaam.contains("Logistiek") || siteNaam.contains("Distributie") || siteNaam.contains("Magazijn")) {
            locaties.add("Magazijn A");
            locaties.add("Magazijn B");
            locaties.add("Magazijn C");
            locaties.add("Laaddok 1");
            locaties.add("Laaddok 2");
            locaties.add("Expeditie");
            locaties.add("Goederenontvangst");
        }

        if (siteNaam.contains("R&D") || siteNaam.contains("Test")) {
            locaties.add("Laboratorium");
            locaties.add("Testruimte");
            locaties.add("Ontwikkelingsafdeling");
            locaties.add("Prototypekamer");
            locaties.add("R&D Vleugel");
        }

        if (siteNaam.contains("Kwaliteit")) {
            locaties.add("Kwaliteitscontrole");
            locaties.add("Meetkamer");
            locaties.add("Controleruimte");
        }

        if (siteNaam.contains("Onderhoud")) {
            locaties.add("Werkplaats");
            locaties.add("Reparatieruimte");
            locaties.add("Technische Dienst");
            locaties.add("Onderhoudsdepot");
        }

        if (locaties.size() < 3) {
            locaties.add("Centrale Hal");
            locaties.add("Zuidvleugel");
            locaties.add("Noordvleugel");
        }

        return locaties;
    }

    private String generateProductInfo(String machineType) {
        String[] fabrikanten = {
                "Siemens", "ABB", "Bosch", "Festo", "Kuka", "Fanuc", "Omron", "Schneider Electric",
                "Mitsubishi", "Honeywell", "Emerson", "Rockwell Automation", "Yaskawa", "Danfoss",
                "Beckhoff", "Weidmüller", "Philips", "Sick", "SEW Eurodrive", "Phoenix Contact"
        };

        String[] modelseries = {
                "Pro", "Ultimate", "Advanced", "Premium", "Industrial", "Master", "Expert", "Professional",
                "Standard", "Performance", "Elite", "Superior", "Prime", "Smart", "Precision", "Ultra",
                "Heavy Duty", "Economic", "Compact", "Modular"
        };

        String modelnr = String.valueOf(1000 + (int) (Math.random() * 9000));

        char serie = (char) ('A' + (int) (Math.random() * 26));

        String fabrikant = fabrikanten[(int) (Math.random() * fabrikanten.length)];

        String modelserie = modelseries[(int) (Math.random() * modelseries.length)];

        return machineType + " " + fabrikant + " " + modelserie + " " + modelnr + serie;
    }

    private MachineStatus generateMachineStatus(String siteNaam) {
        double random = Math.random();

        if (siteNaam.contains("Test") || siteNaam.contains("R&D") || siteNaam.contains("Onderhoud")) {
            if (random < 0.4) return MachineStatus.DRAAIT;
            else if (random < 0.55) return MachineStatus.GESTOPT_MANUEEL;
            else if (random < 0.7) return MachineStatus.GESTOPT_AUTO;
            else if (random < 0.9) return MachineStatus.IN_ONDERHOUD;
            else return MachineStatus.STARTBAAR;
        }
        else if (siteNaam.contains("Productie") || siteNaam.contains("Assemblage")) {
            if (random < 0.7) return MachineStatus.DRAAIT;
            else if (random < 0.8) return MachineStatus.GESTOPT_MANUEEL;
            else if (random < 0.9) return MachineStatus.GESTOPT_AUTO;
            else if (random < 0.95) return MachineStatus.IN_ONDERHOUD;
            else return MachineStatus.STARTBAAR;
        }
        else {
            if (random < 0.6) return MachineStatus.DRAAIT;
            else if (random < 0.7) return MachineStatus.GESTOPT_MANUEEL;
            else if (random < 0.8) return MachineStatus.GESTOPT_AUTO;
            else if (random < 0.9) return MachineStatus.IN_ONDERHOUD;
            else return MachineStatus.STARTBAAR;
        }
    }

    private ProductionStatus generateProductionStatus(MachineStatus status, String siteNaam) {
        double random = Math.random();

        if (status == MachineStatus.IN_ONDERHOUD) {
            return random < 0.7 ? ProductionStatus.FALEND : ProductionStatus.NOOD_AAN_ONDERHOUD;
        }
        else if (status == MachineStatus.GESTOPT_AUTO || status == MachineStatus.GESTOPT_MANUEEL) {
            if (random < 0.4) return ProductionStatus.IN_ORDE;
            else if (random < 0.7) return ProductionStatus.NOOD_AAN_ONDERHOUD;
            else return ProductionStatus.FALEND;
        }
        else {
            if (siteNaam.contains("Test") || siteNaam.contains("R&D")) {
                if (random < 0.6) return ProductionStatus.IN_ORDE;
                else if (random < 0.9) return ProductionStatus.NOOD_AAN_ONDERHOUD;
                else return ProductionStatus.FALEND;
            } else {
                if (random < 0.85) return ProductionStatus.IN_ORDE;
                else if (random < 0.95) return ProductionStatus.NOOD_AAN_ONDERHOUD;
                else return ProductionStatus.FALEND;
            }
        }
    }

    private int generateUptime(MachineStatus status, ProductionStatus productieStatus) {
        if (status == MachineStatus.DRAAIT) {
            if (productieStatus == ProductionStatus.IN_ORDE) {
                return 85 + (int) (Math.random() * 16);
            } else if (productieStatus == ProductionStatus.NOOD_AAN_ONDERHOUD) {
                return 60 + (int) (Math.random() * 21);
            } else {
                return 30 + (int) (Math.random() * 21);
            }
        } else if (status == MachineStatus.IN_ONDERHOUD) {
            return 0;
        } else if (status == MachineStatus.GESTOPT_AUTO || status == MachineStatus.GESTOPT_MANUEEL) {
            return 20 + (int) (Math.random() * 21);
        } else {
            return 35 + (int) (Math.random() * 26);
        }
    }

    private Gebruiker chooseMachineResponsible(List<Gebruiker> admins, List<Gebruiker> techniekers, String siteNaam) {
        double random = Math.random();

        if (siteNaam.contains("R&D") || siteNaam.contains("Test")) {
            if (random < 0.7 && !admins.isEmpty()) {
                return admins.get((int) (Math.random() * admins.size()));
            } else if (!techniekers.isEmpty()) {
                return techniekers.get((int) (Math.random() * techniekers.size()));
            }
        }
        else if (siteNaam.contains("Onderhoud") || siteNaam.contains("Productie") || siteNaam.contains("Assemblage")) {
            if (random < 0.8 && !techniekers.isEmpty()) {
                return techniekers.get((int) (Math.random() * techniekers.size()));
            } else if (!admins.isEmpty()) {
                return admins.get((int) (Math.random() * admins.size()));
            }
        }
        else {
            if (random < 0.5 && !techniekers.isEmpty()) {
                return techniekers.get((int) (Math.random() * techniekers.size()));
            } else if (!admins.isEmpty()) {
                return admins.get((int) (Math.random() * admins.size()));
            }
        }

        return !techniekers.isEmpty() ? techniekers.get(0) : admins.get(0);
    }

    private int generateDaysSinceLastMaintenance(MachineStatus status, ProductionStatus productieStatus) {
        if (status == MachineStatus.IN_ONDERHOUD) {
            return 0;
        } else if (productieStatus == ProductionStatus.FALEND) {
            return 60 + (int) (Math.random() * 121);
        } else if (productieStatus == ProductionStatus.NOOD_AAN_ONDERHOUD) {
            return 30 + (int) (Math.random() * 61);
        } else {
            return (int) (Math.random() * 31);
        }
    }

    private LocalDate generateNextMaintenanceDate(MachineStatus status, ProductionStatus productieStatus) {
        LocalDate now = LocalDate.now();

        if (status == MachineStatus.IN_ONDERHOUD) {
            return now.plusMonths(3 + (int) (Math.random() * 4));
        } else if (productieStatus == ProductionStatus.FALEND) {
            return now.minusDays((int) (Math.random() * 31));
        } else if (productieStatus == ProductionStatus.NOOD_AAN_ONDERHOUD) {
            return now.plusDays((int) (Math.random() * 31));
        } else {
            return now.plusMonths(2 + (int) (Math.random() * 5));
        }
    }

    private void createOnderhoud() {
        onderhouddao.startTransaction();
        try {
            if (this.machines.isEmpty() || this.gebruikers.isEmpty()) {
                System.err.println("Geen machines of gebruikers beschikbaar om onderhoud aan te maken.");
                return;
            }

            List<Gebruiker> techniekers = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.TECHNIEKER && g.getActief())
                    .collect(java.util.stream.Collectors.toList());

            if (techniekers.isEmpty()) {
                techniekers.add(this.gebruikers.get(0));
            }

            int totaalAantalOnderhoudRecords = 0;
            int maxOnderhoudRecords = 100;

            int machinesMetOnderhoud = Math.min(30 + (int) (Math.random() * 10), this.machines.size());
            List<Machine> geselecteerdeMachines = new ArrayList<>(this.machines);
            Collections.shuffle(geselecteerdeMachines);
            geselecteerdeMachines = geselecteerdeMachines.subList(0, machinesMetOnderhoud);

            for (Machine machine : geselecteerdeMachines) {
                if (totaalAantalOnderhoudRecords >= maxOnderhoudRecords) {
                    break;
                }

                int beschikbareRecords = maxOnderhoudRecords - totaalAantalOnderhoudRecords;
                int aantalRecords = 1 + (int) (Math.random() * 2);
                aantalRecords = Math.min(aantalRecords, beschikbareRecords);

                if (machine.getProductieStatus() != ProductionStatus.IN_ORDE && aantalRecords < beschikbareRecords) {
                    aantalRecords += 1;
                }

                int gemaakteRecords = createMaintenanceRecordsForMachine(machine, aantalRecords, techniekers);
                totaalAantalOnderhoudRecords += gemaakteRecords;
            }

            onderhouddao.commitTransaction();
            System.out.println("Alle onderhoudsrecords aangemaakt: " + totaalAantalOnderhoudRecords + " records in totaal.");

        } catch (Exception e) {
            System.err.println("Fout bij het aanmaken van onderhoudsrecords: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int createMaintenanceRecordsForMachine(Machine machine, int aantalRecords, List<Gebruiker> techniekers) {
        int generatedRecords = 0;

        int aantalToekomstigeRecords = aantalRecords > 1 ? 1 : 0;
        int aantalHistorischeRecords = aantalRecords - aantalToekomstigeRecords;

        if (aantalHistorischeRecords > 0) {
            generatePastMaintenance(machine, aantalHistorischeRecords, techniekers);
            generatedRecords += aantalHistorischeRecords;
        }

        if (aantalToekomstigeRecords > 0) {
            generateFutureMaintenance(machine, aantalToekomstigeRecords, techniekers);
            generatedRecords += aantalToekomstigeRecords;
        }

        return generatedRecords;
    }

    private void generatePastMaintenance(Machine machine, int aantalRecords, List<Gebruiker> techniekers) {
        LocalDate startDatum = LocalDate.now().minusMonths(12);
        LocalDate eindDatum = LocalDate.now();

        List<LocalDate> datums = generateRandomDates(startDatum, eindDatum, aantalRecords);

        Collections.sort(datums);

        for (LocalDate datum : datums) {
            Gebruiker technieker = getRandomElement(techniekers);
            createMaintenanceRecord(machine, datum, technieker, false);
        }
    }

    private void generateFutureMaintenance(Machine machine, int aantalRecords, List<Gebruiker> techniekers) {
        LocalDate startDatum = LocalDate.now().plusDays(1);
        LocalDate eindDatum = LocalDate.now().plusMonths(3);

        List<LocalDate> datums = generateRandomDates(startDatum, eindDatum, aantalRecords);

        Collections.sort(datums);

        for (LocalDate datum : datums) {
            Gebruiker technieker = getRandomElement(techniekers);
            createMaintenanceRecord(machine, datum, technieker, true);
        }
    }

    private List<LocalDate> generateRandomDates(LocalDate startDatum, LocalDate eindDatum, int aantal) {
        List<LocalDate> datums = new ArrayList<>();
        long dagVerschil = startDatum.until(eindDatum, java.time.temporal.ChronoUnit.DAYS);

        for (int i = 0; i < aantal; i++) {
            long randomDagen = (long) (Math.random() * dagVerschil);
            datums.add(startDatum.plusDays(randomDagen));
        }

        return datums;
    }

    private void createMaintenanceRecord(Machine machine, LocalDate datum, Gebruiker technieker, boolean isToekomstig) {
        String onderhoudType = bepaalOnderhoudType(machine, isToekomstig);

        LocalTime startTijd = generateStartTime();
        LocalTime eindTijd = generateEndTime(startTijd, onderhoudType);

        OnderhoudStatus status;
        if (isToekomstig) {
            status = OnderhoudStatus.IN_UITVOERING;

        } else {
            status = OnderhoudStatus.VOLTOOID;
        }

        String titel = genereerOnderhoudTitel(machine.getNaam(), onderhoudType);
        String resultaat = genereerOnderhoudResultaat(onderhoudType, machine.getProductieStatus(), isToekomstig, status);
        String opmerkingen = genereerOnderhoudOpmerkingen(onderhoudType, machine.getProductieStatus(), isToekomstig);

        Onderhoud onderhoud = new Onderhoud(
                datum,
                startTijd.truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                eindTijd.truncatedTo(java.time.temporal.ChronoUnit.SECONDS),
                technieker.getGebruikerID(),
                titel,
                resultaat,
                opmerkingen,
                status,
                machine.getMachineID()
        );

        onderhouddao.insert(onderhoud);
    }

    private String bepaalOnderhoudType(Machine machine, boolean isToekomstig) {
        List<String> types = Arrays.asList(
                "Routinecontrole",
                "Periodiek onderhoud",
                "Inspectie",
                "Kalibratie",
                "Preventief onderhoud",
                "Software update",
                "Reparatie",
                "Noodreparatie",
                "Vervanging onderdeel",
                "Olie verversen",
                "Smeren",
                "Filter vervangen",
                "Elektrische controle",
                "Hydrauliek controle",
                "Veiligheidstest",
                "Prestatietest",
                "Installatie upgrades"
        );

        if (machine.getProductieStatus() == ProductionStatus.FALEND) {
            if (Math.random() < 0.7) {
                return getRandomElement(Arrays.asList(
                        "Reparatie",
                        "Noodreparatie",
                        "Vervanging onderdeel",
                        "Elektrische controle",
                        "Hydrauliek controle"
                ));
            }
        }

        else if (machine.getProductieStatus() == ProductionStatus.NOOD_AAN_ONDERHOUD) {
            if (Math.random() < 0.6) {
                return getRandomElement(Arrays.asList(
                        "Periodiek onderhoud",
                        "Inspectie",
                        "Preventief onderhoud",
                        "Kalibratie",
                        "Olie verversen",
                        "Smeren"
                ));
            }
        }

        return getRandomElement(types);
    }

    private LocalTime generateStartTime() {
        int uur = 7 + (int) (Math.random() * 10);
        int minuut = ((int) (Math.random() * 4)) * 15;
        return LocalTime.of(uur, minuut).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    }

    private LocalTime generateEndTime(LocalTime startTijd, String onderhoudType) {
        int duurMinuten;

        switch (onderhoudType.toLowerCase()) {
            case "routinecontrole":
            case "inspectie":
                duurMinuten = 30 + (int) (Math.random() * 90);
                break;
            case "kalibratie":
            case "software update":
            case "olie verversen":
            case "smeren":
            case "filter vervangen":
                duurMinuten = 60 + (int) (Math.random() * 120);
                break;
            case "periodiek onderhoud":
            case "preventief onderhoud":
            case "elektrische controle":
            case "hydrauliek controle":
            case "veiligheidstest":
            case "prestatietest":
                duurMinuten = 120 + (int) (Math.random() * 180);
                break;
            case "reparatie":
            case "vervanging onderdeel":
            case "installatie upgrades":
                duurMinuten = 180 + (int) (Math.random() * 300);
                break;
            case "noodreparatie":
                duurMinuten = 240 + (int) (Math.random() * 600);
                break;
            default:
                duurMinuten = 120 + (int) (Math.random() * 180);
        }

        return startTijd.plusMinutes(duurMinuten).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    }

    private String genereerOnderhoudTitel(String machineNaam, String onderhoudType) {
        String machineCode = machineNaam;
        int spaceIndex = machineNaam.indexOf(' ');
        if (spaceIndex > 0) {
            machineCode = machineNaam.substring(0, spaceIndex);
        } else if (machineNaam.length() > 10) {
            machineCode = machineNaam.substring(0, 10);
        }

        return onderhoudType + " " + machineCode;
    }

    private String genereerOnderhoudResultaat(String onderhoudType, ProductionStatus status,
                                              boolean isToekomstig, OnderhoudStatus onderhoudStatus) {
        if (isToekomstig) {
            if (onderhoudStatus == OnderhoudStatus.IN_UITVOERING) {
                return "In uitvoering";
            } else {
                return "Gepland";
            }
        }

        if (status == ProductionStatus.FALEND) {
            if (onderhoudType.toLowerCase().contains("reparatie") ||
                    onderhoudType.toLowerCase().contains("nood") ||
                    onderhoudType.toLowerCase().contains("vervang")) {
                return getRandomElement(Arrays.asList(
                        "Tijdelijk gerepareerd",
                        "Kritieke problemen opgelost",
                        "Noodreparatie uitgevoerd",
                        "Defecte onderdelen vervangen",
                        "Gedeeltelijk hersteld"
                ));
            } else {
                return getRandomElement(Arrays.asList(
                        "Meerdere problemen geconstateerd",
                        "Kritieke punten geïdentificeerd",
                        "Verdere reparatie nodig",
                        "Niet optimaal functionerend",
                        "Afwijkende waardes gemeten"
                ));
            }
        } else if (status == ProductionStatus.NOOD_AAN_ONDERHOUD) {
            return getRandomElement(Arrays.asList(
                    "Slijtage geconstateerd",
                    "Onderhoudspunten geïdentificeerd",
                    "Normale slijtage verholpen",
                    "Prestaties verbeterd",
                    "Klein defect verholpen"
            ));
        } else {
            return getRandomElement(Arrays.asList(
                    "Volledig uitgevoerd",
                    "Normaal resultaat",
                    "Alles in orde",
                    "Geen problemen gevonden",
                    "Optimale conditie"
            ));
        }
    }

    private String genereerOnderhoudOpmerkingen(String onderhoudType, ProductionStatus status, boolean isToekomstig) {
        if (isToekomstig) {
            if (status == ProductionStatus.FALEND) {
                return getRandomElement(Arrays.asList(
                        "Geplande reparatie van kritieke componenten. Reserveonderdelen besteld.",
                        "Noodreparatie ingepland wegens aanhoudende problemen met aandrijving.",
                        "Complete revisie vereist. Technisch team met specialisatie ingepland.",
                        "Vervanging van defecte besturingselementen en kalibratie van sensoren.",
                        "Geplande correctie van meerdere kritieke problemen. Machine zal enkele dagen offline zijn."
                ));
            } else if (status == ProductionStatus.NOOD_AAN_ONDERHOUD) {
                return getRandomElement(Arrays.asList(
                        "Periodiek onderhoud ingepland. Focus op versleten componenten.",
                        "Preventieve vervanging van slijtage-onderdelen. Normale downtime verwacht.",
                        "Kalibratie van belangrijke componenten en controle van algemene conditie.",
                        "Technicus zal meerdere systemen controleren op basis van laatste inspectierapport.",
                        "Standaard onderhoudsbeurt met extra aandacht voor hydraulieksysteem."
                ));
            } else {
                return getRandomElement(Arrays.asList(
                        "Routinematige controle en afstelling volgens onderhoudsschema.",
                        "Periodiek onderhoud volgens fabrieksspecificaties.",
                        "Update van besturingssoftware naar nieuwste versie.",
                        "Standaard inspectie en preventief onderhoud van alle systeemcomponenten.",
                        "Geplande kalibratie en algemene controle. Minimale downtime verwacht."
                ));
            }
        }

        if (status == ProductionStatus.FALEND) {
            if (onderhoudType.toLowerCase().contains("reparatie") ||
                    onderhoudType.toLowerCase().contains("nood") ||
                    onderhoudType.toLowerCase().contains("vervang")) {
                return getRandomElement(Arrays.asList(
                        "Hoofdmotor vervangen na totale uitval. Lagers en aandrijfas vertoonden ernstige slijtage. Overige componenten gecontroleerd en gesmeerd.",
                        "Noodreparatie aan hydraulisch systeem uitgevoerd. Meerdere lekkages gedicht en defecte klep vervangen. Druk nu stabiel maar monitoring vereist.",
                        "Besturingssysteem gerepareerd na elektrisch defect. Meerdere sensoren vervangen en bedrading vernieuwd waar nodig. Systeem herstart en gekalibreerd.",
                        "Grote scheuren in het frame gerepareerd door lassen en verstevigen. Structurele integriteit tijdelijk hersteld. Complete revisie aanbevolen binnen 3 maanden.",
                        "Kritieke oververhitting verholpen door reiniging koelsysteem en vervanging van koelvloeistof. Temperatuursensoren opnieuw afgesteld. Extra koeling geïnstalleerd."
                ));
            } else {
                return getRandomElement(Arrays.asList(
                        "Inspectie toont ernstige slijtage van meerdere kerncomponenten. Gedetailleerd rapport opgesteld met aanbevelingen voor directe reparatie.",
                        "Algehele toestand van machine is kritiek. Meerdere systemen functioneren onder minimale specificaties. Productielimieten aanbevolen.",
                        "Kalibratie niet mogelijk wegens mechanische problemen. Sensoren geven onbetrouwbare metingen. Mechanische reparatie vereist voor verdere afstelling.",
                        "Controle toont dat eerder gerepareerde componenten opnieuw falen. Structureel probleem geïdentificeerd in het ontwerp. Leverancier gecontacteerd.",
                        "Test resulteerde in onmiddellijke shutdown wegens veiligheidsrisico's. Elektrische bedrading vertoont tekenen van oververhitting en sluiting."
                ));
            }
        } else if (status == ProductionStatus.NOOD_AAN_ONDERHOUD) {
            return getRandomElement(Arrays.asList(
                    "Normale slijtage geconstateerd aan transportbanden. Spanning opnieuw afgesteld. Vervanging binnen 6 maanden aanbevolen.",
                    "Hydraulisch systeem functioneert binnen parameters maar olieniveau laag. Bijgevuld en filters vervangen. Kleine lekkage geïdentificeerd.",
                    "Mechanische delen vertonen verwachte slijtage. Alles gesmeerd en afgesteld. Aanbevolen om lagers te controleren bij volgend onderhoud.",
                    "Prestaties licht onder optimaal niveau. Software parameters aangepast voor betere efficiëntie. Monitoring aanbevolen.",
                    "Routinecontrole toont enkele afwijkingen binnen acceptabele grenzen. Sensoren opnieuw gekalibreerd. Systeem getest en functioneel."
            ));
        } else {
            return getRandomElement(Arrays.asList(
                    "Algemene conditie is uitstekend. Alle systemen functioneren volgens specificaties. Routine onderhoud uitgevoerd.",
                    "Preventief onderhoud compleet. Filters vervangen, systemen gesmeerd, software gecontroleerd. Geen afwijkingen gevonden.",
                    "Kalibratie succesvol. Alle parameters vallen binnen optimale bereik. Testrun uitgevoerd met uitstekende resultaten.",
                    "Inspectie toont geen abnormale slijtage. Machine in perfecte staat. Reguliere onderhoudswerkzaamheden uitgevoerd.",
                    "Software update geïnstalleerd. Nieuwe functies getest en werkend. Gebruikersdocumentatie bijgewerkt en beschikbaar gemaakt."
            ));
        }
    }

    private <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }

    private void createLogs() {
        int totalLogs = 0;
        try {
            logdao.startTransaction();

            if (this.gebruikers.isEmpty()) {
                System.err.println("Geen gebruikers beschikbaar om logs aan te maken.");
                return;
            }

            List<Gebruiker> admins = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.ADMINISTRATOR)
                    .collect(java.util.stream.Collectors.toList());

            List<Gebruiker> verantwoordelijken = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.VERANTWOORDELIJKE)
                    .collect(java.util.stream.Collectors.toList());

            List<Gebruiker> techniekers = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.TECHNIEKER)
                    .collect(java.util.stream.Collectors.toList());

            List<Gebruiker> managers = this.gebruikers.stream()
                    .filter(g -> g.getRol() == Rol.MANAGER)
                    .collect(java.util.stream.Collectors.toList());

            for (Gebruiker admin : admins) {
                if (Math.random() < 0.8) {
                    Log loginLog = new Log(admin, "Login", "Admin login op " + LocalDateTime.now().minusDays((int) (Math.random() * 14)).truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
                    logdao.insert(loginLog);
                    totalLogs++;

                    if (Math.random() < 0.7) {
                        Log gebruikerLog = new Log(admin, "Gebruikersbeheer", "Nieuwe gebruiker toegevoegd: " + getRandomElement(this.gebruikers).getEmail());
                        logdao.insert(gebruikerLog);
                        totalLogs++;
                    }

                    if (Math.random() < 0.6) {
                        Log machineLog = new Log(admin, "Machineconfiguratie", "Instellingen bijgewerkt voor machine " + getRandomElement(this.machines).getNaam());
                        logdao.insert(machineLog);
                        totalLogs++;
                    }
                }
            }

            for (Gebruiker verantwoordelijke : verantwoordelijken) {
                if (Math.random() < 0.7) {
                    Log loginLog = new Log(verantwoordelijke, "Login", "Verantwoordelijke login op " + LocalDateTime.now().minusDays((int) (Math.random() * 10)).truncatedTo(ChronoUnit.SECONDS));
                    logdao.insert(loginLog);
                    totalLogs++;

                    if (Math.random() < 0.6) {
                        Log rapportLog = new Log(verantwoordelijke, "Rapport bekeken", "Maandelijks onderhoudsrapport gecontroleerd");
                        logdao.insert(rapportLog);
                        totalLogs++;
                    }
                }
            }

            for (Gebruiker technieker : techniekers) {
                if (Math.random() < 0.9) {
                    Log loginLog = new Log(technieker, "Login", "Technieker login op " + LocalDateTime.now().minusDays((int) (Math.random() * 10)).truncatedTo(ChronoUnit.SECONDS));
                    logdao.insert(loginLog);
                    totalLogs++;

                    if (Math.random() < 0.8) {
                        Machine machine = getRandomElement(this.machines);
                        Log onderhoudLog = new Log(technieker, "Onderhoud uitgevoerd",
                                "Routine onderhoud op " + machine.getNaam() + " afgerond. " + getRandomOnderhoudOpmerkingen(false));
                        logdao.insert(onderhoudLog);
                        totalLogs++;
                    }

                    if (Math.random() < 0.4) {
                        Log storingLog = new Log(technieker, "Storing verholpen",
                                "Storing opgelost op " + getRandomElement(this.machines).getNaam() + ". " + getRandomOnderhoudOpmerkingen(true));
                        logdao.insert(storingLog);
                        totalLogs++;
                    }
                }
            }

            for (Gebruiker manager : managers) {
                if (Math.random() < 0.6) {
                    Log loginLog = new Log(manager, "Login", "Manager login op " + LocalDateTime.now().minusDays((int) (Math.random() * 5)).truncatedTo(ChronoUnit.SECONDS));
                    logdao.insert(loginLog);
                    totalLogs++;

                    if (Math.random() < 0.5) {
                        Log rapportLog = new Log(manager, "Rapport geëxporteerd", "Productierapport geëxporteerd voor analyse");
                        logdao.insert(rapportLog);
                        totalLogs++;
                    }
                }
            }

            if (!admins.isEmpty()) {
                Gebruiker adminUser = getRandomElement(admins);

                Log updateLog = new Log(adminUser, "Systeemupdate", "Software update v2.1 geïnstalleerd");
                logdao.insert(updateLog);
                totalLogs++;

                Log backupLog = new Log(adminUser, "Database backup", "Automatische backup uitgevoerd");
                logdao.insert(backupLog);
                totalLogs++;

                Log securityLog = new Log(adminUser, "Security alert", "Meerdere mislukte inlogpogingen gedetecteerd van IP 192.168.1.35");
                logdao.insert(securityLog);
                totalLogs++;
            }

            logdao.commitTransaction();
            System.out.println("Alle logs aangemaakt: " + totalLogs + " logs in totaal.");

        } catch (Exception e) {
            logdao.rollbackTransaction();
            System.err.println("Fout bij het aanmaken van logs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getRandomOnderhoudOpmerkingen(boolean isStoring) {
        if (isStoring) {
            return getRandomElement(Arrays.asList(
                    "Defecte sensor vervangen en systeem opnieuw gekalibreerd.",
                    "Elektrisch probleem opgelost door bedrading te herstellen.",
                    "Vastgelopen componenten losgemaakt en gesmeerd.",
                    "Software reset uitgevoerd na kritieke fout.",
                    "Hydraulisch lek gedicht en vloeistof bijgevuld."
            ));
        } else {
            return getRandomElement(Arrays.asList(
                    "Alle systemen functioneren volgens specificaties.",
                    "Filters vervangen en systeem gereinigd.",
                    "Software geüpdatet naar nieuwste versie.",
                    "Kalibratie uitgevoerd met optimale resultaten.",
                    "Preventief onderhoud volledig afgerond."
            ));
        }
    }

    public static void main(String[] args) {
        PopulateDB populator = new PopulateDB();
        populator.run();
    }
}