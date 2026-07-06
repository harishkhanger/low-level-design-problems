package lld.chess;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.GameStatus;
import lld.chess.model.Position;
import lld.chess.piece.Bishop;
import lld.chess.piece.King;
import lld.chess.piece.Knight;
import lld.chess.piece.Pawn;
import lld.chess.piece.Queen;
import lld.chess.piece.Rook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessTest {

    private Position pos(int r, int c) {
        return new Position(r, c);
    }

    @Test
    void rookMovesStraightButNotDiagonally() {
        Board board = new Board();
        Rook rook = new Rook(Color.WHITE);
        board.setPiece(pos(4, 4), rook);
        assertTrue(rook.canMove(board, pos(4, 4), pos(4, 0)));
        assertTrue(rook.canMove(board, pos(4, 4), pos(0, 4)));
        assertFalse(rook.canMove(board, pos(4, 4), pos(2, 2)));
    }

    @Test
    void rookIsBlockedByAPieceInItsPath() {
        Board board = new Board();
        Rook rook = new Rook(Color.WHITE);
        board.setPiece(pos(4, 4), rook);
        board.setPiece(pos(4, 6), new Pawn(Color.BLACK));
        assertFalse(rook.canMove(board, pos(4, 4), pos(4, 7)));
        assertTrue(rook.canMove(board, pos(4, 4), pos(4, 5)));
    }

    @Test
    void bishopMovesDiagonallyAndIsBlocked() {
        Board board = new Board();
        Bishop bishop = new Bishop(Color.WHITE);
        board.setPiece(pos(4, 4), bishop);
        assertTrue(bishop.canMove(board, pos(4, 4), pos(7, 7)));
        assertFalse(bishop.canMove(board, pos(4, 4), pos(4, 6)));
        board.setPiece(pos(5, 5), new Pawn(Color.WHITE));
        assertFalse(bishop.canMove(board, pos(4, 4), pos(6, 6)));
    }

    @Test
    void queenCombinesRookAndBishop() {
        Board board = new Board();
        Queen queen = new Queen(Color.WHITE);
        board.setPiece(pos(4, 4), queen);
        assertTrue(queen.canMove(board, pos(4, 4), pos(4, 0)));
        assertTrue(queen.canMove(board, pos(4, 4), pos(1, 1)));
        assertFalse(queen.canMove(board, pos(4, 4), pos(6, 5)));
    }

    @Test
    void knightJumpsOverOtherPieces() {
        Board board = Board.standard();
        Knight knight = (Knight) board.getPiece(pos(7, 1));
        assertTrue(knight.canMove(board, pos(7, 1), pos(5, 2)));
        assertFalse(knight.canMove(board, pos(7, 1), pos(5, 1)));
    }

    @Test
    void kingMovesExactlyOneSquare() {
        Board board = new Board();
        King king = new King(Color.WHITE);
        board.setPiece(pos(4, 4), king);
        assertTrue(king.canMove(board, pos(4, 4), pos(5, 5)));
        assertTrue(king.canMove(board, pos(4, 4), pos(4, 5)));
        assertFalse(king.canMove(board, pos(4, 4), pos(4, 6)));
    }

    @Test
    void pawnMovesOneOrTwoFromStartThenOnlyOne() {
        Board board = new Board();
        Pawn pawn = new Pawn(Color.WHITE);
        board.setPiece(pos(6, 4), pawn);
        assertTrue(pawn.canMove(board, pos(6, 4), pos(5, 4)));
        assertTrue(pawn.canMove(board, pos(6, 4), pos(4, 4)));

        Pawn moved = new Pawn(Color.WHITE);
        board.setPiece(pos(5, 0), moved);
        assertTrue(moved.canMove(board, pos(5, 0), pos(4, 0)));
        assertFalse(moved.canMove(board, pos(5, 0), pos(3, 0)));
    }

    @Test
    void pawnCapturesDiagonallyButNotForwardIntoAPiece() {
        Board board = new Board();
        Pawn pawn = new Pawn(Color.WHITE);
        board.setPiece(pos(6, 4), pawn);
        board.setPiece(pos(5, 5), new Pawn(Color.BLACK));
        board.setPiece(pos(5, 4), new Pawn(Color.BLACK));
        assertTrue(pawn.canMove(board, pos(6, 4), pos(5, 5)));
        assertFalse(pawn.canMove(board, pos(6, 4), pos(5, 4)));
        assertFalse(pawn.canMove(board, pos(6, 4), pos(5, 3)));
    }

    @Test
    void whiteMovesFirstAndTurnsAlternate() {
        Game game = Game.standard();
        assertEquals(Color.WHITE, game.getCurrentTurn());
        game.move(pos(6, 4), pos(4, 4));
        assertEquals(Color.BLACK, game.getCurrentTurn());
        game.move(pos(1, 4), pos(3, 4));
        assertEquals(Color.WHITE, game.getCurrentTurn());
    }

    @Test
    void movingOutOfTurnIsRejected() {
        Game game = Game.standard();
        assertThrows(IllegalStateException.class, () -> game.move(pos(1, 4), pos(3, 4)));
    }

    @Test
    void cannotCaptureYourOwnPiece() {
        Board board = new Board();
        board.setPiece(pos(0, 0), new Rook(Color.WHITE));
        board.setPiece(pos(0, 3), new Bishop(Color.WHITE));
        Game game = new Game(board);
        assertThrows(IllegalArgumentException.class, () -> game.move(pos(0, 0), pos(0, 3)));
    }

    @Test
    void capturingTheKingEndsTheGame() {
        Board board = new Board();
        board.setPiece(pos(4, 4), new Queen(Color.WHITE));
        board.setPiece(pos(4, 7), new King(Color.BLACK));
        Game game = new Game(board);
        game.move(pos(4, 4), pos(4, 7));
        assertEquals(GameStatus.WHITE_WON, game.getStatus());
        assertThrows(IllegalStateException.class, () -> game.move(pos(4, 7), pos(4, 6)));
    }
}
