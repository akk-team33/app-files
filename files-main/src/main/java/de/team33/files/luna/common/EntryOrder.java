package de.team33.files.luna.common;

import de.team33.patterns.io.adrastea.FileEntry;

import java.util.Comparator;

import static java.util.Comparator.comparing;

@FunctionalInterface
public interface EntryOrder extends Comparator<FileEntry> {

    EntryOrder BY_TYPE_BASE = Util::compareByType;
    EntryOrder BY_PATH = comparing(FileEntry::path, PathOrder.NATURAL)::compare;
    EntryOrder BY_TYPE_PATH = BY_TYPE_BASE.thenComparing(BY_PATH)::compare;
    EntryOrder BY_NAME = comparing(FileEntry::path, PathOrder.BY_NAME)::compare;
    EntryOrder BY_TYPE_NAME = BY_TYPE_BASE.thenComparing(BY_NAME)::compare;
    EntryOrder BY_SIZE = comparing(FileEntry::size).thenComparing(BY_PATH)::compare;
    EntryOrder BY_DATE = comparing(FileEntry::lastModified).thenComparing(BY_PATH)::compare;
}
