package net.nickmcummins.pdf.output;

import net.nickmcummins.pdf.page.FormattedTextLine;
import net.nickmcummins.pdf.page.PdfPage;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class HtmlOutputWriter {
    private static final int HEADER_LENGTH_CUTOFF = 50;
    private PdfPage pdfPage;
    private MultiKeyMap stylesMap;
    private int styleCount;

    public HtmlOutputWriter(PdfPage pdfPage) {
        this.pdfPage = pdfPage;
        this.styleCount = 0;
        this.stylesMap = buildStyles(pdfPage);
    }

    private MultiKeyMap buildStyles(PdfPage pdfPage) {
        MultiKeyMap stylesMap = MultiKeyMap.multiKeyMap(new LinkedMap());
        for (FormattedTextLine text : pdfPage.getLines()) {
            String fontFamilyName = text.getFontFamilyName();
            String fontSize = text.getFontSize().toString();
            if (!stylesMap.containsKey(fontFamilyName, fontFamilyName)) {
                stylesMap.put(fontFamilyName, fontSize, format("style%s", styleCount + 1));
                styleCount++;
            }
        }
        return stylesMap;
    }

    private void writeCss(StringBuilder sb) {
        sb.append("<style type='text/css'>");

        writeFontFaces(sb);
        for (Object object : stylesMap.keySet()) {
            MultiKey multiKey = (MultiKey)object;
            String fontNameKey = (String)multiKey.getKeys()[0];
            String fontSizeKey = (String)multiKey.getKeys()[1];
            String className = (String)stylesMap.get(fontNameKey, fontSizeKey);
            sb.append(format("\n.%s {\n", className));
            sb.append(format("\tfont-family: \"%s\";\n", fontNameKey));
            sb.append(format("\tfont-size: %spt;\n", fontSizeKey));
            sb.append("}\n");
        }

        sb.append("</style>\n");
    }

    private void writeFontFaces(StringBuilder sb) {
        String pdfFilename = pdfPage.getParser().getReader().getFilename();

        String containingDirectory = Paths.get(pdfFilename).getParent().toFile().getAbsolutePath();
        String fontDirectory = format("%s/fonts", containingDirectory);

        String fontFacesCss = Arrays.stream(new File(fontDirectory).listFiles())
                .filter(File::isFile)
                .map(fontFile -> fontFaceCss(fontFile.getName()))
                .collect(Collectors.joining("\n"));
        sb.append("\n");
        sb.append(fontFacesCss);
    }

    private String fontFaceCss(String fontFile) {
        String fontFamilyName = fontFile.split("\\.")[0];
        return format("\t@font-face {\n\t\tfont-family: \"%s\";\n\t\tsrc: url(fonts/%s);\n\t}", fontFamilyName, fontFile);
    }

    private void writeLine(StringBuilder sb, FormattedTextLine textBlock) {
        String className = (String)stylesMap.get(textBlock.getFontFamilyName(), String.valueOf(textBlock.getFontSize()));
        String text = textBlock.getDisplayText();
        String elementType = text.length() > HEADER_LENGTH_CUTOFF ? "p" : "h2";
        sb.append(format("\t<%s class='%s'>", elementType, className));
        sb.append(text);
        sb.append(format("</%s>\n", elementType));
    }

    private void writeHead(StringBuilder sb) {
        sb.append("<head>\n");
        sb.append("\t<meta charset=\"UTF-8\" />\n");
        writeCss(sb);
        sb.append("</head>\n");
        sb.append("<body>\n");
    }


    private String write() {
        StringBuilder sb = new StringBuilder();
        writeHead(sb);
        for (FormattedTextLine text : pdfPage.getLines())
            writeLine(sb, text);
        sb.append("</body>\n");
        return sb.toString();
    }

    public void writeToFile() throws IOException {
        String filename = pdfPage.getParser().getReader().getFilename();
        String destinationFile = filename.replaceAll("pdf", "html");
        System.out.println("Writing to " + destinationFile);
        Files.write(Paths.get(destinationFile), write().getBytes());
    }
}
