package net.nickmcummins.pdf.paragraph;

import net.nickmcummins.pdf.page.FormattedTextLine;

import java.util.List;

public interface TextLineCombinationStrategy {
    List<FormattedTextLine> combine(List<FormattedTextLine> formattedTextLines);
}
