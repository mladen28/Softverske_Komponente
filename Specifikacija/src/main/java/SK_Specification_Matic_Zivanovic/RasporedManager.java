package SK_Specification_Matic_Zivanovic;


public class RasporedManager {

    private static RasporedWrapper rasporedWrapper = null;

    public static RasporedWrapper getRasporedWrapper() {
        return rasporedWrapper;
    }

    public static void setRasporedWrapper(RasporedWrapper specificationImplementacion){
        rasporedWrapper = specificationImplementacion;
    }
}
