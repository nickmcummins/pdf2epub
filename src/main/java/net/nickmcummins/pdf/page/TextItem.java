package net.nickmcummins.pdf.page;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TextItem implements Comparable<TextItem> {
    private final TextRenderInfo textRenderInfo;
    private final String fontFamilyName;
    private final BigDecimal x;
    private final BigDecimal y;
    private final BigDecimal fontSize;
    private final BigDecimal gsFontSize;
    private final String gsFontColor;
    private final boolean isItalic;
    private final double xEnd;

    public TextItem(TextRenderInfo textRenderInfo) {
        this.textRenderInfo = textRenderInfo;
        this.fontFamilyName = String.join("", textRenderInfo.getFont().getFamilyFontName()[0]);
        this.x = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(6)).setScale(2, RoundingMode.FLOOR);
        this.y = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(7) * -1).setScale(2, RoundingMode.FLOOR);
        this.fontSize = BigDecimal.valueOf(textRenderInfo.getTextToUserSpaceTransformMatrix().get(0)).setScale(2, RoundingMode.CEILING);
        //this.width = ColumnText.getXEnd(new Phrase(getText(), new Font(Font.FontFamily.HELVETICA, getFontSize().floatValue())));
        this.gsFontSize = BigDecimal.valueOf(textRenderInfo.getGs().getFontSize());
        this.gsFontColor = rgbToHex(textRenderInfo.getGs().getFillColor());
        this.isItalic = textRenderInfo.getGs().getFont().getFamilyFontName()[0][3].toLowerCase().contains("italic");
        this.xEnd = x.doubleValue() + (textRenderInfo.getUnscaledWidth() * this.fontSize.doubleValue());
    }

    public String getFontFamilyName() {
        return fontFamilyName;
    }

    public BigDecimal getFontSize() {
        return gsFontSize;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }


    public double getXEnd() {
        return xEnd;
    }

    public String getText() {
        return textRenderInfo.getText();
    }

    @Override
    public int compareTo(@Nonnull TextItem o) {
        return this.getX().compareTo(o.getX());
    }

    public String toString() {
        return String.format("%s (%s, %s, %s)", getText(), String.valueOf(x), String.valueOf(xEnd), String.valueOf(y));
    }

    public static String rgbToHex(BaseColor color) {
        if (color == null)
            return "#000000";
        return rgbToHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String rgbToHex(int r, int g, int b)
    {
        return String.format("#%02x%02x%02x", r, g, b);
    }

}
