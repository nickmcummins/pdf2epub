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
    private static final BigDecimal UNSET_LINE_GAP = BigDecimal.ZERO;
    private Map<String, String> whitespaceRemovedStringsToString;
    private List<FormattedTextLine> lines;

    public PdfPage(PdfReaderContentParser parser, int pageNumber) throws IOException {
        this.whitespaceRemovedStringsToString = buildWhitespaceRemovedStringsMappings(parser, pageNumber);
        this.lines = combineContinuous(buildTextLines(parser, pageNumber));
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
        return Arrays.stream(PdfTextExtractor.getTextFromPage(parser.getReader(), pageNumber, new SimpleTextExtractionStrategy()).split("\n"))
                .collect(Collectors.toMap(StringUtils::removeWhitespaces, Function.identity()));
    }

    private List<FormattedTextLine> combineContinuous(List<FormattedTextLine> formattedTextLines) {
        FormattedTextLine previousLine = formattedTextLines.get(0);

        String currentFontFamily = previousLine.getFontFamilyName();
        BigDecimal currentFontSize = previousLine.getFontSize();
        BigDecimal previousLineGap = UNSET_LINE_GAP;

        List<FormattedTextLine> combinedLines = new ArrayList<>();

        StringBuilder currentLineCombiner = new StringBuilder();
        currentLineCombiner.append(previousLine.getDisplayText());

        for (int i = 1; i < formattedTextLines.size(); i++) {
            previousLine = formattedTextLines.get(i - 1);
            FormattedTextLine currentLine = formattedTextLines.get(i);

            BigDecimal currentLineGap = currentLine.getY().subtract(previousLine.getY());

            BigDecimal gapDifference = currentLineGap.subtract(previousLineGap);
            boolean lineGapImpliesNewParagraph = !previousLineGap.equals(UNSET_LINE_GAP) && gapDifference.doubleValue() > 0.2;
            boolean sameFont = currentLine.getFontFamilyName().equals(currentFontFamily)
                    && currentLine.getFontSize().equals(currentFontSize);

            if (sameFont && !lineGapImpliesNewParagraph) {
                currentLineCombiner.append(" ");
                currentLineCombiner.append(currentLine.getDisplayText());
            } else {
                currentLineCombiner.append(format("\t[%s %s\t%s->%s]",  currentFontFamily, currentFontSize.toString(), previousLineGap, currentLineGap));
                combinedLines.add(new FormattedTextLine(currentLineCombiner.toString(), currentFontFamily, currentFontSize));
                currentLineCombiner = new StringBuilder();
                currentLineCombiner.append(currentLine.getDisplayText());
                currentFontFamily = currentLine.getFontFamilyName();
                currentFontSize = currentLine.getFontSize();
            }
            previousLineGap = currentLineGap;

        }

        currentLineCombiner.append(format("\t[%s %s]",  currentFontFamily, currentFontSize.toString()));
        combinedLines.add(new FormattedTextLine(currentLineCombiner.toString(), currentFontFamily, currentFontSize));

        return combinedLines;
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
