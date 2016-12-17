import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

public class DrawingApp {
    
    private static final String INITIAL_PROGRAM =
    "PI=3.1415926;\n"+
    "rA  = 1.2;\n"+
    "rB = 0.2;\n"+
    "d = 1.1;\n"+
    "theta = i * ((2 * PI) / n);\n"+
    "diff = rA - rB;\n"+
    "x = ((diff * cos(theta)) + (d * cos((diff / rB) * theta)))/2;\n"+
    "y = ((diff * sin(theta)) - (d * sin((diff / rB) * theta)))/2;\n"
    ;
    
    private static final int INITIAL_ITERATIONS = 300;
    
    public static void main(String[] args) {
        try {
            // Make this thing look (a bit) nicer:
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Failed to install system look and feel");
        }
        
        DrawingPanel drawingPanel = new DrawingPanel(-1.1, 1.1, -1.1, 1.1);
        drawingPanel.setProgram(INITIAL_PROGRAM);
        drawingPanel.setRepetitions(INITIAL_ITERATIONS);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(drawingPanel);
        splitPane.add(createInputPanel(drawingPanel));
        splitPane.setResizeWeight(1);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        
        JFrame frame = new JFrame("Programmatic Drawing");
        frame.add(splitPane);
        frame.setMinimumSize(new Dimension(300, 300));
        frame.setSize(new Dimension(600, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        while(true) {
            try {
                Thread.sleep(40);
                drawingPanel.repaint();
            } catch(InterruptedException e) {}
        }
    }
    
    private static JPanel createInputPanel(DrawingPanel drawingPanel) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(createProgramPanel(drawingPanel), BorderLayout.CENTER);
        panel.add(createRepetitionsPanel(drawingPanel), BorderLayout.EAST);
        return panel;
    }
    
    private static JPanel createProgramPanel(DrawingPanel drawingPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.add(new JLabel("Program:"));
        panel.add(Box.createVerticalStrut(5));
        
        JTextArea textArea = new JTextArea();
        textArea.setText(INITIAL_PROGRAM);
        textArea.setEditable(true);
        textArea.setRows(7);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(DocumentEvent e) {
                drawingPanel.setProgram(textArea.getText());
            }
            public void insertUpdate(DocumentEvent e) {
                drawingPanel.setProgram(textArea.getText());
            }
            public void changedUpdate(DocumentEvent e) {
                drawingPanel.setProgram(textArea.getText());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);
        return panel;
    }
    
    private static JPanel createRepetitionsPanel(DrawingPanel drawingPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.add(new JLabel("Repetitions (n):"));
        panel.add(Box.createVerticalStrut(5));
        
        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(INITIAL_ITERATIONS, 1, 10000, 1));
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        spinner.setMaximumSize(spinner.getPreferredSize());
        ((DefaultFormatter) ((DefaultEditor) spinner.getEditor()).getTextField().getFormatter())
                .setCommitsOnValidEdit(true);
        spinner.addChangeListener(e -> drawingPanel.setRepetitions((int) spinner.getValue()));
        panel.add(spinner);
        return panel;
    }
}
