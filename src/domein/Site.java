package domein;

import java.util.ArrayList;
import java.util.List;

public class Site {
    private String naam;
    private String adres;
    private List<Machine> machines = new ArrayList<>();

    public Site(String naam, String adres) {
        this.naam = naam;
        this.adres = adres;
    }

    public void voegMachineToe(String code, String locatie) {
        machines.add(new Machine(code, locatie));
    }

}
