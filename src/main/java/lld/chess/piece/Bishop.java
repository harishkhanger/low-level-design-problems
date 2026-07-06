package lld.chess.piece;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.Position;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int dr = to.getRow() - from.getRow();
        int dc = to.getCol() - from.getCol();
        if (dr == 0 || Math.abs(dr) != Math.abs(dc)) {
            return false;
        }
        return board.isPathClear(from, to);
    }
}
