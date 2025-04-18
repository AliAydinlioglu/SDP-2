package domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.GebruikerDaoJpa;
import repository.GebruikerDoa;

public class GebruikerController {

	private GebruikerDoa gebruikerRepo;
	
	private ObservableList<Gebruiker> gebruikerList;
	private FilteredList<GebruikerDTO> filteredGebruikerList;
	
	private SortedList<GebruikerDTO> sortedGebruikerList;
	
	private final Comparator<GebruikerDTO> byFirstName = (p1, p2)
            -> p1.voornaam().compareToIgnoreCase(p2.voornaam());

    private final Comparator<GebruikerDTO> byLastName = (p1, p2)
            -> p1.achternaam().compareToIgnoreCase(p2.achternaam());

    private final Comparator<GebruikerDTO> byEmail = (p1, p2)
            -> p1.email().compareToIgnoreCase(p2.email());

    private final Comparator<GebruikerDTO> sortOrder = byFirstName.thenComparing(byLastName).
            thenComparing(byEmail);
    
	
	public GebruikerController() {
		// new PopulateDB().run();
		gebruikerRepo = new GebruikerDaoJpa();		
	}
	
	public GebruikerController(GebruikerDoa gebruikerRepo) { //voor mockito
		//new PopulateDB().run();
		this.gebruikerRepo = gebruikerRepo;		
	}
	
	private void initData() {
		try {
            
            gebruikerList = FXCollections.observableArrayList(gebruikerRepo.findAll().stream()
            		.filter(Gebruiker::getActief)
            		
            		.collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            gebruikerList = FXCollections.observableArrayList(gebruikerRepo.findAll().stream()
            		.filter(Gebruiker::getActief)
            		.collect(Collectors.toList()));        
        }
		//gebruikerList = FXCollections.observableArrayList(gebruikerRepo.findAll());
		ObservableList<GebruikerDTO> gebruikerDTOs = FXCollections.observableArrayList(gebruikerList.stream()
				.map(g -> new GebruikerDTO(g.getGebruikerID(), g.getVoornaam(), g.getAchternaam(), g.getGeboorteDatum(), g.getAdres(), g.getEmail(), g.getGsm(), g.getRol(), g.getActief()))
				.collect(Collectors.toList()));
		filteredGebruikerList = new FilteredList<>(gebruikerDTOs, p -> true);
		sortedGebruikerList = new SortedList<>(filteredGebruikerList, sortOrder);
	}
	
	
	public GebruikerDTO getGebruiker(int id) {
		Gebruiker g = gebruikerRepo.get(id);
		return new GebruikerDTO(g.getGebruikerID(), g.getVoornaam(), g.getAchternaam(), g.getGeboorteDatum(), g.getAdres(), g.getEmail(), g.getGsm(), g.getRol(), g.getActief());
	}
	
	public ObservableList<GebruikerDTO> findAll(){
		if(gebruikerList == null) initData();
		return sortedGebruikerList;
	}
	
	public GebruikerDTO getGebruikerByEmailDTO(String email)
	{
		Gebruiker g = gebruikerRepo.getGebruikerByEmail(email);
		return new GebruikerDTO(g.getGebruikerID(), g.getVoornaam(), g.getAchternaam(), g.getGeboorteDatum(), g.getAdres(), g.getEmail(), g.getGsm(), g.getRol(), g.getActief());

	}
	private Gebruiker getGebruikerByEmail(String email)
	{
		return gebruikerRepo.getGebruikerByEmail(email);

	}
	
	public void addGebruiker(String naam, String voornaam, LocalDate geboortedatum, String straat, String huisNr, String postcode, String stad, String land, String email, String gsm, Rol rol) {
		
		Gebruiker g = new Gebruiker(naam, voornaam, geboortedatum, new Adres(straat, huisNr, stad, land, postcode), email, gsm, rol);
		
		gebruikerRepo.startTransaction();
		gebruikerRepo.insert(g);
		gebruikerRepo.commitTransaction();
		gebruikerList.add(g);
	}
	
	public void changeFilter(String filterValue) {
        filteredGebruikerList.setPredicate(person -> {
            // If filter text is empty, display all persons.
            if (filterValue == null || filterValue.isBlank()) {
                return true;
            }
            // Compare first name and last name of every person with   
            //filter text.
            String lowerCaseValue = filterValue.toLowerCase();
            return person.voornaam().toLowerCase().contains(lowerCaseValue)
                    || person.achternaam().toLowerCase().contains(lowerCaseValue);
        }
        );
    }
	
	public void removeGebruiker(GebruikerDTO gebruiker) {
		for(int i = 0; i < gebruikerList.size(); i++) {
			if(gebruikerList.get(i).getGebruikerID() == gebruiker.id()) {
				gebruikerList.get(i).setActief(false);
				gebruikerRepo.startTransaction();
				gebruikerRepo.update(gebruikerList.get(i));
				gebruikerRepo.commitTransaction();
				gebruikerList.remove(gebruikerList.get(i));
				return;
			}
		}
	}
	
	public GebruikerDTO login(String email, String wachtwoord) {
		Gebruiker g = getGebruikerByEmail(email);
		
		if(g == null || !g.checkWachtwoord(email, wachtwoord)) {
			throw new IllegalArgumentException("Ongeldige email of wachtwoord");
		}
		return new GebruikerDTO(g.getGebruikerID(), g.getVoornaam(), g.getAchternaam(), g.getGeboorteDatum(), g.getAdres(), g.getEmail(), g.getGsm(), g.getRol(), g.getActief());
		
		
	}
}
