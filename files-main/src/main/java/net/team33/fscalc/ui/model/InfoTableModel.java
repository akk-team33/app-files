package net.team33.fscalc.ui.model;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.work.Context;
import net.team33.fscalc.work.Order;
import net.team33.messaging.Listener;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class InfoTableModel extends AbstractTableModel {
    private static final FileService FS = FileService.getInstance();
    private static final FileInfo[] NOPATHS = new FileInfo[0];
    private List<FileInfo> fileInfos = new Vector();

    private static List<FileInfo> sorted(FileInfo[] infos, Order order) {
        List<FileInfo> ret = new Vector();
        Collections.addAll(ret, infos);
        Collections.sort(ret, order);
        return ret;
    }

    private void setInfos(File[] files, Order order) {
        FileInfo[] infos = FS.getInfos(files, NOPATHS);
        this.fileInfos = sorted(infos, order);
        this.fireTableDataChanged();
    }

    public InfoTableModel(Context context) {
        context.getRegister().add(new LSTNR_CHDIR());
        context.getRegister().add(new LSTNR_CHORD());
        FS.getRegister().add(new LSTNR_UPDT());
        FS.getRegister().add(new LSTNR_INVAL());
    }

    @Override
    public final int getColumnCount() {
        return 6;
    }

    @Override
    public final String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Name";
            case 1:
                return "Summe";
            case 2:
                return "Mittel";
            case 3:
                return "# Dateien";
            case 4:
                return "# Ordner";
            case 5:
                return "# Fehler";
            default:
                return "-";
        }
    }

    @Override
    public final int getRowCount() {
        return this.fileInfos.size();
    }

    @Override
    public final FileInfo getValueAt(int rowIndex, int columnIndex) {
        return (FileInfo)this.fileInfos.get(rowIndex);
    }

    @Override
    public final Class<FileInfo> getColumnClass(int columnIndex) {
        return FileInfo.class;
    }

    private class LSTNR_CHDIR implements Listener<Context.MsgChDir> {

        @Override
        public final void pass(Context.MsgChDir message) {
            InfoTableModel.this.setInfos(message.getPath().listFiles(), ((Context)message.getSender()).getOrder());
        }
    }

    private class LSTNR_CHORD implements Listener<Context.MsgChOrder> {

        @Override
        public final void pass(Context.MsgChOrder message) {
            InfoTableModel.this.setInfos(((Context)message.getSender()).getPath().listFiles(), message.getOrder());
        }
    }

    private class LSTNR_INVAL implements Listener<FileService.MsgInvalid> {

        @Override
        public final void pass(FileService.MsgInvalid message) {
            synchronized(InfoTableModel.this.fileInfos) {
                int i = InfoTableModel.this.fileInfos.indexOf(message.getInfo());
                if (i >= 0) {
                    File path = message.getInfo().getPath();
                    if (path.exists()) {
                        InfoTableModel.this.fileInfos.set(i, FileService.getInstance().getInfo(path));
                        InfoTableModel.this.fireTableRowsUpdated(i, i);
                    } else {
                        InfoTableModel.this.fileInfos.remove(i);
                        InfoTableModel.this.fireTableRowsDeleted(i, i);
                    }
                }

            }
        }
    }

    private class LSTNR_UPDT implements Listener<FileService.MsgUpdate> {

        @Override
        public final void pass(FileService.MsgUpdate message) {
            synchronized(InfoTableModel.this.fileInfos) {
                int i = InfoTableModel.this.fileInfos.indexOf(message.getInfo());
                if (i >= 0) {
                    InfoTableModel.this.fireTableRowsUpdated(i, i);
                }

            }
        }
    }
}
