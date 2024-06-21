package org.kviat;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskManagerApp {
    private static TaskManager taskManager = new TaskManager();
    private static DefaultListModel<SimpleTask> listModel = new DefaultListModel<>();
    private static JList<SimpleTask> taskList = new JList<>(listModel);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManagerApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Task Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        taskList.setCellRenderer(new TaskRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setSelectionBackground(new Color(97, 135, 151));// Ensure single selection
        JScrollPane scrollPane = new JScrollPane(taskList);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField taskField = new JTextField(15);

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());

        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Remove Selected Task");
        JButton markCompletedButton = new JButton("Mark as Completed");

        JButton sortByNameButton = new JButton("Sort by Name");
        JButton sortByDeadlineButton = new JButton("Sort by Deadline");

        // Styling buttons
        styleButton(addButton);
        styleButton(removeButton);
        styleButton(markCompletedButton);
        styleButton(sortByNameButton);
        styleButton(sortByDeadlineButton);

        inputPanel.add(new JLabel("Task:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Deadline:"));
        inputPanel.add(datePicker);
        inputPanel.add(timeSpinner);
        inputPanel.add(addButton);

        addButton.addActionListener(e -> {
            String taskName = taskField.getText();
            Date selectedDate = (Date) datePicker.getModel().getValue();
            Date selectedTime = (Date) timeSpinner.getValue();
            if (!taskName.isEmpty() && selectedDate != null && selectedTime != null) {
                LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime localTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                LocalDateTime deadline = LocalDateTime.of(localDate, localTime);

                if (deadline.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(frame, "Cannot add a task with a past deadline.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SimpleTask simpleTask = new SimpleTask(taskName, deadline);
                taskManager.addTask(simpleTask);
                listModel.addElement(simpleTask);
                taskField.setText("");
                model.setValue(null);
                timeSpinner.setValue(new Date());
            }
        });

        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                SimpleTask simpleTask = listModel.getElementAt(selectedIndex);
                taskManager.removeTask(simpleTask);
                listModel.removeElement(simpleTask);
            }
        });

        markCompletedButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                SimpleTask simpleTask = listModel.getElementAt(selectedIndex);
                simpleTask.setCompleted(!simpleTask.isCompleted());
                taskList.repaint();
            }
        });

        sortByNameButton.addActionListener(e -> {
            sortTasks(Comparator.comparing(t -> t.getName().toLowerCase())); // Ignore case
        });

        sortByDeadlineButton.addActionListener(e -> sortTasks(Comparator.comparing(SimpleTask::getDeadline)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.add(removeButton);
        buttonPanel.add(markCompletedButton);
        buttonPanel.add(sortByNameButton);
        buttonPanel.add(sortByDeadlineButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void sortTasks(Comparator<SimpleTask> comparator) {
        ArrayList<SimpleTask> simpleTasks = Collections.list(listModel.elements());
        simpleTasks.sort(comparator);
        listModel.clear();
        for (SimpleTask simpleTask : simpleTasks) {
            listModel.addElement(simpleTask);
        }
    }

    private static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    static class TaskRenderer extends JPanel implements ListCellRenderer<SimpleTask> {
        private JLabel nameLabel = new JLabel();
        private JLabel deadlineLabel = new JLabel();
        private JCheckBox completedCheckBox = new JCheckBox();

        public TaskRenderer() {
            setLayout(new BorderLayout(5, 5));
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.add(nameLabel);
            textPanel.add(deadlineLabel);
            add(textPanel, BorderLayout.CENTER);
            add(completedCheckBox, BorderLayout.WEST);
            setBorder(new EmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SimpleTask> list, SimpleTask simpleTask, int index, boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(simpleTask.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            deadlineLabel.setText(simpleTask.getDeadline().format(dateTimeFormatter));
            deadlineLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            completedCheckBox.setSelected(simpleTask.isCompleted());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

    static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "dd-MM-yyyy";
        private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                java.util.Calendar cal = (java.util.Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}