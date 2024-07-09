package org.example;

import SK_Specification_Matic_Zivanovic.RasporedManager;
import SK_Specification_Matic_Zivanovic.RasporedWrapper;
import exception.*;
import model.FormatFajla;
import model.Termin;

import java.time.LocalDate;
import java.util.Iterator;

public class DrugaImplementacija extends RasporedWrapper {

    static {
        RasporedManager.setRasporedWrapper(new DrugaImplementacija());
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
    public void obrisiProstoriju(String s) throws NePostojiProstorija {
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
        Iterator<Termin> iterator2 = super.getNedeljniTermini().iterator();

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
        while (iterator2.hasNext()) {
            Termin t = iterator2.next();
            if (t.getProstorija().equals(termin.getProstorija()) &&
                    t.getDatum().equals(termin.getDatum()) &&
                    t.getDan().equals(termin.getDan())&&
                    !t.getPocetak().isAfter(termin.getKraj()) &&
                    !t.getKraj().isBefore(termin.getPocetak()))
            {
                iterator2.remove();
                terminPronadjen = true;
                System.out.println("Termin obrisan.");
            }
        }


        if (!terminPronadjen) {
            throw new NePostojiTermin();
        }
        System.out.println(super.getNedeljniTermini().toString());
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
        super.setFiltriraniTermini(TerminManagerDrugaImplementacija.filtrirajTermine(super.getTermini(),
                TerminManagerDrugaImplementacija.filtrirajPoPocetku(termin.getPocetak()),
                TerminManagerDrugaImplementacija.filtrirajPoKraju(termin.getKraj()),
                TerminManagerDrugaImplementacija.filtrirajPoProstoriji(termin.getProstorija()),
                TerminManagerDrugaImplementacija.filtrirajPoDanu(termin.getDan()),
                TerminManagerDrugaImplementacija.filtrirajPoDatumu(termin.getDatum()),
                TerminManagerDrugaImplementacija.filtrirajPoDodatnimPodacima(termin.getAdditionalData())));
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
