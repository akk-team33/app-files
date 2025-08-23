package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JLabels;
import de.team33.sphinx.alpha.visual.JTables;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

public final class FileTable {

    private static final int MARGIN = 8;

    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<Path> cwd, final Columns columns, final Icons icons) {
        final TableModel model = new Model(columns, cwd);
        this.table = JTables.builder()
                            .setModel(model)
                            .setDefaultRenderer(FileEntry.class, new CellRenderer(columns, icons, cwd))
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(new HeadRenderer(columns)))
                            .setShowGrid(false)
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            //.setAutoCreateRowSorter(true)
                            //.setRowSorter(new TableRowSorter<>())
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
//                            .on(Event.MOUSE_CLICKED, new InfoTable.MOUSE_LISTENER(context)::mouseClicked)
//                            .setup(table -> table.getSelectionModel()
//                                                 .addListSelectionListener(new InfoTable.SelectionListener(table)))
//                            .setup(table -> FS.getRegister().add(new InfoTable.LSTNR_UPDINFO(table, context)))
                            .build();
        this.component = new JScrollPane(table);
        Event.MOUSE_CLICKED.add(table.getTableHeader(), this::onMouseClicked);
    }

    public static FileTable by(final Context context) {
        return new FileTable(context.cwd(), new Columns(context.columns()), context.icons());
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

    public enum Column {

        NAME____("Name", cwd -> FileEntry::name,
                 SwingConstants.LEADING),
        PATH____("Path", cwd -> entry -> cwd.relativize(entry.path()).toString(),
                 SwingConstants.LEADING),
        PARENT__("Directory", cwd -> entry -> min(cwd.relativize(entry.path().getParent()).toString()),
                 SwingConstants.LEADING),
        UPDATE__("Last Modified", cwd -> entry -> dateTime(entry.lastModified()),
                 SwingConstants.CENTER),
        UPDATE_D("Last Mod. Date", cwd -> entry -> date(entry.lastModified()),
                 SwingConstants.CENTER),
        UPDATE_T("Last Mod. Time", cwd -> entry -> time(entry.lastModified()),
                 SwingConstants.CENTER),
        SIZE____("Size", cwd -> entry -> "%,d".formatted(entry.size()),
                 SwingConstants.TRAILING);

        private static final ZoneId ZONE_ID =
                ZoneId.systemDefault();
        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                 .withLocale(Locale.getDefault());
        private static final DateTimeFormatter TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                                 .withLocale(Locale.getDefault());
        private static final DateTimeFormatter DATE_FORMATTER =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                 .withLocale(Locale.getDefault());

        private final String title;
        private final Function<Path, Function<FileEntry, String>> toText;
        private final int alignment;

        Column(final String title, final Function<Path, Function<FileEntry, String>> toText, final int alignment) {
            this.title = title;
            this.toText = toText;
            this.alignment = alignment;
        }

        private static String min(final String path) {
            return path.isBlank() ? "." : path;
        }

        private static String time(final Instant instant) {
            return LocalTime.ofInstant(instant, ZONE_ID)
                            .format(TIME_FORMATTER);
        }

        private static String date(final Instant instant) {
            return LocalDate.ofInstant(instant, ZONE_ID)
                            .format(DATE_FORMATTER);
        }

        private static String dateTime(final Instant instant) {
            return LocalDateTime.ofInstant(instant, ZONE_ID)
                                .format(DATE_TIME_FORMATTER);
        }

        private static Column of(final String columnName) {
            //noinspection CallToSuspiciousStringMethod
            return Stream.of(values())
                         .filter(value -> value.title.equals(columnName))
                         .findAny()
                         .orElseThrow();
        }
    }

    public interface Icons {

        Icon stdFolder();

        Icon stdFile();
    }

    public interface Context {

        Icons icons();

        List<Column> columns();

        Retrievable<Path> cwd();
    }

    private static final class Columns {

        private final List<Column> backing;

        private Columns(final Collection<Column> columns) {
            this.backing = List.copyOf(columns);
        }

        final Column get(final int index) {
            return backing.get(index);
        }

        final int size() {
            return backing.size();
        }

        final int indexOf(final Column column) {
            return backing.indexOf(column);
        }
    }

    private abstract static class BaseRenderer<V> implements TableCellRenderer {

        final TableCellRenderer backing;
        final Columns columns;
        private final Class<V> vClass;

        @SuppressWarnings("BoundedWildcard")
        BaseRenderer(final Class<V> vClass, final Columns columns, final Supplier<TableCellRenderer> newRenderer) {
            this.vClass = vClass;
            this.backing = newRenderer.get();
            this.columns = columns;
        }

        private static <R> R cast(final Class<R> rClass, final Object value) {
            if (rClass.isInstance(value)) {
                return rClass.cast(value);
            } else {
                final Class<?> vClass = (null == value) ? null : value.getClass();
                throw new IllegalStateException(
                        "<value> is expected to be an instance of %s - but was %s".formatted(rClass, vClass));
            }
        }

        @Override
        public final Component getTableCellRendererComponent(final JTable table,
                                                             final Object value,
                                                             final boolean isSelected,
                                                             final boolean hasFocus,
                                                             final int rowIndex,
                                                             final int colIndex) {
            final Column column =
                    columns.get(colIndex);
            final Component result =
                    backing.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, colIndex);
            cast(JLabel.class, result).setHorizontalAlignment(column.alignment);
            return updated(column, cast(vClass, value), cast(JLabel.class, result));
        }

        abstract JLabel updated(final Column column, final V value, final JLabel label);
    }

    private static final class HeadRenderer extends BaseRenderer<String> {

        private HeadRenderer(final Columns columns) {
            super(String.class, columns, () -> new JTable().getTableHeader().getDefaultRenderer());
        }

        @Override
        final JLabel updated(final Column column, final String value, final JLabel label) {
            label.setHorizontalAlignment(column.alignment);
            return label;
        }
    }

    private static final class CellRenderer extends BaseRenderer<FileEntry> {

        private final Icons icons;
        private final Gettable<? extends Path> cwd;

        private CellRenderer(final Columns columns, final Icons icons, final Gettable<? extends Path> cwd) {
            super(FileEntry.class, columns, () -> new JTable().getDefaultRenderer(String.class));
            this.icons = icons;
            this.cwd = cwd;
        }

        @Override
        final JLabel updated(final Column column, final FileEntry entry, final JLabel label) {
            return JLabels.charger(label)
                          .setText(column.toText.apply(cwd.get())
                                                .apply(entry))
                          .setIcon(iconOf(entry, column))
                          .setHorizontalAlignment(column.alignment)
                          .charged();
        }

        @SuppressWarnings("ReturnOfNull")
        private Icon iconOf(final FileEntry entry, final Column column) {
            if (column != columns.get(0)) {
                return null;
            } else if (entry.isDirectory()) {
                return icons.stdFolder();
            } else {
                return icons.stdFile();
            }
        }
    }

    private static final class Model extends AbstractTableModel {

        private final Columns columns;
        private volatile List<FileEntry> entries = List.of();

        private Model(final Columns columns, final Retrievable<? extends Path> cwd) {
            this.columns = columns;
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
        public final int getRowCount() {
            return entries.size();
        }

        @Override
        public final int getColumnCount() {
            return columns.size();
        }

        @Override
        public final Object getValueAt(final int rowIndex, final int colIndex) {
            return entries.get(rowIndex);
        }

        @Override
        public final String getColumnName(final int colIndex) {
            return columns.get(colIndex).title;
        }

        @Override
        public final int findColumn(final String columnName) {
            return columns.indexOf(Column.of(columnName));
        }

        @Override
        public final Class<?> getColumnClass(final int colIndex) {
            return FileEntry.class;
        }
    }
}
