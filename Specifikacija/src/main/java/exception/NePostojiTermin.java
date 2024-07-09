package exception;

/**
 * Eksepsn koja se baca kada se pokusa obrisati termin koji ne postoji
 */
public class NePostojiTermin extends Exception {
    String message = "Ne postoji termin koji pokusavate da obrisete!";

    @Override
    public String getMessage() {
        return message;
    }
}
