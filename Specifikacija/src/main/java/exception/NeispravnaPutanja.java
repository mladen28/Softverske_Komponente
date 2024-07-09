package exception;

/**
 * Exception koji se baca kada je putanja neispravna
 */
public class NeispravnaPutanja extends Exception{
    String message = "Neispravna putanja";

    @Override
    public String getMessage() {
        return message;
    }
}
