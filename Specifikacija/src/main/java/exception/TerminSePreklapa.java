package exception;

/**
 * Eksepsn koji se baca kada se pokusa premestiti termin na termin koji vec postoji
 */
public class TerminSePreklapa extends Exception{
    String message = "Termin se preklapa!";

    @Override
    public String getMessage() {
        return message;
    }
}
