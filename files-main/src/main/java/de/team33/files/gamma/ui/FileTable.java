package de.team33.files.gamma.ui;

import de.team33.files.gamma.ui.table.Entry;
import de.team33.files.gamma.ui.table.Model;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.delta.table.CellProperty;
import de.team33.sphinx.delta.table.CellRenderer;
import de.team33.sphinx.delta.table.HeadRenderer;
import de.team33.sphinx.delta.table.RowColumnModel;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static javax.swing.JTable.AUTO_RESIZE_OFF;

public final class FileTable {

    private static final UnaryOperator<List<Column>> COPY_COLUMNS = List::copyOf;

    private final Variable<List<Column>> columns;
    private final Variable<Path> cwd;
    private final Icons icons;
    private final JTable table;
    private final JScrollPane panel;

    private FileTable(final Context context) {
        this.columns = new Component<>(context.executor(), COPY_COLUMNS,
                                       List.of(Column.NAME, Column.LAST_MODIFIED, Column.SIZE));
        this.cwd = context.cwd();
        this.icons = context.icons();
        this.table = JTables.builder()
                            .setModel(new Model(context.cwd(), columns))
                            .setDefaultRenderer(Property.class, new MyCellRenderer())
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(new MyHeadRenderer()))
                            .setShowGrid(false)
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            .setAutoCreateRowSorter(true)
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                            .subscribe(Channel.MOUSE_CLICKED, this::onMouseClickedInBody)
                            .build();
        this.panel = new JScrollPane(table);
        Channel.MOUSE_CLICKED.subscribe(table.getTableHeader(), this::onMouseClickedInHeader);
    }

    public static FileTable with(final Context context) {
        return new FileTable(context);
    }

    private void onMouseClickedInBody(final MouseEvent event) {
        if (event.getComponent() == table) {
            if (event.getClickCount() == 2) {
                final int row = table.rowAtPoint(event.getPoint());
                final Property property = (Property) table.getValueAt(row, 0);
                final Entry entry = property.rowContent();
                if (entry.isDirectory()) {
                    cwd.set(entry.path());
                }
            }
        }
    }

    private void onMouseClickedInHeader(final MouseEvent event) {
        if ((event.getComponent() instanceof final JTableHeader header) && (table == header.getTable())) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                final int viewColIndex = header.columnAtPoint(event.getPoint());
                if (0 <= viewColIndex) {
                    final int colIndex = table.convertColumnIndexToModel(viewColIndex);
                    JTables.resizeColumn(table, colIndex);
                }
            }
        }
    }

    public final JScrollPane ui() {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);
        return panel;
    }

    public final Variable<List<Column>> columns() {
        return columns;
    }

    @SuppressWarnings({"WeakerAccess", "MethodMayBeStatic"})
    public final Set<Column> availableColumns() {
        return Set.of(Column.values());
    }

    public final void resizeColumns() {
        JTables.resizeColumns(table);
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public enum Column implements RowColumnModel.Column<Entry>, CellProperty.Column<Entry>, CellRenderer.Column {

        NAME(new Backing("Name", Property::byName,
                         Entry.NAME_ORDER, Entry::nameToString, SwingConstants.LEADING)),
        PATH(new Backing("Path", Property::byPath,
                         Entry.FINAL_ORDER, Entry::pathToString, SwingConstants.LEADING)),
        LOCATION(new Backing("Location", Property::byLocation,
                             Entry.FINAL_ORDER, Entry::locationToString, SwingConstants.LEADING)),
        LAST_MODIFIED(new Backing("Last Modified", Property::byLastModified,
                                  Entry.LAST_MODIFIED_ORDER, Entry::lastModifiedToString,
                                  SwingConstants.CENTER)),
        LAST_UPDATE(new Backing("Last Update", Property::byLastUpdate,
                                Entry.LAST_UPDATE_ORDER, Entry::lastUpdateToString,
                                SwingConstants.CENTER)),
        SIZE(new Backing("Size", Property::bySize,
                         Entry.SIZE_ORDER, Entry::sizeToString, SwingConstants.TRAILING)),
        DATA_SIZE(new Backing("Data Size", Property::byDataSize,
                              Entry.DATA_SIZE_ORDER, Entry::dataSizeToString, SwingConstants.TRAILING));

        private final Backing backing;

        Column(final Backing backing) {
            this.backing = backing;
        }

        @Override
        public Comparator<Entry> order() {
            return backing.order;
        }

        @Override
        public String toString(final Entry rowContent) {
            return backing.toStringFunction.apply(rowContent);
        }

        @Override
        public int horizontalAlignment() {
            return backing.horizontalAlignment;
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
        public Object map(final Entry element) {
            return backing.mapping().apply(element);
        }

        private record Backing(String title,
                               Function<Entry, ?> mapping,
                               Comparator<Entry> order,
                               Function<Entry, String> toStringFunction,
                               int horizontalAlignment) {
        }
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    public interface Icons {

        Icon stdFolder();

        Icon stdFile();
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    public interface Context {

        Executor executor();

        Icons icons();

        Variable<Path> cwd();
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

    private final class MyHeadRenderer extends HeadRenderer<Column> {

        @Override
        protected List<FileTable.Column> columns() {
            return columns.get();
        }
    }

    private final class MyCellRenderer extends CellRenderer<Property, Column> {

        private MyCellRenderer() {
            super(Property.class);
        }

        @Override
        protected List<FileTable.Column> columns() {
            return columns.get();
        }

        @Override
        protected void setup(final JLabel result, final Property value, final FileTable.Column column) {
            result.setIcon(column == columns().get(0) ? icon(value) : null);
        }

        private Icon icon(final Property value) {
            return value.rowContent().isDirectory() ? icons.stdFolder() : icons.stdFile();
        }
    }
}
