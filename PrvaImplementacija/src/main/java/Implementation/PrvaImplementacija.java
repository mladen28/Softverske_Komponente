package Implementation;

import SK_Specification_Matic_Zivanovic.RasporedManager;
import SK_Specification_Matic_Zivanovic.RasporedWrapper;
import exception.*;
import model.FormatFajla;
import model.Termin;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

public class PrvaImplementacija extends RasporedWrapper {


    static {
        RasporedManager.setRasporedWrapper(new PrvaImplementacija());
    }

    @Override
    public void setujRaspored(String putanja, FormatFajla formatFajla, String config, String configd) {
        super.setujRaspored(putanja, formatFajla, config, configd);
    }
    @Override
    public void inicijalizacija() {
        super.inicijalizacija();
    }

    @Override
    public void odrediDaneUNedelji(LocalDate pocetniDatum, LocalDate krajnjiDatum) {
        super.odrediDaneUNedelji(pocetniDatum, krajnjiDatum);
    }


    @Override
    public void dodajProstoriju(String identifikator, String additionalData) throws ProstorijaVecPostoji {
        super.dodajProstoriju(identifikator, additionalData);
    }

    @Override
    public void obrisiProstoriju(String s) throws NePostojiProstorija{
        super.obrisiProstoriju(s);
    }

    @Override
    public void dodajTermin(Termin termin) throws NevalidanTerminException {
        if(super.getTermini().contains(termin))
            throw new NevalidanTerminException();
        for(Termin t: super.getTermini()){
            if(t.getProstorija().equals(termin.getProstorija())&&
                    t.getDatum().equals(termin.getDatum()) &&
                    t.getDan().equals(termin.getDan())&&
                    !t.getPocetak().isAfter(termin.getKraj())&&
                    !t.getKraj().isBefore(termin.getPocetak()))
                throw new NevalidanTerminException();
        }
        super.getTermini().add(termin);
    }

    @Override
    public void obrisiTermin(Termin termin) throws NePostojiTermin {
        boolean terminPronadjen = false;
        Iterator<Termin> iterator = super.getTermini().iterator();

        while (iterator.hasNext()) {
            Termin t = iterator.next();
            if (t.getProstorija().equals(termin.getProstorija()) &&
                    t.getDatum().equals(termin.getDatum()) &&
                    t.getDan().equals(termin.getDan())&&
                    !t.getPocetak().isAfter(termin.getKraj()) &&
                    !t.getKraj().isBefore(termin.getPocetak()))
                    {
                iterator.remove();
                terminPronadjen = true;
                System.out.println("Termin obrisan.");
            }
        }


        if (!terminPronadjen) {
            throw new NePostojiTermin();
        }
        System.out.println(super.getTermini().toString());
    }

    @Override
    public void premestiTermin(Termin stariTermin, Termin noviTermin) throws TerminSePreklapa {
        boolean stariTerminPronadjen = false;

        for (Termin t : super.getTermini()) {
            if (t.equals(stariTermin)) {
                stariTerminPronadjen = true;
                System.out.println("Pronasao stari termin");
                break;
            }
        }

        if (stariTerminPronadjen && uporedi(stariTermin, noviTermin)) {
            super.getTermini().remove(stariTermin);
            super.getTermini().add(noviTermin);
            System.out.println(super.getTermini().toString());
        } else {
            throw new TerminSePreklapa();
        }
    }

    @Override
    public void ucitajIzFajla(String putanja, FormatFajla formatFajla, String config, String config2) throws NeispravnaPutanja {
        super.ucitajIzFajla(putanja, formatFajla, config, config2);
    }

    @Override
    public void snimiUFajl(String putanja, FormatFajla formatFajla) throws NeispravnaPutanja{
        super.snimiUFajl(putanja, formatFajla);
    }

    @Override
    public void filtriraj(Termin termin){
        super.setFiltriraniTermini(TerminManager.filtrirajTermine(super.getTermini(),
                TerminManager.filtrirajPoPocetku(termin.getPocetak()),
                TerminManager.filtrirajPoKraju(termin.getKraj()),
                TerminManager.filtrirajPoProstoriji(termin.getProstorija()),
                TerminManager.filtrirajPoDanu(termin.getDan()),
                TerminManager.filtrirajPoDatumu(termin.getDatum()),
                TerminManager.filtrirajPoDodatnimPodacima(termin.getAdditionalData())));
        System.out.println("Filtrirani termini: " + super.getFiltriraniTermini().toString());
    }

    @Override
    public void filtrirajUcionicu(String identifikator){
        for (Termin t: super.getTermini()) {
            if(t.getProstorija().getIdentifikator().equals(identifikator))
                super.getFiltriraniTermini().add(t);
        }
    }


    @Override
    public void filtrirajDan(DayOfWeek dan){
        super.getFiltriraniTermini().removeAll(super.getFiltriraniTermini());
       for(Termin t: super.getTermini()){
           if(t.getDan().equals(dan))
               super.getFiltriraniTermini().add(t);
       }
        System.out.println("Filtrirani termini: " + super.getFiltriraniTermini().toString());
    }

    @Override
    public void filtrirajDatum(LocalDate datum) {
        super.getFiltriraniTermini().removeAll(super.getFiltriraniTermini());
        for(Termin t: super.getTermini()){
            if(t.getDatum().equals(datum))
                super.getFiltriraniTermini().add(t);
        }
        System.out.println("Filtrirani termini: " + super.getFiltriraniTermini().toString());
    }

    @Override
    public void filtrirajPocetak(LocalTime pocetak) {
        super.getFiltriraniTermini().removeAll(super.getFiltriraniTermini());
        for(Termin t: super.getTermini()){
            if(t.getPocetak().equals(pocetak))
                super.getFiltriraniTermini().add(t);
        }
        System.out.println("Filtrirani termini: " + super.getFiltriraniTermini().toString());
    }

    @Override
    public void filtrirajKraj(LocalTime kraj) {
        super.getFiltriraniTermini().removeAll(super.getFiltriraniTermini());
        for(Termin t: super.getTermini()){
            if(t.getKraj().equals(kraj))
                super.getFiltriraniTermini().add(t);
        }
        System.out.println("Filtrirani termini: " + super.getFiltriraniTermini().toString());
    }

    @Override
    public boolean uporedi(Termin termin1, Termin termin2) {
        for (Termin t : super.getTermini()) {
            // Preskoči proveru za sam stari termin
            if (t.equals(termin1)) {
                continue;
            }

            // Provera za novi termin
            if (t.getProstorija().equals(termin2.getProstorija()) &&
                    t.getDatum().equals(termin2.getDatum()) &&
                    !t.getPocetak().isAfter(termin2.getKraj()) &&
                    !t.getKraj().isBefore(termin2.getPocetak())) {
                return false; // Novi termin se poklapa sa nekim postojećim terminom
            }
        }

        return true; // Nema poklapanja, premestanje je moguće
    }
}
