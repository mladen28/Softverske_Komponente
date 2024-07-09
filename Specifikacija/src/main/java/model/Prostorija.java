package model;

import java.util.Map;

/**
 * Kreira prostoriju sa datim identifikatorom i dodatnim podacima.
 * Identifikator je jedinstven za svaku prostoriju.
 * Dodatni podaci su (u)-ako je ucionica koja ima racunare.
 * Dodatni podaci su (a)-ako je prostorija koja nema racunare.
 */
public class Prostorija {
    private String identifikator;
    private String additionalData;

    public Prostorija(String identifikator, String additionalData) {
        this.identifikator = identifikator;
        this.additionalData = additionalData;

    }
    public Prostorija(String identifikator){
        this.identifikator = identifikator;
    }

    public String getIdentifikator() {
        return identifikator;
    }

    public void setIdentifikator(String identifikator) {
        this.identifikator = identifikator;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public String toString() {
        return "Ucionica:" + identifikator + additionalData;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Ista referenca
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Objekat je null ili nije iste klase
        }

        Prostorija drugaProstorija = (Prostorija) obj;
        // Provera da li su identifikatori i dodatni podaci isti
        return (identifikator != null ? identifikator.equals(drugaProstorija.identifikator) : drugaProstorija.identifikator == null) &&
                (additionalData != null ? additionalData.equals(drugaProstorija.additionalData) : drugaProstorija.additionalData == null);
    }

    @Override
    public int hashCode() {
        int result = identifikator != null ? identifikator.hashCode() : 0;
        result = 31 * result + (additionalData != null ? additionalData.hashCode() : 0);
        return result;
    }
}
