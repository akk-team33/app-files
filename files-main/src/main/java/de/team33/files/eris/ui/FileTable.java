package de.team33.files.eris.ui;

import de.team33.files.eris.ui.FileTableEntry.*;
import de.team33.files.luna.context.Icons;
import de.team33.files.luna.context.TableViewConfig;
import de.team33.files.luna.context.UIContext;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.epsilon.table.RowColumnModel;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import static javax.swing.SwingConstants.LEADING;
import static javax.swing.SwingConstants.TRAILING;

@SuppressWarnings({"unused", "ClassWithTooManyFields"})
public final class FileTable {

    private static final int HEAD_GAP = 12;
    private static final FileEntry.Lister LISTER = FileEntry.lister(LinkHandling.RESOLVE);
    private static final FileEntry.Streamer STREAMER = FileEntry.streamer(LISTER);
    private static final TableCellRenderer CELL_RENDERER;
    private static final TableCellRenderer HEAD_RENDERER;

    static {
        final JTable jTable = new JTable();
        CELL_RENDERER = jTable.getDefaultRenderer(Property.class);
        HEAD_RENDERER = jTable.getTableHeader().getDefaultRenderer();
    }

    private final Variable<Mode> mode;
    private final Variable<List<Column>> columns;
    private final Variable<Path> cwd;
    private final Icons icons;
    @SuppressWarnings("FieldCanBeLocal")
    private final JTable table;
    private final JScrollPane scrollPane;
    private final Model model;

    private FileTable(final UIContext context) {
        this.mode = new Component<>(context.executor(), Mode.FLAT);
        this.columns = new Component<>(context.executor(), List.of(Column.values()));
        this.cwd = context.cwd();
        this.icons = context.icons();
        this.model = new Model();
        this.table = JTables.builder(model)
                            .setDefaultRenderer(Property.class, new CellRenderer(CELL_RENDERER))
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(new CellRenderer(HEAD_RENDERER)))
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            .setAutoCreateRowSorter(true)
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                            .build();
        this.scrollPane = new JScrollPane(table);
        context.tableViewConfig().optColumnWidth().subscribe(INIT, this::resizeColumns);
        context.tableViewConfig().depth().subscribe(INIT, this::onSwitchDepth);
    }

    private void onSwitchDepth(final TableViewConfig.Depth depth) {
        switch (depth) {
            case FLAT -> mode.set(Mode.FLAT);
            case DEEP -> mode.set(Mode.DEEP);
        }
        model.fireTableDataChanged();
    }

    public static FileTable by(final UIContext context) {
        return new FileTable(context);
    }

    private void resizeColumns(final Instant ignored) {
        IntStream.range(0, table.getColumnCount())
                 .map(table::convertColumnIndexToModel)
                 .forEach(this::resizeColumn);
    }

    private void resizeColumn(final int colIndex) {
        final TableColumn column = table.getColumnModel().getColumn(colIndex);
        final TableCellRenderer headRenderer = Optional.ofNullable(column.getHeaderRenderer())
                                                       .orElseGet(() -> table.getTableHeader()
                                                                             .getDefaultRenderer());
        final java.awt.Component head = headRenderer.getTableCellRendererComponent(
                table, column.getHeaderValue(), false, false, 0, colIndex);
        final int maxWidth = IntStream.range(0, table.getRowCount())
                                      .map(rowIndex -> preferredWidth(colIndex, rowIndex))
                                      .reduce(head.getPreferredSize().width, Math::max);
        column.setPreferredWidth(maxWidth + HEAD_GAP);
    }

    private int preferredWidth(final int colIndex, final int rowIndex) {
        final TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, colIndex);
        final java.awt.Component cell = table.prepareRenderer(cellRenderer, rowIndex, colIndex);
        return cell.getPreferredSize().width;
    }

    public Variable<Mode> mode() {
        return mode;
    }

    public JComponent ui() {
        return scrollPane;
    }

    public enum Mode {

        FLAT(entry -> LISTER.list(entry).stream()),
        DEEP(entry -> STREAMER.stream(entry).skip(1));

        private final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming;

        Mode(final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming) {
            this.streaming = streaming;
        }

        private Stream<FileEntry> stream(final Path path) {
            return streaming.apply(FileEntry.resolved(path));
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public enum Column implements RowColumnModel.Column<FileTableEntry> {

        NAME(new Backing<>("Name", Name.class, FileTableEntry::name, LEADING)),
        LOCATION(new Backing<>("Location", Location.class, FileTableEntry::location, LEADING)),
        LAST_MODIFIED(new Backing<>("Last Modified", LastModified.class, FileTableEntry::lastModified, LEADING)),
        SIZE(new Backing<>("Size", Size.class, FileTableEntry::size, TRAILING));

        private final Backing<?> backing;

        <P> Column(final Backing<P> backing) {
            this.backing = backing;
        }

        @Override
        public String title() {
            return backing.title;
        }

        @Override
        public Class<?> type() {
            return backing.type;
        }

        @SuppressWarnings("ClassEscapesDefinedScope")
        @Override
        public Object map(final FileTableEntry element) {
            return backing.mapping.apply(element);
        }

        private record Backing<P>(String title, Class<P> type, Function<FileTableEntry, P> mapping, int alignment) {
        }
    }

    private final class CellRenderer implements TableCellRenderer {

        private final TableCellRenderer renderer;

        private CellRenderer(final TableCellRenderer renderer) {
            this.renderer = renderer;
        }

        private Icon iconFor(final FileEntry entry) {
            return entry.isDirectory() ? icons.stdFolder() : icons.stdFile();
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(final JTable jTable,
                                                                final Object value,
                                                                final boolean isSelected,
                                                                final boolean hasFocus,
                                                                final int rowIndex,
                                                                final int colIndex) {
            final var modelIndex = jTable.convertColumnIndexToModel(colIndex);
            final var column = columns.get().get(modelIndex);
            final var stage = renderer.getTableCellRendererComponent(
                    jTable, value, isSelected, hasFocus, rowIndex, colIndex);
            if (stage instanceof final JLabel label) {
                label.setHorizontalAlignment(column.backing.alignment);
                if (CELL_RENDERER == renderer) {
                    if ((0 == colIndex) && (value instanceof final Property<?> property)) {
                        label.setIcon(iconFor(property.fileEntry()));
                    } else {
                        label.setIcon(null);
                    }
                }
                if (HEAD_RENDERER == renderer) {
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
            }
            return stage;
        }
    }

    private class Model extends RowColumnModel<FileTableEntry> {

        private volatile List<FileTableEntry> entries = List.of();

        Model() {
            cwd.subscribe(INIT, newPath -> onSetPath(newPath, mode.get()));
            mode.subscribe(newMode -> onSetPath(cwd.get(), newMode));
            columns.subscribe(ignored -> fireTableStructureChanged());
        }

        private void onSetPath(final Path newPath, final Mode newMode) {
            this.entries = newMode.stream(newPath)
                                  .map(entry -> entry.isMissing() ? entry.original() : entry)
                                  .map(entry -> new FileTableEntry(cwd, entry))
                                  .toList();
            fireTableDataChanged();
        }

        @Override
        protected final List<FileTableEntry> rows() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return entries;
        }

        @Override
        protected final List<FileTable.Column> columns() {
            return columns.get();
        }
    }
}