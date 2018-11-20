package net.nickmcummins.pdf;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import net.nickmcummins.pdf.page.TextItem;

import java.util.ArrayList;
import java.util.List;

public class MyRenderListener implements RenderListener {
    private final String filename;
    public final List<TextItem> textItems;

    public MyRenderListener(String filename) {
        this.filename = filename;
        this.textItems = new ArrayList<>();
    }

    @Override
    public void beginTextBlock() {

    }

    @Override
    public void renderText(TextRenderInfo textRenderInfo) {
        if (textRenderInfo.getText() != null) {
            TextItem textItem = new TextItem(textRenderInfo);
            textItems.add(textItem);
        }
    }

    @Override
    public void endTextBlock() {

    }

    @Override
    public void renderImage(ImageRenderInfo imageRenderInfo) {

    }
}
