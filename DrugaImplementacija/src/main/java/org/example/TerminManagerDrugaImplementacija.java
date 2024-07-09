package org.example;

import model.Prostorija;
import model.Termin;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TerminManagerDrugaImplementacija {
    @FunctionalInterface
    public interface TerminFilter {
        boolean test(Termin termin);
    }

    // Metode za kreiranje filtera
    public static TerminFilter filtrirajPoPocetku(LocalTime pocetak) {
        return termin -> termin.getPocetak().equals(pocetak);
    }

    public static TerminFilter filtrirajPoKraju(LocalTime kraj) {
        return termin -> termin.getKraj().equals(kraj);
    }

    public static TerminFilter filtrirajPoProstoriji(Prostorija prostorija) {
        return termin -> termin.getProstorija().equals(prostorija);
    }

    public static TerminFilter filtrirajPoDatumu(LocalDate datum) {
        return termin -> termin.getDatum().equals(datum);
    }

    public static TerminFilter filtrirajPoDanu(DayOfWeek dan) {
        return termin -> termin.getDan().equals(dan);
    }

    public static TerminFilter filtrirajPoDodatnimPodacima(Map<String, String> additionalData) {
        return termin -> {
            if(termin.getAdditionalData() == null) return additionalData == null;
            if(additionalData == null) return false;
            return termin.getAdditionalData().equals(additionalData);
        };
    }

    // Metoda za primenu filtera
    public static List<Termin> filtrirajTermine(List<Termin> termini, TerminFilter... filters) {
        List<Termin> filtriraniTermini = new ArrayList<>();
        for (Termin termin : termini) {
            boolean odgovara = true;
            for (TerminFilter filter : filters) {
                if (!filter.test(termin)) {
                    odgovara = false;
                    break;
                }
            }
            if (odgovara) {
                filtriraniTermini.add(termin);
            }
        }
        return filtriraniTermini;
    }
}
