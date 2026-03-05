import org.junit.Test;
import ws.siri.termbuffer.*;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * TerminalBufferTestByClaude
 *
 * Extended test suite covering normal, boundary, and edge cases
 * for TerminalBuffer.
 */
public class TerminalBufferTestByClaude {

    // =========================================================================
    // 1. CONSTRUCTION & INITIAL STATE
    // =========================================================================

    /** Minimal 1x1 terminal is initialised correctly */
    @Test
    public void init_1x1() {
        TerminalBuffer term = new TerminalBuffer(1, 1, 10);
        assertEquals("", term.getScreenString().replace(" ", "").replace("\n", ""));
        assertEquals(new Vec2I(0, 0), term.getCursorPos());
    }

    /** 1-wide terminal has correct dimensions */
    @Test
    public void init_1wide() {
        TerminalBuffer term = new TerminalBuffer(1, 5, 10);
        assertEquals(new Vec2I(1, 5), term.getScreenDimension());
    }

    /** 1-tall terminal has correct dimensions */
    @Test
    public void init_1tall() {
        TerminalBuffer term = new TerminalBuffer(5, 1, 10);
        assertEquals(new Vec2I(5, 1), term.getScreenDimension());
    }

    /** Screen dimension is reported correctly for normal sizes */
    @Test
    public void init_dimensions() {
        TerminalBuffer term = new TerminalBuffer(80, 24, 500);
        assertEquals(new Vec2I(80, 24), term.getScreenDimension());
    }

    /** Scrollback starts empty */
    @Test
    public void init_scrollbackEmpty() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        assertEquals(0, term.getScrollbackLength());
    }

    /** Cursor starts at (0,0) */
    @Test
    public void init_cursorAtOrigin() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        assertEquals(new Vec2I(0, 0), term.getCursorPos());
    }

    /** Every cell in a fresh terminal is a space */
    @Test
    public void init_allSpaces() {
        TerminalBuffer term = new TerminalBuffer(4, 3, 100);
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 4; x++)
                assertEquals(' ', term.charAt(new Vec2I(x, y)));
    }

    // =========================================================================
    // 2. setCursorX / setCursorY
    // =========================================================================

    /** setCursorX moves cursor to specified column */
    @Test
    public void setCursorX_normal() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.setCursorX(7);
        assertEquals(7, term.getCursorPos().x);
    }

    /** setCursorY moves cursor to specified row */
    @Test
    public void setCursorY_normal() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.setCursorY(4);
        assertEquals(4, term.getCursorPos().y);
    }

    /** setCursorX clamps at 0 for negative input */
    @Test
    public void setCursorX_clampNegative() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.setCursorX(-5);
        assertEquals(0, term.getCursorPos().x);
    }

    /** setCursorY clamps at 0 for negative input */
    @Test
    public void setCursorY_clampNegative() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.setCursorY(-1);
        assertEquals(0, term.getCursorPos().y);
    }

    /** setCursorX clamps at width-1 for out-of-bounds input */
    @Test
    public void setCursorX_clampMax() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(100);
        assertEquals(4, term.getCursorPos().x);
    }

    /** setCursorY clamps at height-1 for out-of-bounds input */
    @Test
    public void setCursorY_clampMax() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorY(100);
        assertEquals(4, term.getCursorPos().y);
    }

    /** setCursorX to last valid column (width-1) */
    @Test
    public void setCursorX_lastColumn() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(4);
        assertEquals(4, term.getCursorPos().x);
    }

    /** setCursorY to last valid row (height-1) */
    @Test
    public void setCursorY_lastRow() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorY(4);
        assertEquals(4, term.getCursorPos().y);
    }

    // =========================================================================
    // 3. moveCursor*
    // =========================================================================

    /** moveCursorRight by 1 increments x */
    @Test
    public void moveCursorRight_normal() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.moveCursorRight(3);
        assertEquals(3, term.getCursorPos().x);
    }

    /** moveCursorLeft by 1 decrements x */
    @Test
    public void moveCursorLeft_normal() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.setCursorX(5);
        term.moveCursorLeft(2);
        assertEquals(3, term.getCursorPos().x);
    }

    /** moveCursorDown increments y */
    @Test
    public void moveCursorDown_normal() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.moveCursorDown(3);
        assertEquals(3, term.getCursorPos().y);
    }

    /** moveCursorUp decrements y */
    @Test
    public void moveCursorUp_normal() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.setCursorY(5);
        term.moveCursorUp(2);
        assertEquals(3, term.getCursorPos().y);
    }

    /** moveCursorLeft clamps at left boundary */
    @Test
    public void moveCursorLeft_clamp() {
        TerminalBuffer term = new TerminalBuffer(10, 10, 100);
        term.moveCursorLeft(10);
        assertEquals(0, term.getCursorPos().x);
    }

    /** moveCursorRight clamps at right boundary */
    @Test
    public void moveCursorRight_clamp() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.moveCursorRight(100);
        assertEquals(4, term.getCursorPos().x);
    }

    /** moveCursorUp clamps at top boundary */
    @Test
    public void moveCursorUp_clamp() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.moveCursorUp(10);
        assertEquals(0, term.getCursorPos().y);
    }

    /** moveCursorDown clamps at bottom boundary */
    @Test
    public void moveCursorDown_clamp() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.moveCursorDown(100);
        assertEquals(4, term.getCursorPos().y);
    }

    // =========================================================================
    // 4. replaceChar (writeText helpers)
    // =========================================================================

    /** replaceChar writes a character at cursor and advances it */
    @Test
    public void replaceChar_writesCharacter() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.replaceChar(Optional.of('Z'));
        assertEquals('Z', term.charAt(new Vec2I(0, 0)));
        assertEquals(new Vec2I(1, 0), term.getCursorPos());
    }

    /** replaceChar with empty Optional writes a space */
    @Test
    public void replaceChar_emptyIsSpace() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("A");
        term.setCursorX(0);
        term.setCursorY(0);
        term.replaceChar(Optional.empty());
        assertEquals(' ', term.charAt(new Vec2I(0, 0)));
    }

    /** replaceChar at end of line wraps to next line */
    @Test
    public void replaceChar_wrapsToNextLine() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.setCursorX(2);
        term.replaceChar(Optional.of('X'));
        assertEquals(0, term.getCursorPos().x);
        assertEquals(1, term.getCursorPos().y);
    }

    /** replaceChar at bottom-right corner creates new line */
    @Test
    public void replaceChar_atBottomRightCreatesNewLine() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.setCursorX(2);
        term.setCursorY(2);
        term.replaceChar(Optional.of('X'));
        // cursor should now be at 0,2 after scroll
        assertEquals(0, term.getCursorPos().x);
    }

    // =========================================================================
    // 5. writeText – normal cases
    // =========================================================================

    /** Single character is written at (0,0) */
    @Test
    public void writeText_singleChar() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("A");
        assertEquals('A', term.charAt(new Vec2I(0, 0)));
        assertEquals(new Vec2I(1, 0), term.getCursorPos());
    }

    /** Exactly filling one line leaves cursor at start of next line */
    @Test
    public void writeText_exactlyOneLine() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("12345");
        assertEquals("12345", term.getLineString(0));
        assertEquals(new Vec2I(0, 1), term.getCursorPos());
    }

    /** Writing two full lines places them correctly */
    @Test
    public void writeText_twoFullLines() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("AAAAABBBBB");
        assertEquals("AAAAA", term.getLineString(0));
        assertEquals("BBBBB", term.getLineString(1));
    }

    /** Writing past the bottom scrolls the screen */
    @Test
    public void writeText_scrollsPastBottom() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.writeText("ABCDEF"); // fills both lines exactly
        term.writeText("G"); // should scroll
        assertEquals(1, term.getScrollbackLength());
    }

    /** Writing at mid-screen position (cursor already moved) works correctly */
    @Test
    public void writeText_fromMidScreen() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(2);
        term.setCursorY(1);
        term.writeText("HI");
        assertEquals('H', term.charAt(new Vec2I(2, 1)));
        assertEquals('I', term.charAt(new Vec2I(3, 1)));
    }

    /** Explicit \n in writeText moves cursor to next line start */
    @Test
    public void writeText_newlineMovesToNextLine() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("AB\nCD");
        assertEquals('A', term.charAt(new Vec2I(0, 0)));
        assertEquals('B', term.charAt(new Vec2I(1, 0)));
        assertEquals('C', term.charAt(new Vec2I(0, 1)));
        assertEquals('D', term.charAt(new Vec2I(1, 1)));
    }

    /** Multiple consecutive \n characters leave empty lines */
    @Test
    public void writeText_multipleNewlines() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("A\n\n\nB");
        assertEquals('A', term.charAt(new Vec2I(0, 0)));
        assertEquals(' ', term.charAt(new Vec2I(0, 1)));
        assertEquals(' ', term.charAt(new Vec2I(0, 2)));
        assertEquals('B', term.charAt(new Vec2I(0, 3)));
    }

    /** writeText does not alter cells beyond what was written */
    @Test
    public void writeText_doesNotCorruptOtherCells() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("AB");
        assertEquals(' ', term.charAt(new Vec2I(2, 0)));
        assertEquals(' ', term.charAt(new Vec2I(3, 0)));
    }

    // =========================================================================
    // 6. insertText – normal and overwrite comparison
    // =========================================================================

    /** insertText on empty buffer is identical to writeText */
    @Test
    public void insertText_emptyBufferMatchesWrite() {
        TerminalBuffer t1 = new TerminalBuffer(5, 5, 100);
        TerminalBuffer t2 = new TerminalBuffer(5, 5, 100);
        t1.writeText("hello world");
        t2.insertText("hello world");
        assertEquals(t1.getScreenString(), t2.getScreenString());
    }

    /** insertText pushes existing content to the right */
    @Test
    public void insertText_pushesRight() {
        TerminalBuffer term = new TerminalBuffer(6, 3, 100);
        term.writeText("BCDE");
        term.setCursorX(0);
        term.setCursorY(0);
        term.insertText("A");
        assertEquals('A', term.charAt(new Vec2I(0, 0)));
        assertEquals('B', term.charAt(new Vec2I(1, 0)));
        assertEquals('C', term.charAt(new Vec2I(2, 0)));
    }

    /** insertText with \n pushes entire remainder of line to next line */
    @Test
    public void insertText_newlinePushesRemainder() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("ABCDE");
        term.setCursorX(2);
        term.setCursorY(0);
        term.insertText("\n");
        assertEquals('A', term.charAt(new Vec2I(0, 0)));
        assertEquals('B', term.charAt(new Vec2I(1, 0)));
        // C, D, E should now be on next line
        assertEquals('C', term.charAt(new Vec2I(0, 1)));
    }

    // =========================================================================
    // 7. newLine
    // =========================================================================

    /** newLine fills a line with the given character */
    @Test
    public void newLine_fillsWithChar() {
        TerminalBuffer term = new TerminalBuffer(4, 4, 100);
        term.newLine('X');
        assertEquals("XXXX", term.getLineString(0));
    }

    /** newLine does not move the cursor */
    @Test
    public void newLine_doesNotMoveCursor() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(2);
        term.setCursorY(3);
        term.newLine('A');
        assertEquals(new Vec2I(2, 3), term.getCursorPos());
    }

    /** newLine adds line at bottom when screen is not full */
    @Test
    public void newLine_addsAtBottom() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.newLine('A');
        term.newLine('B');
        assertEquals("AAA", term.getLineString(0));
        assertEquals("BBB", term.getLineString(1));
        assertEquals("   ", term.getLineString(2));
    }

    /** Overfilling with newLine pushes oldest line to scrollback */
    @Test
    public void newLine_scrollsToScrollback() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        assertEquals(1, term.getScrollbackLength());
    }

    /** newEmptyLine produces a blank line */
    @Test
    public void newEmptyLine_isAllSpaces() {
        TerminalBuffer term = new TerminalBuffer(4, 4, 100);
        term.newLine('X');
        term.newEmptyLine();
        assertEquals("    ", term.getLineString(1));
    }

    // =========================================================================
    // 8. Scrollback
    // =========================================================================

    /** Scrollback accumulates lines in order */
    @Test
    public void scrollback_orderPreserved() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A'); // line 0
        term.newLine('B'); // line 1 → scrolls A off
        term.newLine('C'); // scrolls B off
        // scrollback should have A then B
        String all = term.getAllString();
        int idxA = all.indexOf("AAA");
        int idxB = all.indexOf("BBB");
        assertTrue(idxA < idxB);
    }

    /** Scrollback is bounded by maxScrollback */
    @Test
    public void scrollback_respectedMaxScrollback() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 3);
        for (int i = 0; i < 10; i++)
            term.newLine((char) ('A' + i));
        assertTrue(term.getScrollbackLength() <= 3);
    }

    /** With maxScrollback=0, scrollback stays empty */
    @Test
    public void scrollback_zeroMax() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 0);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        assertEquals(0, term.getScrollbackLength());
    }

    /** charAt with negative y reads from scrollback */
    @Test
    public void scrollback_charAtNegativeY() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C'); // A goes to scrollback at y=-1
        assertEquals('A', term.charAt(new Vec2I(0, -1)));
    }

    /** getAllString includes scrollback lines before screen lines */
    @Test
    public void scrollback_getAllStringIncludes() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        String all = term.getAllString();
        assertTrue(all.contains("AAA"));
        assertTrue(all.contains("BBB"));
        assertTrue(all.contains("CCC"));
    }

    // =========================================================================
    // 9. clearScreen and clearAll
    // =========================================================================

    /** clearScreen sets every cell to space */
    @Test
    public void clearScreen_allSpaces() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("hello world");
        term.clearScreen();
        assertEquals(
                "     \n     \n     \n     \n     ",
                term.getScreenString());
    }

    /** clearScreen does not remove scrollback */
    @Test
    public void clearScreen_preservesScrollback() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        int before = term.getScrollbackLength();
        term.clearScreen();
        assertEquals(before, term.getScrollbackLength());
    }

    /** clearAll removes both screen and scrollback */
    @Test
    public void clearAll_removesScrollback() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        term.clearAll();
        assertEquals(0, term.getScrollbackLength());
    }

    /** clearAll leaves screen as all spaces */
    @Test
    public void clearAll_screenIsSpaces() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.writeText("ABCDEF");
        term.clearAll();
        assertEquals("   \n   \n   ", term.getScreenString());
    }

    /** Calling clearScreen twice is idempotent */
    @Test
    public void clearScreen_idempotent() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.writeText("ABC");
        term.clearScreen();
        String first = term.getScreenString();
        term.clearScreen();
        assertEquals(first, term.getScreenString());
    }

    /** Calling clearAll twice is idempotent */
    @Test
    public void clearAll_idempotent() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.writeText("ABC");
        term.clearAll();
        String first = term.getAllString();
        term.clearAll();
        assertEquals(first, term.getAllString());
    }

    // =========================================================================
    // 10. getLineString
    // =========================================================================

    /** getLineString returns correct content for a written line */
    @Test
    public void getLineString_writtenLine() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("ABCDE");
        assertEquals("ABCDE", term.getLineString(0));
    }

    /** getLineString returns spaces for an unwritten line */
    @Test
    public void getLineString_emptyLine() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        assertEquals("     ", term.getLineString(2));
    }

    /** getLineString at y=0 after scroll reflects scrolled content */
    @Test
    public void getLineString_afterScroll() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        // screen now has B, C
        assertEquals("BBB", term.getLineString(0));
        assertEquals("CCC", term.getLineString(1));
    }

    // =========================================================================
    // 11. Overwrite behaviour
    // =========================================================================

    /** Writing over existing content replaces it character-by-character */
    @Test
    public void write_overwritesExisting() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("AAAAA");
        term.setCursorX(0);
        term.setCursorY(0);
        term.writeText("BB");
        assertEquals("BBAAA", term.getLineString(0));
    }

    /** Writing a shorter string does not erase tail of existing line */
    @Test
    public void write_shorterStringKeepsTail() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("ABCDE");
        term.setCursorX(0);
        term.setCursorY(0);
        term.writeText("XY");
        assertEquals("XYCDE", term.getLineString(0));
    }

    // =========================================================================
    // 12. Cursor after wrapping
    // =========================================================================

    /** Cursor x is reset to 0 after wrapping to next line via writeText */
    @Test
    public void cursor_xResetOnWrap() {
        TerminalBuffer term = new TerminalBuffer(3, 5, 100);
        term.writeText("ABCD"); // A,B,C fills line 0 → wraps; D at (0,1)
        assertEquals(1, term.getCursorPos().x);
        assertEquals(1, term.getCursorPos().y);
    }

    /** Cursor moves down after filling a line exactly */
    @Test
    public void cursor_movesDownAfterFullLine() {
        TerminalBuffer term = new TerminalBuffer(3, 5, 100);
        term.writeText("ABC"); // exactly fills line 0
        assertEquals(0, term.getCursorPos().x);
        assertEquals(1, term.getCursorPos().y);
    }

    /** Cursor stays at (0, lastRow) after scrolling */
    @Test
    public void cursor_afterScrollStaysOnScreen() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.writeText("ABCDEF"); // fills both lines → cursor at (0,1)
        int prevY = term.getCursorPos().y;
        term.writeText("G"); // forces scroll
        assertTrue(term.getCursorPos().y <= 1);
    }

    // =========================================================================
    // 13. markLineEmpty
    // =========================================================================

    /** markLineEmpty(y, true) causes newLine to reuse that line */
    @Test
    public void markLineEmpty_allowsReuseByNewLine() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.newLine('A');
        term.newLine('B');
        term.markLineEmpty(1, true); // mark line 1 (BBB) as empty
        term.newLine('C'); // should fill line 1 (the first empty)
        assertEquals("CCC", term.getLineString(1));
    }

    // =========================================================================
    // 14. Edge cases – 1-column terminal
    // =========================================================================

    /** Writing to a 1-column terminal wraps every character to a new row */
    @Test
    public void oneColumn_writesDownwards() {
        TerminalBuffer term = new TerminalBuffer(1, 5, 100);
        term.writeText("ABCDE");
        assertEquals("A", term.getLineString(-1));
        assertEquals("B", term.getLineString(0));
        assertEquals("C", term.getLineString(1));
        assertEquals("D", term.getLineString(2));
        assertEquals("E", term.getLineString(3));
    }

    /** 1-column terminal scrolls correctly */
    @Test
    public void oneColumn_scrolls() {
        TerminalBuffer term = new TerminalBuffer(1, 2, 100);
        term.writeText("ABC");
        assertEquals(2, term.getScrollbackLength());
    }

    // =========================================================================
    // 15. Edge cases – 1-row terminal
    // =========================================================================

    /** Writing to a 1-row terminal fills it and scrolls */
    @Test
    public void oneRow_fillsAndScrolls() {
        TerminalBuffer term = new TerminalBuffer(3, 1, 100);
        term.writeText("ABCDEF");
        assertEquals(2, term.getScrollbackLength());
    }

    /** 1x1 terminal: writing two characters keeps one in scrollback */
    @Test
    public void oneByOne_scrollbackAfterTwoWrites() {
        TerminalBuffer term = new TerminalBuffer(1, 1, 100);
        term.writeText("AB");
        assertEquals(2, term.getScrollbackLength());
    }

    // =========================================================================
    // 16. Multiple writes on same row without moving cursor
    // =========================================================================

    /** Writing character-by-character matches writing full string */
    @Test
    public void write_charByCharMatchesFull() {
        TerminalBuffer t1 = new TerminalBuffer(10, 5, 100);
        TerminalBuffer t2 = new TerminalBuffer(10, 5, 100);
        t1.writeText("hello");
        for (char c : "hello".toCharArray())
            t2.writeText(String.valueOf(c));
        assertEquals(t1.getScreenString(), t2.getScreenString());
    }

    // =========================================================================
    // 17. writeText vs insertText diverge when buffer non-empty
    // =========================================================================

    /** writeText and insertText produce different results when content exists */
    @Test
    public void writeVsInsert_differ() {
        TerminalBuffer t1 = new TerminalBuffer(5, 5, 100);
        TerminalBuffer t2 = new TerminalBuffer(5, 5, 100);
        t1.writeText("AAAAA");
        t2.writeText("AAAAA");
        t1.setCursorX(0);
        t1.setCursorY(0);
        t2.setCursorX(0);
        t2.setCursorY(0);
        t1.writeText("B");
        t2.insertText("B");
        assertNotEquals(t1.getScreenString(), t2.getScreenString());
    }

    // =========================================================================
    // 18. Scrollback content correctness
    // =========================================================================

    /** First line written is first scrollback line after overflow */
    @Test
    public void scrollback_firstLineIsOldest() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.writeText("ABCDEF"); // ABC on row 0, DEF on row 1
        term.writeText("GHI"); // scroll: ABC goes to scrollback
        assertEquals('D', term.charAt(new Vec2I(0, -1)));
        /*
         * ABC
         * DEF
         * GHI
         */
    }

    /** Scrollback character at x=0 of pushed line is correct */
    @Test
    public void scrollback_charAtXzero() {
        TerminalBuffer term = new TerminalBuffer(4, 2, 100);
        term.writeText("ABCDEFGHIJ"); // fills 2 lines, then J pushes A line
        // check scrollback line
        assertEquals('A', term.charAt(new Vec2I(0, -1)));
    }

    // =========================================================================
    // 19. getScreenString format
    // =========================================================================

    /** getScreenString has exactly (height-1) newlines */
    @Test
    public void getScreenString_newlineCount() {
        TerminalBuffer term = new TerminalBuffer(5, 4, 100);
        String s = term.getScreenString();
        long count = s.chars().filter(c -> c == '\n').count();
        assertEquals(3, count);
    }

    /** getScreenString does not end with a newline */
    @Test
    public void getScreenString_noTrailingNewline() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        String s = term.getScreenString();
        assertFalse(s.endsWith("\n"));
    }

    /** getAllString with empty scrollback equals getScreenString */
    @Test
    public void getAllString_noScrollbackEqualsScreen() {
        TerminalBuffer term = new TerminalBuffer(5, 3, 100);
        term.writeText("ABC");
        assertEquals(term.getScreenString(), term.getAllString());
    }

    // =========================================================================
    // 20. Mixed operations
    // =========================================================================

    /** Write, clear, write again gives only the second write */
    @Test
    public void mixed_writeClearWrite() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("AAAAA");
        term.clearAll();
        term.writeText("BBBBB"); // cursor pos does not reset on clear
        assertEquals("     ", term.getLineString(0));
        assertEquals("BBBBB", term.getLineString(1));
    }

    /** Cursor position is preserved across newLine calls */
    @Test
    public void mixed_cursorPreservedAcrossNewLine() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(3);
        term.setCursorY(2);
        term.newLine('Z');
        assertEquals(new Vec2I(3, 2), term.getCursorPos());
    }

    /** Writing after a manual cursor repositioning goes to right place */
    @Test
    public void mixed_writeAfterManualCursorMove() {
        TerminalBuffer term = new TerminalBuffer(10, 5, 100);
        term.writeText("HELLO");
        term.setCursorX(0);
        term.setCursorY(2);
        term.writeText("WORLD");
        assertEquals("HELLO     ", term.getLineString(0));
        assertEquals("WORLD     ", term.getLineString(2));
    }

    /** Interleaving newLine and writeText produces correct output */
    @Test
    public void mixed_newLineAndWriteText() {
        TerminalBuffer term = new TerminalBuffer(5, 4, 100);
        term.newLine('='); // line 0: =====
        term.setCursorY(1);
        term.writeText("HI"); // line 1: HI (cursor already on row 1 due to newLine)
        assertEquals("=====", term.getLineString(0));
        assertEquals('H', term.charAt(new Vec2I(0, 1)));
        assertEquals('I', term.charAt(new Vec2I(1, 1)));
    }

    /** After scrolling, old lines are accessible via getAllString */
    @Test
    public void mixed_scrolledLinesInAll() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('1');
        term.newLine('2');
        term.newLine('3'); // 1 scrolls to scrollback
        String all = term.getAllString();
        assertTrue(all.startsWith("111"));
    }

    /** Writing empty string does not change anything */
    @Test
    public void writeText_emptyString() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        String before = term.getScreenString();
        term.writeText("");
        assertEquals(before, term.getScreenString());
        assertEquals(new Vec2I(0, 0), term.getCursorPos());
    }

    /** insertText empty string does not change anything */
    @Test
    public void insertText_emptyString() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        String before = term.getScreenString();
        term.insertText("");
        assertEquals(before, term.getScreenString());
        assertEquals(new Vec2I(0, 0), term.getCursorPos());
    }

    /** clearScreen followed by getAllString still shows scrollback */
    @Test
    public void clearScreen_getAllStringShowsScrollback() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C'); // A→scrollback
        term.clearScreen();
        String all = term.getAllString();
        assertTrue(all.contains("AAA"));
    }

    /** writeText of exactly screen-size string fills screen completely */
    @Test
    public void writeText_fillsEntireScreen() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.writeText("ABCDEFGHI");
        assertEquals("ABC", term.getLineString(-1));
        assertEquals("DEF", term.getLineString(0));
        assertEquals("GHI", term.getLineString(1));
    }

    /** Two insertTexts at same position produces correct interleaved result */
    @Test
    public void insertText_twice_atSamePosition() {
        TerminalBuffer term = new TerminalBuffer(8, 3, 100);
        term.writeText("BD"); // BD______
        term.setCursorX(0);
        term.setCursorY(0);
        term.insertText("A"); // ABD_____
        term.setCursorX(2);
        term.setCursorY(0);
        term.insertText("C"); // ABCD____
        assertEquals('A', term.charAt(new Vec2I(0, 0)));
        assertEquals('B', term.charAt(new Vec2I(1, 0)));
        assertEquals('C', term.charAt(new Vec2I(2, 0)));
        assertEquals('D', term.charAt(new Vec2I(3, 0)));
    }

    /** Cursor y after \n at last line causes scroll and stays on screen */
    @Test
    public void writeText_newlineAtLastLine_staysOnScreen() {
        TerminalBuffer term = new TerminalBuffer(5, 3, 100);
        term.setCursorY(2);
        term.writeText("\n");
        assertTrue(term.getCursorPos().y < 3);
        assertTrue(term.getCursorPos().y >= 0);
    }

    /** Scrollback maxScrollback=1 keeps only single line */
    @Test
    public void scrollback_maxOne() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 1);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        term.newLine('D');
        assertEquals(1, term.getScrollbackLength());
    }

    /** Writing single char at each position fills the screen correctly */
    @Test
    public void writeText_fillByCharAt() {
        TerminalBuffer term = new TerminalBuffer(3, 2, 100);
        term.replaceChar(Optional.of('1'));
        term.replaceChar(Optional.of('2'));
        term.replaceChar(Optional.of('3'));
        term.replaceChar(Optional.of('4'));
        term.replaceChar(Optional.of('5'));
        term.replaceChar(Optional.of('6'));
        assertEquals("123", term.getLineString(-1));
        assertEquals("456", term.getLineString(0));
    }

    /** newLine with space character fills line with spaces (same as empty) */
    @Test
    public void newLine_spaceCharEqualsEmpty() {
        TerminalBuffer t1 = new TerminalBuffer(4, 4, 100);
        TerminalBuffer t2 = new TerminalBuffer(4, 4, 100);
        t1.newLine(' ');
        t2.newEmptyLine();
        assertEquals(t1.getLineString(0), t2.getLineString(0));
    }

    /** getAllString line count equals screen height + scrollback length */
    @Test
    public void getAllString_lineCount() {
        TerminalBuffer term = new TerminalBuffer(3, 3, 100);
        term.newLine('A');
        term.newLine('B');
        term.newLine('C');
        term.newLine('D'); // 1 scrollback
        String all = term.getAllString();
        long lines = all.lines().count();
        assertEquals(term.getScreenDimension().y + term.getScrollbackLength(), lines);
    }

    /** moveCursorRight by 0 keeps position the same */
    @Test
    public void moveCursorRight_byZero() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(2);
        term.moveCursorRight(0);
        assertEquals(2, term.getCursorPos().x);
    }

    /** moveCursorLeft by 0 keeps position the same */
    @Test
    public void moveCursorLeft_byZero() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorX(2);
        term.moveCursorLeft(0);
        assertEquals(2, term.getCursorPos().x);
    }

    /** moveCursorDown by 0 keeps position the same */
    @Test
    public void moveCursorDown_byZero() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorY(2);
        term.moveCursorDown(0);
        assertEquals(2, term.getCursorPos().y);
    }

    /** moveCursorUp by 0 keeps position the same */
    @Test
    public void moveCursorUp_byZero() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.setCursorY(2);
        term.moveCursorUp(0);
        assertEquals(2, term.getCursorPos().y);
    }
}
