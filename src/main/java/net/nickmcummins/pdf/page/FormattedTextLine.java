package net.nickmcummins.pdf.page;

import java.util.List;

import static java.util.Collections.sort;

public class FormattedTextLine {
    private List<TextItem> lineItems;
    private String formattedText;
    private String displayText;
    private String fontFamilyName;
    private float fontSize;

    public FormattedTextLine(List<TextItem> lineItems) {
        sort(lineItems);
        this.lineItems = lineItems;
        this.formattedText = buildFormattedText();
        this.fontFamilyName = lineItems.get(0).getFontFamilyName();
        this.fontSize = lineItems.get(0).getFontSize();
    }

    public String buildFormattedText() {
        StringBuilder sb = new StringBuilder();
        TextItem prevTextItem = null;
        for (TextItem textItem : lineItems) {
            if (prevTextItem != null) {
                //double gapFromPrevious = textItem.getX().subtract(prevTextItem.getX()).doubleValue() / ((double) prevTextItem.getText().length() + 1);
            }
            sb.append(textItem.getText());
            prevTextItem = textItem;
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

    public float getFontSize() {
        return fontSize;
    }

    public String toString() {
        return displayText;
    }
}
