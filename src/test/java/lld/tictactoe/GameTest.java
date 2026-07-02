package lld.tictactoe;

import lld.tictactoe.model.GameStatus;
import lld.tictactoe.model.Player;
import lld.tictactoe.model.Symbol;
import lld.tictactoe.strategy.ClassicWinningStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {

    private Game newGame() {
        return new Game(3,
            new Player("Alice", Symbol.X),
            new Player("Bob", Symbol.O),
            new ClassicWinningStrategy());
    }

    @Test
    void xWinsTopRow() {
        Game game = newGame();
        game.makeMove(0, 0);   // X
        game.makeMove(1, 0);   // O
        game.makeMove(0, 1);   // X
        game.makeMove(1, 1);   // O
        game.makeMove(0, 2);   // X completes top row
        assertEquals(GameStatus.X_WON, game.getStatus());
    }

    @Test
    void oWinsFirstColumn() {
        Game game = newGame();
        game.makeMove(0, 1);   // X
        game.makeMove(0, 0);   // O
        game.makeMove(0, 2);   // X
        game.makeMove(1, 0);   // O
        game.makeMove(2, 2);   // X
        game.makeMove(2, 0);   // O completes first column
        assertEquals(GameStatus.O_WON, game.getStatus());
    }

    @Test
    void xWinsMainDiagonal() {
        Game game = newGame();
        game.makeMove(0, 0);   // X
        game.makeMove(0, 1);   // O
        game.makeMove(1, 1);   // X
        game.makeMove(0, 2);   // O
        game.makeMove(2, 2);   // X completes main diagonal
        assertEquals(GameStatus.X_WON, game.getStatus());
    }

    @Test
    void fullBoardWithNoLineIsDraw() {
        Game game = newGame();
        // X O X
        // X O O
        // O X X
        game.makeMove(0, 0); // X
        game.makeMove(0, 1); // O
        game.makeMove(0, 2); // X
        game.makeMove(1, 1); // O
        game.makeMove(1, 0); // X
        game.makeMove(1, 2); // O
        game.makeMove(2, 1); // X
        game.makeMove(2, 0); // O
        game.makeMove(2, 2); // X
        assertEquals(GameStatus.DRAW, game.getStatus());
    }

    @Test
    void turnsAlternateBetweenPlayers() {
        Game game = newGame();
        assertEquals(Symbol.X, game.getCurrentPlayer().getSymbol());
        game.makeMove(0, 0);
        assertEquals(Symbol.O, game.getCurrentPlayer().getSymbol());
        game.makeMove(1, 1);
        assertEquals(Symbol.X, game.getCurrentPlayer().getSymbol());
    }

    @Test
    void moveAfterGameOverThrows() {
        Game game = newGame();
        game.makeMove(0, 0);   // X
        game.makeMove(1, 0);   // O
        game.makeMove(0, 1);   // X
        game.makeMove(1, 1);   // O
        game.makeMove(0, 2);   // X wins
        assertThrows(IllegalStateException.class, () -> game.makeMove(2, 2));
    }

    @Test
    void moveOnTakenCellThrows() {
        Game game = newGame();
        game.makeMove(1, 1);   // X
        assertThrows(IllegalStateException.class, () -> game.makeMove(1, 1));
    }

    @Test
    void playersWithSameSymbolRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            new Game(3,
                new Player("Alice", Symbol.X),
                new Player("Bob", Symbol.X),
                new ClassicWinningStrategy()));
    }
}
