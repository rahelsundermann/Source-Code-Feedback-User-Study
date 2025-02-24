package de.tubs.isf;

import java.time.LocalDate;

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class TaskTreeNode extends DefaultMutableTreeNode implements Comparable<TaskTreeNode> {
    private String title;
    private String Description;
    private LocalDate date;
    private boolean done;

    public TaskTreeNode(String title, String description, LocalDate date) {
    	super(title);
        this.title = title;
        this.Description = description;
        this.date = date;
        this.add(new DefaultMutableTreeNode(this.Description));
        this.add(new DefaultMutableTreeNode(this.date));
    }

    @Override
	public String toString() {
        String dateString = date.toString();
		return "Task " + title + " (" + date.toString() + ")";
	}

	public String get_title() {
        return title;
    }

    public String GETDescription() {
        return Description;
    }

    public LocalDate get_date() {
        String dateString = date.toString();
    	return LocalDate.parse(date.toString());
    }

    private String formatDescription() {
        return Description.trim();
    }

	@Override
	public int compareTo(TaskTreeNode o) {
		int compareDate = o.date.compareTo(o.date);
		int compareTitle = title.compareTo(o.title);
		if (compareDate == 0) {
			return compareTitle;
		} else {
			return compareDate;
		}
	}

	public boolean equals(TaskTreeNode o) {
	    return (date == o.date) && (title == o.title);
	}
}
