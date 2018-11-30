package net.nickmcummins.pdf.page;

import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import net.nickmcummins.pdf.MyRenderListener;
import net.nickmcummins.pdf.StringUtils;
import net.nickmcummins.pdf.paragraph.ParagraphBuilderStrategy;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class PdfPage {
    private PdfReaderContentParser parser;
    private Map<String, List<String>> whitespaceRemovedStringsToString;
    private List<FormattedTextLine> lines;

    public PdfPage(PdfReaderContentParser parser, int pageNumber) throws IOException {
        this.parser = parser;
        this.whitespaceRemovedStringsToString = buildWhitespaceRemovedStringsMappings(parser, pageNumber);
        this.lines = buildParagraphTextLines(parser, pageNumber);
        System.out.println();
    }

    private List<FormattedTextLine> buildParagraphTextLines(PdfReaderContentParser parser, int pageNumber) throws IOException {
        MyRenderListener renderListener = parser.processContent(pageNumber, new MyRenderListener());
        List<TextItem> pageTextItems = renderListener.getTextItems();

        Map<BigDecimal, List<TextItem>> lines = pageTextItems.stream()
                .collect(groupingBy(TextItem::getY));
        TreeMap<BigDecimal, List<TextItem>> sortedLines = new TreeMap<>(lines);

        List<FormattedTextLine> paragraphTextLines = new ParagraphBuilderStrategy().buildParagraphs(sortedLines);

        return paragraphTextLines;
    }

    private static Map<String, List<String>> buildWhitespaceRemovedStringsMappings(PdfReaderContentParser parser, int pageNumber) throws IOException {
        String[] pageLines = PdfTextExtractor.getTextFromPage(parser.getReader(), pageNumber, new SimpleTextExtractionStrategy()).split("\n");
        return Arrays.stream(pageLines)
                .filter(line -> line.length() > 1)
                .collect(groupingBy(StringUtils::removeWhitespaces));
    }

    public List<FormattedTextLine> getLines() {
        return lines;
    }

    public void printLines() {
        for (FormattedTextLine formattedTextLine : lines) {
            System.out.println(formattedTextLine.getDisplayText());
        }
    }

    public PdfReaderContentParser getParser() {
        return parser;
    }
}
