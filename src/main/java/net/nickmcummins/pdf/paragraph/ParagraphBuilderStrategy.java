package net.nickmcummins.pdf.paragraph;

import net.nickmcummins.pdf.line.LineRange;
import net.nickmcummins.pdf.page.FormattedTextLine;
import net.nickmcummins.pdf.page.TextItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.sort;

public class ParagraphBuilderStrategy {
    private static final String UNSET_FONT_FAMILY = "";
    private static final BigDecimal UNSET_FONT_SIZE = BigDecimal.ZERO;


    private TreeMap<BigDecimal, List<TextItem>> moveInterlineTextItems(TreeMap<BigDecimal, List<TextItem>> sortedLines) {
        List<Double> lineGaps = new ArrayList<>();

        BigDecimal previousLineY = sortedLines.firstKey();

        for (BigDecimal currentLineY : sortedLines.navigableKeySet()) {
            if (currentLineY.equals(previousLineY)) continue;
            double gapFromPrevious = currentLineY.doubleValue() - previousLineY.doubleValue();
            lineGaps.add(gapFromPrevious);
            previousLineY = currentLineY;
        }

        BigDecimal[] linePositions = sortedLines.navigableKeySet().toArray(new BigDecimal[0]);
        List<LineRange> lineRanges = new ArrayList<>();
        LineRange currentLineRange = new LineRange(0);
        TreeMap<BigDecimal, List<TextItem>> mappedSortedLines = new TreeMap<>();

        int currentLineNumber = 0;
        while (currentLineNumber <= lineGaps.size()) {
            BigDecimal currentLinePosition = linePositions[currentLineNumber];
            if (currentLineNumber < lineGaps.size() - 1
                    && Math.abs(lineGaps.get(currentLineNumber) - lineGaps.get(currentLineNumber + 1)) < 0.2) {
                currentLineRange.setLineGap(new BigDecimal(lineGaps.get(currentLineNumber)).setScale(2, RoundingMode.FLOOR));
                mappedSortedLines.put(currentLinePosition, mappedSortedLines.get(currentLinePosition));
                currentLineNumber += 1;


            } else if (currentLineNumber < lineGaps.size() - 2
                    && ((Math.abs(lineGaps.get(currentLineNumber + 2) - (lineGaps.get(currentLineNumber) + lineGaps.get(currentLineNumber + 1))) < 0.2)
                        || (Math.abs(lineGaps.get(currentLineNumber) - (lineGaps.get(currentLineNumber + 1) + lineGaps.get(currentLineNumber + 2)))) < 0.2)) {
                currentLineNumber += 3;
            } else {
                currentLineRange.setEndLine(currentLineNumber);
                lineRanges.add(currentLineRange);
                currentLineRange = new LineRange(currentLineNumber + 1);
                currentLineNumber += 1;
            }
        }


        int actualLinePositionIdx = 0;
        List<BigDecimal> mappedLinePositions = new ArrayList<>();
        for (LineRange lineRange : lineRanges) {
            if (lineRange.getStartLine() == lineRange.getEndLine()) {
                BigDecimal linePosition = linePositions[lineRange.getStartLine()];
                mappedLinePositions.add(linePosition);
                mappedSortedLines.put(linePosition, sortedLines.get(linePosition));
                actualLinePositionIdx++;
            } else {
                BigDecimal calculatedLinePosition = linePositions[lineRange.getStartLine()];
                while (Math.abs(calculatedLinePosition.doubleValue() - linePositions[lineRange.getEndLine()].doubleValue()) > 0.2
                        && actualLinePositionIdx != lineRange.getEndLine()) {
                    while (Math.abs(linePositions[actualLinePositionIdx].subtract(calculatedLinePosition).doubleValue()) > 0.2) {
                        actualLinePositionIdx++;
                    }
                    mappedLinePositions.add(linePositions[actualLinePositionIdx]);
                    calculatedLinePosition = calculatedLinePosition.add(lineRange.getLineGap());
                    actualLinePositionIdx++;
                }

                if (actualLinePositionIdx == lineRange.getEndLine()) {
                    mappedLinePositions.add(linePositions[actualLinePositionIdx]);
                }
            }
        }

        int mappedLinePositionIdx = 0;
        for (BigDecimal mappedLinePosition : mappedLinePositions)
            mappedSortedLines.put(mappedLinePosition, new ArrayList<>());


        for (BigDecimal unmappedLinePosition : linePositions) {
            if (Math.abs(unmappedLinePosition.doubleValue() - mappedLinePositions.get(mappedLinePositionIdx).doubleValue()) < 0.2) {
                if (!mappedSortedLines.containsKey(unmappedLinePosition)) mappedSortedLines.put(unmappedLinePosition, new ArrayList<>());
                mappedSortedLines.get(unmappedLinePosition).addAll(sortedLines.get(unmappedLinePosition));
                mappedLinePositionIdx++;
            } else {
                    mappedSortedLines.get(mappedLinePositions.get(mappedLinePositionIdx)).addAll(sortedLines.get(unmappedLinePosition));
            }
        }

        for (List<TextItem> lineItems : mappedSortedLines.values())
            sort(lineItems);

        System.out.println();
        return mappedSortedLines;
    }

    public List<FormattedTextLine> buildParagraphs(TreeMap<BigDecimal, List<TextItem>> sortedLines) {
        Map<BigDecimal, BigDecimal> maxXs = new HashMap<>();

        TreeMap<BigDecimal, List<TextItem>> filteredSortedLines = moveInterlineTextItems(sortedLines);

        for (BigDecimal lineY : filteredSortedLines.navigableKeySet()) {
            List<TextItem> lineTextItems = filteredSortedLines.get(lineY);
            List<BigDecimal> xs = lineTextItems.stream()
                    .map(TextItem::getX).sorted()
                    .collect(Collectors.toList());
            maxXs.put(lineY, xs.get(xs.size() - 1));
            System.out.println();
        }

        double averageMaxX = computeAverage(maxXs.values());

        List<FormattedTextLine> combinedParagraphLines = new ArrayList<>();
        StringBuilder currentLineCombiner = new StringBuilder();

        TextItem previousTextItem;
        String currentFontFamily = UNSET_FONT_FAMILY;
        BigDecimal currentFontSize = UNSET_FONT_SIZE;

        BigDecimal lineY;
        for (BigDecimal linePosition : filteredSortedLines.navigableKeySet()) {
            lineY = linePosition;
            List<TextItem> lineTextItems = filteredSortedLines.get(lineY);

            previousTextItem = lineTextItems.get(0);
            currentFontFamily = previousTextItem.getFontFamilyName();
            currentFontSize = previousTextItem.getFontSize();

            currentLineCombiner.append(previousTextItem.getText());

            for (int i = 1; i < lineTextItems.size() - 1; i++) {
                previousTextItem = lineTextItems.get(i - 1);
                TextItem currentTextItem = lineTextItems.get(i);

                boolean sameFont = // currentTextItem.getFontFamilyName().equals(currentFontFamily)
                        currentTextItem.getFontSize().equals(currentFontSize);
                boolean isSpecialCharacter = !Character.isLetter(currentTextItem.getText().charAt(0));

                if (sameFont || isSpecialCharacter) {
                    double spacingBetweenTextItems = currentTextItem.getX().doubleValue() - previousTextItem.getXEnd();
                    if (spacingBetweenTextItems > 1.0)
                        currentLineCombiner.append(" ");
                    currentLineCombiner.append(currentTextItem.getText());
                } else {
                    combinedParagraphLines.add(new FormattedTextLine(currentLineCombiner.toString(), currentFontFamily, currentFontSize));
                    currentLineCombiner = new StringBuilder();
                    currentLineCombiner.append(currentTextItem.getText());
                    currentFontFamily = currentTextItem.getFontFamilyName();
                    currentFontSize = currentTextItem.getFontSize();
                }
            }

            TextItem lastTextItem = lineTextItems.get(lineTextItems.size() - 1);


            double currentLineMaxX = maxXs.get(lineY).doubleValue();
            boolean lineMaxXImpliesNewParagraph = Math.abs(currentLineMaxX - averageMaxX) > 30;
            boolean isSpecialCharacter = !Character.isLetter(lastTextItem.getText().charAt(0));
            if (lineTextItems.size() > 1) {
                if (lastTextItem.getX().doubleValue() - lineTextItems.get(lineTextItems.size() - 2).getXEnd() > 1.0)
                    currentLineCombiner.append(" ");
                currentLineCombiner.append(lastTextItem.getText());
            }
            if (lineMaxXImpliesNewParagraph && !isSpecialCharacter) {
                combinedParagraphLines.add(new FormattedTextLine(currentLineCombiner.toString(), currentFontFamily, currentFontSize));
                currentLineCombiner = new StringBuilder();
            } else {
                currentLineCombiner.append(" ");
            }
        }
        combinedParagraphLines.add(new FormattedTextLine(currentLineCombiner.toString(), currentFontFamily, currentFontSize));

        return combinedParagraphLines;

    }

    private static double computeAverage(Collection<BigDecimal> values) {
        double sum = 0.0;
        for (BigDecimal value : values)
            sum += value.doubleValue();

        double rawAverage = sum / values.size();

        List<Double> filteredValues = values.stream()
                .map(BigDecimal::doubleValue)
                .filter(value -> Math.abs(value - rawAverage) < 100)
                .collect(Collectors.toList());

        double filteredSum = 0.0;
        for (Double filteredValue : filteredValues)
            filteredSum += filteredValue;

        return filteredSum / filteredValues.size();
    }
}
