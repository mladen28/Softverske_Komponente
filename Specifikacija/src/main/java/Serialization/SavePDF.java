package Serialization;

import SK_Specification_Matic_Zivanovic.RasporedWrapper;
import model.Termin;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;

public class SavePDF {

    public final RasporedWrapper rw;

    public SavePDF(RasporedWrapper rw) {
        this.rw = rw;
    }
        public void sacuvajPDF(String path) {
        try {
            // Kreiranje objekta Document
            Document document = new Document();

            // Putanja gde će PDF biti sačuvan
            String filePath = path;

            // Lista termina koju želite sačuvati u PDF-u
            List<Termin> termini = rw.getTermini();

            // Kreiranje objekta PdfWriter
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            // Otvaramo dokument
            document.open();

            // Dodajemo informacije o terminima u dokument
            for (Termin termin : termini) {
                document.add(new Paragraph(termin.toString()));
            }
            System.out.println("Dodao sam sve termine u dokument");
            // Zatvaramo dokument
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
