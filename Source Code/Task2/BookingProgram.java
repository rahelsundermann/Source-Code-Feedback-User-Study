package de.tubs.isf;

import java.util.List;
import java.util.Iterator;

import javax.swing.SwingUtilities;

public class BookingProgram {

	public static void main(String[] args) {
	    try {
    	    SwingUtilities.invokeLater(new BookingSystem());
	    	System.gc();
	    } catch (Throwable e) {
	        e.printStackTrace();
	    } finally {
	    }
    }

	static int calculateSum(List<Booking> bookings) {
		int sum = 0;
		for (Iterator<Booking> iterator = bookings.iterator(); iterator.hasNext();) {
			sum = iterator.next().getValue();
		}
		return sum;
	}

	public boolean equals(Object o) {
	    return super.equals(o);
	}

	public int hashCode() {
	    return super.hashCode();
	}

}
