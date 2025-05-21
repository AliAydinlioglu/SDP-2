package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AdresTest {

    private final String STRAAT = "Teststraat";
    private final String HUIS_NR = "123B";
    private final String POSTCODE = "9000";
    private final String STAD = "Gent";
    private final String LAND = "België";

    @Test
    void constructor_AllArgsConstructor_InitializesFieldsCorrectly() {
        Adres adres = new Adres(STRAAT, HUIS_NR, POSTCODE, STAD, LAND);

        assertEquals(STRAAT, adres.getStraat());
        assertEquals(HUIS_NR, adres.getHuis_nr());
        assertEquals(POSTCODE, adres.getPostcode());
        assertEquals(STAD, adres.getStad());
        assertEquals(LAND, adres.getLand());
    }

    @Test
    void builder_ValidData_CreatesAdresCorrectly() {
        Adres adres = Adres.builder()
                .straat(STRAAT)
                .huis_nr(HUIS_NR)
                .postcode(POSTCODE)
                .stad(STAD)
                .land(LAND)
                .build();

        assertNotNull(adres);
        assertEquals(STRAAT, adres.getStraat());
        assertEquals(HUIS_NR, adres.getHuis_nr());
        assertEquals(POSTCODE, adres.getPostcode());
        assertEquals(STAD, adres.getStad());
        assertEquals(LAND, adres.getLand());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  " })
    void builder_InvalidStraat_ThrowsIllegalArgumentException(String invalidStraat) {
        Adres.Builder builder = Adres.builder()
                .huis_nr(HUIS_NR)
                .postcode(POSTCODE)
                .stad(STAD)
                .land(LAND);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.straat(invalidStraat));
        assertEquals("Straat mag niet leeg zijn", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  " })
    void builder_InvalidHuisNr_ThrowsIllegalArgumentException(String invalidHuisNr) {
        Adres.Builder builder = Adres.builder()
                .straat(STRAAT)
                .postcode(POSTCODE)
                .stad(STAD)
                .land(LAND);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.huis_nr(invalidHuisNr));
        assertEquals("Huisnummer mag niet leeg zijn", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  " })
    void builder_InvalidPostcode_ThrowsIllegalArgumentException(String invalidPostcode) {
        Adres.Builder builder = Adres.builder()
                .straat(STRAAT)
                .huis_nr(HUIS_NR)
                .stad(STAD)
                .land(LAND);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.postcode(invalidPostcode));
        assertEquals("Postcode mag niet leeg zijn", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  " })
    void builder_InvalidStad_ThrowsIllegalArgumentException(String invalidStad) {
        Adres.Builder builder = Adres.builder()
                .straat(STRAAT)
                .huis_nr(HUIS_NR)
                .postcode(POSTCODE)
                .land(LAND);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.stad(invalidStad));
        assertEquals("Stad mag niet leeg zijn", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  " })
    void builder_InvalidLand_ThrowsIllegalArgumentException(String invalidLand) {
        Adres.Builder builder = Adres.builder()
                .straat(STRAAT)
                .huis_nr(HUIS_NR)
                .postcode(POSTCODE)
                .stad(STAD);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.land(invalidLand));
        assertEquals("Land mag niet leeg zijn", exception.getMessage());
    }

    @Test
    void toString_ReturnsCorrectFormat() {
        Adres adres = new Adres(STRAAT, HUIS_NR, POSTCODE, STAD, LAND);
        String expectedString = String.format("%s %s, %s %s %s", STRAAT, HUIS_NR, POSTCODE, STAD, LAND);
        assertEquals(expectedString, adres.toString());
    }
}
