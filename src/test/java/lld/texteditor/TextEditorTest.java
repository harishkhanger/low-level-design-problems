package lld.texteditor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextEditorTest {

    @Test
    void insertBuildsUpText() {
        TextEditor editor = new TextEditor();
        editor.insert(0, "hello");
        editor.insert(5, " world");
        assertEquals("hello world", editor.getText());
    }

    @Test
    void undoReversesLastInsert() {
        TextEditor editor = new TextEditor();
        editor.insert(0, "hello");
        editor.insert(5, " world");

        editor.undo();
        assertEquals("hello", editor.getText());
    }

    @Test
    void redoReappliesUndoneInsert() {
        TextEditor editor = new TextEditor();
        editor.insert(0, "hello");
        editor.insert(5, " world");

        editor.undo();
        editor.redo();
        assertEquals("hello world", editor.getText());
    }

    @Test
    void deleteThenUndoRestoresExactCharacters() {
        TextEditor editor = new TextEditor();
        editor.insert(0, "hello world");

        editor.delete(5, 6);
        assertEquals("hello", editor.getText());

        editor.undo();
        assertEquals("hello world", editor.getText());
    }

    @Test
    void newEditAfterUndoClearsRedoStack() {
        TextEditor editor = new TextEditor();
        editor.insert(0, "hello");
        editor.insert(5, " world");

        editor.undo();
        editor.insert(5, " there");
        assertEquals("hello there", editor.getText());

        editor.redo();
        assertEquals("hello there", editor.getText());
    }

    @Test
    void undoRedoAcrossMixedOperations() {
        TextEditor editor = new TextEditor();
        editor.insert(0, "abc");
        editor.insert(3, "def");
        editor.delete(0, 3);

        editor.undo();
        assertEquals("abcdef", editor.getText());
        editor.undo();
        assertEquals("abc", editor.getText());
        editor.redo();                    
        assertEquals("abcdef", editor.getText());
    }

    @Test
    void undoOnEmptyHistoryIsNoOp() {
        TextEditor editor = new TextEditor();
        editor.undo();
        editor.redo();
        assertEquals("", editor.getText());
    }
}
