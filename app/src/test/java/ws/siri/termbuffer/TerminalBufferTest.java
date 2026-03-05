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

        assertEquals(term.getScreenString(), expected);
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

        assertEquals(term.getScreenString(), expected);
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

        assertEquals(term.getScreenString(), expected);
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

        assertEquals(term.getScreenString(), expected1);

        term.setCursorX(0);
        term.setCursorY(0);
        term.writeText("bye world");

        String expected2 = "bye w" + '\n'
                + "orldl" + '\n'
                + "d    " + '\n'
                + "     " + '\n'
                + "     ";

        assertEquals(term.getScreenString(), expected2);
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

        assertEquals(term.getScreenString(), expected1);

        term.setCursorX(0);
        term.setCursorY(0);
        term.insertText("bye world");

        String expected2 = "bye w" + '\n'
                + "orldh" + '\n'
                + "ello " + '\n'
                + "world" + '\n'
                + "     ";

        assertEquals(term.getScreenString(), expected2);
    }
}
