package lld.texteditor.command;

public interface Command {
    void execute();
    void undo();
}
