package lld.chess.piece;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.Position;

public class Knight extends Piece {

    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int dr = Math.abs(to.getRow() - from.getRow());
        int dc = Math.abs(to.getCol() - from.getCol());
        return (dr == 1 && dc == 2) || (dr == 2 && dc == 1);
    }
}
