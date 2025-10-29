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
    private final JTextField cwdInput;
    private final JPanel jPanel;

    private volatile String lastText = "";

    private CWDPanel(final Context context) {
        cwd = context.cwd();
        cwdInput = JTextFields.builder()
                              .subscribe(Channel.FOCUS_GAINED, this::onInputFocusGained)
                              .subscribe(Channel.FOCUS_LOST, this::onInputConfirmed)
                              .subscribe(Channel.JTF_ACTION_PERFORMED, this::onInputConfirmed)
                              .build();
        jPanel = JPanels.builder(new BorderLayout())
                        .add(new WestPanel().ui(), BorderLayout.LINE_START)
                        .add(cwdInput, BorderLayout.CENTER)
                        .add(new EastPanel().ui(), BorderLayout.LINE_END)
                        .build();
        cwd.subscribe(INIT, this::onSetCWD);
    }

    static CWDPanel by(final Context context) {
        return new CWDPanel(context);
    }

    private void onSetCWD(final Path path) {
        cwdInput.setText(path.toString());
    }

    private void onInputFocusGained(final FocusEvent event) {
        lastText = cwdInput.getText();
    }

    private void onInputConfirmed(final AWTEvent event) {
        if (!Objects.equals(lastText, cwdInput.getText())) {
            cwd.set(Path.of(cwdInput.getText()));
            if (cwdInput.isFocusOwner()) {
                cwdInput.transferFocus();
                cwdInput.requestFocusInWindow();
            }
        }
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
            final JLabel symbol = new JLabel(Ico.OPNDIR);
            panel = JPanels.builder(new GridLayout(1, 4))
                           .add(bckButton)
                           .add(fwdButton)
                           .add(upButton)
                           .add(symbol)
                           .build();
        }

        final JPanel ui() {
            return panel;
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

        final JPanel ui() {
            return panel;
        }
    }
}
