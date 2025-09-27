package de.team33.sphinx.delta.table.publics;

import de.team33.files.ui.Context;
import de.team33.files.ui.FileTree;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.delta.table.CellProperty;
import de.team33.sphinx.delta.table.CellRenderer;
import de.team33.sphinx.delta.table.HeadRenderer;
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
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import static javax.swing.SwingConstants.*;

final class RendererTrial extends SwingApp {

    @SuppressWarnings("StaticCollection")
    private static final List<Column> COLUMNS = List.of(
            Column.NAME, Column.LAST_MODIFIED, Column.SIZE);

    private final FileTree.Context context = new Context();
    private final TableModel model = new FileModel(context.cwd());
    private final JTable fileTable = JTables.builder(model)
                                            .setDefaultRenderer(Property.class, new MyCellRenderer())
                                            .setup(jTable -> jTable.getTableHeader()
                                                                   .setDefaultRenderer(new MyHeadRenderer()))
                                            .setAutoCreateRowSorter(true)
                                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                                            .build();

    public static void main(final String[] args) {
        start(new RendererTrial());
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
    private interface Column extends RowColumnModel.Column<File>, CellProperty.Column<File>, CellRenderer.Column {

        Column NAME = new ColumnImpl("Name", LEFT, Property::byName, Property::nameToString, Property.NAME_ORDER);
        Column LAST_MODIFIED = new ColumnImpl("Last Modified", CENTER, Property::byLastModified,
                                              Property::lastModifiedToString, Property.LAST_MODIFIED_ORDER);
        Column SIZE = new ColumnImpl("Size", RIGHT, Property::bySize, Property::sizeToString, Property.SIZE_ORDER);
    }

    private record ColumnImpl(String title,
                              int horizontalAlignment,
                              Function<File, Property> propertyFunction,
                              Function<File, String> toStringFunction,
                              Comparator<File> order)
            implements Column {

        @Override
        public final String toString(final File file) {
            return toStringFunction.apply(file);
        }

        @Override
        public final Class<?> type() {
            return Property.class;
        }

        @Override
        public final Property map(final File file) {
            return propertyFunction.apply(file);
        }

        @Override
        public final String toString() {
            return title;
        }
    }

    private static final class Property extends CellProperty<File, Column> {

        private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
        private static final Comparator<String> RESPECT_CASE = String::compareTo;
        private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
        private static final Comparator<File> NAME_ORDER = Comparator.comparing(File::getName, STRING_ORDER);
        private static final Comparator<File> LAST_MODIFIED_ORDER = Comparator.comparing(File::lastModified);
        private static final Comparator<File> SIZE_ORDER = Comparator.comparing(File::length);

        private Property(final File rowContent, final RendererTrial.Column column) {
            super(rowContent, column);
        }

        private static Property byName(final File file) {
            return new Property(file, RendererTrial.Column.NAME);
        }

        private static Property byLastModified(final File file) {
            return new Property(file, RendererTrial.Column.LAST_MODIFIED);
        }

        private static Property bySize(final File file) {
            return new Property(file, RendererTrial.Column.SIZE);
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
        protected final List<RendererTrial.Column> columns() {
            return COLUMNS;
        }
    }

    private static final class MyHeadRenderer extends HeadRenderer<Column> {

        @Override
        protected List<RendererTrial.Column> columns() {
            return COLUMNS;
        }
    }

    private static final class MyCellRenderer extends CellRenderer<Property, Column> {

        private MyCellRenderer() {
            super(Property.class);
        }

        @Override
        protected final List<RendererTrial.Column> columns() {
            return COLUMNS;
        }

        @Override
        protected final void setup(final JLabel result, final Property value, final RendererTrial.Column column) {
            // preliminary nothing to do
        }
    }
}