package net.team33.fscalc.ui;

import net.team33.fscalc.ui.rsrc.Ico;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class PathPane extends JPanel {

    protected static final Insets GBC_INSETS = new Insets(2, 2, 2, 2);
    protected static final int GBC_ANCHOR = 10;
    protected static final int GBC_FILL = 2;
    private static final GridBagConstraints GBC_NAVBCK;
    private static final GridBagConstraints GBC_NAVFWD;
    private static final GridBagConstraints GBC_NAVUP;
    private static final GridBagConstraints GBC_ICON;
    private static final GridBagConstraints GBC_TEXT;
    private static final GridBagConstraints GBC_REFRESH;

    static {
        GBC_NAVBCK = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_NAVFWD = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_NAVUP = new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_ICON = new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_TEXT = new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
        GBC_REFRESH = new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0);
    }

    public PathPane(final Context context) {
        super(new GridBagLayout());
        add(navBack(), GBC_NAVBCK);
        add(navForward(), GBC_NAVFWD);
        add(navUp(), GBC_NAVUP);
        add(new ICON(), GBC_ICON);
        add(new TEXT(context), GBC_TEXT);
        add(refresh(), GBC_REFRESH);
    }

    private class ICON extends JLabel {
        public ICON() {
            super(Ico.OPNDIR);
        }
    }

    private static JButton navBack() {
        return SymButton.builder(Ico.PRED, "Navigate back")
                        .build();
    }

    private static JButton navForward() {
        return SymButton.builder(Ico.SUCC, "Navigate forward")
                        .build();
    }

    private static JButton navUp() {
        return SymButton.builder(Ico.UPDIR, "Navigate to upper directory")
                        .build();
    }

    private static JButton refresh() {
        return SymButton.builder(Ico.RELOAD, "Refresh view")
                        .build();
    }

    private static class TEXT extends JTextField {

        private TEXT(final Context context) {
            super(16);
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            setFont(new Font(getFont().getName(), 1, getFont().getSize()));
            context.getRegister().add(new ADAPTER());
        }

        private class ADAPTER implements Consumer<Context.MsgChDir> {

            @Override
            public final void accept(final Context.MsgChDir message) {
                setText(message.getPath().getPath());
            }
        }
    }
}
