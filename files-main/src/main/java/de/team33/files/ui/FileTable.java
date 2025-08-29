package de.team33.files.ui;

import de.team33.files.ui.table.*;
import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.gamma.table.GenericModel;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

public final class FileTable {

    private static final int MARGIN = 8;

    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<Path> cwd,
                      final List<Column<?>> columns,
                      final Icons icons) {
        final TableModel model = new Model(columns, cwd);
        this.table = JTables.builder()
                            .setModel(model)
                            //.setDefaultRenderer(FileEntry.class, new CellRenderer(columns, icons, cwd))
                            //.setup(jTable -> jTable.getTableHeader()
                            //                       .setDefaultRenderer(new HeadRenderer(columns)))
                            .setShowGrid(false)
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            .setAutoCreateRowSorter(true)
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
//                            .on(Event.MOUSE_CLICKED, new InfoTable.MOUSE_LISTENER(context)::mouseClicked)
//                            .setup(table -> table.getSelectionModel()
//                                                 .addListSelectionListener(new InfoTable.SelectionListener(table)))
//                            .setup(table -> FS.getRegister().add(new InfoTable.LSTNR_UPDINFO(table, context)))
                            .build();
        this.component = JPanels.builder()
                                .setLayout(new BorderLayout())
                                .add(new Controls(icons).panel, BorderLayout.PAGE_START)
                                .add(new JScrollPane(table), BorderLayout.CENTER)
                                .build();
        Channel.MOUSE_CLICKED.subscribe(table.getTableHeader(), this::onMouseClicked);
    }

    public static FileTable by(final Context context) {
        return new FileTable(context.cwd(), Column.VALUES, context.icons());
    }

    private void onMouseClicked(final MouseEvent event) {
        if ((event.getComponent() instanceof final JTableHeader header) && (table == header.getTable())) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                final int viewColIndex = header.columnAtPoint(event.getPoint());
                if (0 <= viewColIndex) {
                    final int colIndex = table.convertColumnIndexToModel(viewColIndex);
                    switch (event.getClickCount()) {
                        case 1 -> sort_(colIndex);
                        case 2 -> resizeColumn(colIndex);
                    }
                }
            }
        }
    }

    private void sort_(final int colIndex) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    private void resizeColumn(final int colIndex) {
        final TableColumn column = table.getColumnModel().getColumn(colIndex);
        final TableCellRenderer headRenderer = Optional.ofNullable(column.getHeaderRenderer())
                                                       .orElseGet(() -> table.getTableHeader()
                                                                             .getDefaultRenderer());
        final Component head = headRenderer.getTableCellRendererComponent(
                table, column.getHeaderValue(), false, false, 0, colIndex);
        final int maxWidth = IntStream.range(0, table.getRowCount())
                                      .map(rowIndex -> preferredWidth(colIndex, rowIndex))
                                      .reduce(head.getPreferredSize().width, Math::max);
        column.setPreferredWidth(maxWidth + MARGIN);
    }

    private int preferredWidth(final int colIndex, final int rowIndex) {
        final TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, colIndex);
        final Component cell = table.prepareRenderer(cellRenderer, rowIndex, colIndex);
        return cell.getPreferredSize().width;
    }

    public final Component component() {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);
        return component;
    }

    public interface Context {

        Icons icons();

        List<Column<?>> columns();

        Retrievable<Path> cwd();
    }

    public interface Icons {

        Icon stdFolder();

        Icon stdFile();

        Icon optWidth();

        Icon parentFolder();
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "WeakerAccess"})
    public record Column<V extends Comparable<V>>(String title, Class<V> type,
                                                  BiFunction<Gettable<Path>, FileEntry, V> biMapping)
            implements GenericModel.Column<FileEntry, Gettable<Path>, V> {

        public static final Column<FileName> NAME =
                new Column<>("Name", FileName.class, FileName::new);
        public static final Column<FilePath> PATH =
                new Column<>("Path", FilePath.class, FilePath::new);
        public static final Column<FileParent> PARENT =
                new Column<>("Parent", FileParent.class, FileParent::new);
        public static final Column<FileDateTime> UPDATE =
                new Column<>("Last Modified", FileDateTime.class, FileDateTime::new);
        public static final Column<FileDate> UPDATE_DATE =
                new Column<>("Last Mod. Date", FileDate.class, FileDate::new);
        public static final Column<FileTime> UPDATE_TIME =
                new Column<>("Last Mod. Time", FileTime.class, FileTime::new);
        public static final Column<FileSize> SIZE =
                new Column<>("Size", FileSize.class, FileSize::new);
        @SuppressWarnings("StaticCollection")
        public static final List<Column<?>> VALUES = List.of(NAME, PATH, PARENT, UPDATE, UPDATE_DATE, UPDATE_TIME, SIZE);

        @Override
        public Function<FileEntry, V> mapping(final Gettable<Path> context) {
            return entry -> biMapping.apply(context, entry);
        }
    }

    private static final class Model extends GenericModel<FileEntry, Gettable<Path>> {

        private final List<FileTable.Column<?>> columns;
        private final Retrievable<? extends Path> cwd;
        private volatile List<FileEntry> entries = List.of();

        private Model(final List<FileTable.Column<?>> columns,
                      final Retrievable<? extends Path> cwd) {
            this.columns = columns;
            this.cwd = cwd;
            cwd.subscribe(INIT, this::onSetPath);
        }

        private void onSetPath(final Path path) {
            this.entries = FileEntry.of(path)
                                    .resolved()
                                    .entries()
                                    .toList();
            fireTableDataChanged();
        }

        @Override
        protected final Gettable<Path> context() {
            return cwd::get;
        }

        @Override
        protected final List<FileEntry> rows() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return entries;
        }

        @Override
        protected final List<FileTable.Column<?>> columns() {
            return columns;
        }
    }

    private class Controls {

        private final JPanel panel;

        private Controls(final Icons icons) {
            panel = JPanels.builder()
                           .setLayout(new GridBagLayout())
                           .add(JButtons.builder()
                                        .setIcon(icons.parentFolder())
                                        .setToolTipText("Switch to parent directory")
                                        .build())
                           .add(JButtons.builder()
                                        .setIcon(icons.optWidth())
                                        .setToolTipText("Optimize column width")
                                        .build())
//                           .add(JComboBoxes.builder(ComboListModel.of(Column.class))
//                                           //.setIcon(icons.stdFolder()) // TODO!
//                                           .setToolTipText("Set file order")
//                                           .build())
                           .build();
        }
    }
}
