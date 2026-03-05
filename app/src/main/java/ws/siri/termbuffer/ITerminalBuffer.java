package ws.siri.termbuffer;

import java.util.Optional;

/**
 * ITerminalBuffer
 *
 * - Top left pixel on screen is (0,0)
 * - Scrollback has negative Y index
 */
public interface ITerminalBuffer {
    /**
     * get screen dimension
     */
    public Vec2I getScreenDimension();

    /**
     * get number of lines in scrollback
     */
    public int getScrollbackLength();

    /**
     * get cursor position on screen
     */
    public Vec2I getCursorPos();

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
    public void replaceChar(Optional<Character> c);

    /**
     * insert character at cursor position, moving the cursor
     */
    public void insertChar(Optional<Character> c);

    /**
     * insert empty line at bottom of screen
     */
    default void newEmptyLine() {
        newLine(Optional.empty());
    }

    /**
     * insert line filled with specified character at bottom of screen
     */
    public void newLine(Optional<Character> c);

    /**
     * insert line filled with specified character at bottom of screen
     */
    default void newLine(char c) {
        newLine(Optional.of(c));
    }

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
    default String getLineString(int y) {
        StringBuilder bobTheBuilder = new StringBuilder();
        Vec2I dimension = getScreenDimension();
        for (Vec2I pos = new Vec2I(0, y); pos.x < dimension.x; pos.x++)
            bobTheBuilder.append(charAt(pos));

        return bobTheBuilder.toString();
    }

    /**
     * get screen content as string
     */
    default String getScreenString() {
        StringBuilder bobTheBuilder = new StringBuilder();
        Vec2I dimension = getScreenDimension();

        for (int y = 0; y < dimension.y - 1; y++) {
            bobTheBuilder.append(getLineString(y));
            bobTheBuilder.append('\n');
        }

        bobTheBuilder.append(getLineString(dimension.y - 1));

        return bobTheBuilder.toString();
    }

    /**
     * get screen + scrollback content as string
     */
    default String getAllString() {
        StringBuilder bobTheBuilder = new StringBuilder();
        Vec2I dimension = getScreenDimension();

        for (int y = -getScrollbackLength(); y < dimension.y - 1; y++) {
            bobTheBuilder.append(getLineString(y));
            bobTheBuilder.append('\n');
        }

        bobTheBuilder.append(getLineString(dimension.y - 1));

        return bobTheBuilder.toString();
    }

    /**
     * clear sceen content
     */
    public void clearScreen();

    /**
     * clear screen and scrollback
     */
    public void clearAll();

    /**
     * Write a text on a line, overriding the current content. Moves the cursor.
     */
    default void writeText(String s) {
        for (char c : s.toCharArray()) {
            if (c != '\n') { // normal character (not linebreak)
                replaceChar(Optional.of(c));
                continue;
            }

            // special code for handling line breaks
            if (getCursorPos().y == getScreenDimension().y - 1) // at last line
                newEmptyLine();

            // move cursor to start of next line
            moveCursorDown(1);
            setCursorX(0);
        }
    }

    /*
     * Insert a text on a line, possibly wrapping the line. Moves the cursor.
     */
    default void insertText(String s) {
        for (char c : s.toCharArray()) {
            if (c != '\n') { // normal character (not linebreak)
                insertChar(Optional.of(c));
                continue;
            }

            // if newline, insert empty cells to current line to push everything else back
            int remainingCharacters = getScreenDimension().x - getCursorPos().x;
            for (int i = 0; i < remainingCharacters; i++)
                insertChar(Optional.empty());

            // move cursor to start of next line
            moveCursorDown(1);
            setCursorX(0);
        }
    }
}
