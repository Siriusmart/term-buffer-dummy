|Quick Navigation|
|---|
|[Java Implementation](https://github.com/Siriusmart/term-buffer-dummy/tree/master/app/src/main/java/ws/siri/termbuffer)|
|[Test cases](https://github.com/Siriusmart/term-buffer-dummy/tree/master/app/src/test/java/ws/siri/termbuffer)|

# Terminal Text Buffer

> [!NOTE]
> All code except TerminalBufferTestByClaude.java are human-written without assistance of generative AI tools.

## Design

### Scrollback Representation

Scrollback is a 2D list of **Cells**, each cell contains a character and the styling information.
- BG/FG colours for the 16 ANSI terminal colours.
- Bold/italics/underline boolean values.

### Screen Representation

Screen is a 1D array of Optional\<Cells\>, length screen.width * screen.height. Using a 1D array makes operations such as text wrapping, and shifting all characters back to make space when inserting a text simple to implement.

None represents an empty cell, here's the properties of an empty cell
- **Writing** overwrites over any cell regardless if it is empty or not.
- **Inserting** adds a new character in cursor position without overwriting any characters.
  It will try to do this by shifting the minimal number of characters when doing so.
  
  Consider the following buffer
  ```
  XXXXX|XX[   empty   ]XXX
  ```
  where `|` represents the cursor, writing `AAAA` to the buffer will result in
  ```
  XXXXX|AAAAXX[ empty ]XXX
  ```
  
  This behaviour allows for multiple lines to be edited in the same buffer, consider the buffer below
  ```
  X|X[  empty  ]
  XXX[  empty  ]
  x[   empty   ]
  ```
  Inserting a character at cursor position will not cause the rest of the lines to be modified unless the first line is full.

The length of the 1D array is maintained to be width * height after every operation.

### Alternative Designs

#### Cell Style Representation
The 16 fg and bg colours, bold, italic and underline can be represented in 2 bytes.
- 2 bit flags for whether fg and bg are default
- 8 bits total for fg and bg colours
- 3 bit flags for bold, italics and underline

For readability purposes, I won't be doing this optimisation in the dummy repository.

#### Scrollback Representation

It is trivial to use a 2D array for representing grid of cells. An alternative would be to use List\<Option\<Cell\>\> instead of List\<Cell\>.

But since scrollback cannot be modified, we do not need to distinguish between a cell that is empty or has a space in it. It is therefore of no interest to distinguish between the two.

#### Screen Representation

The obvious alternative representation would be to use a 2D array, but that would make inserting and line wrapping unnecessarily complicated.

Consider the following code which shifts all characters 1 to 5 back by one cell, handles line wrapping, and insert `A` to the first cell.
```java
for(int i = 0; i < 5; i++)
    screen[i + 1] = screen[i];
screen[0] = 'A';
```

Compared to the 2D array implementation, which would involve consideration of the screen width, cursor jumping to the start of a new line, it is really clean.

> [!NOTE]
> **Line wrapping is a behaviour of a single line trying to fit into a grid**, although visually the content is in a 2D grid, the representation should be a 1D array.

## Implementation

### Interface Functions

**ITerminalBuffer** specifies the minimum functions that a terminal buffer needs to implement.

> [!NOTE]
> insertText and writeText have a default implementation that uses insertChar and replaceChar.

| Function Name | Type Signature | Comment |
|---|---|---|
| getScreenDimension | () → Vec2I | Get screen dimension |
| getScrollbackLength | () → int | Get number of lines in scrollback |
| getCursorPos | () → Vec2I | Get cursor position on screen |
| setCursorFg | (Color color) → void | Set cursor foreground colour |
| setCursorBg | (Color color) → void | Set cursor background colour |
| setBold | (boolean b) → void | Set bold style to on/off |
| setItalic | (boolean b) → void | Set italics style to on/off |
| setUnderline | (boolean b) → void | Set underline style to on/off |
| setCursorX | (int x) → void | Set X position of cursor on screen |
| setCursorY | (int y) → void | Set Y position of cursor on screen |
| moveCursorUp | (int n) → void | Move cursor up by n cells; stops at screen boundary |
| moveCursorDown | (int n) → void | Move cursor down by n cells; stops at screen boundary |
| moveCursorLeft | (int n) → void | Move cursor left by n cells; stops at screen boundary, does not wrap |
| moveCursorRight | (int n) → void | Move cursor right by n cells; stops at screen boundary, does not wrap |
| replaceChar | (Optional\<Character\> c) → void | Replace character at cursor position, moving the cursor |
| insertChar | (Optional\<Character\> c) → void | Insert character at cursor position, moving the cursor |
| newEmptyLine | () → void *(default)* | Insert empty line at bottom of screen |
| newLine | (Optional\<Character\> c) → void | Insert line filled with specified character at bottom of screen |
| newLine | (char c) → void *(default)* | Insert line filled with specified character at bottom of screen |
| charAt | (Vec2I xy) → char | Get char at position; scrollback lines have negative y |
| styleAt | (Vec2I xy) → CellStyle | Get style attribute at position; scrollback lines have negative y |
| getLineString | (int y) → String *(default)* | Get line as string; scrollback lines have negative y |
| getScreenString | () → String *(default)* | Get screen content as string |
| getAllString | () → String *(default)* | Get screen and scrollback content as string |
| clearScreen | () → void | Clear screen content |
| clearAll | () → void | Clear screen and scrollback |
| markLineEmpty | (int y, boolean b) → void | Mark a line as used |
| writeText | (String s) → void *(default)* | Write text on a line, overriding current content; moves the cursor |
| insertText | (String s) → void *(default)* | Insert text on a line, possibly wrapping the line; moves the cursor |

### Internal Representation

The main class in TerminalBuffer.java contains the following fields.

|Field in TerminalBuffer|Type|Description|
|---|---|---|
|screenDimensions|Vec2I|Width and height of screen|
|cursorPos|Vec2I|XY position of cursor on screen, top left is (0,0)|
|maxScrollback|int|Maximum length of scrollback before oldest line are removed|
|cursorStyle|CellStyle|The style of the cell when the cursor writes to the buffer|
|scrollback|List\<List\<Cell\>\>|Scrollback content, blank cells are represented by a cells containing the whitespace character and the default style, they are not differentiated|
|screen|List\<Optional\<Cell\>\>|Modifiable screen content, Optional.empty represents an empty cell|
|lineIsEmpty|List\<Boolean\>|Whether a line on screen should be considered empty. A line with a new line character appears empty on this.screen, but should be marked as not empty|
