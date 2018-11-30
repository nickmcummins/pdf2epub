package net.nickmcummins.pdf.page;

import net.nickmcummins.pdf.line.SimpleTextLineBuilderStrategy;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.sort;

public class FormattedTextLine {
    private List<TextItem> lineItems;
    private String formattedText;
    private String displayText;
    private String fontFamilyName;
    private BigDecimal fontSize;
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal minX;
    private BigDecimal maxX;

    public FormattedTextLine(List<TextItem> lineItems) {
        sort(lineItems);
        this.lineItems = lineItems;
        this.formattedText = new SimpleTextLineBuilderStrategy().buildFormattedText(lineItems);

        this.x = lineItems.get(0).getX();
        this.y = lineItems.get(0).getY();
        this.minX = lineItems.get(0).getX();
        this.maxX = lineItems.get(lineItems.size() - 1).getX();
        this.fontFamilyName = lineItems.get(0).getFontFamilyName();
        this.fontSize = lineItems.get(0).getFontSize();
    }

    public FormattedTextLine(String displayText, String fontFamilyName, BigDecimal fontSize) {
        this.displayText = displayText;
        this.fontFamilyName = fontFamilyName;
        this.fontSize = fontSize;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getFontFamilyName() {
        return fontFamilyName;
    }

    public BigDecimal getFontSize() {
        return fontSize;
    }

    public BigDecimal getY() {
        return y;
    }

    public BigDecimal getMinX() {
        return minX;
    }

    public BigDecimal getMaxX() {
        return maxX;
    }

    public String toString() {
        return displayText;
    }

}
