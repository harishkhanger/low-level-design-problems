package lld.chess.piece;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.Position;

public abstract class Piece {
    private final Color color;

    protected Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract boolean canMove(Board board, Position from, Position to);
}
