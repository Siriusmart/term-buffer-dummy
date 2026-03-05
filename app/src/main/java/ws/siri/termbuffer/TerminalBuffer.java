package ws.siri.termbuffer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TerminalBuffer implements ITerminalBuffer {
    private Vec2I screenDimensions;
    private Vec2I cursorPos;
    private int maxScrollback;

    private CellStyle cursorStyle;

    private class Cell {
        public CellStyle style;
        public char content;

        public Cell(CellStyle style, char content) {
            this.style = style;
            this.content = content;
        }
    }

    // linked list for O(1) pushing/popping
    //
    // empty cells will be stored as default style with content space
    private LinkedList<List<Cell>> scrollback;
    // one dimensional array for easy inserting
    //
    // screen is maintained so that its length is exactly width * height
    private ArrayList<Optional<Cell>> screen;

    /**
     * Create new terminal buffer with initial settings
     */
    public TerminalBuffer(int screenWidth, int screenHeight, int maxScrollback) {
        this.screenDimensions = new Vec2I(screenWidth, screenHeight);
        this.maxScrollback = maxScrollback;
        this.cursorPos = new Vec2I(0, 0);

        this.scrollback = new LinkedList<>(); // empty
        this.screen = new ArrayList<>(screenWidth * screenHeight);

        // initialise screen with empty cells
        for (int i = 0; i < screenWidth * screenHeight; i++)
            screen.add(Optional.empty());
    }

    @Override
    public Vec2I getScreenDimension() {
        return screenDimensions;
    }

    @Override
    public int getScrollbackLength() {
        return scrollback.size();
    }

    @Override
    public Vec2I getCursorPos() {
        return cursorPos;
    }

    @Override
    public void setCursorFg(Color color) {
        cursorStyle = cursorStyle.withFg(color);
    }

    @Override
    public void setCursorBg(Color color) {
        cursorStyle = cursorStyle.withBg(color);
    }

    @Override
    public void setBold(boolean b) {
        cursorStyle = cursorStyle.withBold(b);
    }

    @Override
    public void setItalic(boolean b) {
        cursorStyle = cursorStyle.withItalic(b);
    }

    @Override
    public void setUnderline(boolean b) {
        cursorStyle = cursorStyle.withUnderline(b);
    }

    @Override
    public void setCursorX(int x) {
        this.cursorPos.x = Math.clamp(x, 0, screenDimensions.x - 1);
    }

    @Override
    public void setCursorY(int y) {
        this.cursorPos.y = Math.clamp(y, 0, screenDimensions.y - 1);
    }

    @Override
    public void moveCursorUp(int n) {
        this.setCursorY(cursorPos.y - n);
    }

    @Override
    public void moveCursorDown(int n) {
        this.setCursorY(cursorPos.y + n);
    }

    @Override
    public void moveCursorLeft(int n) {
        this.setCursorX(cursorPos.x - n);
    }

    @Override
    public void moveCursorRight(int n) {
        this.setCursorX(cursorPos.x + n);
    }

    @Override
    public void replaceChar(Optional<Character> c) {
        Optional<Cell> cell = c.isPresent() ? Optional.of(new Cell(cursorStyle, c.get())) : Optional.empty();
        screen.set(getCursorIndex(), cell);
        wrappingMoveRight();
    }

    @Override
    public void insertChar(Optional<Character> c) {
        Optional<Cell> cell = c.isPresent() ? Optional.of(new Cell(cursorStyle, c.get())) : Optional.empty();

        int cur = getCursorIndex();
        int i = cur;
        // search for next empty position
        while (i < screen.size() && screen.get(i).isPresent())
            i++;

        if (i == screen.size()) { // new line has to be created
            Optional<Cell> lastCell = screen.get(screen.size() - 1);
            for (int j = i - 1; j > cur; j++) // note: the last cell is no longer on screen after this
                screen.set(j, screen.get(j - 1));

            screen.set(cur, cell);

            // add new line with the first cell being the cell that we havent yet added
            screen.add(lastCell);
            for (int j = 1; j < screenDimensions.x; j++)
                screen.add(Optional.empty());

            flushScreen(); // remove the extra line we just added
            wrappingMoveRight();
        } else { // new line does not have to be created
            for (int j = i; j < cur; j++)
                screen.set(j, screen.get(j - 1));

            screen.set(cur, cell);
            wrappingMoveRight();
        }
    }

    @Override
    public void newLine(Optional<Character> c) {
        for (int i = 0; i < screenDimensions.x; i++)
            if (c.isPresent())
                screen.add(Optional.of(new Cell(cursorStyle, c.get())));
            else
                screen.add(Optional.empty());

        flushScreen();
    }

    @Override
    public char charAt(Vec2I xy) {
        if (xy.x < 0 || xy.x >= screenDimensions.x)
            throw new RuntimeException(String.format("x=%d on screen with width %d", xy.x, screenDimensions.x));

        if (xy.y >= 0) { // on screen
            if (xy.y >= screenDimensions.y)
                throw new RuntimeException(String.format("y=%d on screen with height %d", xy.y, screenDimensions.y));

            Optional<Cell> cell = screen.get(xy.y * screenDimensions.x + xy.x);
            return cell.isPresent() ? cell.get().content : ' ';
        } else {
            if ((-xy.y) <= scrollback.size())
                throw new RuntimeException(String.format("y=%d on scrollback with %d lines", xy.y, scrollback.size()));

            return scrollback.get(scrollback.size() + xy.y).get(xy.y).content;
        }
    }

    @Override
    public CellStyle styleAt(Vec2I xy) {
        if (xy.x < 0 || xy.x >= screenDimensions.x)
            throw new RuntimeException(String.format("x=%d on screen with width %d", xy.x, screenDimensions.x));

        if (xy.y >= 0) { // on screen
            if (xy.y >= screenDimensions.y)
                throw new RuntimeException(String.format("y=%d on screen with height %d", xy.y, screenDimensions.y));

            Optional<Cell> cell = screen.get(xy.y * screenDimensions.x + xy.x);
            return cell.isPresent() ? cell.get().style : CellStyle.empty();
        } else {
            if ((-xy.y) <= scrollback.size())
                throw new RuntimeException(String.format("y=%d on scrollback with %d lines", xy.y, scrollback.size()));

            return scrollback.get(scrollback.size() + xy.y).get(xy.y).style;
        }
    }

    /**
     * gets cursor index on this.screen
     */
    private int getCursorIndex() {
        return cursorPos.y * screenDimensions.x + cursorPos.x;
    }

    /**
     * move right
     * - wraps if reaches end of line
     * - creates new line if reaches end of screen
     */
    private void wrappingMoveRight() {
        if (cursorPos.x != screenDimensions.x - 1) { // if not end of line
            moveCursorRight(1);
            return;
        }

        if (cursorPos.y == screenDimensions.y - 1) // if end of line
            newEmptyLine();

        moveCursorDown(1);
        moveCursorLeft(cursorPos.x);
    }

    /**
     * remove first line if there are too many lines on screen
     *
     * also keeps scrollback lines to leq maximum
     */
    private void flushScreen() {
        // do nothing if screen does not have too many lines
        if (screen.size() == screenDimensions.x * screenDimensions.y)
            return;

        // add first line to scrollback
        List<Cell> firstLine = screen.subList(0, screenDimensions.x).stream()
                .map((ocell) -> (ocell.isPresent() ? ocell.get() : new Cell(CellStyle.empty(), ' '))).toList();
        scrollback.addLast(firstLine);

        if (scrollback.size() > maxScrollback)
            scrollback.removeFirst();

        // remove line from screen
        screen.subList(0, screenDimensions.x).clear();
        cursorPos.y--; // correct cursor position
    }

    @Override
    public void clearScreen() {
        for (int i = 0; i < screen.size(); i++)
            screen.set(i, Optional.empty());
    }

    @Override
    public void clearAll() {
        clearScreen();
        scrollback.clear();
    }
}
