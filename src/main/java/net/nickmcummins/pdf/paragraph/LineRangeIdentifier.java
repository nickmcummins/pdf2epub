package net.nickmcummins.pdf.paragraph;

import net.nickmcummins.pdf.line.LineRange;
import net.nickmcummins.pdf.page.TextItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class LineRangeIdentifier {
    public static List<LineRange> identifyLineRanges(TreeMap<BigDecimal, List<TextItem>> sortedLines) {
        List<Double> lineGaps = new ArrayList<>();

        BigDecimal previousLineY = sortedLines.firstKey();

        for (BigDecimal currentLineY : sortedLines.navigableKeySet()) {
            double gapFromPrevious = currentLineY.doubleValue() - previousLineY.doubleValue();
            lineGaps.add(gapFromPrevious);
            previousLineY = currentLineY;
        }

        BigDecimal[] sortedLinePositions = sortedLines.navigableKeySet().toArray(new BigDecimal[0]);

        List<LineRange> lineRanges = new ArrayList<>();
        LineRange currentLineRange = new LineRange(0);

        int currentLineNumber = 0;
        while (currentLineNumber < lineGaps.size()) {
            if (currentLineNumber < lineGaps.size() - 1
                    && Math.abs(lineGaps.get(currentLineNumber) - lineGaps.get(currentLineNumber + 1)) < 0.2) {
                currentLineRange.setLineGap(new BigDecimal(lineGaps.get(currentLineNumber)).setScale(2, RoundingMode.FLOOR));
                currentLineRange.addLine(sortedLines.get(sortedLinePositions[currentLineNumber]));
                currentLineNumber += 1;
            } else if (currentLineNumber < lineGaps.size() - 2
                    && ((Math.abs(lineGaps.get(currentLineNumber + 2) - (lineGaps.get(currentLineNumber) + lineGaps.get(currentLineNumber + 1))) < 0.2)
                    || (Math.abs(lineGaps.get(currentLineNumber) - (lineGaps.get(currentLineNumber + 1) + lineGaps.get(currentLineNumber + 2)))) < 0.2)) {
                currentLineRange.addLine(sortedLines.get(sortedLinePositions[currentLineNumber]));
                currentLineRange.addLine(sortedLines.get(sortedLinePositions[currentLineNumber + 1]));
                currentLineRange.addLine(sortedLines.get(sortedLinePositions[currentLineNumber + 2]));
                currentLineNumber += 3;
            } else {
                currentLineRange.setEndLine(currentLineNumber);
                currentLineRange.addLine(sortedLines.get(sortedLinePositions[currentLineNumber]));
                lineRanges.add(currentLineRange);
                currentLineRange = new LineRange(currentLineNumber + 1);
                currentLineNumber += 1;
            }
        }

        return lineRanges;
    }
}
