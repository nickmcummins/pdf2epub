package net.nickmcummins.pdf.line;

public class LineRange {
    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    private int startLine;
    private int endLine;

    public LineRange(int startLine) {
        this.startLine = startLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String toString() {
        return String.format("[%d, %d]", startLine, endLine);
    }
}
