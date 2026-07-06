package lld.chess.model;

import lld.chess.piece.Bishop;
import lld.chess.piece.King;
import lld.chess.piece.Knight;
import lld.chess.piece.Pawn;
import lld.chess.piece.Piece;
import lld.chess.piece.Queen;
import lld.chess.piece.Rook;

public class Board {
    private final Piece[][] grid = new Piece[8][8];

    public Piece getPiece(Position position) {
        return grid[position.getRow()][position.getCol()];
    }

    public void setPiece(Position position, Piece piece) {
        grid[position.getRow()][position.getCol()] = piece;
    }

    public boolean isEmpty(Position position) {
        return getPiece(position) == null;
    }

    public boolean isPathClear(Position from, Position to) {
        int dr = Integer.signum(to.getRow() - from.getRow());
        int dc = Integer.signum(to.getCol() - from.getCol());
        int r = from.getRow() + dr;
        int c = from.getCol() + dc;
        while (r != to.getRow() || c != to.getCol()) {
            if (grid[r][c] != null) {
                return false;
            }
            r += dr;
            c += dc;
        }
        return true;
    }

    public static Board standard() {
        Board board = new Board();
        board.placeBackRank(0, Color.BLACK);
        board.placePawns(1, Color.BLACK);
        board.placePawns(6, Color.WHITE);
        board.placeBackRank(7, Color.WHITE);
        return board;
    }

    private void placeBackRank(int row, Color color) {
        setPiece(new Position(row, 0), new Rook(color));
        setPiece(new Position(row, 1), new Knight(color));
        setPiece(new Position(row, 2), new Bishop(color));
        setPiece(new Position(row, 3), new Queen(color));
        setPiece(new Position(row, 4), new King(color));
        setPiece(new Position(row, 5), new Bishop(color));
        setPiece(new Position(row, 6), new Knight(color));
        setPiece(new Position(row, 7), new Rook(color));
    }

    private void placePawns(int row, Color color) {
        for (int col = 0; col < 8; col++) {
            setPiece(new Position(row, col), new Pawn(color));
        }
    }
}
