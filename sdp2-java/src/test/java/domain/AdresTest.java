package domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdresTest {

    @Test
    public void testAdresCreationAndToString() {
        Adres adres = new Adres("Teststraat", "123", "9000", "Gent", "België");
        Assertions.assertEquals("Teststraat", adres.getStraat());
        Assertions.assertEquals("123", adres.getHuis_nr());
        Assertions.assertEquals("9000", adres.getPostcode());
        Assertions.assertEquals("Gent", adres.getStad());
        Assertions.assertEquals("België", adres.getLand());
        Assertions.assertEquals("Teststraat 123, 9000 Gent België", adres.toString());
    }

    @Test
    public void testAdresWithNullValuesToString() {
        Adres adres = new Adres(null, "123", null, "Gent", null);
        Assertions.assertEquals("null 123, null Gent null", adres.toString());
    }

    @Test
    public void testAdresWithEmptyValuesToString() {
        Adres adres = new Adres("", "123", "", "Gent", "");
        Assertions.assertEquals(" 123,  Gent ", adres.toString());
    }
}
