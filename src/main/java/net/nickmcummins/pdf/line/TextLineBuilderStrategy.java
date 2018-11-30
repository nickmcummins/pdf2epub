package net.nickmcummins.pdf.line;

import net.nickmcummins.pdf.page.TextItem;

import java.util.List;

public interface TextLineBuilderStrategy {
    String buildFormattedText(List<TextItem> lineItems);
}
