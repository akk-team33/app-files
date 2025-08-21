package net.team33.fscalc.ui.model;

import net.team33.fscalc.info.FileInfo;
import net.team33.fscalc.info.FileService;
import net.team33.fscalc.work.Context;
import net.team33.fscalc.work.Order;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class InfoTableModel extends AbstractTableModel {
    private static final FileService FS = FileService.getInstance();
    private static final FileInfo[] NOPATHS = new FileInfo[0];
    private List<FileInfo> fileInfos = new Vector();

    private static List<FileInfo> sorted(final FileInfo[] infos, final Order order) {
        final List<FileInfo> ret = new Vector();
        Collections.addAll(ret, infos);
        Collections.sort(ret, order);
        return ret;
    }

    private void setInfos(final File[] files, final Order order) {
        final FileInfo[] infos = FS.getInfos(files, NOPATHS);
        this.fileInfos = sorted(infos, order);
        fireTableDataChanged();
    }

    public InfoTableModel(final Context context) {
        context.path().retrieve(path -> setInfos(path.toFile().listFiles(), context.order().get()));
        context.order().retrieve(order -> setInfos(context.path().get().toFile().listFiles(), order));
        FS.getRegister().add(new LSTNR_UPDT());
        FS.getRegister().add(new LSTNR_INVAL());
    }

    @Override
    public final int getColumnCount() {
        return 6;
    }

    @Override
    public final String getColumnName(final int col) {
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
        return fileInfos.size();
    }

    @Override
    public final FileInfo getValueAt(final int rowIndex, final int columnIndex) {
        return fileInfos.get(rowIndex);
    }

    @Override
    public final Class<FileInfo> getColumnClass(final int columnIndex) {
        return FileInfo.class;
    }

    private class LSTNR_INVAL implements Consumer<FileService.MsgInvalid> {

        @Override
        public final void accept(final FileService.MsgInvalid message) {
            final int i = fileInfos.indexOf(message.getInfo());
            if (i >= 0) {
                final File path = message.getInfo().getPath();
                if (path.exists()) {
                    fileInfos.set(i, FileService.getInstance().getInfo(path));
                    fireTableRowsUpdated(i, i);
                } else {
                    fileInfos.remove(i);
                    fireTableRowsDeleted(i, i);
                }
            }
        }
    }

    private class LSTNR_UPDT implements Consumer<FileService.MsgUpdate> {

        @Override
        public final void accept(final FileService.MsgUpdate message) {
            synchronized (fileInfos) {
                final int i = fileInfos.indexOf(message.getInfo());
                if (i >= 0) {
                    fireTableRowsUpdated(i, i);
                }

            }
        }
    }
}
