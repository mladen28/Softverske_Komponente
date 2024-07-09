package exception;

/**
 * Eksepsn koja se baca kada se pokusa dodati termin koji je zauzet
 */
public class TerminZauzetException extends Exception{
    String message = "Termin je zauzet. Izvinjavamo se!";

    @Override
    public String getMessage() {
        return message;
    }
}

