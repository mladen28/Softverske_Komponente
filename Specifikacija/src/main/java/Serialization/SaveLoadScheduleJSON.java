package Serialization;

import SK_Specification_Matic_Zivanovic.RasporedWrapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Prostorija;
import model.Termin;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.*;

public class SaveLoadScheduleJSON {
    public final RasporedWrapper rw;

    public SaveLoadScheduleJSON(RasporedWrapper rw) {
        this.rw = rw;
    }

    public boolean exportData(String path) throws IOException {
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
        Map<String, String> mappings = new HashMap<>();
        Map<String, String> mappingsDay = new HashMap<>();
        //LocalTime endDateTime = null;
        for (ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getCustom(), configMapping.getOriginal());
        }
        for (DayMapping dayMapping : columnMappingsDay) {
            mappingsDay.put(dayMapping.getOriginal(), dayMapping.getCustom());
        }

        FileReader fileReader = new FileReader(filePath);
        JsonReader jsonReader = new JsonReader(fileReader);
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonReader, JsonArray.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        DateTimeFormatter inputFormatterStart = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter inputFormatterEnd = new DateTimeFormatterBuilder()
                .appendPattern("HH")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Set<String> identifikatoriProstorija = new HashSet<>();
        for(Prostorija p : rw.getProstorije()) {
            identifikatoriProstorija.add(p.getIdentifikator());
        }

        for (JsonElement element : jsonArray) {
            Termin appointment = new Termin();
            JsonObject jsonObject = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                switch (mappings.get(key)) {
                    case "prostorija":
                        String[] split2 = value.getAsString().split("-", 2);
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
                        if (value.getAsString().contains("-")) {
                            String[] split = value.getAsString().split("-", 2);
                            LocalTime startDateTime = LocalTime.parse(split[0], inputFormatterStart);
                            //endDateTime = LocalTime.parse(split[1], inputFormatterEnd);
                            String formattedStartTime = outputFormatter.format(startDateTime);
                            appointment.setPocetak(LocalTime.parse(formattedStartTime, outputFormatter));
                            //String[] split1 = record.get(columnIndex).split("-", 2);
                            LocalTime endDateTime = LocalTime.parse(split[1], inputFormatterEnd);
                            String formattedEndTime = outputFormatter.format(endDateTime);
                            appointment.setKraj(LocalTime.parse(formattedEndTime, outputFormatter));
                            //System.out.println("Pocetak: " + appointment.getPocetak() + " Kraj: " + appointment.getKraj());
                        } else {
                            LocalTime startDateTime = LocalTime.parse(value.getAsString(), inputFormatterStart);
                            String formattedStartTime = outputFormatter.format(startDateTime);
                            appointment.setPocetak(LocalTime.parse(formattedStartTime, outputFormatter));
                            //System.out.println("Pocetak: " + appointment.getPocetak());
                        }
                        break;
                    case "kraj":
                        LocalTime endDateTime = LocalTime.parse(value.getAsString(), inputFormatterEnd);
                        String formattedEndTime = outputFormatter.format(endDateTime);
                        appointment.setKraj(LocalTime.parse(formattedEndTime, outputFormatter));
                        break;
                    case "datum":
                        LocalDate datum = LocalDate.parse(value.getAsString(), formatter);
                        appointment.setDatum(datum);
                        if (appointment.getDatum() == null) {
                            System.out.println("DALI UDJES");
                            LocalDate trenutniDatum = rw.getPeriodVazenjaRasporedaOd();
                            DayOfWeek dan = appointment.getDan();
                            //Prolazimo kroz dane sve dok ne pronadjemo prvi datum koji odgovara zadatom danu
                            while (trenutniDatum.getDayOfWeek() != dan) {
                                trenutniDatum = trenutniDatum.plusDays(1);
                            }
                            appointment.setDatum(trenutniDatum);
                        }
                        break;
                    case "dan":
                        String tab = value.getAsString();
                        tab = tab.replaceAll("[ \\t\\n\\x0B\\f\\r\\u00A0\\u2028\\u2029]+", "");
                        switch (mappingsDay.get(tab)) {
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
                        appointment.getAdditionalData().put(key, value.getAsString());
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

    private static List<ConfigMapping>  readConfig(String filePath) throws FileNotFoundException {
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
        FileWriter fileWriter = new FileWriter(path);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new LocalDateAdapterFactory())
                .registerTypeAdapterFactory(new LocalTimeAdapterFactory())
                .setPrettyPrinting()
                .create();

        for (Termin appointment : rw.getTermini()) {
            String jsonData = gson.toJson(appointment);
            fileWriter.write(jsonData);
        }
        fileWriter.close();
    }
    static class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

        @Override
        public void write(JsonWriter jsonWriter,LocalDate localDate) throws IOException {
            jsonWriter.value(localDate.toString());
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            return LocalDate.parse(jsonReader.nextString());
        }
    }
    static class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {

        @Override
        public void write(JsonWriter jsonWriter,LocalTime localTime) throws IOException {
            jsonWriter.value(localTime.toString());
        }

        @Override
        public LocalTime read(final JsonReader jsonReader) throws IOException {
            return LocalTime.parse(jsonReader.nextString());
        }
    }
    static class LocalTimeAdapterFactory implements TypeAdapterFactory {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
            return typeToken.getRawType() == LocalTime.class
                    ? (TypeAdapter<T>) new LocalTimeTypeAdapter()
                    : null;
        }
    }
    static class LocalDateAdapterFactory implements TypeAdapterFactory {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
            return typeToken.getRawType() == LocalDate.class
                    ? (TypeAdapter<T>) new LocalDateTypeAdapter()
                    : null;
        }
    }
}

