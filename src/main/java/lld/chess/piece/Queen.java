package lld.chess.piece;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.Position;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int dr = to.getRow() - from.getRow();
        int dc = to.getCol() - from.getCol();
        boolean straight = (dr == 0) ^ (dc == 0);
        boolean diagonal = dr != 0 && Math.abs(dr) == Math.abs(dc);
        if (!straight && !diagonal) {
            return false;
        }
        return board.isPathClear(from, to);
    }
}
