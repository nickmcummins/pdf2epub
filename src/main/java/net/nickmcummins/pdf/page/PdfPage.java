package net.nickmcummins.pdf.page;

import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import net.nickmcummins.pdf.MyRenderListener;
import net.nickmcummins.pdf.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.sort;
import static java.util.stream.Collectors.groupingBy;
import static net.nickmcummins.pdf.StringUtils.removeWhitespaces;

public class PdfPage {
    private Map<String, String> whitespaceRemovedStringsToString;
    private List<FormattedTextLine> lines;

    public PdfPage(PdfReaderContentParser parser, int pageNumber) throws IOException {
        this.whitespaceRemovedStringsToString = buildWhitespaceRemovedStringsMappings(parser, pageNumber);
        this.lines = buildTextLines(parser, pageNumber);
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
            String fontLabel = format("\t[%s %.2f]",  formattedTextLine.getFontFamilyName(), formattedTextLine.getFontSize());
            String whitespaceRemovedString = removeWhitespaces(formattedTextLine.getFormattedText());
            if (whitespaceRemovedStringsToString.containsKey(whitespaceRemovedString))
                formattedTextLine.setDisplayText(whitespaceRemovedStringsToString.get(whitespaceRemovedString) + fontLabel);
            else
                formattedTextLine.setDisplayText(formattedTextLine.getFormattedText() + fontLabel);

            formattedTextLines.add(formattedTextLine);
        }

        return formattedTextLines;
    }

    private static Map<String, String> buildWhitespaceRemovedStringsMappings(PdfReaderContentParser parser, int pageNumber) throws IOException {
        return Arrays.stream(PdfTextExtractor.getTextFromPage(parser.getReader(), pageNumber, new SimpleTextExtractionStrategy()).split("\n"))
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
}
