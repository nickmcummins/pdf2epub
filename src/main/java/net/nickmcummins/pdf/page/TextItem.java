package net.nickmcummins.pdf.page;

import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TextItem implements Comparable<TextItem> {
    private final TextRenderInfo textRenderInfo;
    private final String fontFamilyName;
    private final BigDecimal x;
    private final BigDecimal y;

    public TextItem(TextRenderInfo textRenderInfo) {
        this.textRenderInfo = textRenderInfo;
        this.fontFamilyName = Arrays.stream(textRenderInfo.getFont().getFamilyFontName()[0]).collect(Collectors.joining(""));
        this.x = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(6));
        this.y = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(7) * -1);
    }

    public String getFontFamilyName() {
        return fontFamilyName;
    }

    public int getFontSize() {
        return (int)textRenderInfo.getGs().getFontSize();
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    @Override
    public int compareTo(TextItem o) {
        return this.getX().compareTo(o.getX());
    }
}
