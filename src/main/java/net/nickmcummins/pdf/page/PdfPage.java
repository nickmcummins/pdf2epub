package net.nickmcummins.pdf.page;

import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import net.nickmcummins.pdf.MyRenderListener;
import net.nickmcummins.pdf.StringUtils;
import net.nickmcummins.pdf.paragraph.FontAndLineGapTextLineCombiner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.sort;
import static java.util.stream.Collectors.groupingBy;
import static net.nickmcummins.pdf.StringUtils.removeWhitespaces;

public class PdfPage {
    private PdfReaderContentParser parser;
    private Map<String, String> whitespaceRemovedStringsToString;
    private List<FormattedTextLine> lines;

    public PdfPage(PdfReaderContentParser parser, int pageNumber) throws IOException {
        this.parser = parser;
        this.whitespaceRemovedStringsToString = buildWhitespaceRemovedStringsMappings(parser, pageNumber);
        this.lines = new FontAndLineGapTextLineCombiner().combine(buildTextLines(parser, pageNumber));
    }

    private List<FormattedTextLine> buildTextLines(PdfReaderContentParser parser, int pageNumber) throws IOException {
        MyRenderListener renderListener = parser.processContent(pageNumber, new MyRenderListener());
        List<TextItem> pageTextItems = renderListener.getTextItems();

        Map<BigDecimal, List<TextItem>> lines = pageTextItems.stream()
                .collect(groupingBy(TextItem::getY));
        BigDecimal[] linePositions = lines.keySet().toArray(new BigDecimal[0]);
        sort(linePositions);
        List<FormattedTextLine> formattedTextLines = new ArrayList<>(lines.size());
        for (BigDecimal linePosition : linePositions) {
            FormattedTextLine formattedTextLine = new FormattedTextLine(lines.get(linePosition));
            String whitespaceRemovedString = removeWhitespaces(formattedTextLine.getFormattedText());
            if (whitespaceRemovedStringsToString.containsKey(whitespaceRemovedString))
                formattedTextLine.setDisplayText(whitespaceRemovedStringsToString.get(whitespaceRemovedString));
            else
                formattedTextLine.setDisplayText(formattedTextLine.getFormattedText());

            formattedTextLines.add(formattedTextLine);
        }

        return formattedTextLines;
    }

    private static Map<String, String> buildWhitespaceRemovedStringsMappings(PdfReaderContentParser parser, int pageNumber) throws IOException {
        String[] pageLines = PdfTextExtractor.getTextFromPage(parser.getReader(), pageNumber, new SimpleTextExtractionStrategy()).split("\n");
        return Arrays.stream(pageLines)
                .filter(line -> line.length() > 1)
                .collect(Collectors.toMap(StringUtils::removeWhitespaces, Function.identity()));
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
