package de.team33.sphinx.delta.table.publics;

import de.team33.files.ui.Context;
import de.team33.files.ui.FileTree;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.delta.table.RowColumnModel;
import de.team33.sphinx.lambda.SwingApp;
import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JSplitPanes;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

final class RowModelTrial extends SwingApp {

    @SuppressWarnings("StaticCollection")
    private static final List<Column> COLUMNS = List.of(
            Column.NAME, Column.LAST_MODIFIED, Column.SIZE);

    private final FileTree.Context context = new Context();
    private final TableModel model = new FileModel(context.cwd());
    private final JTable fileTable = JTables.builder(model)
                                            .setAutoCreateRowSorter(true)
                                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                                            .build();

    public static void main(final String[] args) {
        start(new RowModelTrial());
    }

    @Override
    protected JFrame newFrame() {
        return JFrames.builder(getClass().getCanonicalName())
                      .setContentPane(JSplitPanes.builder()
                                                 //.setOrientation(JSplitPane.VERTICAL_SPLIT)
                                                 .setLeftComponent(FileTree.by(context).component())
                                                 .setRightComponent(new JScrollPane(fileTable))
                                                 .build())
                      .setup(jFrame -> context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString())))
                      .build();
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    private interface Column extends RowColumnModel.Column<File> {

        Column NAME = new ColumnImpl<>("Name", String.class, File::getName);
        Column LAST_MODIFIED = new ColumnImpl<>("Last Modified", Instant.class,
                                                file -> Instant.ofEpochMilli(file.lastModified()));
        Column SIZE = new ColumnImpl<>("Size", Long.class, File::length);
    }

    private record ColumnImpl<T>(String title,
                                 Class<T> type,
                                 Function<File, T> propertyFunction)
            implements Column {

        @Override
        public final T map(final File file) {
            return propertyFunction.apply(file);
        }

        @Override
        public final String toString() {
            return title;
        }
    }

    private static class FileModel extends RowColumnModel<File> {

        private volatile List<File> files = List.of();

        FileModel(final Retrievable<? extends Path> cwd) {
            cwd.subscribe(INIT, this::onSetCWD);
        }

        private void onSetCWD(final Path path) {
            this.files = List.of(path.toFile().listFiles());
            fireTableDataChanged();
        }

        @Override
        protected final List<? extends File> rows() {
            // Already is an immutable List ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return files;
        }

        @Override
        protected final List<RowModelTrial.Column> columns() {
            return COLUMNS;
        }
    }
}