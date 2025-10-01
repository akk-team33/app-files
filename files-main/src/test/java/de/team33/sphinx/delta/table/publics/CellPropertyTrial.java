package de.team33.sphinx.delta.table.publics;

import de.team33.files.alpha.ui.Context;
import de.team33.files.alpha.ui.FileTree;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.delta.table.CellProperty;
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

final class CellPropertyTrial extends SwingApp {

    @SuppressWarnings("StaticCollection")
    private static final List<Column> COLUMNS = List.of(Column.values());

    private final FileTree.Context context = new Context();
    private final TableModel model = new FileModel(context.cwd());
    private final JTable fileTable = JTables.builder(model)
                                            .setAutoCreateRowSorter(true)
                                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                                            .build();

    public static void main(final String[] args) {
        start(new CellPropertyTrial());
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

    @SuppressWarnings("ClassNameSameAsAncestorName")
    private enum Column implements RowColumnModel.Column<File>, CellProperty.Column<File> {

        NAME(new Backing("Name", Property::nameOf, Property::nameToString, Property.NAME_ORDER)),
        LAST_MODIFIED(new Backing("Last Modified", Property::lastModifiedOf,
                                  Property::lastModifiedToString, Property.LAST_MODIFIED_ORDER)),
        SIZE(new Backing("Size", Property::sizeOf, Property::sizeToString, Property.SIZE_ORDER));

        private final Backing backing;

        Column(final Backing backing) {
            this.backing = backing;
        }

        @Override
        public Comparator<File> order() {
            return backing.order;
        }

        @Override
        public String toString(final File rowContent) {
            return backing.toStringFunction.apply(rowContent);
        }

        @Override
        public String title() {
            return backing.title;
        }

        @Override
        public Class<?> type() {
            return Property.class;
        }

        @Override
        public Object map(final File element) {
            return backing.mapFunction.apply(element);
        }

        private record Backing(String title,
                               Function<File, Property> mapFunction,
                               Function<File, String> toStringFunction,
                               Comparator<File> order) {
        }
    }

    private static final class Property extends CellProperty<File, Column> {

        private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
        private static final Comparator<String> RESPECT_CASE = String::compareTo;
        private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
        private static final Comparator<File> NAME_ORDER = Comparator.comparing(File::getName, STRING_ORDER);
        private static final Comparator<File> LAST_MODIFIED_ORDER = Comparator.comparing(File::lastModified);
        private static final Comparator<File> SIZE_ORDER = Comparator.comparing(File::length);

        private Property(final File rowContent, final CellPropertyTrial.Column column) {
            super(rowContent, column);
        }

        private static Property nameOf(final File file) {
            return new Property(file, CellPropertyTrial.Column.NAME);
        }

        private static Property lastModifiedOf(final File file) {
            return new Property(file, CellPropertyTrial.Column.LAST_MODIFIED);
        }

        private static Property sizeOf(final File file) {
            return new Property(file, CellPropertyTrial.Column.SIZE);
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
        protected final List<CellPropertyTrial.Column> columns() {
            return COLUMNS;
        }
    }
}