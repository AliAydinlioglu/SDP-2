package domein;

import java.util.Date;

public class Onderhoud {
    private Date datum;
    private String reden;
    private String technieker;

    public Onderhoud(String reden, String technieker) {
        this.datum = new Date();
        this.reden = reden;
        this.technieker = technieker;
    }
}
