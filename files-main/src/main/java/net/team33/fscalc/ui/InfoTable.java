package net.team33.fscalc.ui;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.ui.model.InfoTableModel;
import net.team33.fscalc.work.Context;
import net.team33.fscalc.work.Order;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Originator;
import net.team33.messaging.multiplex.Register;
import net.team33.messaging.multiplex.Router;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.function.Consumer;

public class InfoTable extends JTable implements Originator<Message<InfoTable>> {

    private static final FileService FS = FileService.getInstance();
    private static final Order[][] orders;

    private final Router<Message<InfoTable>> router = new Router();

    static {
        orders = new Order[][]{{Order.DEFAULT_ASC, Order.DEFAULT_DSC}, {Order.TTLSIZE_DSC, Order.TTLSIZE_ASC}, {Order.AVGSIZE_DSC, Order.AVGSIZE_ASC}, {Order.FILECNT_DSC, Order.FILECNT_ASC}, {Order.DIRCNT_DSC, Order.DIRCNT_ASC}, {Order.ERRCNT_DSC, Order.ERRCNT_ASC}};
    }

    public InfoTable(final InfoTableModel model, final Context context) {
        super(model);
        getTableHeader().setDefaultRenderer(headRenderer(context));
        final CellRenderer cr = new CellRenderer();
        cr.setFileInfo(FS.getInfo(new File("")), 0);
        setRowHeight(cr.getPreferredSize().height + 3);
        setDefaultRenderer(FileInfo.class, cr);
        setShowGrid(false);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        getTableHeader().addMouseListener(new HEADER_MOUSE_LISTENER(context));
        addMouseListener(new MOUSE_LISTENER(context));
        getSelectionModel().addListSelectionListener(new SEL_LISTENER());
        FileService.getInstance().getRegister().add(new LSTNR_UPDINFO(context));
        router.addInitial(new UPD_SEL(null));
    }

    @Override
    public final InfoTableModel getModel() {
        return (InfoTableModel)super.getModel();
    }

    @Override
    public final Register<Message<InfoTable>> getRegister() {
        return router;
    }

    private void fire(final Message<InfoTable> message) {
        router.route(message);
    }

    private class HEADER_MOUSE_LISTENER extends MouseAdapter {

        private final Context context;

        private HEADER_MOUSE_LISTENER(final Context context) {
            this.context = context;
        }

        @Override
        public final void mouseClicked(final MouseEvent e) {
            final int tcol = getTableHeader().columnAtPoint(e.getPoint());
            final int mcol = convertColumnIndexToModel(tcol);
            final Order ord0 = context.getOrder();
            context.setOrder(getOrder(mcol, ord0));
        }

        private Order getOrder(final int colIndex, final Order currOrder) {
            Order ret = null;
            if (InfoTable.orders.length > colIndex) {
                if (currOrder == InfoTable.orders[colIndex][0]) {
                    ret = InfoTable.orders[colIndex][1];
                } else {
                    ret = InfoTable.orders[colIndex][0];
                }
            }

            return ret == null ? currOrder : ret;
        }
    }

    private static HeadRenderer headRenderer(final Context context) {
        return new HeadRenderer(context);
    }

    private class LSTNR_UPDINFO implements Consumer<FileService.MsgUpdate> {

        private final Context context;

        private LSTNR_UPDINFO(final Context context) {
            this.context = context;
        }

        @Override
        public final void accept(final FileService.MsgUpdate message) {
            final File path = message.getInfo().getPath();
            if (path.equals(context.getPath())) {
                getTableHeader().repaint();
            }

        }
    }

    private class MOUSE_LISTENER extends MouseAdapter {

        private final Context context;

        private MOUSE_LISTENER(final Context context) {
            this.context = context;
        }

        @Override
        public final void mouseClicked(final MouseEvent e) {
            if (e.getClickCount() == 2) {
                final int row = rowAtPoint(e.getPoint());
                final FileInfo it = (FileInfo) getValueAt(row, 0);
                final File path = it.getPath();
                if (path.isDirectory()) {
                    context.setPath(it.getPath());
                }
            }

        }
    }

    private class MSG_BASE implements Message<InfoTable> {

        @Override
        public final InfoTable getSender() {
            return InfoTable.this;
        }
    }

    private class SEL_LISTENER implements ListSelectionListener {

        @Override
        public final void valueChanged(final ListSelectionEvent e) {
            fire(InfoTable.this.new UPD_SEL(e));
        }
    }

    private class UPD_SEL extends MSG_BASE implements UpdateSelection {
        private final ListSelectionEvent evnt;

        private UPD_SEL(final ListSelectionEvent evnt) {
            this.evnt = evnt;
        }

        @Override
        public final ListSelectionEvent getListSelectionEvent() {
            return evnt;
        }
    }

    public interface UpdateSelection extends Message<InfoTable> {
        ListSelectionEvent getListSelectionEvent();
    }
}
