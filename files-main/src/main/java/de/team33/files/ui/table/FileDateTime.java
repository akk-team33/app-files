package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;

public class FileDateTime extends FileProperty<FileDateTime> {

    static final ZoneId ZONE_ID = ZoneId.systemDefault();
    static final Locale DEFAULT = Locale.getDefault();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                                                        .withLocale(DEFAULT);
    private static final Comparator<FileDateTime> ORDER = Comparator.comparing(FileProperty::entry, ENTRY_LAST_MODIFIED);

    private final LocalDateTime dateTime;

    public FileDateTime(final FileEntry entry) {
        super(entry, FileDateTime.class, ORDER);
        this.dateTime = LocalDateTime.ofInstant(entry().lastModified(), ZONE_ID);
    }

    @Override
    public final String toString() {
        return dateTime.format(FORMATTER);
    }
}
