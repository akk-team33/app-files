package net.team33.fscalc.ui;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.ui.model.InfoTableModel;
import net.team33.fscalc.work.Context;
import net.team33.fscalc.work.Order;
import net.team33.messaging.Listener;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Originator;
import net.team33.messaging.multiplex.Register;
import net.team33.messaging.multiplex.Router;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public abstract class InfoTable extends JTable implements Originator<Message<InfoTable>> {
    private static final FileService FS = FileService.getInstance();
    private static final Order[][] orders;
    private final Router<Message<InfoTable>> router = new Router();

    static {
        orders = new Order[][]{{Order.DEFAULT_ASC, Order.DEFAULT_DSC}, {Order.TTLSIZE_DSC, Order.TTLSIZE_ASC}, {Order.AVGSIZE_DSC, Order.AVGSIZE_ASC}, {Order.FILECNT_DSC, Order.FILECNT_ASC}, {Order.DIRCNT_DSC, Order.DIRCNT_ASC}, {Order.ERRCNT_DSC, Order.ERRCNT_ASC}};
    }

    protected abstract Context getContext();

    public InfoTable(InfoTableModel model) {
        super(model);
        this.getTableHeader().setDefaultRenderer(new HEADRENDERER());
        CellRenderer cr = new CellRenderer();
        cr.setFileInfo(FS.getInfo(new File("")), 0);
        this.setRowHeight(cr.getPreferredSize().height + 3);
        this.setDefaultRenderer(FileInfo.class, cr);
        this.setShowGrid(false);
        this.setRowSelectionAllowed(true);
        this.setColumnSelectionAllowed(false);
        this.getTableHeader().addMouseListener(new HEADER_MOUSE_LISTENER());
        this.addMouseListener(new MOUSE_LISTENER());
        this.getSelectionModel().addListSelectionListener(new SEL_LISTENER());
        FileService.getInstance().getRegister().add(new LSTNR_UPDINFO());
        this.router.addInitial(new UPD_SEL(null));
    }

    @Override
    public final InfoTableModel getModel() {
        return (InfoTableModel)super.getModel();
    }

    @Override
    public final Register<Message<InfoTable>> getRegister() {
        return this.router;
    }

    private void fire(Message<InfoTable> message) {
        this.router.route(message);
    }

    private class HEADER_MOUSE_LISTENER extends MouseAdapter {

        @Override
        public final void mouseClicked(MouseEvent e) {
            int tcol = InfoTable.this.getTableHeader().columnAtPoint(e.getPoint());
            int mcol = InfoTable.this.convertColumnIndexToModel(tcol);
            Order ord0 = InfoTable.this.getContext().getOrder();
            InfoTable.this.getContext().setOrder(this.getOrder(mcol, ord0));
        }

        private Order getOrder(int colIndex, Order currOrder) {
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

    private class HEADRENDERER extends HeadRenderer implements TableCellRenderer {

        @Override
        protected final Context getContext() {
            return InfoTable.this.getContext();
        }
    }

    private class LSTNR_UPDINFO implements Listener<FileService.MsgUpdate> {

        @Override
        public final void pass(FileService.MsgUpdate message) {
            File path = message.getInfo().getPath();
            if (path.equals(InfoTable.this.getContext().getPath())) {
                InfoTable.this.getTableHeader().repaint();
            }

        }
    }

    private class MOUSE_LISTENER extends MouseAdapter {

        @Override
        public final void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int row = InfoTable.this.rowAtPoint(e.getPoint());
                FileInfo it = (FileInfo)InfoTable.this.getValueAt(row, 0);
                File path = it.getPath();
                if (path.isDirectory()) {
                    InfoTable.this.getContext().setPath(it.getPath());
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
        public final void valueChanged(ListSelectionEvent e) {
            InfoTable.this.fire(InfoTable.this.new UPD_SEL(e));
        }
    }

    private class UPD_SEL extends MSG_BASE implements UpdateSelection {
        private final ListSelectionEvent evnt;

        private UPD_SEL(ListSelectionEvent evnt) {
            this.evnt = evnt;
        }

        @Override
        public final ListSelectionEvent getListSelectionEvent() {
            return this.evnt;
        }
    }

    public interface UpdateSelection extends Message<InfoTable> {
        ListSelectionEvent getListSelectionEvent();
    }
}
