package de.team33.files.eris.ui;

import de.team33.files.luna.common.EntryOrder;
import de.team33.files.luna.context.Icons;
import de.team33.files.luna.context.UIContext;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.epsilon.table.CellProperty;
import de.team33.sphinx.epsilon.table.RowColumnModel;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

@SuppressWarnings("unused")
public final class FileTable {

    private static final FileEntry.Lister LISTER = FileEntry.lister(LinkHandling.RESOLVE);
    private static final FileEntry.Streamer STREAMER = FileEntry.streamer(LISTER);
    private static final TableCellRenderer CELL_RENDERER = new JTable().getDefaultRenderer(Objects.class);
    private static final TableCellRenderer HEAD_RENDERER = new JTable().getTableHeader().getDefaultRenderer();
    private static final int HEAD_GAP = 12;

    private final Variable<Mode> mode;
    private final Variable<List<Column>> columns;
    private final Variable<Path> cwd;
    private final Icons icons;
    @SuppressWarnings("FieldCanBeLocal")
    private final JTable table;
    private final JScrollPane scrollPane;

    private FileTable(final UIContext context) {
        this.mode = new Component<>(context.executor(), Mode.FLAT);
        this.columns = new Component<>(context.executor(), List.of(Column.values()));
        this.cwd = context.cwd();
        this.icons = context.icons();
        this.table = JTables.builder(new Model())
                            .setDefaultRenderer(Property.class, this::cellComponent)
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(this::headComponent))
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            .setAutoCreateRowSorter(true)
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                            .build();
        this.scrollPane = new JScrollPane(table);
        context.tableViewConfig().optColumnWidth().subscribe(INIT, this::resizeColumns);
    }

    private java.awt.Component headComponent(final JTable jTable,
                                             final Object value,
                                             final boolean isSelected,
                                             final boolean hasFocus,
                                             final int rowIndex,
                                             final int colIndex) {
        return headOrCellComponent(HEAD_RENDERER, jTable, value, isSelected, hasFocus, rowIndex, colIndex);
    }

    private java.awt.Component cellComponent(final JTable jTable,
                                             final Object value,
                                             final boolean isSelected,
                                             final boolean hasFocus,
                                             final int rowIndex,
                                             final int colIndex) {
        return headOrCellComponent(CELL_RENDERER, jTable, value, isSelected, hasFocus, rowIndex, colIndex);
    }

    private java.awt.Component headOrCellComponent(final TableCellRenderer renderer,
                                                   final JTable jTable,
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
            if (0 == colIndex && value instanceof final Property<?> property) {
                label.setIcon(iconFor(property.fileEntry()));
            } else {
                label.setIcon(null);
            }
            label.setHorizontalAlignment(column.backing.alignment);
        }
        return stage;
    }

    private Icon iconFor(final FileEntry entry) {
        return entry.isDirectory() ? icons.stdFolder() : icons.stdFile();
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

    public static FileTable by(final UIContext context) {
        return new FileTable(context);
    }

    public Variable<Mode> mode() {
        return mode;
    }

    public JComponent ui() {
        return scrollPane;
    }

    public enum Mode {

        FLAT(entry -> LISTER.list(entry).stream()),
        DEEP(STREAMER::stream);

        private final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming;

        Mode(final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming) {
            this.streaming = streaming;
        }

        private Stream<FileEntry> stream(final Path path) {
            return streaming.apply(FileEntry.resolved(path));
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public enum Column implements RowColumnModel.Column<Entry> {

        NAME(new Backing<>("Name", Name.class, Entry::name, SwingConstants.LEADING)),
        LOCATION(new Backing<>("Location", Location.class, Entry::location, SwingConstants.LEADING)),
        LAST_MODIFIED(new Backing<>("Last Modified", LastModified.class, Entry::lastModified, SwingConstants.LEADING)),
        SIZE(new Backing<>("Size", Size.class, Entry::size, SwingConstants.TRAILING));

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
        public Object map(final Entry element) {
            return backing.mapping.apply(element);
        }

        private record Backing<P>(String title, Class<P> type, Function<Entry, P> mapping, int alignment) {
        }
    }

    private record Entry(Variable<Path> cwd, FileEntry entry) {

        private Name name() {
            return new Name(this);
        }

        private Location location() {
            return new Location(this);
        }

        private LastModified lastModified() {
            return new LastModified(this);
        }

        private Size size() {
            return new Size(this);
        }
    }

    private abstract static class Property<P extends Property<P>> extends CellProperty<P> {

        static final Locale LOCALE = Locale.getDefault();
        static final ZoneId ZONE_ID = ZoneId.systemDefault();

        private final Entry entry;

        Property(final Entry entry, final Class<P> finalClass, final Comparator<FileEntry> order) {
            super(finalClass, comparing(Property::fileEntry, order));
            this.entry = entry;
        }

        static LocalDateTime localDateTime(final Instant instant) {
            return LocalDateTime.ofInstant(instant, ZONE_ID);
        }

        final FileEntry fileEntry() {
            return entry.entry;
        }

        final Path cwd() {
            return entry.cwd.get();
        }

        @SuppressWarnings("EqualsDoesntCheckParameterClass")
        @Override
        public final boolean equals(final Object other) {
            return equals(THIS(), other);
        }

        @Override
        public final int hashCode() {
            return fileEntry().path().hashCode();
        }
    }

    private static class Name extends Property<Name> {

        Name(final Entry entry) {
            super(entry, Name.class, EntryOrder.BY_TYPE_NAME);
        }

        @Override
        public final String toString() {
            return fileEntry().name();
        }
    }

    private static class Location extends Property<Location> {

        private final Path location;

        Location(final Entry entry) {
            super(entry, Location.class, EntryOrder.BY_PATH);
            this.location = cwd().relativize(fileEntry().path().getParent());
        }

        @Override
        public final String toString() {
            return Optional.of(location.toString())
                           .filter(not(String::isBlank))
                           .orElse(".");
        }
    }

    private static class LastModified extends Property<LastModified> {

        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                 .withLocale(LOCALE);

        private final LocalDateTime dateTime;

        LastModified(final Entry entry) {
            super(entry, LastModified.class, EntryOrder.BY_DATE);
            this.dateTime = localDateTime(fileEntry().lastModified());
        }

        @Override
        public final String toString() {
            return dateTime.format(DATE_TIME_FORMATTER);
        }
    }

    private static class Size extends Property<Size> {

        Size(final Entry entry) {
            super(entry, Size.class, EntryOrder.BY_SIZE);
        }

        @Override
        public final String toString() {
            return "%,d".formatted(fileEntry().size());
        }
    }

    private class Model extends RowColumnModel<Entry> {

        private volatile List<Entry> entries = List.of();

        Model() {
            cwd.subscribe(INIT, newPath -> onSetPath(newPath, mode.get()));
            mode.subscribe(newMode -> onSetPath(cwd.get(), newMode));
            columns.subscribe(ignored -> fireTableStructureChanged());
        }

        private void onSetPath(final Path newPath, final Mode newMode) {
            this.entries = newMode.stream(newPath)
                                  .map(entry -> entry.isMissing() ? entry.original() : entry)
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
}