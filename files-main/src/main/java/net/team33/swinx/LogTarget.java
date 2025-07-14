package net.team33.swinx;

import net.team33.application.logging.Formatter;
import net.team33.application.logging.Loggable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.function.Consumer;

public class LogTarget implements Consumer<Loggable> {
    private final PrintWriter out = new PrintWriter(new WRITER());
    private JFrame frame = null;
    private JTextArea outputArea = null;
    private final String title;
    private final Formatter fmt;

    public LogTarget(String title, Formatter fmt) {
        this.title = title;
        this.fmt = fmt;
    }

    @Override
    public final void accept(Loggable entry) {
        this.fmt.format(entry, this.out);
        this.out.println();
        this.out.flush();
    }

    public final JFrame getFrame() {
        if (this.frame == null) {
            this.frame = new JFrame(this.title);
            this.frame.setDefaultCloseOperation(2);
            this.frame.getContentPane().add(new JScrollPane(this.getOutputArea()), "Center");
            this.frame.pack();
            this.frame.setSize(400, 300);
            this.frame.setLocationRelativeTo((Component)null);
        }

        return this.frame;
    }

    public final JTextArea getOutputArea() {
        if (this.outputArea == null) {
            this.outputArea = new JTextArea();
            this.outputArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            this.outputArea.setEditable(false);
        }

        return this.outputArea;
    }

    private class Opening implements Runnable {

        @Override
        public final void run() {
            LogTarget.this.getFrame().setVisible(true);
            LogTarget.this.getFrame().toFront();
            LogTarget.this.getOutputArea().setCaretPosition(LogTarget.this.getOutputArea().getText().length());
        }
    }

    private class WRITER extends Writer {

        @Override
        public void close() throws IOException {
        }

        @Override
        public final void flush() throws IOException {
            SwingUtilities.invokeLater(LogTarget.this.new Opening());
        }

        @Override
        public final void write(char[] cbuf, int off, int len) throws IOException {
            LogTarget.this.getOutputArea().append(new String(cbuf, off, len));
        }
    }
}
