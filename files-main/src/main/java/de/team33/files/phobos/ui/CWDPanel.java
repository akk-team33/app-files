package de.team33.files.phobos.ui;

import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTextFields;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.nio.file.Path;
import java.util.Objects;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

final class CWDPanel {

    private final Variable<Path> cwd;
    private final JPanel jPanel;

    private volatile String lastText = "";

    private CWDPanel(final Context context) {
        cwd = context.cwd();
        jPanel = JPanels.builder(new BorderLayout())
                        .add(new WestPanel().panel, BorderLayout.LINE_START)
                        .add(new CenterPanel().panel, BorderLayout.CENTER)
                        .add(new EastPanel().panel, BorderLayout.LINE_END)
                        .build();
    }

    static CWDPanel by(final Context context) {
        return new CWDPanel(context);
    }

    final JPanel ui() {
        return jPanel;
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    @FunctionalInterface
    interface Context {

        Variable<Path> cwd();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private final class WestPanel {

        private final JPanel panel;

        private WestPanel() {
            final JButton bckButton = JButtons.builder()
                                              .setIcon(Ico.PRED)
                                              .build();
            final JButton fwdButton = JButtons.builder()
                                              .setIcon(Ico.SUCC)
                                              .build();
            final JButton upButton = JButtons.builder()
                                             .setIcon(Ico.UPDIR)
                                             .build();
            panel = JPanels.builder(new GridLayout(1, 4))
                           .add(bckButton)
                           .add(fwdButton)
                           .add(upButton)
                           .build();
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private final class EastPanel {

        private final JPanel panel;

        private EastPanel() {
            final JButton bckButton = JButtons.builder()
                                              .setIcon(Ico.RELOAD)
                                              .build();
            panel = JPanels.builder(new GridLayout(1, 1))
                           .add(bckButton)
                           .build();
        }
    }

    private final class CenterPanel {

        private final JTextField textField;
        private final JPanel panel;

        private CenterPanel() {
            textField = JTextFields.builder()
                                   .subscribe(Channel.FOCUS_GAINED, this::onInputFocusGained)
                                   .subscribe(Channel.FOCUS_LOST, this::onInputConfirmed)
                                   .subscribe(Channel.JTF_ACTION_PERFORMED, this::onInputConfirmed)
                                   .build();
            panel = JPanels.builder(new BorderLayout())
                           .add(new JLabel(Ico.OPNDIR), BorderLayout.LINE_START)
                           .add(textField, BorderLayout.CENTER)
                           .build();
            cwd.subscribe(INIT, this::onSetCWD);
        }

        private void onSetCWD(final Path path) {
            textField.setText(path.toString());
        }

        private void onInputFocusGained(final FocusEvent event) {
            lastText = textField.getText();
        }

        private void onInputConfirmed(final AWTEvent event) {
            if (!Objects.equals(lastText, textField.getText())) {
                cwd.set(Path.of(textField.getText()));
                if (textField.isFocusOwner()) {
                    textField.transferFocus();
                    textField.requestFocusInWindow();
                }
            }
        }
    }
}
