package domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.GebruikerDaoJpa;

public class GebruikerController {

	private GebruikerDaoJpa gebruikerDaoJpa;
	
	private ObservableList<Gebruiker> gebruikerList;
	private FilteredList<Gebruiker> filteredGebruikerList;
	
	private SortedList<Gebruiker> sortedGebruikerList;
	
	private final Comparator<Gebruiker> byFirstName = (p1, p2)
            -> p1.getVoornaam().compareToIgnoreCase(p2.getVoornaam());

    private final Comparator<Gebruiker> byLastName = (p1, p2)
            -> p1.getAchternaam().compareToIgnoreCase(p2.getAchternaam());

    private final Comparator<Gebruiker> byEmail = (p1, p2)
            -> p1.getEmail().compareToIgnoreCase(p2.getEmail());

    private final Comparator<Gebruiker> sortOrder = byFirstName.thenComparing(byLastName).
            thenComparing(byEmail);
    
    private static List<Gebruiker> data;
	
	public GebruikerController() {
		//new PopulateDB().run();
		gebruikerDaoJpa = new GebruikerDaoJpa();
		data = gebruikerDaoJpa.findAll();
		
		gebruikerList = FXCollections.observableArrayList(data);
		filteredGebruikerList = new FilteredList<>(gebruikerList, p -> true);
		sortedGebruikerList = new SortedList<>(filteredGebruikerList, sortOrder);
		
	}
	
	
	public Gebruiker getGebruiker(int id) {
		return gebruikerDaoJpa.get(id);
	}
	
	public ObservableList<Gebruiker> getAll(){
		return sortedGebruikerList;
	}
	
	public Gebruiker getGebruikerByEmail(String email)
	{
		return gebruikerDaoJpa.getGebruikerByEmail(email);
	}
	
	public void addGebruiker(String naam, String voornaam, LocalDate geboortedatum, String straat, String huisNr, String postcode, String stad, String land, String email, String gsm, Rol rol) {
		
		Gebruiker gebruiker = new Gebruiker(naam, voornaam, geboortedatum, new Adres(straat, huisNr, stad, land, postcode), email, gsm, rol);
		
		gebruikerDaoJpa.startTransaction();
		gebruikerDaoJpa.insert(gebruiker);
		gebruikerDaoJpa.commitTransaction();
		gebruikerList.add(gebruiker);
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
            return person.getVoornaam().toLowerCase().contains(lowerCaseValue)
                    || person.getAchternaam().toLowerCase().contains(lowerCaseValue);
        }
        );
    }
}
