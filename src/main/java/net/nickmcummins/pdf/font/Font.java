package net.nickmcummins.pdf.font;

public class Font {
    private final String fontFamilyName;
    private final FontStyle fontStyle;

    public Font(String fontFamilyName, FontStyle fontStyle) {
        this.fontFamilyName = fontFamilyName;
        this.fontStyle = fontStyle;
    }

    public Font(String fontFamilyName) {
        this(fontFamilyName, FontStyle.Normal);
    }
}
