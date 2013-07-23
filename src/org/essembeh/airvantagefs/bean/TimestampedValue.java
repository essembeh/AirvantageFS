package org.essembeh.airvantagefs.bean;

public class TimestampedValue {

	private Long timestamp;
	private Object value;

	public TimestampedValue() {
		this(0, null);
	}

	public TimestampedValue(long timestamp, Object value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
