package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

public final class FileTable {

    private static final int MARGIN = 8;

    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<Path> cwd,
                      final Collection<? extends GenericModel.Column<FileEntry, ?>> columns,
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

    public record Column<C extends Comparable<C>>(String title, Class<C> type, Function<FileEntry, C> mapping)
            implements GenericModel.Column<FileEntry, C> {

        private static Column<String> NAME =
                new Column<>("Name", String.class, FileEntry::name);
        private static Column<Path> PATH =
                new Column<>("Path", Path.class, FileEntry::path);
        private static Column<Path> PARENT =
                new Column<>("Parent", Path.class, fileEntry -> fileEntry.path().getParent());
        private static Column<Instant> UPDATE =
                new Column<>("Last Modified", Instant.class, FileEntry::lastModified);
        private static Column<Long> SIZE =
                new Column<>("Size", Long.class, FileEntry::size);
        static List<Column<?>> VALUES = List.of(NAME, PATH, PARENT, UPDATE, SIZE);

        @Override
        public final C map(final FileEntry element) {
            return mapping.apply(element);
        }
    }

    private static final class Model extends GenericModel<FileEntry> {

        private final List<Column<FileEntry, ?>> columns;
        private volatile List<FileEntry> entries = List.of();

        private Model(final Collection<? extends Column<FileEntry, ?>> columns, final Retrievable<? extends Path> cwd) {
            this.columns = List.copyOf(columns);
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
        protected final List<FileEntry> rows() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return entries;
        }

        @Override
        protected final List<Column<FileEntry, ?>> columns() {
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
