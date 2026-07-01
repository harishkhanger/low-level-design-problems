package lld.tictactoe.strategy;

import lld.tictactoe.model.Board;
import lld.tictactoe.model.Symbol;

public class ClassicWinningStrategy implements WinningStrategy {

    @Override
    public boolean hasWon(Board board, Symbol symbol) {
        int n = board.getSize();

        for (int r = 0; r < n; r++) {
            if (lineMatches(board, symbol, r, 0, 0, 1)) {
                return true;
            }
        }

        for (int c = 0; c < n; c++) {
            if (lineMatches(board, symbol, 0, c, 1, 0)) {
                return true;
            }
        }

        if (lineMatches(board, symbol, 0, 0, 1, 1)) {
            return true;
        }

        return lineMatches(board, symbol, 0, n - 1, 1, -1);
    }

    private boolean lineMatches(Board board, Symbol symbol,
                                int startRow, int startCol, int dRow, int dCol) {
        int n = board.getSize();
        int r = startRow;
        int c = startCol;
        for (int i = 0; i < n; i++) {
            if (board.get(r, c) != symbol) {
                return false;
            }
            r += dRow;
            c += dCol;
        }
        return true;
    }
}
