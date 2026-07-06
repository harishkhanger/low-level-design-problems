package lld.chess.piece;

import lld.chess.model.Board;
import lld.chess.model.Color;
import lld.chess.model.Position;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int direction = getColor() == Color.WHITE ? -1 : 1;
        int startRow = getColor() == Color.WHITE ? 6 : 1;
        int dr = to.getRow() - from.getRow();
        int dc = to.getCol() - from.getCol();
        Piece target = board.getPiece(to);

        if (dc == 0) {
            if (dr == direction && target == null) {
                return true;
            }
            if (dr == 2 * direction && from.getRow() == startRow && target == null) {
                Position between = new Position(from.getRow() + direction, from.getCol());
                return board.isEmpty(between);
            }
            return false;
        }

        if (Math.abs(dc) == 1 && dr == direction) {
            return target != null && target.getColor() != getColor();
        }
        return false;
    }
}
