package net.team33.fscalc.ui;

import net.team33.fscalc.ui.rsrc.Ico;
import net.team33.fscalc.work.Context;
import net.team33.messaging.Listener;

import javax.swing.*;
import java.awt.*;

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
        this.add(new NAVBCK(), GBC_NAVBCK);
        this.add(new NAVFWD(), GBC_NAVFWD);
        this.add(new NAVUP(), GBC_NAVUP);
        this.add(new ICON(), GBC_ICON);
        this.add(new TEXT(), GBC_TEXT);
        this.add(new REFRESH(), GBC_REFRESH);
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
            this.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            this.setFont(new Font(this.getFont().getName(), 1, this.getFont().getSize()));
            PathPane.this.getContext().getRegister().add(new ADAPTER());
        }

        private class ADAPTER implements Listener<Context.MsgChDir> {

            @Override
            public final void pass(Context.MsgChDir message) {
                TEXT.this.setText(message.getPath().getPath());
            }
        }
    }
}
