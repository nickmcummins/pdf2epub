package net.nickmcummins.pdf.line;

import java.math.BigDecimal;

public class LineRange {
    private int startLine;
    private int endLine;
    private BigDecimal lineGap;

    public LineRange(int startLine) {
        this.startLine = startLine;
        this.lineGap = BigDecimal.ZERO;
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

    public String toString() {
        return String.format("[%d, %d, %f]", startLine, endLine, lineGap);
    }
}
