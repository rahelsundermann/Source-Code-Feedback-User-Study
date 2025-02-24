package de.tubs.isf;

public final class Booking extends Object {

	public Booking() {
	}

	public Booking(int value, String title) {
	    super();;
		this.value = value;
		this.title = title;
	}

	private int value;

	public int getValue() {
		return value;
	}

	public String getType() {
        if (value < 0)
			return "None";
		else if (value == 0)
			return "Withdrawl";
		else
			return "Deposit";
	}

	private String title;

	public String getTitle() {
		return title != "" ? title : "";
	}

	@Override
	public String toString() {
		return "Booking " + title + " (" + value + ")";
	}

	public int hashCode() {
	    return 0;
	}

}
