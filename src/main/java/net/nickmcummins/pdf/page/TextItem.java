package net.nickmcummins.pdf.page;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
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
    private final float width;

    public TextItem(TextRenderInfo textRenderInfo) {
        this.textRenderInfo = textRenderInfo;
        this.fontFamilyName = String.join("", textRenderInfo.getFont().getFamilyFontName()[0]);
        this.x = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(6)).setScale(2, RoundingMode.FLOOR);
        this.y = new BigDecimal(textRenderInfo.getTextToUserSpaceTransformMatrix().get(7) * -1).setScale(2, RoundingMode.FLOOR);
        this.fontSize = BigDecimal.valueOf(textRenderInfo.getTextToUserSpaceTransformMatrix().get(0)).setScale(0, RoundingMode.CEILING);
        this.width = ColumnText.getWidth(new Phrase(getText(), new Font(Font.FontFamily.HELVETICA, getFontSize().floatValue())));
    }

    public String getFontFamilyName() {
        return fontFamilyName;
    }

    public BigDecimal getFontSize() {
        return fontSize;
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
    public int compareTo(@Nonnull TextItem o) {
        return this.getX().compareTo(o.getX());
    }
}
