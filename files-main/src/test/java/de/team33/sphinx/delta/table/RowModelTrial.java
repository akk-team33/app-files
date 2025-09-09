package de.team33.sphinx.delta.table;

import de.team33.files.testing.SwingTrial;
import de.team33.files.ui.Context;
import de.team33.files.ui.FileTree;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.metis.JSplitPanes;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import static javax.swing.SwingConstants.*;

final class RowModelTrial extends SwingTrial {

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
        run(new RowModelTrial());
    }

    @Override
    protected Container contentPane() {
        return JSplitPanes.builder()
                          //.setOrientation(JSplitPane.VERTICAL_SPLIT)
                          .setLeftComponent(FileTree.by(context).component())
                          .setRightComponent(new JScrollPane(fileTable))
                          .build();
    }

    @Override
    protected void setupFrame(final JFrame jFrame) {
        context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString()));
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    private interface Column extends RowModel.Column<File>, CellProperty.Column<File> {

        Column NAME = new ColumnImpl("Name", LEFT, Columns::nameProperty, Columns::nameToString, Columns.NAME_ORDER);
        Column LAST_MODIFIED = new ColumnImpl("Last Modified", CENTER, Columns::lastModifiedProperty,
                                              Columns::lastModifiedToString, Columns.LAST_MODIFIED_ORDER);
        Column SIZE = new ColumnImpl("Size", RIGHT, Columns::sizeProperty, Columns::sizeToString, Columns.SIZE_ORDER);
    }

    private static class Columns {

        private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
        private static final Comparator<String> RESPECT_CASE = String::compareTo;
        private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
        private static final Comparator<File> NAME_ORDER = Comparator.comparing(File::getName, STRING_ORDER);
        private static final Comparator<File> LAST_MODIFIED_ORDER = Comparator.comparing(File::lastModified);
        private static final Comparator<File> SIZE_ORDER = Comparator.comparing(File::length);

        private static CellProperty<File, Column> nameProperty(final File file) {
            return new CellProperty<>(file, Column.NAME);
        }

        private static CellProperty<File, Column> lastModifiedProperty(final File file) {
            return new CellProperty<>(file, Column.LAST_MODIFIED);
        }

        private static CellProperty<File, Column> sizeProperty(final File file) {
            return new CellProperty<>(file, Column.SIZE);
        }

        private static String nameToString(final File file) {
            return file.getName();
        }

        private static String lastModifiedToString(final File file) {
            return Instant.ofEpochMilli(file.lastModified()).toString();
        }

        private static String sizeToString(final File file) {
            return "%,d".formatted(file.length());
        }
    }

    private record ColumnImpl(String title,
                              int horizontalAlignment,
                              Function<File, CellProperty<File, Column>> propertyFunction,
                              Function<File, String> toStringFunction,
                              Comparator<File> order)
            implements Column {

        @Override
        public final String toString(final File file) {
            return toStringFunction.apply(file);
        }

        @Override
        public final Class<?> type() {
            return CellProperty.class;
        }

        @Override
        public final CellProperty<File, Column> map(final File file) {
            return propertyFunction.apply(file);
        }

        @Override
        public final String toString() {
            return title;
        }
    }

    private static class FileModel extends RowModel<File> {

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