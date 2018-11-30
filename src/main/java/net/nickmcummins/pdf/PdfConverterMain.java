package net.nickmcummins.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import net.nickmcummins.pdf.output.HtmlOutputWriter;
import net.nickmcummins.pdf.page.PdfPage;
import org.mabb.fontverter.pdf.PdfFontExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class PdfConverterMain {
    private static void convertPdfToHtml(String filename) throws IOException {
        try {
            PdfFontExtractor fontExtractor = new PdfFontExtractor();
            fontExtractor.extractFontsToDir(new File(filename), containingDir(filename) + "/fonts");
        } catch (Exception e) {
            System.out.println("Exception extracting fonts: " + e);
        }

        PdfReader reader = new PdfReader(filename);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        PdfPage pdfPage = new PdfPage(parser, 5);
        pdfPage.printLines();

        HtmlOutputWriter htmlOutputWriter = new HtmlOutputWriter(pdfPage);
        htmlOutputWriter.writeToFile();

        reader.close();
    }

    private static String containingDir(String filename) {
        return Paths.get(filename).getParent().toFile().getAbsolutePath();
    }

    public static void main(String[] args) throws IOException {
        convertPdfToHtml(args[0]);

    }
}
