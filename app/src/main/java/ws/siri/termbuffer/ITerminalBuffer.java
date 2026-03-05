package ws.siri.termbuffer;

/**
 * ITerminalBuffer
 */
public interface ITerminalBuffer {
    /**
     * Set cursor foreground colour
     */
    public void setCursorFg(Color color);

    /**
     * Set cursor background colour
     */
    public void setCursorBg(Color color);

    /**
     * Set bold style to on/off
     */
    public void setBold(boolean b);

    /**
     * Set italics style to on/off
     */
    public void setItalic(boolean b);

    /**
     * Set underline style to on/off
     */
    public void setUnderline(boolean b);

    /**
     * set X position of cursor on screen
     */
    public void setCursorX(int x);

    /**
     * set Y position of cursor on screen
     */
    public void setCursorY(int y);

    /**
     * move cursor up by n cell, stops when reaches screen boundary
     */
    public void moveCursorUp(int n);

    /**
     * move cursor down by n cell, stops when reaches screen boundary
     */
    public void moveCursorDown(int n);

    /**
     * move cursor left by n cell, stops when reaches screen boundary, does not wrap
     * around
     */
    public void moveCursorLeft(int n);

    /**
     * move cursor right by n cell, stops when reaches screen boundary, does not
     * wrap around
     */
    public void moveCursorRight(int n);

    /**
     * replaces character at cursor position, moving the cursor
     */
    public void replaceChar(char c);

    /**
     * insert character at cursor position, moving the cursor
     */
    public void insertChar(char c);

    /**
     * insert empty line at bottom of screen
     */
    public void newEmptyLine();

    /**
     * insert line filled with specified character at bottom of screen
     */
    public void newLine(char c);

    /**
     * get char at position
     *
     * lines in scrollback has negative y
     */
    public char charAt(Vec2I xy);

    /**
     * get style attribute at position
     *
     * lines in scrollback has negative y
     */
    public CellStyle styleAt(Vec2I xy);

    /**
     * get line as string
     *
     * lines in scrollback has negative y
     */
    public String getLineString(int y);

    /**
     * get screen content as string
     */
    public String getScreenString();

    /**
     * get screen + scrollback content as string
     */
    public String getAllString();
}
