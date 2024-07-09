package exception;

/**
 * Eksepsn koja se baca kada se pokusa dodati prostorija koja vec postoji
 */
public class ProstorijaVecPostoji extends Exception{
    String message = "Prostorija vec postoji!";

    @Override
    public String getMessage() {
        return message;
    }
}
