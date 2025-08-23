package net.team33.fscalc.ui;

import net.team33.fscalc.ui.rsrc.Ico;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public abstract class PathPane extends JPanel {
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

    protected abstract Context getContext();

    public PathPane() {
        super(new GridBagLayout());
        add(new NAVBCK(), GBC_NAVBCK);
        add(new NAVFWD(), GBC_NAVFWD);
        add(new NAVUP(), GBC_NAVUP);
        add(new ICON(), GBC_ICON);
        add(new TEXT(), GBC_TEXT);
        add(new REFRESH(), GBC_REFRESH);
    }

    private class ICON extends JLabel {
        public ICON() {
            super(Ico.OPNDIR);
        }
    }

    private class NAVBCK extends SymButton {
        private static final long serialVersionUID = -7984022565970896881L;

        private NAVBCK() {
            super(Ico.PRED, "Zurück");
        }
    }

    private class NAVFWD extends SymButton {
        private static final long serialVersionUID = 1682308769397587228L;

        private NAVFWD() {
            super(Ico.SUCC, "Vor");
        }
    }

    private class NAVUP extends SymButton {
        private static final long serialVersionUID = -228761213609241105L;

        private NAVUP() {
            super(Ico.UPDIR, "Zum übergeordneten Verzeichnis");
        }
    }

    private class REFRESH extends SymButton {
        private static final long serialVersionUID = 1666955799426456527L;

        private REFRESH() {
            super(Ico.RELOAD, "Aktualisieren");
        }
    }

    private class TEXT extends JTextField {
        private TEXT() {
            super(16);
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            setFont(new Font(getFont().getName(), 1, getFont().getSize()));
            getContext().cwd().subscribe(INIT, path -> setText(path.toString()));
        }
    }
}
