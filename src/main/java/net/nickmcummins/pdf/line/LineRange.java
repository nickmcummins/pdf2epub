package net.nickmcummins.pdf.line;

import net.nickmcummins.pdf.page.TextItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LineRange {
    private int startLine;
    private int endLine;
    private BigDecimal lineGap;
    private List<List<TextItem>> lines;

    public LineRange(int startLine) {
        this.startLine = startLine;
        this.lineGap = BigDecimal.ZERO;
        this.lines = new ArrayList<>();
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public BigDecimal getLineGap() {
        return lineGap;
    }

    public void setLineGap(BigDecimal lineGap) {
        this.lineGap = lineGap;
    }

    public void addLine(List<TextItem> line) {
        this.lines.add(line);
    }

    public String toString() {
        return String.format("[%d, %d, %f]", startLine, endLine, lineGap);
    }
}
