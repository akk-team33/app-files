package de.team33.sphinx.gamma.table;

/**
 * @param <R> The type that represents the data of each row as a whole.
 * @param <P> The type that represents the cell property of the column in question.
 */
public interface Column<R, P> {

    String title();

    Class<P> type();

    P map(R row);
}
