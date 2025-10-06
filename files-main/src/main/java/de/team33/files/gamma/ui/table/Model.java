package de.team33.files.gamma.ui.table;

import de.team33.files.gamma.ui.FileTable;
import de.team33.patterns.io.delta.FileEntry;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.delta.table.RowColumnModel;

import java.nio.file.Path;
import java.util.List;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class Model extends RowColumnModel<Entry> {

    private final Retrievable<? extends Path> cwd;
    private final Retrievable<? extends List<FileTable.Column>> columns;
    private volatile List<Entry> entries = List.of();

    public Model(final Retrievable<? extends Path> cwd,
                 final Retrievable<? extends List<FileTable.Column>> columns) {
        this.cwd = cwd;
        this.columns = columns;
        cwd.subscribe(INIT, this::onSetPath);
        columns.subscribe(ignored -> fireTableStructureChanged());
    }

    private void onSetPath(final Path path) {
        this.entries = FileEntry.of(path)
                                .entries()
                                .map(entry -> new Entry(cwd, entry))
                                .toList();
        fireTableDataChanged();
    }

    @Override
    protected final List<Entry> rows() {
        // Already IS immutable ...
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return entries;
    }

    @Override
    protected final List<FileTable.Column> columns() {
        return columns.get();
    }
}
