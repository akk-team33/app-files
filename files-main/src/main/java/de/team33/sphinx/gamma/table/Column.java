package de.team33.sphinx.gamma.table;

/**
 * Represents a table column description.
 *
 * @param <R> The type that represents the content of a table row as a whole.
 */
public interface Column<R> {

    String title();

    Class<?> type();

    Object map(R row);
}
