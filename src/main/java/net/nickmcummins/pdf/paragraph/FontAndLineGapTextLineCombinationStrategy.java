package net.nickmcummins.pdf.paragraph;

import net.nickmcummins.pdf.page.FormattedTextLine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class FontAndLineGapTextLineCombinationStrategy implements TextLineCombinationStrategy {
    private static final BigDecimal UNSET_LINE_GAP = BigDecimal.ZERO;

    @Override
    public List<FormattedTextLine> combine(List<FormattedTextLine> formattedTextLines) {
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
                //currentLineCombiner.append(format("\t[%s %s\t%s->%s]",  currentFontFamily, currentFontSize.toString(), previousLineGap, currentLineGap));
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

        return combinedLines;    }
}
