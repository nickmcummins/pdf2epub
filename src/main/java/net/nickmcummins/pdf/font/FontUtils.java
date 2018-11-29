package net.nickmcummins.pdf.font;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.nickmcummins.pdf.font.FontStyle.Bold;
import static net.nickmcummins.pdf.font.FontStyle.BoldItalic;

public class FontUtils {
    private static final Map<String, Font> FONT_EQUIVALENTS;
    static {
        Map<String, Font> fontEquivalents = new HashMap<>();
        fontEquivalents.put("KJPKKB+AdvP7D0F", new Font("Gill Sans", Bold));
        fontEquivalents.put("KJPKKA+AdvP7D0C", new Font("Gill Sans", BoldItalic));
        fontEquivalents.put("KJPKKC+AdvP7D09", new Font("Gill Sans"));

        FONT_EQUIVALENTS = Collections.unmodifiableMap(fontEquivalents);
    }

    public static Font getFontOrEquivalent(String fontName) {
        return FONT_EQUIVALENTS.containsKey(fontName)
                ? FONT_EQUIVALENTS.get(fontName)
                : new Font(fontName);
    }

}
