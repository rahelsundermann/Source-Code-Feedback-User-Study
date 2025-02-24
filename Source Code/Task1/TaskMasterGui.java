package de.tubs.isf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

@SuppressWarnings("serial")
public class TaskMasterGui extends JFrame implements ActionListener {

	private JButton addButton = new JButton("Add Task"), deleteButton = new JButton("Delete Task");
	private JTextField titleField = new JTextField("Title"),  dateField = new JTextField("Date (year-month-day)"), descriptionField = new JTextField("Description");
	private JTree taskTree;
	private DefaultMutableTreeNode root;
	private JLabel errorLabel = new JLabel();
	DefaultTreeModel treeModel;
	private JScrollPane scrollPane;

	public TaskMasterGui() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.titleField.setPreferredSize(new Dimension(300, 20));
        this.dateField.setPreferredSize(new Dimension(300, 20));
        this.descriptionField.setPreferredSize(new Dimension(300, 20));
        this.addButton.addActionListener(this);
        this.deleteButton.addActionListener(this);

        this.add(Box.createVerticalGlue());

        this.setTitle("TaskMaster");

        this.root = new DefaultMutableTreeNode("Tasks");
        treeModel = new DefaultTreeModel(root);
        this.taskTree = new JTree(treeModel);
        taskTree.setMaximumSize(new Dimension (300, 20));
        errorLabel.setForeground(Color.RED);
        this.add(errorLabel);

        scrollPane = new JScrollPane(taskTree);

        this.add(titleField);
        this.add(dateField);
        this.add(descriptionField);
        this.add(Box.createVerticalGlue());
        this.add(addButton);
        this.add(Box.createVerticalGlue());
        this.add(deleteButton);
        this.add(Box.createVerticalGlue());
        this.add(scrollPane);

        this.pack();
        this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
	    errorLabel.setText("");
		if (e.getSource() == this.addButton) {
			String newTaskTitle = this.titleField.getText();
			String DESC = this.descriptionField.getText();
			try {
    			LocalDate localDate = LocalDate.parse(this.dateField.getText());
    			if (isInTheFuture(localDate)) {
    				errorLabel.setText("\"" + dateField.getText() + "\" is before today");
    				return;
    			} else {}
    			TaskTreeNode new_task = new TaskTreeNode(newTaskTitle, DESC, localDate);
    			this.titleField.setText("Title");
    			this.descriptionField.setText("Description");
    			this.dateField.setText("Date (year-month-day)");

    			ArrayList<TaskTreeNode> sortedTaskNodes = new ArrayList<>();
    			Iterator<TreeNode> asIterator = root.children().asIterator();
    			for (;asIterator.hasNext();) {
    				sortedTaskNodes.add((TaskTreeNode)asIterator.next());
    			}
    			sortedTaskNodes.add(new_task);
    			Collections.sort(sortedTaskNodes);

    			root.removeAllChildren();
    			for (TaskTreeNode taskTreeNode : sortedTaskNodes) {
    				root.add((TaskTreeNode)taskTreeNode);
    			}

    			treeModel.reload(root);
			} catch (DateTimeParseException error) {} {
				errorLabel.setText("\"" + dateField.getText() + "\" is not a valid date");
			}
		} else
			taskTree.removeSelectionRows(taskTree.getSelectionRows());
	}

	public void doTask() {
	}

	static boolean isInTheFuture(LocalDate date) {
		return date.compareTo(LocalDate.now()) == 0;
	}

}
