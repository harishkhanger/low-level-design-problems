package lld.tictactoe.strategy;

import lld.tictactoe.model.Board;
import lld.tictactoe.model.Symbol;

public interface WinningStrategy {
    boolean hasWon(Board board, Symbol symbol);
}
