package net.team33.fscalc.info;

import net.team33.application.Log;
import net.team33.fscalc.task.Controller;
import net.team33.messaging.Message;
import net.team33.messaging.multiplex.Sender;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileService extends Sender<Message<FileService>> {
    private static final FileService SINGLETON = new FileService();
    private static final String PRFX_FILE = "Die Datei";
    private static final String PRFX_DIR = "Das Verzeichnis";
    private static final String FMT_UNDELETABLE = "%s '%s' konnte nicht gelöscht werden";
    private static final Map<File, DIR_INFO> cache = new HashMap();

    public static FileService getInstance() {
        return SINGLETON;
    }

    private FileService() {
    }

    public final FileInfo getInfo(File path) {
        try {
            path = path.getCanonicalFile();
        } catch (final IOException var3) {
            Log.warning(var3);
            path = path.getAbsoluteFile();
        }

        if (path.isDirectory()) {
            return getDirInfo(path);
        } else if (path.isFile()) {
            return new FILE_INFO(path);
        } else if (path.exists()) {
            throw new RuntimeException("Kein Verzeichnis oder Datei: '" + path.getPath() + "'");
        } else {
            throw new RuntimeException("Existiert nicht: '" + path.getPath() + "'");
        }
    }

    public final FileInfo[] getInfos(final File[] paths, final FileInfo[] fallback) {
        if (paths == null) {
            return fallback;
        } else {
            final FileInfo[] ret = new FileInfo[paths.length];

            for(int i = 0; i < paths.length; ++i) {
                ret[i] = getInfo(paths[i]);
            }

            return ret;
        }
    }

    public final void delete(final File[] paths, final Controller ctrl) {
        delete(paths, ctrl, 0);
    }

    private void delete(final File[] paths, final Controller ctrl, final int i) {
        final File[] var7 = paths;
        final int var6 = paths.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            final File path = var7[var5];
            if (ctrl.isQuitting()) {
                break;
            }

            delete(path, ctrl, i);
        }

    }

    private void delete(final File path, Controller ctrl, final int i) {
        if (path.isDirectory()) {
            final File[] files = path.listFiles();
            if (files != null) {
                ctrl = ctrl.getSubController(files.length + 1);
                delete(files, ctrl, i + 1);
            }
        }

        final FileInfo info = getInfo(path);
        if (path.delete()) {
            cache.remove(info);
            fire(new MSG_INVALID(info));
            if (i == 0) {
                final File parent = path.getParentFile();
                if (cache.containsKey(parent)) {
                    cache.get(parent).reset();
                }
            }
        } else {
            final String msgPrefix = path.isDirectory() ? "Das Verzeichnis" : "Die Datei";
            Log.warning(String.format("%s '%s' konnte nicht gelöscht werden", msgPrefix, path.getPath()));
        }

        ctrl.increment(path.getPath(), 1L);
    }

    private DIR_INFO getDirInfo(final File path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        } else {
            final DIR_INFO ret = new DIR_INFO(path);
            cache.put(path, ret);
            return ret;
        }
    }

    private abstract class BASE_INFO implements FileInfo {
        private final File path;

        private BASE_INFO(final File path) {
            this.path = path;
        }

        @Override
        public final long getAverageSize() {
            return getFileCount() > 0L ? getTotalSize() / getFileCount() : 0L;
        }

        @Override
        public final File getPath() {
            return path;
        }

        @Override
        public final void reset() {
            resetLocal();
            final File parent = getPath().getParentFile();
            if (FileService.cache.containsKey(parent)) {
                FileService.cache.get(parent).reset();
            }

        }

        protected abstract void resetLocal();

        public final boolean equals(final Object obj) {
            if (obj != null && obj instanceof FileInfo) {
                final FileInfo fi = (FileInfo)obj;
                return fi.getPath().equals(path);
            } else {
                return false;
            }
        }

        public final int hashCode() {
            return path.hashCode();
        }
    }

    private static class DIR_DATA {
        private boolean definite;
        private long dirCount;
        private long errCount;
        private long fileCount;
        private long totalSize;

        private DIR_DATA() {
            this.definite = false;
            this.dirCount = 1L;
            this.errCount = 0L;
            this.fileCount = 0L;
            this.totalSize = 0L;
        }
    }

    private class DIR_INFO extends BASE_INFO {
        private DIR_DATA dir_data;

        private DIR_INFO(final File path) {
            super(path);
            this.dir_data = new DIR_DATA();
        }

        @Override
        protected final void resetLocal() {
            this.dir_data = new DIR_DATA();
            fire(new MSG_UPDATE());
        }

        @Override
        public final void calculate(Controller ctrl) {
            if (!isDefinite()) {
                final DIR_DATA dta = new DIR_DATA();
                final FileInfo[] children = getInfos(getPath().listFiles(), null);
                dta.definite = true;
                if (children == null) {
                    dta.errCount = dta.errCount + 1L;
                } else {
                    ctrl = ctrl.getSubController(children.length + 1);
                    final FileInfo[] var7 = children;
                    final int var6 = children.length;

                    for(int var5 = 0; var5 < var6; ++var5) {
                        final FileInfo child = var7[var5];
                        if (ctrl.isQuitting()) {
                            dta.definite = false;
                            break;
                        }

                        child.calculate(ctrl);
                        dta.definite = dta.definite & child.isDefinite();
                        dta.dirCount = dta.dirCount + child.getDirCount();
                        dta.errCount = dta.errCount + child.getErrorCount();
                        dta.fileCount = dta.fileCount + child.getFileCount();
                        dta.totalSize = dta.totalSize + child.getTotalSize();
                    }
                }

                if (dta.definite) {
                    this.dir_data = dta;
                    fire(new MSG_UPDATE());
                }
            }

            ctrl.increment(getPath().getPath(), 1L);
        }

        @Override
        public final long getDirCount() {
            return dir_data.dirCount;
        }

        @Override
        public final long getErrorCount() {
            return dir_data.errCount;
        }

        @Override
        public final long getFileCount() {
            return dir_data.fileCount;
        }

        @Override
        public final long getTotalSize() {
            return dir_data.totalSize;
        }

        @Override
        public final boolean isDefinite() {
            return dir_data.definite;
        }

        private class MSG_UPDATE extends MSG_BASE implements MsgUpdate {

            @Override
            public final FileInfo getInfo() {
                return DIR_INFO.this;
            }
        }
    }

    private class FILE_INFO extends BASE_INFO {
        private FILE_INFO(final File path) {
            super(path);
        }

        @Override
        protected void resetLocal() {
        }

        @Override
        public final void calculate(final Controller ctrl) {
            ctrl.increment(getPath().getPath(), 1L);
        }

        @Override
        public final long getDirCount() {
            return 0L;
        }

        @Override
        public final long getErrorCount() {
            return 0L;
        }

        @Override
        public final long getFileCount() {
            return 1L;
        }

        @Override
        public final long getTotalSize() {
            return getPath().length();
        }

        @Override
        public final boolean isDefinite() {
            return true;
        }
    }

    private class MSG_BASE implements Message<FileService> {

        @Override
        public final FileService getSender() {
            return FileService.this;
        }
    }

    private class MSG_INVALID extends MSG_BASE implements MsgInvalid {
        private final FileInfo info;

        private MSG_INVALID(final FileInfo info) {
            this.info = info;
        }

        @Override
        public final FileInfo getInfo() {
            return info;
        }
    }

    public interface MsgInvalid extends Message<FileService> {
        FileInfo getInfo();
    }

    public interface MsgUpdate extends Message<FileService> {
        FileInfo getInfo();
    }
}
