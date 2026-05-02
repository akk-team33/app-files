package de.team33.files.luna.ui;

import de.team33.files.luna.context.Icons;
import de.team33.files.luna.context.UIContext;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTextFields;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.nio.file.Path;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class CWDInput {

    private final Variable<Path> cwd;
    private final JTextField textField;
    private final JPanel panel;

    private CWDInput(final UIContext context) {
        this.cwd = context.cwd();
        this.textField = JTextFields.builder()
                                    .subscribe(Channel.JTF_ACTION_PERFORMED, this::onTextInput)
                                    .subscribe(Channel.FOCUS_LOST, this::onTextInput)
                                    .subscribe(Channel.FOCUS_GAINED, this::onFocusGained)
                                    .build();
        this.panel = JPanels.builder(new BorderLayout())
                            .add(newOptions(context.icons()), BorderLayout.LINE_START)
                            .add(textField, BorderLayout.CENTER)
                            .build();
        cwd.subscribe(INIT, this::onSetCWD);
    }

    public static CWDInput by(final UIContext context) {
        return new CWDInput(context);
    }

    private Component newOptions(final Icons icons) {
        return JButtons.builder()
                       .setIcon(icons.upFolder())
                       .subscribe(Channel.ACTION_PERFORMED, this::onDirUp)
                       .build();
    }

    private void onDirUp(final ActionEvent event) {
        final Path parent = cwd.get().getParent();
        if (null != parent) {
            cwd.set(parent);
        }
    }

    private void onFocusGained(final FocusEvent event) {
        textField.setSelectionStart(0);
        textField.setSelectionEnd(Integer.MAX_VALUE);
    }

    private void onTextInput(final Object event) {
        cwd.set(Path.of(textField.getText()));
    }

    private void onSetCWD(final Path path) {
        textField.setText(path.toString());
    }

    public final Component ui() {
        return panel;
    }
}