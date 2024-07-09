package Serialization;

import SK_Specification_Matic_Zivanovic.RasporedWrapper;
import model.Prostorija;
import model.Termin;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;


import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.List;
import java.util.Map;

public class SaveLoadScheduleCSV {

    public final RasporedWrapper rw;

    public SaveLoadScheduleCSV(RasporedWrapper rw) {
        this.rw = rw;
    }

    public boolean exportData(String path) throws IOException{
        System.out.println("ddddddddddddddddddddddddddd");
        writeData(path);
        return true;
    }

    public boolean loadFile(String putanja, String configPath, String configpath2) throws IOException {

        loadApache(putanja, configPath, configpath2);
        return true;
    }
    public void loadApache(String filePath, String configPath, String configPath2) throws IOException {
        List<ConfigMapping> columnMappings = readConfig(configPath);
        List<DayMapping> columnMappingsDay = readConfigDay(configPath2);
        Map<Integer, String> mappings = new HashMap<>();
        Map<String, String> mappingsDay = new HashMap<>();
        //LocalTime endDateTime = null;
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal());
        }
        for(DayMapping dayMapping : columnMappingsDay) {
            mappingsDay.put(dayMapping.getOriginal(), dayMapping.getCustom());
        }

        FileReader fileReader = new FileReader(filePath);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1));
        DateTimeFormatter inputFormatterStart = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter inputFormatterEnd = new DateTimeFormatterBuilder()
                .appendPattern("HH")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(mappings.get(-2));

        for (CSVRecord record : parser) {
            Termin appointment = new Termin();

            for (ConfigMapping entry : columnMappings) {
                int columnIndex = entry.getIndex();

                if(columnIndex == -1) continue;

                String columnName = entry.getCustom();

                switch (mappings.get(columnIndex)) {
                    case "prostorija":
                        String [] split2 = record.get(columnIndex).split("-",2);
                        String identifikator = split2[0];
                        String additionalData = split2[1];
                        appointment.setProstorija(new Prostorija(identifikator, additionalData));
                        if(identifikator != null && !identifikator.isEmpty() && additionalData != null) {
                            boolean postoji = false;
                            for (Prostorija p : rw.getProstorije()) {
                                if (p.getIdentifikator().equals(identifikator)) {
                                    postoji = true;
                                    break;
                                }
                            }

                            if (!postoji) {
                                rw.getProstorije().add(new Prostorija(identifikator, additionalData));
                            }
                        }
                        break;
                    case "pocetak":
                        if(record.get(columnIndex).contains("-")) {
                            String[] split = record.get(columnIndex).split("-", 2);
                            LocalTime startDateTime = LocalTime.parse(split[0], inputFormatterStart);
                            //endDateTime = LocalTime.parse(split[1], inputFormatterEnd);
                            String formattedStartTime = outputFormatter.format(startDateTime);
                            appointment.setPocetak(LocalTime.parse(formattedStartTime, outputFormatter));
                            //String[] split1 = record.get(columnIndex).split("-", 2);
                            LocalTime endDateTime = LocalTime.parse(split[1], inputFormatterEnd);
                            String formattedEndTime = outputFormatter.format(endDateTime);
                            appointment.setKraj(LocalTime.parse(formattedEndTime, outputFormatter));
                            //System.out.println("Pocetak: " + appointment.getPocetak() + " Kraj: " + appointment.getKraj());
                        }
                        else {
                            LocalTime startDateTime = LocalTime.parse(record.get(columnIndex), inputFormatterStart);
                            String formattedStartTime = outputFormatter.format(startDateTime);
                            appointment.setPocetak(LocalTime.parse(formattedStartTime, outputFormatter));
                            //System.out.println("Pocetak: " + appointment.getPocetak());
                        }
                        break;
                        case "kraj":
                        LocalTime endDateTime = LocalTime.parse(record.get(columnIndex), inputFormatterEnd);
                        String formattedEndTime = outputFormatter.format(endDateTime);
                        appointment.setKraj(LocalTime.parse(formattedEndTime, outputFormatter));
                        break;
                    case "datum":
                        LocalDate datum = LocalDate.parse(record.get(columnIndex), formatter);
                        appointment.setDatum(datum);
                        if(appointment.getDatum() == null) {
                            LocalDate trenutniDatum = rw.getPeriodVazenjaRasporedaOd();
                            DayOfWeek dan = appointment.getDan();
                            //Prolazimo kroz dane sve dok ne pronadjemo prvi datum koji odgovara zadatom danu
                            while(trenutniDatum.getDayOfWeek() != dan) {
                                trenutniDatum = trenutniDatum.plusDays(1);
                            }
                            appointment.setDatum(trenutniDatum);
                        }
                        break;
                    case "dan":
                        String tab = record.get(columnIndex);
                        tab = tab.replaceAll("[ \\t\\n\\x0B\\f\\r\\u00A0\\u2028\\u2029]+","");
                        System.out.println(tab);
                        switch(mappingsDay.get(tab))
                        {
                            case "MONDAY":
                                appointment.setDan(DayOfWeek.MONDAY);
                                break;
                            case "TUESDAY":
                                appointment.setDan(DayOfWeek.TUESDAY);
                                break;
                            case "WEDNESDAY":
                                appointment.setDan(DayOfWeek.WEDNESDAY);
                                break;
                            case "THURSDAY":
                                appointment.setDan(DayOfWeek.THURSDAY);
                                break;
                            case "FRIDAY":
                                appointment.setDan(DayOfWeek.FRIDAY);
                                break;
                        }
                        break;
                    case "additionalData":
                        appointment.getAdditionalData().put(columnName, record.get(columnIndex));
                        break;
                }
            }
            rw.getTermini().add(appointment);
            List<Termin> terminiZaUklanjanje = new ArrayList<>();

            for (Termin t : rw.getTermini()) {
                if (t.getDatum() == null) {
                    LocalDate trenutniDatum = rw.getPeriodVazenjaRasporedaOd();
                    DayOfWeek day = t.getDan();
                    //Prolazimo kroz dane sve dok ne pronadjemo prvi datum koji odgovara zadatom danu
                    while (trenutniDatum.getDayOfWeek() != day) {
                        trenutniDatum = trenutniDatum.plusDays(1);
                        if (trenutniDatum.isAfter(rw.getPeriodVazenjaRasporedaDo())) {
                            terminiZaUklanjanje.add(t);
                        }
                    }
                    if(!terminiZaUklanjanje.contains(t)) {
                        t.setDatum(trenutniDatum);
                    }
                }
            }
            rw.getTermini().removeAll(terminiZaUklanjanje);
        }
    }

    private static List<ConfigMapping>  readConfig(String filePath) throws FileNotFoundException{
        List<ConfigMapping> mappings = new ArrayList<>();

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ", 3);

            mappings.add(new ConfigMapping(Integer.valueOf(splitLine[0]), splitLine[1], splitLine[2]));
        }

        scanner.close();


        return mappings;
    }
    private static List<DayMapping>  readConfigDay(String filePath) throws FileNotFoundException {
        List<DayMapping> mappingsDay = new ArrayList<>();

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ", 2);

            mappingsDay.add(new DayMapping(splitLine[0], splitLine[1]));
        }

        scanner.close();


        return mappingsDay;
    }
    private void writeData(String path) throws IOException {
        // Create a FileWriter and CSVPrinter
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeee");
        FileWriter fileWriter = new FileWriter(path);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

        for (Termin appointment : rw.getTermini()) {
            System.out.println("fffffffffffffffffffffffffffff");
            csvPrinter.printRecord(
                    appointment.getPocetak(),
                    appointment.getKraj(),
                    appointment.getDan(),
                    appointment.getDatum(),
                    appointment.getProstorija(),
                    appointment.getAdditionalData()
            );
        }

        csvPrinter.close();
        fileWriter.close();
    }
}
