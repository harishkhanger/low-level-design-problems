package lld.tictactoe.model;


public class Board {
    private final int size;
    private final Symbol[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Symbol[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = Symbol.EMPTY;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty(int row, int col) {
        return grid[row][col] == Symbol.EMPTY;
    }

    public void place(int row, int col, Symbol symbol) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IllegalArgumentException("cell out of range: " + row + "," + col);
        }
        if (!isEmpty(row, col)) {
            throw new IllegalStateException("cell already taken: " + row + "," + col);
        }
        grid[row][col] = symbol;
    }

    public Symbol get(int row, int col) {
        return grid[row][col];
    }

    public boolean isFull() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == Symbol.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
