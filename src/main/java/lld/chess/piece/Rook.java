package lld.chess.piece;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.Position;

public class Rook extends Piece {

    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int dr = to.getRow() - from.getRow();
        int dc = to.getCol() - from.getCol();
        if (dr != 0 && dc != 0) {
            return false;
        }
        if (dr == 0 && dc == 0) {
            return false;
        }
        return board.isPathClear(from, to);
    }
}
