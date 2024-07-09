package exception;

/**
 * Eksepsn koja se baca kada se pokusa dodati termin koji nije validan
 */
public class NevalidanTerminException extends Exception{
        String message = "Nevalidan je termin, probajte drugi";

    @Override
    public String getMessage() {
        return message;
    }
}
