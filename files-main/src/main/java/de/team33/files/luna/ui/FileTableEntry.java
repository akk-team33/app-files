package de.team33.files.luna.ui;

import de.team33.files.luna.common.EntryOrder;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.epsilon.table.CellProperty;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;

record FileTableEntry(Variable<Path> cwd, FileEntry entry) {

    final Name name() {
        return new Name(this);
    }

    final Location location() {
        return new Location(this);
    }

    final LastModified lastModified() {
        return new LastModified(this);
    }

    final Size size() {
        return new Size(this);
    }

    abstract static class Property<P extends Property<P>> extends CellProperty<P> {

        static final Locale LOCALE = Locale.getDefault();
        static final ZoneId ZONE_ID = ZoneId.systemDefault();

        private final FileTableEntry entry;

        Property(final FileTableEntry entry, final Class<P> finalClass, final Comparator<FileEntry> order) {
            super(finalClass, comparing(Property::fileEntry, order));
            this.entry = entry;
        }

        static LocalDateTime localDateTime(final Instant instant) {
            return LocalDateTime.ofInstant(instant, ZONE_ID);
        }

        final FileEntry fileEntry() {
            return entry.entry;
        }

        final Path cwd() {
            return entry.cwd.get();
        }

        @SuppressWarnings("EqualsDoesntCheckParameterClass")
        @Override
        public final boolean equals(final Object other) {
            return equals(THIS(), other);
        }

        @Override
        public final int hashCode() {
            return fileEntry().path().hashCode();
        }
    }

    static class Name extends Property<Name> {

        Name(final FileTableEntry entry) {
            super(entry, Name.class, EntryOrder.BY_TYPE_NAME);
        }

        @Override
        public final String toString() {
            return fileEntry().name();
        }
    }

    static class Location extends Property<Location> {

        private final Path location;

        Location(final FileTableEntry entry) {
            super(entry, Location.class, EntryOrder.BY_PATH);
            this.location = cwd().relativize(fileEntry().path().getParent());
        }

        @Override
        public final String toString() {
            return Optional.of(location.toString())
                           .filter(not(String::isBlank))
                           .orElse(".");
        }
    }

    static class LastModified extends Property<LastModified> {

        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                 .withLocale(LOCALE);

        private final LocalDateTime dateTime;

        LastModified(final FileTableEntry entry) {
            super(entry, LastModified.class, EntryOrder.BY_DATE);
            this.dateTime = localDateTime(fileEntry().lastModified());
        }

        @Override
        public final String toString() {
            return dateTime.format(DATE_TIME_FORMATTER);
        }
    }

    static class Size extends Property<Size> {

        Size(final FileTableEntry entry) {
            super(entry, Size.class, EntryOrder.BY_SIZE);
        }

        @Override
        public final String toString() {
            return "%,d".formatted(fileEntry().size());
        }
    }
}
