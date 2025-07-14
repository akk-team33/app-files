package net.team33.fscalc.work;

import net.team33.fscalc.info.FileInfo;

import java.util.Comparator;

public enum Order implements Comparator<FileInfo> {
    DEFAULT_ASC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareDefault(fileInfo1, fileInfo2);
        }
    },
    DEFAULT_DSC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareDefault(fileInfo2, fileInfo1);
        }
    },
    TTLSIZE_ASC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareTtlSize(fileInfo1, fileInfo2);
        }
    },
    TTLSIZE_DSC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareTtlSize(fileInfo2, fileInfo1);
        }
    },
    AVGSIZE_ASC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareAvgSize(fileInfo1, fileInfo2);
        }
    },
    AVGSIZE_DSC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareAvgSize(fileInfo2, fileInfo1);
        }
    },
    FILECNT_ASC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareFileCnt(fileInfo1, fileInfo2);
        }
    },
    FILECNT_DSC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareFileCnt(fileInfo2, fileInfo1);
        }
    },
    DIRCNT_ASC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareDirCnt(fileInfo1, fileInfo2);
        }
    },
    DIRCNT_DSC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareDirCnt(fileInfo2, fileInfo1);
        }
    },
    ERRCNT_ASC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareErrCnt(fileInfo1, fileInfo2);
        }
    },
    ERRCNT_DSC {
        @Override
        public int compare(FileInfo fileInfo1, FileInfo fileInfo2) {
            return Order.compareErrCnt(fileInfo2, fileInfo1);
        }
    };

    private static int compareDefault(FileInfo fileInfo1, FileInfo fileInfo2) {
        int ret = 0;
        if (fileInfo1.getPath().isDirectory()) {
            ret -= 2;
        } else if (fileInfo1.getPath().isFile()) {
            --ret;
        }

        if (fileInfo2.getPath().isDirectory()) {
            ret += 2;
        } else if (fileInfo2.getPath().isFile()) {
            ++ret;
        }

        return ret == 0 ? compareName(fileInfo1, fileInfo2) : ret;
    }

    private static int compareName(FileInfo fileInfo1, FileInfo fileInfo2) {
        return fileInfo1.getPath().getName().compareToIgnoreCase(fileInfo2.getPath().getName());
    }

    private static int compareLong(long num1, long num2) {
        if (num1 > num2) {
            return 1;
        } else {
            return num1 < num2 ? -1 : 0;
        }
    }

    private static int compareTtlSize(FileInfo fileInfo1, FileInfo fileInfo2) {
        return compareLong(fileInfo1.getTotalSize(), fileInfo2.getTotalSize());
    }

    private static int compareAvgSize(FileInfo fileInfo1, FileInfo fileInfo2) {
        return compareLong(fileInfo1.getAverageSize(), fileInfo2.getAverageSize());
    }

    private static int compareFileCnt(FileInfo fileInfo1, FileInfo fileInfo2) {
        return compareLong(fileInfo1.getFileCount(), fileInfo2.getFileCount());
    }

    private static int compareDirCnt(FileInfo fileInfo1, FileInfo fileInfo2) {
        return compareLong(fileInfo1.getDirCount(), fileInfo2.getDirCount());
    }

    private static int compareErrCnt(FileInfo fileInfo1, FileInfo fileInfo2) {
        return compareLong(fileInfo1.getErrorCount(), fileInfo2.getErrorCount());
    }
}
