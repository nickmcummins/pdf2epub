package net.nickmcummins.pdf.page;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.math.BigDecimal;

public class TextItem implements Comparable<TextItem> {
    private final TextRenderInfo textRenderInfo;
    private final String fontFamilyName;
    private final BigDecimal x;
    private final BigDecimal y;
    private final float width;

    public TextItem(TextRenderInfo textRenderInfo) {
        this.textRenderInfo = textRenderInfo;
        this.fontFamilyName = String.join("", textRenderInfo.getFont().getFamilyFontName()[0]);
        this.x = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(6));
        this.y = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(7) * -1);
        this.width = ColumnText.getWidth(new Phrase(getText(), new Font(Font.FontFamily.HELVETICA, getFontSize())));
    }

    public String getFontFamilyName() {
        return fontFamilyName;
    }

    public float getFontSize() {
        return textRenderInfo.getTextToUserSpaceTransformMatrix().get(0);
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    public String getText() {
        return textRenderInfo.getText().trim();
    }

    @Override
    public int compareTo(TextItem o) {
        return this.getX().compareTo(o.getX());
    }
}
