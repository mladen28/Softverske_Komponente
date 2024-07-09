package SK_Specification_Matic_Zivanovic;

import exception.*;
import model.FormatFajla;
import model.Termin;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface RasporedSpecifikacija {


    void inicijalizacija();

    void setujRaspored(String putanja, FormatFajla formatFajla, String config, String configd);

    void odrediDaneUNedelji(LocalDate pocetniDatum, LocalDate krajnjiDatum);

    void dodajProstoriju(String identifikator, String additionalData) throws ProstorijaVecPostoji;


    void obrisiProstoriju(String identifikator) throws NePostojiProstorija;

    void dodajTermin(Termin termin) throws NevalidanTerminException;

    void obrisiTermin(Termin termin) throws NePostojiTermin;

    void premestiTermin(Termin stariTermin, Termin noviTermin) throws TerminSePreklapa;


    void ucitajIzFajla(String putanja, FormatFajla format, String config, String config2) throws NeispravnaPutanja;

    void snimiUFajl(String putanja, FormatFajla format)throws NeispravnaPutanja;

    void filtriraj(Termin t);

    void filtrirajDan(DayOfWeek dan);

    void filtrirajUcionicu(String identifikator);

    void filtrirajDatum(LocalDate datum);

    void filtrirajPocetak(LocalTime pocetak);

    void filtrirajKraj(LocalTime kraj);

    boolean uporedi(Termin termin1, Termin termin2);


}
