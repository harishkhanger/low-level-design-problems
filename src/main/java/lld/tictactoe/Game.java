package lld.tictactoe;

import lld.tictactoe.model.Board;
import lld.tictactoe.model.GameStatus;
import lld.tictactoe.model.Player;
import lld.tictactoe.model.Symbol;
import lld.tictactoe.strategy.WinningStrategy;

public class Game {
    private final Board board;
    private final Player[] players;   // exactly two
    private final WinningStrategy winningStrategy;
    private int currentPlayerIndex;
    private GameStatus status = GameStatus.IN_PROGRESS;

    public Game(int boardSize, Player first, Player second, WinningStrategy winningStrategy) {
        if (first.getSymbol() == second.getSymbol()) {
            throw new IllegalArgumentException("players must use different symbols");
        }
        this.board = new Board(boardSize);
        this.players = new Player[]{first, second};
        this.winningStrategy = winningStrategy;
        this.currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public GameStatus getStatus() {
        return status;
    }

    public Board getBoard() {
        return board;
    }

    public void makeMove(int row, int col) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("game is over: " + status);
        }
        Player current = getCurrentPlayer();
        Symbol symbol = current.getSymbol();
        board.place(row, col, symbol);

        if (winningStrategy.hasWon(board, symbol)) {
            status = (symbol == Symbol.X) ? GameStatus.X_WON : GameStatus.O_WON;
        } else if (board.isFull()) {
            status = GameStatus.DRAW;
        } else {
            currentPlayerIndex = 1 - currentPlayerIndex;   // toggle 0 <-> 1
        }
    }
}
