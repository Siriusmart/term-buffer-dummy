package ws.siri.termbuffer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * TerminalBufferTest
 */
public class TerminalBufferTest {
    @Test
    public void emptyTerminal() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);

        String expected = "     " + '\n'
                + "     " + '\n'
                + "     " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(expected, term.getScreenString());
    }

    @Test
    public void helloWorldWrite() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("hello world");

        String expected = "hello" + '\n'
                + " worl" + '\n'
                + "d    " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(expected, term.getScreenString());
    }

    @Test
    public void helloWorldInsert() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.insertText("hello world");

        String expected = "hello" + '\n'
                + " worl" + '\n'
                + "d    " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(expected, term.getScreenString());
    }

    @Test
    public void byeWorldWrite() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.writeText("hello world");

        String expected1 = "hello" + '\n'
                + " worl" + '\n'
                + "d    " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(expected1, term.getScreenString());

        term.setCursorX(0);
        term.setCursorY(0);
        term.writeText("bye world");

        String expected2 = "bye w" + '\n'
                + "orldl" + '\n'
                + "d    " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(expected2, term.getScreenString());
    }

    @Test
    public void byeWorldInsert() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);
        term.insertText("hello world");

        String expected1 = "hello" + '\n'
                + " worl" + '\n'
                + "d    " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(expected1, term.getScreenString());

        term.setCursorX(0);
        term.setCursorY(0);
        term.insertText("bye world");

        String expected2 = "bye w" + '\n'
                + "orldh" + '\n'
                + "ello " + '\n'
                + "world" + '\n'
                + "     ";

        assertEquals(expected2, term.getScreenString());
    }

    @Test
    public void newLine() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);

        term.newLine('A');
        String expected1 = "AAAAA" + '\n'
                + "     " + '\n'
                + "     " + '\n'
                + "     " + '\n'
                + "     ";
        assertEquals(expected1, term.getScreenString());

        term.newLine('B');
        String expected2 = "AAAAA" + '\n'
                + "BBBBB" + '\n'
                + "     " + '\n'
                + "     " + '\n'
                + "     ";
        assertEquals(expected2, term.getScreenString());

        term.newLine('C');
        term.newLine('D');
        term.newLine('E');
        String expected3 = "AAAAA" + '\n'
                + "BBBBB" + '\n'
                + "CCCCC" + '\n'
                + "DDDDD" + '\n'
                + "EEEEE";
        assertEquals(expected3, term.getScreenString());

        term.newLine('F');
        String expected4 = "BBBBB" + '\n'
                + "CCCCC" + '\n'
                + "DDDDD" + '\n'
                + "EEEEE" + '\n'
                + "FFFFF";
        assertEquals(expected4, term.getScreenString());

        String expected5 = "AAAAA" + '\n'
                + "BBBBB" + '\n'
                + "CCCCC" + '\n'
                + "DDDDD" + '\n'
                + "EEEEE" + '\n'
                + "FFFFF";
        assertEquals(expected5, term.getAllString());
    }

    @Test
    public void lineBreakTest() {
        TerminalBuffer term = new TerminalBuffer(5, 5, 100);

        term.writeText("a\n\nb\n\nc");
        String expected1 = "a    " + '\n'
                + "     " + '\n'
                + "b    " + '\n'
                + "     " + '\n'
                + "c    ";
        assertEquals(expected1, term.getScreenString());

        term.writeText("de");
        String expected2 = "a    " + '\n'
                + "     " + '\n'
                + "b    " + '\n'
                + "     " + '\n'
                + "cde  ";
        assertEquals(expected2, term.getScreenString());

        term.writeText("\n");
        String expected3 = "     " + '\n'
                + "b    " + '\n'
                + "     " + '\n'
                + "cde  " + '\n'
                + "     ";
        assertEquals(expected3, term.getScreenString());
    }
}
