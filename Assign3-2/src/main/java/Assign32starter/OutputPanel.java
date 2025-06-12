package Assign32starter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.*;

public class OutputPanel extends JPanel {
    private static final long serialVersionUID = 2L;

    public interface EventHandlers {
        void inputUpdated(String input);
        void submitClicked();
    }

    private JLabel pointsLabel;
    private JTextField input;
    private JButton submit;
    private JTextArea area;
    private ArrayList<EventHandlers> handlers = new ArrayList<>();

    public OutputPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        pointsLabel = new JLabel("Current Points this round: 0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3;
        add(pointsLabel, c);

        input = new JTextField();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.75;
        input.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                for (EventHandlers handler : handlers) {
                    handler.inputUpdated(input.getText());
                }
            }
        });
        input.addActionListener(e -> {
            for (EventHandlers handler : handlers) {
                handler.submitClicked();
            }
        });
        add(input, c);

        submit = new JButton("Submit");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        submit.addActionListener(e -> {
            for (EventHandlers handler : handlers) {
                handler.submitClicked();
            }
        });
        add(submit, c);

        area = new JTextArea();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weighty = 0.75;
        JScrollPane pane = new JScrollPane(area);
        add(pane, c);
    }

    public String getInputText() {
        return input.getText();
    }

    public void setPoints(int points) {
        pointsLabel.setText("Current Points this round: " + points);
    }

    public void setInputText(String newText) {
        input.setText(newText);
    }

    public void addEventHandlers(EventHandlers handlerObj) {
        handlers.add(handlerObj);
    }

    public void appendOutput(String message) {
        area.append(message + "\n");
    }
}