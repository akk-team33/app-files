package de.team33.files.gamma.ui;

import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTextFields;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.nio.file.Path;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class CWDInput {

    private final Variable<Path> cwd;
    private final JTextField textField;
    private final JPanel panel;

    private CWDInput(final Context context) {
        this.cwd = context.cwd();
        this.textField = JTextFields.builder()
                                    .subscribe(Channel.FOCUS_LOST, this::onTextInput)
                                    .build();
        this.panel = JPanels.builder(new BorderLayout())
                            .add(textField, BorderLayout.CENTER)
                            .build();
        cwd.subscribe(INIT, this::onSetCWD);
    }

    private void onTextInput(final FocusEvent event) {
        cwd.set(Path.of(textField.getText()));
    }

    private void onSetCWD(final Path path) {
        textField.setText(path.toString());
    }

    public static CWDInput with(final Context context) {
        return new CWDInput(context);
    }

    public final Component ui() {
        return panel;
    }

    public interface Context {

        Variable<Path> cwd();
    }
}
