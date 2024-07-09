package exception;

/**
 * Eksepsn koja se baca kada se pokusa obrisati prostorija koja ne postoji
 */
public class NePostojiProstorija extends Exception{
    String message = "Ne postoji prostorija koju pokusavate da obrisete!";

    @Override
    public String getMessage() {
        return message;
    }
}
