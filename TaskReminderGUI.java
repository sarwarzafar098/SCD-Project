package task_reminder_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskReminderGUI extends JFrame {
    private final TaskManager taskManager = new TaskManager();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(listModel);

    public TaskReminderGUI() {
        setTitle("📋 Task Reminder App");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // === Fonts ===
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        // === Input Fields ===
        JTextField titleField = new JTextField();
        JTextField dueDateField = new JTextField(); // Format: yyyy-mm-dd
        titleField.setPreferredSize(new Dimension(200, 25));
        dueDateField.setPreferredSize(new Dimension(200, 25));

        // === Buttons ===
        JButton addButton = new JButton("Add Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton completeButton = new JButton("Mark Completed");
        JButton sortButton = new JButton("Sort by Due Date");

        // === Input Panel with GridBagLayout ===
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 - Task Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Task Title:");
        titleLabel.setFont(labelFont);
        inputPanel.add(titleLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        // Row 1 - Due Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel dateLabel = new JLabel("Due Date (yyyy-mm-dd):");
        dateLabel.setFont(labelFont);
        inputPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(dueDateField, gbc);

        // Row 2 - Add Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(addButton, gbc);

        // === Task List ===
        taskList.setVisibleRowCount(10);
        taskList.setFixedCellHeight(30);
        taskList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane listScroll = new JScrollPane(taskList);
        listScroll.setPreferredSize(new Dimension(550, 250));

        // === Button Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(sortButton);

        // === Main Layout ===
        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(listScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // === Event Listeners ===

        // Add Task
        addButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String dateText = dueDateField.getText().trim();
            if (title.isEmpty() || dateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both fields are required.");
                return;
            }

            try {
                LocalDate dueDate = LocalDate.parse(dateText);
                Task task = new Task(title, dueDate);
                taskManager.addTask(task);
                updateTaskList();
                titleField.setText("");
                dueDateField.setText("");
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date (yyyy-mm-dd).");
            }
        });

        // Trigger Add Button on ENTER key press in title or dueDate field
        titleField.addActionListener(e -> addButton.doClick());
        dueDateField.addActionListener(e -> addButton.doClick());

        // Delete Task
        deleteButton.addActionListener(e -> {
            int index = taskList.getSelectedIndex();
            if (index != -1) {
                taskManager.deleteTask(index);
                updateTaskList();
            } else {
                JOptionPane.showMessageDialog(this, "Select a task to delete.");
            }
        });

        // Mark Completed
        completeButton.addActionListener(e -> {
            int index = taskList.getSelectedIndex();
            if (index != -1) {
                taskManager.markTaskCompleted(index);
                updateTaskList();
            } else {
                JOptionPane.showMessageDialog(this, "Select a task to mark as completed.");
            }
        });

        // Sort
        sortButton.addActionListener(e -> {
            taskManager.sortTasksByDueDate();
            updateTaskList();
        });

        // Exit Confirm
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit?",
                        "Exit", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });

        setVisible(true);
    }

    private void updateTaskList() {
        listModel.clear();
        for (Task task : taskManager.getAllTasks()) {
            listModel.addElement(task.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskReminderGUI::new);
    }
}
