package de.team33.files.ui;

import de.team33.patterns.notes.eris.Audience;
import de.team33.patterns.notes.eris.Channel;
import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JTableHeaders;
import de.team33.sphinx.alpha.visual.JTables;
import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.ui.CellRenderer;
import net.team33.fscalc.ui.HeadRenderer;
import net.team33.fscalc.ui.model.InfoTableModel;
import net.team33.fscalc.work.Context;
import net.team33.fscalc.work.Order;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.function.Consumer;

public final class InfoTable extends JTable {

    private static final FileService FS = FileService.getInstance();
    private static final Order[][] ORDERS;
    private static final Channel<SelectionMessage> CHANNEL = new Channel<>() {
    };

    static {
        ORDERS = new Order[][]{{Order.DEFAULT_ASC, Order.DEFAULT_DSC}, {Order.TTLSIZE_DSC, Order.TTLSIZE_ASC}, {Order.AVGSIZE_DSC, Order.AVGSIZE_ASC}, {Order.FILECNT_DSC, Order.FILECNT_ASC}, {Order.DIRCNT_DSC, Order.DIRCNT_ASC}, {Order.ERRCNT_DSC, Order.ERRCNT_ASC}};
    }

    private final Audience audience = new Audience();

    @SuppressWarnings("ThisEscapedInObjectConstruction")
    private InfoTable(final Context context) {
        super(new InfoTableModel(context));
    }

    static InfoTable by(final Context context) {
        final CellRenderer cr = new CellRenderer();
        cr.setFileInfo(FS.getInfo(new File("")), 0);
        return JTables.builder(() -> new InfoTable(context))
                      .setup(table -> JTableHeaders.charger(table.getTableHeader())
                                                   .setDefaultRenderer(headRenderer(context))
                                                   .on(Event.MOUSE_CLICKED,
                                                       new HEADER_MOUSE_LISTENER(table, context)::mouseClicked))
                      .setRowHeight(cr.getPreferredSize().height + 3)
                      .setDefaultRenderer(FileInfo.class, cr)
                      .setShowGrid(false)
                      .setRowSelectionAllowed(true)
                      .setColumnSelectionAllowed(false)
                      .on(Event.MOUSE_CLICKED, new MOUSE_LISTENER(context)::mouseClicked)
                      .setup(table -> table.getSelectionModel()
                                           .addListSelectionListener(new SelectionListener(table)))
                      .setup(table -> FS.getRegister().add(new LSTNR_UPDINFO(table, context)))
                      .build();
    }

    private static void onMouseClicked(final MouseEvent event) {
        if (event.getComponent() instanceof InfoTable table) {

        }
        throw new UnsupportedOperationException("not yet implemented");
    }

    private static HeadRenderer headRenderer(final Context context) {
        return new HeadRenderer() {
            @Override
            protected Context getContext() {
                return context;
            }
        };
    }

    @Override
    public final InfoTableModel getModel() {
        return (InfoTableModel) super.getModel();
    }

    public void add(final Consumer<? super SelectionMessage> listener) {
        listener.accept(new SelectionMessage(this, null));
        audience.add(CHANNEL, listener);
    }

    private static final class HEADER_MOUSE_LISTENER extends MouseAdapter {

        private final InfoTable table;
        private final Context context;

        private HEADER_MOUSE_LISTENER(final InfoTable table, final Context context) {
            this.table = table;
            this.context = context;
        }

        @Override
        public final void mouseClicked(final MouseEvent e) {
            final int tcol = table.getTableHeader().columnAtPoint(e.getPoint());
            final int mcol = table.convertColumnIndexToModel(tcol);
            final Order ord0 = context.getOrder();
            context.setOrder(getOrder(mcol, ord0));
        }

        private Order getOrder(final int colIndex, final Order currOrder) {
            Order ret = null;
            if (ORDERS.length > colIndex) {
                if (currOrder == ORDERS[colIndex][0]) {
                    ret = ORDERS[colIndex][1];
                } else {
                    ret = ORDERS[colIndex][0];
                }
            }

            return (null == ret) ? currOrder : ret;
        }
    }

    private record LSTNR_UPDINFO(InfoTable table, Context context) implements Consumer<FileService.MsgUpdate> {

        @Override
        public final void accept(final FileService.MsgUpdate message) {
            final File path = message.getInfo().getPath();
            if (path.equals(context.path().get().toFile())) {
                table.getTableHeader().repaint();
            }
        }
    }

    private static final class MOUSE_LISTENER extends MouseAdapter {

        private final Context context;

        private MOUSE_LISTENER(final Context context) {
            this.context = context;
        }

        @Override
        public final void mouseClicked(final MouseEvent e) {
            if (e.getComponent() instanceof final InfoTable table) {
                if (e.getClickCount() == 2) {
                    final int row = table.rowAtPoint(e.getPoint());
                    final FileInfo it = (FileInfo) table.getValueAt(row, 0);
                    final File path = it.getPath();
                    if (path.isDirectory()) {
                        context.path().set(path.toPath());
                    }
                }
            }
        }
    }

    public record SelectionMessage(InfoTable table, ListSelectionEvent event) {
    }

    private record SelectionListener(InfoTable table) implements ListSelectionListener {

        @Override
        public final void valueChanged(final ListSelectionEvent e) {
            table.audience.send(CHANNEL, new SelectionMessage(table, e));
        }
    }
}
