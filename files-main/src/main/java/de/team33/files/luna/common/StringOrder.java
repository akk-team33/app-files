package de.team33.files.luna.common;

import java.util.Comparator;

@FunctionalInterface
public interface StringOrder extends Comparator<String> {

    StringOrder IGNORE_CASE = String::compareToIgnoreCase;
    StringOrder RESPECT_CASE = String::compareTo;
    StringOrder NATURAL = IGNORE_CASE.thenComparing(RESPECT_CASE)::compare;
}
