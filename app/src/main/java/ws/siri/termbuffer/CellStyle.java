package ws.siri.termbuffer;

/**
 * CellStyle
 */
public record CellStyle(Color fg, Color bg, boolean bold, boolean italic, boolean underline) {
    /**
     * create copy of cell style with specified fg colour
     */
    public CellStyle withFg(Color fg) {
        return new CellStyle(fg, bg, bold, italic, underline);
    }

    /**
     * create copy of cell style with specified bg colour
     */
    public CellStyle withBg(Color bg) {
        return new CellStyle(fg, bg, bold, italic, underline);
    }

    /**
     * create copy of cell style with specified bold style
     */
    public CellStyle withBold(boolean bold) {
        return new CellStyle(fg, bg, bold, italic, underline);
    }

    /**
     * create copy of cell style with specified italic style
     */
    public CellStyle withItalic(boolean italic) {
        return new CellStyle(fg, bg, bold, italic, underline);
    }

    /**
     * create copy of cell style with specified italic style
     */
    public CellStyle withUnderline(boolean underline) {
        return new CellStyle(fg, bg, bold, italic, underline);
    }
}
