package de.team33.files.luna.context;

import de.team33.patterns.serving.alpha.Variable;

import java.time.Instant;

public interface TableViewConfig {

    Variable<Instant> optColumnWidth();
}
