package net.nickmcummins.pdf.line;

import net.nickmcummins.pdf.page.TextItem;

import java.util.List;

public class SimpleTextLineBuilderStrategy implements TextLineBuilderStrategy {


    @Override
    public String buildFormattedText(List<TextItem> lineItems) {
        StringBuilder sb = new StringBuilder();
        for (TextItem textItem : lineItems) {
            sb.append(textItem.getText());
        }

        return sb.toString();
    }
}
