package net.nickmcummins.pdf.page;

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

    public FormattedTextLine(List<TextItem> lineItems) {
        sort(lineItems);
        this.lineItems = lineItems;
        this.formattedText = buildFormattedText();
    }

    public FormattedTextLine(String displayText, String fontFamilyName, BigDecimal fontSize) {
        this.displayText = displayText;
        this.fontFamilyName = fontFamilyName;
        this.fontSize = fontSize;
    }

    private String buildFormattedText() {
        StringBuilder sb = new StringBuilder();
        for (TextItem textItem : lineItems) {
            if (x == null) {
                x = textItem.getX();
                y = textItem.getY();
                fontFamilyName = textItem.getFontFamilyName();
                fontSize = textItem.getFontSize();
            }
            sb.append(textItem.getText());
        }

        return sb.toString();
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

    public String toString() {
        return displayText;
    }
}
