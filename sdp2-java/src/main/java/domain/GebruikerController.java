package domain;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import dto.GebruikerDTO;
import enums.Rol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import repository.GebruikerDaoJpa;
import repository.GebruikerDao;
import utils.AlertHelper;

public class GebruikerController {

	private GebruikerDao gebruikerRepo;
	
	private List<Gebruiker> data;
	private ObservableList<GebruikerDTO> gebruikerList;
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
	
	public GebruikerController(GebruikerDao gebruikerRepo) { //voor mockito
		//new PopulateDB().run();
		this.gebruikerRepo = gebruikerRepo;		
	}
	
	private void initData() {
		try {
            
            data = gebruikerRepo.findAll();
        } catch (Exception e) {
            AlertHelper.showError("Connectie met databank mislukt", e.getMessage());     
        }
		
		gebruikerList = FXCollections.observableArrayList(data.stream()
				.map(GebruikerDTO::fromEntity)
				.collect(Collectors.toList()));
		filteredGebruikerList = new FilteredList<>(gebruikerList, p -> true);
		sortedGebruikerList = new SortedList<>(filteredGebruikerList, sortOrder);
	}
	
	
	public GebruikerDTO getGebruiker(int id) {
		Gebruiker g = gebruikerRepo.get(id);
		return GebruikerDTO.fromEntity(g);
	}
	
	protected Gebruiker getRealGebruiker(int id) {
		return gebruikerRepo.get(id);
	}
	
	public ObservableList<GebruikerDTO> findAll(){
		if(data == null) initData();
		return sortedGebruikerList;
	}
	
	public GebruikerDTO getGebruikerByEmailDTO(String email)
	{
		Gebruiker g = gebruikerRepo.getGebruikerByEmail(email);
		return GebruikerDTO.fromEntity(g);

	}
	private Gebruiker getGebruikerByEmail(String email)
	{
		return gebruikerRepo.getGebruikerByEmail(email);

	}
	
	public void addGebruiker(String naam, String voornaam, LocalDate geboortedatum, String straat, String huisNr, String postcode, String stad, String land, String email, String gsm, Rol rol, boolean actief) {
    	

		try {
			Gebruiker g = Gebruiker.builder().
					voornaam(voornaam).
					achternaam(naam).
					geboorteDatum(geboortedatum).
					adres(Adres.builder().straat(straat).huis_nr(huisNr).stad(stad).land(land).postcode(postcode).build()).
					email(email).
					rol(rol).
					gsm(gsm).
					actief(actief).build();
			
	        gebruikerRepo.startTransaction();
	        gebruikerRepo.insert(g);
	        gebruikerRepo.commitTransaction();
	        gebruikerList.add(GebruikerDTO.fromEntity(g));
	        data.add(g);
	    } catch (Exception e) {
	    	gebruikerRepo.rollbackTransaction();
	    	throw new IllegalArgumentException(e.getMessage());
	    }
	}

	
	public void changeFilter(String filterValue, Rol rol, Boolean actiefChecked, Boolean nonActiefChecked) {
	    filteredGebruikerList.setPredicate(person -> {
	        boolean matchesText = true;
	        boolean matchesRole = true;
	        boolean matchesActief = true;

	        // Filter de string
	        if (filterValue != null && !filterValue.isBlank()) {
	            String lowerCaseValue = filterValue.toLowerCase();
	            matchesText = person.voornaam().toLowerCase().contains(lowerCaseValue)
	                    || person.achternaam().toLowerCase().contains(lowerCaseValue)
	                    || person.email().toLowerCase().contains(lowerCaseValue);
	        }

	        // Filter de rol
	        if (rol != null) {
	            matchesRole = person.rol() == rol;
	        }

	        // Filter actief status
	        if (actiefChecked) {
	            matchesActief = person.actief(); // Only show actief=true
	        } else if (nonActiefChecked) {
	            matchesActief = !person.actief(); // Only show actief=false
	        }

	        return matchesText && matchesRole && matchesActief;
	    });
	}

	
	public void removeGebruiker(GebruikerDTO gebruiker) {
		for(int i = 0; i < gebruikerList.size(); i++) {
			if(data.get(i).getGebruikerID() == gebruiker.id()) {
				try {
					data.get(i).setActief(false);
					gebruikerRepo.startTransaction();
					gebruikerRepo.update(data.get(i));
					gebruikerRepo.commitTransaction();
					gebruikerList.remove(gebruikerList.get(i));
					data.remove(i);
					return;
				} catch (Exception e) {
					gebruikerRepo.rollbackTransaction();
					throw new IllegalArgumentException("Gebruiker kon niet worden verwijdert: " + e.getMessage());
				}
			}
		}
	}
	
	public GebruikerDTO login(String email, String wachtwoord) {
		Gebruiker g = getGebruikerByEmail(email);
		
		if(g == null || !g.checkWachtwoord(email, wachtwoord)) {
			throw new IllegalArgumentException("Ongeldige email of wachtwoord");
		}
		return GebruikerDTO.fromEntity(g);
		
		
	}

	public void updateGebruiker(GebruikerDTO bewerkteDTO) {
	    Gebruiker g = data.stream()
	        .filter(e -> e.getGebruikerID() == bewerkteDTO.id())
	        .findFirst()
	        .orElse(null);
	    if (g == null) return;

	    int index = data.indexOf(g);

	    g.setVoornaam(bewerkteDTO.voornaam());
	    g.setAchternaam(bewerkteDTO.achternaam());
	    g.setGeboorteDatum(bewerkteDTO.geboortedatum());
	    g.setAdres(Adres.builder()
	            .straat(bewerkteDTO.adres().straat())
	            .huis_nr(bewerkteDTO.adres().huis_nr())
	            .postcode(bewerkteDTO.adres().postcode())
	            .stad(bewerkteDTO.adres().stad())
	            .land(bewerkteDTO.adres().land())
	            .build());
	    g.setEmail(bewerkteDTO.email());
	    g.setGsm(bewerkteDTO.gsm());
	    g.setRol(bewerkteDTO.rol());
	    g.setActief(bewerkteDTO.actief());

	    try {
	    	gebruikerRepo.startTransaction();
		    gebruikerRepo.update(g);
		    gebruikerRepo.commitTransaction();

		    data.set(index, g);
		    GebruikerDTO gg = gebruikerList.stream().filter(e -> e.id() == bewerkteDTO.id()).findFirst().orElse(null);
		    gebruikerList.set(gebruikerList.indexOf(gg), bewerkteDTO);
			
		} catch (Exception e2) {
			gebruikerRepo.rollbackTransaction();
			throw new IllegalArgumentException("Gebruiker kon niet aangepas worden: " + e2.getMessage());
		}
	    

	}

}
