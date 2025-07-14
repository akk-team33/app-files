package net.team33.fscalc.ui;

import net.team33.fscalc.task.Task;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ProgressPane extends JPanel {
    private static final Insets GBC_INSETS = new Insets(2, 2, 2, 2);
    private int visibility = 0;
    private final JLabel lblPrfx;
    private final JLabel pathInfo;
    private final JLabel progInfo;

    public ProgressPane(Context context) {
        super(new GridBagLayout());
        this.setVisible(false);
        this.lblPrfx = new JLabel("Ermittle");
        this.pathInfo = new JLabel("");
        this.progInfo = new JLabel("");
        this.add(this.lblPrfx, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0));
        this.add(this.pathInfo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 10, 2, GBC_INSETS, 0, 0));
        this.add(this.progInfo, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, 10, 2, GBC_INSETS, 0, 0));
        context.getRegister().add(new LSN_START());
    }

    private void incVisibility(int val) {
        this.visibility += val;
        this.setVisible(this.visibility > 0);
    }

    private class LSN_PROGRESS implements Consumer<Task.Progress> {
        public LSN_PROGRESS(Task task) {
            task.getRegister().add(new LSN_CLOSE());
        }

        @Override
        public final void accept(Task.Progress message) {
            ProgressPane.this.lblPrfx.setText(message.getPrefix());
            ProgressPane.this.pathInfo.setText(message.getSubject());
            ProgressPane.this.progInfo.setText(String.format("%7.3f%%", 100.0 * message.getRatio()));
        }

        private class LSN_CLOSE implements Consumer<Task.Closure> {

            @Override
            public final void accept(Task.Closure message) {
                ((Task)message.getSender()).getRegister().remove(this);
                ((Task)message.getSender()).getRegister().remove(LSN_PROGRESS.this);
                ProgressPane.this.incVisibility(-1);
            }
        }
    }

    private class LSN_START implements Consumer<Context.MsgStarting> {

        @Override
        public final void accept(Context.MsgStarting message) {
            message.getTask().getRegister().add(ProgressPane.this.new LSN_PROGRESS(message.getTask()));
            ProgressPane.this.incVisibility(1);
        }
    }
}
