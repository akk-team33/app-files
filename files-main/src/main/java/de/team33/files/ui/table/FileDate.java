package de.team33.files.ui.table;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

import static de.team33.files.ui.table.FileDateTime.DEFAULT;
import static de.team33.files.ui.table.FileDateTime.ZONE_ID;

public class FileDate extends FileProperty<FileDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                                                        .withLocale(DEFAULT);
    private static final Comparator<FileDate> ORDER = Comparator.comparing(FileProperty::entry, ENTRY_LAST_MODIFIED);

    private final LocalDate date;

    public FileDate(final Gettable<Path> cwd, final FileEntry entry) {
        super(entry, FileDate.class, ORDER);
        this.date = LocalDate.ofInstant(entry().lastModified(), ZONE_ID);
    }

    @Override
    public final String toString() {
        return date.format(FORMATTER);
    }
}
