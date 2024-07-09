package SK_Specification_Matic_Zivanovic;

import Serialization.SaveLoadScheduleCSV;
import Serialization.SaveLoadScheduleJSON;
import Serialization.SavePDF;
import exception.*;
import model.FormatFajla;
import model.Prostorija;
import model.Termin;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RasporedWrapper implements RasporedSpecifikacija{
    private List<Termin> termini;
    private List<Termin> filtriraniTermini;

    private List<DayOfWeek> dani;
    private List<Prostorija> prostorije;
    private List<Termin> nedeljniTermini;
    private DayOfWeek pocetniDan;
    private DayOfWeek krajnjiDan;

    private LocalDate periodVazenjaRasporedaOd;
    private LocalDate periodVazenjaRasporedaDo;

    public RasporedWrapper() {
    }

    /**
     * Setovanje rasporeda
     * @param putanja
     * @param formatFajla
     * @param config
     * @param configd
     */
    @Override
    public  void setujRaspored(String putanja, FormatFajla formatFajla, String config, String configd) {
        if (this.getPocetniDan().equals(DayOfWeek.SUNDAY)) {
            this.setPocetniDan(DayOfWeek.MONDAY);
            this.setPeriodVazenjaRasporedaOd(this.getPeriodVazenjaRasporedaOd().plusDays(1));
        } else if (this.getPocetniDan().equals(DayOfWeek.SATURDAY)) {
            this.setPocetniDan(DayOfWeek.MONDAY);
            this.setPeriodVazenjaRasporedaOd(this.getPeriodVazenjaRasporedaOd().plusDays(2));
        }
        this.setPeriodVazenjaRasporedaOd(this.getPeriodVazenjaRasporedaOd().plusDays(7));
        while (this.getPeriodVazenjaRasporedaOd().isBefore(this.getPeriodVazenjaRasporedaDo())) {
            try {
                this.ucitajIzFajla(putanja, formatFajla,config,configd);
            } catch (NeispravnaPutanja e) {
                throw new RuntimeException(e);
            }
            this.setPeriodVazenjaRasporedaOd(this.getPeriodVazenjaRasporedaOd().plusDays(7));
        }
    }

    /**
     * Inicijalizacija liste termina i prostorija
     */
    @Override
    public void inicijalizacija() {
        termini = new ArrayList<>();
        prostorije = new ArrayList<>();
        filtriraniTermini = new ArrayList<>();
        dani = new ArrayList<>();
        nedeljniTermini = new ArrayList<>();
    }

    /**
     * Odredjivanje dana u nedelji
     * @param pocetniDatum
     * @param krajnjiDatum
     */
    @Override
    public void odrediDaneUNedelji(LocalDate pocetniDatum, LocalDate krajnjiDatum){
        pocetniDan = pocetniDatum.getDayOfWeek();
        krajnjiDan = krajnjiDatum.getDayOfWeek();
        dani.add(pocetniDan);
        dani.add(krajnjiDan);
        System.out.println("Pocetni dan: " + pocetniDan + " Krajnji dan: " + krajnjiDan);
    }

    /**
     * Dodavanje nove prostorije u listu prostorija
     * @param identifikator
     * @param additionalData
     * @throws ProstorijaVecPostoji
     */
    @Override
    public void dodajProstoriju(String identifikator, String additionalData) throws ProstorijaVecPostoji{
        // Provera da li prostorija vec postoji
        for (Prostorija p: prostorije){
            if(p.getIdentifikator().equals(identifikator)){
                throw new ProstorijaVecPostoji();
            }
        }
        // Kreiranje nove prostorije i dodavanje u listu
        Prostorija prostorija = new Prostorija(identifikator, additionalData);
        prostorije.add(prostorija);

    }
    /**
     * Brisanje prostorije iz liste prostorija
     * @param identifikator
     * @throws NePostojiProstorija
     */
    @Override
    public void obrisiProstoriju(String identifikator) throws NePostojiProstorija
    {
        for (Prostorija p: prostorije){
            if(p.getIdentifikator().equals(identifikator)){
                prostorije.remove(p);
                return;
            }
        }
        throw new NePostojiProstorija();
    }
    /**
     * Dodavanje novog termina u listu termina
     * @param termin
     */
    @Override
    public void dodajTermin(Termin termin)throws NevalidanTerminException {
    }
    /**
     * Brisanje termina iz liste termina
     * @param termin
     */
    @Override
    public void obrisiTermin(Termin termin) throws NePostojiTermin {
    }
       /**
        * Premestanje termina na novu vrednost vremena ili mesta
        * @param stariTermin
        * @param noviTermin
        */
    @Override
    public void premestiTermin(Termin stariTermin, Termin noviTermin) throws TerminSePreklapa {

    }
    /**
     * Ucitavanje podataka iz fajla
     * @param putanja
     * @param format
     * @param config
     */
    @Override
    public void ucitajIzFajla(String putanja, FormatFajla format, String config, String config2) throws NeispravnaPutanja {
        SaveLoadScheduleCSV csv = new SaveLoadScheduleCSV(this);
        SaveLoadScheduleJSON json = new SaveLoadScheduleJSON(this);
        if(format == FormatFajla.CSV)
            try {
                csv.loadFile(putanja, config, config2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        else if(format == FormatFajla.JSON)
            try {
                json.loadFile(putanja, config, config2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
    /**
     * Snimanje podataka u fajl
     * @param putanja
     * @param format
     */
    @Override
    public void snimiUFajl(String putanja, FormatFajla format) throws NeispravnaPutanja{
        SaveLoadScheduleJSON json = new SaveLoadScheduleJSON(this);
        SaveLoadScheduleCSV csv = new SaveLoadScheduleCSV(this);
        SavePDF pdf = new SavePDF(this);
        if(format == FormatFajla.CSV)
            try {
                csv.exportData(putanja);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        else if(format == FormatFajla.JSON)
            try {
                json.exportData(putanja);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        else if(format == FormatFajla.PDF) {
            try {
                System.out.println("Usao");
                pdf.sacuvajPDF(putanja);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void filtrirajUcionicu(String identifikator) {

    }
    @Override
    public void filtrirajDan(DayOfWeek dan){

    }

    @Override
    public void filtrirajDatum(LocalDate datum) {

    }

    @Override
    public void filtrirajPocetak(LocalTime pocetak) {

    }

    @Override
    public void filtrirajKraj(LocalTime kraj) {

    }

    @Override
    public void filtriraj(Termin termin){

    }
    /**
     * Poredjenje dva termina
     * @param termin1
     * @param termin2
     * @return
     */
    @Override
    public boolean uporedi(Termin termin1, Termin termin2) {
        return false;
    }
    /**
     * Filtriranje termina
     * @return
     */
    public List<Termin> getFiltriraniTermini() {
        return filtriraniTermini;
    }
    /**
     *
     * @param filtriraniTermini
     */
    public void setFiltriraniTermini(List<Termin> filtriraniTermini) {
        this.filtriraniTermini = filtriraniTermini;
    }

    public void setujPeriodVazenjaRasporeda(LocalDate pocetniDatum, LocalDate krajnjiDatum){

    }

    public DayOfWeek getPocetniDan() {
        return pocetniDan;
    }

    public void setPocetniDan(DayOfWeek pocetniDan) {
        this.pocetniDan = pocetniDan;
    }

    public DayOfWeek getKrajnjiDan() {
        return krajnjiDan;
    }

    public void setKrajnjiDan(DayOfWeek krajnjiDan) {
        this.krajnjiDan = krajnjiDan;
    }

    public LocalDate getPeriodVazenjaRasporedaOd() {
        return periodVazenjaRasporedaOd;
    }

    public void setPeriodVazenjaRasporedaOd(LocalDate periodVazenjaRasporedaOd) {
        this.periodVazenjaRasporedaOd = periodVazenjaRasporedaOd;
    }

    public LocalDate getPeriodVazenjaRasporedaDo() {
        return periodVazenjaRasporedaDo;
    }

    public void setPeriodVazenjaRasporedaDo(LocalDate periodVazenjaRasporedaDo) {
        this.periodVazenjaRasporedaDo = periodVazenjaRasporedaDo;
    }
    public List<Termin> getTermini() {
        return termini;
    }

    public void setTermini(List<Termin> termini) {
        this.termini = termini;
    }

    public List<Prostorija> getProstorije() {
        return prostorije;
    }

    public void setProstorije(List<Prostorija> prostorije) {
        this.prostorije = prostorije;
    }

    public List<Termin> getNedeljniTermini() {
        return nedeljniTermini;
    }

    public void setNedeljniTermini(List<Termin> nedeljniTermini) {
        this.nedeljniTermini = nedeljniTermini;
    }

}
