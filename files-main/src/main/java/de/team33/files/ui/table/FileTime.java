package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;

import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

import static de.team33.files.ui.table.FileDateTime.DEFAULT;
import static de.team33.files.ui.table.FileDateTime.ZONE_ID;

public class FileTime extends FileProperty<FileTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                                                                        .withLocale(DEFAULT);
    private static final Comparator<FileTime> ORDER = Comparator.comparing((FileTime fileTime) -> fileTime.time,
                                                                           LocalTime::compareTo)
                                                                .thenComparing(FileProperty::entry,
                                                                               ENTRY_LAST_MODIFIED);
    private final LocalTime time;

    public FileTime(final Gettable<Path> cwd, final FileEntry entry) {
        super(entry, FileTime.class, ORDER);
        this.time = LocalTime.ofInstant(entry().lastModified(), ZONE_ID);
    }

    @Override
    public final String toString() {
        return time.format(FORMATTER);
    }
}
