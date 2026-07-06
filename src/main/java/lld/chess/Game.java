package lld.chess;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.GameStatus;
import lld.chess.model.Position;
import lld.chess.piece.King;
import lld.chess.piece.Piece;

public class Game {
    private final Board board;
    private Color currentTurn = Color.WHITE;
    private GameStatus status = GameStatus.IN_PROGRESS;

    public Game(Board board) {
        this.board = board;
    }

    public static Game standard() {
        return new Game(Board.standard());
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void move(Position from, Position to) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("game is over: " + status);
        }
        Piece piece = board.getPiece(from);
        if (piece == null) {
            throw new IllegalArgumentException("no piece at " + from);
        }
        if (piece.getColor() != currentTurn) {
            throw new IllegalStateException("it is " + currentTurn + "'s turn");
        }
        Piece target = board.getPiece(to);
        if (target != null && target.getColor() == piece.getColor()) {
            throw new IllegalArgumentException("cannot capture your own piece");
        }
        if (!piece.canMove(board, from, to)) {
            throw new IllegalArgumentException("illegal move for " + piece.getClass().getSimpleName());
        }

        boolean kingCaptured = target instanceof King;
        board.setPiece(to, piece);
        board.setPiece(from, null);

        if (kingCaptured) {
            status = currentTurn == Color.WHITE ? GameStatus.WHITE_WON : GameStatus.BLACK_WON;
        } else {
            currentTurn = currentTurn.opposite();
        }
    }
}
