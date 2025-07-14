//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
    private static Map<File, DIR_INFO> cache = new HashMap();

    public static FileService getInstance() {
        return SINGLETON;
    }

    private FileService() {
    }

    public FileInfo getInfo(File path) {
        try {
            path = path.getCanonicalFile();
        } catch (IOException var3) {
            Log.warning(var3);
            path = path.getAbsoluteFile();
        }

        if (path.isDirectory()) {
            return this.getDirInfo(path);
        } else if (path.isFile()) {
            return new FILE_INFO(path);
        } else if (path.exists()) {
            throw new RuntimeException("Kein Verzeichnis oder Datei: '" + path.getPath() + "'");
        } else {
            throw new RuntimeException("Existiert nicht: '" + path.getPath() + "'");
        }
    }

    public FileInfo[] getInfos(File[] paths, FileInfo[] fallback) {
        if (paths == null) {
            return fallback;
        } else {
            FileInfo[] ret = new FileInfo[paths.length];

            for(int i = 0; i < paths.length; ++i) {
                ret[i] = this.getInfo(paths[i]);
            }

            return ret;
        }
    }

    public void delete(File[] paths, Controller ctrl) {
        this.delete((File[])paths, ctrl, 0);
    }

    private void delete(File[] paths, Controller ctrl, int i) {
        File[] var7 = paths;
        int var6 = paths.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            File path = var7[var5];
            if (ctrl.isQuitting()) {
                break;
            }

            this.delete(path, ctrl, i);
        }

    }

    private void delete(File path, Controller ctrl, int i) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                ctrl = ctrl.getSubController((long)(files.length + 1));
                this.delete(files, ctrl, i + 1);
            }
        }

        FileInfo info = this.getInfo(path);
        if (path.delete()) {
            cache.remove(info);
            this.fire(new MSG_INVALID(info));
            if (i == 0) {
                File parent = path.getParentFile();
                if (cache.containsKey(parent)) {
                    ((DIR_INFO)cache.get(parent)).reset();
                }
            }
        } else {
            String msgPrefix = path.isDirectory() ? "Das Verzeichnis" : "Die Datei";
            Log.warning(String.format("%s '%s' konnte nicht gelöscht werden", msgPrefix, path.getPath()));
        }

        ctrl.increment(path.getPath(), 1L);
    }

    private DIR_INFO getDirInfo(File path) {
        if (cache.containsKey(path)) {
            return (DIR_INFO)cache.get(path);
        } else {
            DIR_INFO ret = new DIR_INFO(path);
            cache.put(path, ret);
            return ret;
        }
    }

    private abstract class BASE_INFO implements FileInfo {
        private File path;

        private BASE_INFO(File path) {
            this.path = path;
        }

        public long getAverageSize() {
            return this.getFileCount() > 0L ? this.getTotalSize() / this.getFileCount() : 0L;
        }

        public File getPath() {
            return this.path;
        }

        public final void reset() {
            this.resetLocal();
            File parent = this.getPath().getParentFile();
            if (FileService.cache.containsKey(parent)) {
                ((DIR_INFO)FileService.cache.get(parent)).reset();
            }

        }

        protected abstract void resetLocal();

        public boolean equals(Object obj) {
            if (obj != null && obj instanceof FileInfo) {
                FileInfo fi = (FileInfo)obj;
                return fi.getPath().equals(this.path);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.path.hashCode();
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

        private DIR_INFO(File path) {
            super(path);
            this.dir_data = new DIR_DATA();
        }

        protected void resetLocal() {
            this.dir_data = new DIR_DATA();
            FileService.this.fire(new MSG_UPDATE());
        }

        public void calculate(Controller ctrl) {
            if (!this.isDefinite()) {
                DIR_DATA dta = new DIR_DATA();
                FileInfo[] children = FileService.this.getInfos(this.getPath().listFiles(), (FileInfo[])null);
                dta.definite = true;
                if (children == null) {
                    dta.errCount = dta.errCount + 1L;
                } else {
                    ctrl = ctrl.getSubController((long)(children.length + 1));
                    FileInfo[] var7 = children;
                    int var6 = children.length;

                    for(int var5 = 0; var5 < var6; ++var5) {
                        FileInfo child = var7[var5];
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
                    FileService.this.fire(new MSG_UPDATE());
                }
            }

            ctrl.increment(this.getPath().getPath(), 1L);
        }

        public long getDirCount() {
            return this.dir_data.dirCount;
        }

        public long getErrorCount() {
            return this.dir_data.errCount;
        }

        public long getFileCount() {
            return this.dir_data.fileCount;
        }

        public long getTotalSize() {
            return this.dir_data.totalSize;
        }

        public boolean isDefinite() {
            return this.dir_data.definite;
        }

        private class MSG_UPDATE extends MSG_BASE implements MsgUpdate {
            private MSG_UPDATE() {
                super();
            }

            public FileInfo getInfo() {
                return DIR_INFO.this;
            }
        }
    }

    private class FILE_INFO extends BASE_INFO {
        private FILE_INFO(File path) {
            super(path);
        }

        protected void resetLocal() {
        }

        public void calculate(Controller ctrl) {
            ctrl.increment(this.getPath().getPath(), 1L);
        }

        public long getDirCount() {
            return 0L;
        }

        public long getErrorCount() {
            return 0L;
        }

        public long getFileCount() {
            return 1L;
        }

        public long getTotalSize() {
            return this.getPath().length();
        }

        public boolean isDefinite() {
            return true;
        }
    }

    private class MSG_BASE implements Message<FileService> {
        private MSG_BASE() {
        }

        public FileService getSender() {
            return FileService.this;
        }
    }

    private class MSG_INVALID extends MSG_BASE implements MsgInvalid {
        private FileInfo info;

        private MSG_INVALID(FileInfo info) {
            this.info = info;
        }

        public FileInfo getInfo() {
            return this.info;
        }
    }

    public interface MsgInvalid extends Message<FileService> {
        FileInfo getInfo();
    }

    public interface MsgUpdate extends Message<FileService> {
        FileInfo getInfo();
    }
}
