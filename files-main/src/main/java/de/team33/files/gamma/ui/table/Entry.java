package de.team33.files.gamma.ui.table;

import de.team33.patterns.io.delta.FileEntry;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.function.Predicate.not;

@SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "ClassWithTooManyFields"})
public final class Entry extends FileEntry {

    private static final Locale LOCALE = Locale.getDefault();
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                             .withLocale(LOCALE);
    private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
    private static final Comparator<String> RESPECT_CASE = String::compareTo;
    private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
    private static final Comparator<Path> PATH_ORDER = Comparator.comparing(Path::toString, STRING_ORDER);
    public static final Comparator<Entry> FINAL_ORDER = Comparator.comparing(Entry::path, PATH_ORDER);
    public static final Comparator<Entry> NAME_ORDER = Comparator.comparing(Entry::name, STRING_ORDER)
                                                                 .thenComparing(FINAL_ORDER);
    public static final Comparator<Entry> LAST_MODIFIED_ORDER = Comparator.comparing(Entry::lastModified)
                                                                          .thenComparing(FINAL_ORDER);
    public static final Comparator<Entry> LAST_UPDATE_ORDER = Comparator.comparing(Entry::lastUpdated)
                                                                        .thenComparing(FINAL_ORDER);
    public static final Comparator<Entry> SIZE_ORDER = Comparator.comparing(Entry::size)
                                                                 .thenComparing(FINAL_ORDER);
    public static final Comparator<Entry> DATA_SIZE_ORDER = Comparator.comparing(Entry::dataSize)
                                                                      .thenComparing(FINAL_ORDER);

    private final Supplier<? extends Path> cwd;

    Entry(final Supplier<? extends Path> cwd, final FileEntry entry) {
        super(entry);
        this.cwd = cwd;
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private static String dateTimeToString(final LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private static LocalDateTime localDateTime(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    private static String longToString(final long l) {
        return "%,d".formatted(l);
    }

    public final String nameToString() {
        return name();
    }

    public final String pathToString() {
        return cwd.get().relativize(path()).toString();
    }

    public final String locationToString() {
        return Optional.ofNullable(path().getParent())
                       .map(path -> cwd.get().relativize(path))
                       .map(Path::toString)
                       .filter(not(String::isBlank))
                       .orElse(".");
    }

    public final String lastModifiedToString() {
        return dateTimeToString(localDateTime(lastModified()));
    }

    public final String lastUpdateToString() {
        return dateTimeToString(localDateTime(lastUpdated()));
    }

    public final String sizeToString() {
        return longToString(size());
    }

    public final String dataSizeToString() {
        return longToString(dataSize());
    }
}
