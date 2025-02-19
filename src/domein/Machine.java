package domein;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private String code;
    private String locatie;
    private List<Onderhoud> onderhoudsHistoriek = new ArrayList<>();

    public Machine(String code, String locatie) {
        this.code = code;
        this.locatie = locatie;
    }

    public void voegOnderhoudToe(String reden, String technieker) {
        onderhoudsHistoriek.add(new Onderhoud(reden, technieker));
    }

    public void onderhoudUitvoeren() {
    }
}
