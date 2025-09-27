package de.team33.files.ui;

import de.team33.patterns.io.delta.FileEntry;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.delta.table.CellProperty;
import de.team33.sphinx.delta.table.CellRenderer;
import de.team33.sphinx.delta.table.HeadRenderer;
import de.team33.sphinx.delta.table.RowColumnModel;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static java.util.function.Predicate.not;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

@SuppressWarnings("ClassWithTooManyFields")
public final class FileTable {

    private static final int MARGIN = 8;
    private static final Locale LOCALE = Locale.getDefault();
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private final List<? extends Column> columns;
    private final Icons icons;
    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<Path> cwd,
                      final List<? extends Column> columns,
                      final Icons icons) {
        this.columns = columns;
        this.icons = icons;
        this.table = JTables.builder()
                            .setModel(new Model(cwd))
                            .setDefaultRenderer(Property.class, new MyCellRenderer())
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(new MyHeadRenderer()))
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
                                .add(new Controls().panel, BorderLayout.PAGE_START)
                                .add(new JScrollPane(table), BorderLayout.CENTER)
                                .build();
        Channel.MOUSE_CLICKED.subscribe(table.getTableHeader(), this::onMouseClicked);
    }

    public static FileTable by(final Context context) {
        return new FileTable(context.cwd(), context.columns(), context.icons());
    }

    private void onMouseClicked(final MouseEvent event) {
        if ((event.getComponent() instanceof final JTableHeader header) && (table == header.getTable())) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                final int viewColIndex = header.columnAtPoint(event.getPoint());
                if (0 <= viewColIndex) {
                    final int colIndex = table.convertColumnIndexToModel(viewColIndex);
                    resizeColumn(colIndex);
                }
            }
        }
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

        List<Column> columns();

        Retrievable<Path> cwd();
    }

    public interface Icons {

        Icon stdFolder();

        Icon stdFile();

        Icon optWidth();

        Icon parentFolder();
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    public interface Column extends RowColumnModel.Column<Entry>, CellProperty.Column<Entry>, CellRenderer.Column {

        @Override
        default Class<?> type() {
            return Property.class;
        }

        Column NAME = new ColumnImpl("Name", Property::byName,
                                     Entry.NAME_ORDER, Entry::nameToString, SwingConstants.LEADING);
        Column PATH = new ColumnImpl("Path", Property::byPath,
                                     Entry.FINAL_ORDER, Entry::pathToString, SwingConstants.LEADING);
        Column LOCATION = new ColumnImpl("Location", Property::byLocation,
                                         Entry.FINAL_ORDER, Entry::locationToString, SwingConstants.LEADING);
        Column LAST_MODIFIED = new ColumnImpl("Last Modified", Property::byLastModified,
                                              Entry.LAST_MODIFIED_ORDER, Entry::lastModifiedToString,
                                              SwingConstants.CENTER);
        // Column LAST_MODIFIED_DATE = new FinalColumn<>("Last Mod. Date", LastModifiedDate.class,
        //                                               SwingConstants.CENTER, LastModifiedDate::new);
        // Column LAST_MODIFIED_TIME = new FinalColumn<>("Last Mod. Time", LastModifiedTime.class,
        //                                               SwingConstants.CENTER, LastModifiedTime::new);
        Column LAST_UPDATE = new ColumnImpl("Last Update", Property::byLastUpdate,
                                            Entry.LAST_UPDATE_ORDER, Entry::lastUpdateToString,
                                            SwingConstants.CENTER);
        Column SIZE = new ColumnImpl("Size", Property::bySize,
                                     Entry.SIZE_ORDER, Entry::sizeToString, SwingConstants.TRAILING);
        Column DATA_SIZE = new ColumnImpl("Data Size", Property::byDataSize,
                                          Entry.DATA_SIZE_ORDER, Entry::dataSizeToString, SwingConstants.TRAILING);

        @SuppressWarnings({"StaticCollection", "StaticMethodOnlyUsedInOneClass"}) // List is immutable!
        List<Column> VALUES = List.of(NAME, PATH, LOCATION, LAST_MODIFIED, /*LAST_MODIFIED_DATE, LAST_MODIFIED_TIME,*/
                                      LAST_UPDATE, SIZE, DATA_SIZE);


    }

    private record ColumnImpl(String title, Function<Entry, ?> mapping,
                              Comparator<Entry> order, Function<Entry, String> toStringFunction,
                              int horizontalAlignment)
            implements Column {

        @Override
        public Object map(final Entry entry) {
            return mapping.apply(entry);
        }

        @Override
        public String toString(final Entry entry) {
            return toStringFunction.apply(entry);
        }
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    private static final class Property extends CellProperty<Entry, Column> {

        private Property(final Entry entry, final FileTable.Column column) {
            super(entry, column);
        }

        static Property byName(final Entry entry) {
            return new Property(entry, FileTable.Column.NAME);
        }

        static Property byPath(final Entry entry) {
            return new Property(entry, FileTable.Column.PATH);
        }

        static Property byLocation(final Entry entry) {
            return new Property(entry, FileTable.Column.LOCATION);
        }

        static Property byLastModified(final Entry entry) {
            return new Property(entry, FileTable.Column.LAST_MODIFIED);
        }

        static Property byLastUpdate(final Entry entry) {
            return new Property(entry, FileTable.Column.LAST_UPDATE);
        }

        static Property bySize(final Entry entry) {
            return new Property(entry, FileTable.Column.SIZE);
        }

        static Property byDataSize(final Entry entry) {
            return new Property(entry, FileTable.Column.DATA_SIZE);
        }

    }

    private static final class Entry extends FileEntry {

        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                 .withLocale(LOCALE);
        private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
        private static final Comparator<String> RESPECT_CASE = String::compareTo;
        private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
        private static final Comparator<Path> PATH_ORDER = Comparator.comparing(Path::toString, STRING_ORDER);
        static final Comparator<Entry> FINAL_ORDER = Comparator.comparing(Entry::path, PATH_ORDER);
        static final Comparator<Entry> NAME_ORDER = Comparator.comparing(Entry::name, STRING_ORDER)
                                                              .thenComparing(FINAL_ORDER);
        static final Comparator<Entry> LAST_MODIFIED_ORDER = Comparator.comparing(Entry::lastModified)
                                                                       .thenComparing(FINAL_ORDER);
        static final Comparator<Entry> LAST_UPDATE_ORDER = Comparator.comparing(Entry::lastUpdated)
                                                                     .thenComparing(FINAL_ORDER);
        static final Comparator<Entry> SIZE_ORDER = Comparator.comparing(Entry::size)
                                                              .thenComparing(FINAL_ORDER);
        static final Comparator<Entry> DATA_SIZE_ORDER = Comparator.comparing(Entry::dataSize)
                                                                   .thenComparing(FINAL_ORDER);

        private final Supplier<? extends Path> cwd;

        private Entry(final Supplier<? extends Path> cwd, final FileEntry entry) {
            super(entry);
            this.cwd = cwd;
        }

        @SuppressWarnings("TypeMayBeWeakened")
        private static String dateTimeToString(final LocalDateTime dateTime) {
            return dateTime.format(DATE_TIME_FORMATTER);
        }

        private static LocalDateTime localDateTime(final Instant instant) {
            return LocalDateTime.ofInstant(instant, ZONE_ID);
        }

        private static String longToString(final long l) {
            return "%,d".formatted(l);
        }

        final String nameToString() {
            return name();
        }

        final String pathToString() {
            return cwd.get().relativize(path()).toString();
        }

        final String locationToString() {
            return Optional.ofNullable(path().getParent())
                           .map(path -> cwd.get().relativize(path))
                           .map(Path::toString)
                           .filter(not(String::isBlank))
                           .orElse(".");
        }

        final String lastModifiedToString() {
            return dateTimeToString(localDateTime(lastModified()));
        }

        final String lastUpdateToString() {
            return dateTimeToString(localDateTime(lastUpdated()));
        }

        final String sizeToString() {
            return longToString(size());
        }

        final String dataSizeToString() {
            return longToString(dataSize());
        }
    }

    private final class MyHeadRenderer extends HeadRenderer<Column> {

        @Override
        protected List<? extends FileTable.Column> columns() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return columns;
        }
    }

    private final class MyCellRenderer extends CellRenderer<Property, Column> {

        private MyCellRenderer() {
            super(Property.class);
        }

        @Override
        protected List<? extends FileTable.Column> columns() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return columns;
        }

        @Override
        protected void setup(final JLabel result, final Property value, final FileTable.Column column) {
            result.setIcon(column == columns().get(0) ? icon(value) : null);
        }

        private Icon icon(final Property value) {
            return value.rowContent().isDirectory() ? icons.stdFolder() : icons.stdFile();
        }
    }

    private final class Controls {

        private final JPanel panel;

        private Controls() {
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

    private final class Model extends RowColumnModel<Entry> {

        private final Retrievable<? extends Path> cwd;
        private volatile List<Entry> entries = List.of();

        private Model(final Retrievable<? extends Path> cwd) {
            this.cwd = cwd;
            cwd.subscribe(INIT, this::onSetPath);
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
        protected final List<? extends FileTable.Column> columns() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return columns;
        }
    }
}
