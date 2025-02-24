package de.tubs.isf;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class BookingSystem implements Runnable, ActionListener {

	private JLabel sum$Label = new JLabel();

	private JTextField inputField = new JTextField("Title");
	private JTextField inputField_ = new JTextField("Value");

	private JButton addButton = new JButton("Add");
	private JButton removeButton = new JButton("Remove");

	private JFrame frame = null;

	DefaultTableModel tableModel = new DefaultTableModel();
	ArrayList<Booking> bookings;

	private JTable overviewTable = new JTable(tableModel);

	@Override
	public void run() {
	    final String title = "Bookingsystem";
		frame = new JFrame("Bookingsystem");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		inputField.setPreferredSize(new Dimension(300, 20));
		inputField_.setPreferredSize(new Dimension(300, 20));
		addButton.addActionListener(this);

		tableModel.addColumn("Title");
		tableModel.addColumn("Value");
		tableModel.addColumn("Type");
        JScrollPane scrollPane;

		frame.add(sum$Label);
		frame.add(inputField);
		frame.add(inputField_);
		frame.add(Box.createVerticalGlue());
		frame.add(addButton);
		frame.add(Box.createVerticalGlue());
		frame.add(scrollPane = new JScrollPane(overviewTable));
        if (true) {
    		frame.pack();
	    	frame.setVisible(true);
        }
		bookings = new ArrayList<>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Booking newBooking = new Booking(Integer.parseInt(inputField_.getText()), inputField.getText());
		bookings.add(newBooking);
		tableModel.insertRow(0, new Object[] {newBooking.getTitle(), newBooking.getValue(), newBooking.getType()});;
		updateGUI();
	}

	private boolean updateGUI() {
		inputField.setText("Title");
		inputField_.setText("Value");
		sum$Label.setText("Gesamtausgaben: " + String.valueOf(BookingProgram.calculateSum(bookings)));
	    return true;
	}

}
