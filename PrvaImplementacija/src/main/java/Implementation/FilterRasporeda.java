package Implementation;

import SK_Specification_Matic_Zivanovic.RasporedSpecifikacija;
import SK_Specification_Matic_Zivanovic.RasporedWrapper;
import model.FormatFajla;
import model.Termin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

public class FilterRasporeda extends RasporedWrapper {

    @Override
    public void inicijalizacija() {

    }

    @Override
    public void dodajProstoriju(String identifikator, String additionalData) {

    }

    @Override
    public void obrisiProstoriju(String identifikator) {

    }

    @Override
    public void dodajTermin(Termin termin) {

    }

    @Override
    public void obrisiTermin(Termin termin) {

    }

    @Override
    public void premestiTermin(Termin stariTermin, Termin noviTermin) {

    }

    @Override
    public void ucitajIzFajla(String putanja, FormatFajla format, String config) {

    }

    @Override
    public void snimiUFajl(String putanja, FormatFajla format) {

    }

    @Override
    public void filtriraj(Termin termin){
        super.setFiltriraniTermini(TerminManager.filtrirajTermine(super.getTermini(),
                TerminManager.filtrirajPoPocetku(termin.getPocetak()),
                TerminManager.filtrirajPoProstoriji(termin.getProstorija()),
                TerminManager.filtrirajPoDanu(termin.getDan()),
                TerminManager.filtrirajPoDatumu(termin.getDatum()),
                TerminManager.filtrirajPoDodatnimPodacima(termin.getAdditionalData())));
    }
    @Override
    public boolean uporedi(Termin termin1, Termin termin2) {
        return false;
    }

}
