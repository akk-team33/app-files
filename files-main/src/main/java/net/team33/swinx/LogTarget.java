package net.team33.swinx;

import net.team33.application.logging.Formatter;
import net.team33.application.logging.Loggable;

import javax.swing.*;
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

    public LogTarget(final String title, final Formatter fmt) {
        this.title = title;
        this.fmt = fmt;
    }

    @Override
    public final void accept(final Loggable entry) {
        fmt.format(entry, out);
        out.println();
        out.flush();
    }

    public final JFrame getFrame() {
        if (frame == null) {
            this.frame = new JFrame(title);
            frame.setDefaultCloseOperation(2);
            frame.getContentPane().add(new JScrollPane(getOutputArea()), "Center");
            frame.pack();
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
        }

        return frame;
    }

    public final JTextArea getOutputArea() {
        if (outputArea == null) {
            this.outputArea = new JTextArea();
            outputArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            outputArea.setEditable(false);
        }

        return outputArea;
    }

    private class Opening implements Runnable {

        @Override
        public final void run() {
            getFrame().setVisible(true);
            getFrame().toFront();
            getOutputArea().setCaretPosition(getOutputArea().getText().length());
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
        public final void write(final char[] cbuf, final int off, final int len) throws IOException {
            getOutputArea().append(new String(cbuf, off, len));
        }
    }
}
