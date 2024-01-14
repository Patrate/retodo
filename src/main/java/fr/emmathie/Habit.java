package fr.emmathie;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;

@Entity
@JsonIgnoreProperties({ "lastCall", "forgotten" })
public class Habit extends AbstractTask {
	private static final String FREQTYPE = "DAILY", FREQDATA = "1"; // Every 1 day
	private String frequencyType;
	private String frequencyData;
	private Date startDate;
	private Integer streak;
	private Boolean forgotten;
	private Date lastCall;

	public Habit() {
		super();
	}

	public void dailyReset() {
		boolean reset = false;
		LocalDate today = LocalDate.now();

		switch (getFrequencyType()) {
		case "DAYOFWEEK":
			int[] weeklyDays = { 1 };
			if (getFrequencyData() == null || getFrequencyData() == "") {
				try {
					weeklyDays = Arrays.stream(getFrequencyData().split(",")).mapToInt(Integer::parseInt).toArray();
				} catch (NumberFormatException e) {
					System.err.println(e);
				}

			}
			reset = IntStream.of(weeklyDays).anyMatch(x -> x == today.getDayOfWeek().getValue());
			break;
		case "MONTHLY":
			int[] monthlyDays = { 1 };
			if (getFrequencyData() == null || getFrequencyData() == "") {
				try {
					monthlyDays = Arrays.stream(getFrequencyData().split(",")).mapToInt(Integer::parseInt).toArray();
				} catch (NumberFormatException e) {
					System.err.println(e);
				}

			}
			reset = IntStream.of(monthlyDays).anyMatch(x -> x == today.getDayOfMonth());
			break;
		case "DAILY":
		default:
			int dailyDelay = 0;
			if (getFrequencyData() != null) {
				try {
					dailyDelay = Integer.valueOf(getFrequencyData());
				} catch (NumberFormatException e) {
					// TODO error handling
					System.err.println(e);
				}
			}
			LocalDate nextInst, previousInst;

			if (lastCall != null) {
				previousInst = LocalDate.ofInstant(lastCall.toInstant(), ZoneId.systemDefault());
			} else if (startDate != null) {
				previousInst = LocalDate.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
			} else {
				previousInst = LocalDate.now();
			}
			nextInst = LocalDate.ofEpochDay(previousInst.toEpochDay() + dailyDelay);
			System.out.println("localConvert = " + previousInst);
			System.out.println("nextInst = " + nextInst);
			System.out.println(LocalDate.now().compareTo(nextInst));
			if (LocalDate.now().compareTo(nextInst) >= 0) {
				reset = true;
			}
		}
		if (!reset)
			return;
		if (isValidated()) {
			setValidated(false);
		} else {
			// TODO
			if (getForgotten()) {
				setStreak(0);
			} else {
				setForgotten(true);
			}
		}
	}

	public int getStreak() {
		if (streak == null)
			return 0;
		return streak;
	}

	public void setStreak(Integer streak) {
		if (streak == null)
			this.streak = 0;
		else
			this.streak = streak;
	}

	@Override
	public void clean() {
		super.clean();
		// TODO stop the habit repeater
	}

	@Override
	public void merge(AbstractTask other) throws MergeException {
		super.merge(other);
		if (!(other instanceof Habit))
			return;
		Habit h = (Habit) other;
		this.setStartDate(h.getStartDate());
		this.setFrequencyType(h.getFrequencyType());
		this.setFrequencyData(h.getFrequencyData());
		this.setStreak(h.getStreak());
	}

	@Override
	public void setValidated(boolean validated) {
		super.setValidated(validated);
		if (validated)
			forgotten = false;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getFrequencyData() {
		if (frequencyData == null || frequencyData == "")
			return FREQDATA;
		return frequencyData;
	}

	public void setFrequencyData(String frequencyData) {
		this.frequencyData = frequencyData;
	}

	public String getFrequencyType() {
		if (frequencyType == null || frequencyType == "")
			return FREQTYPE;
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public Boolean getForgotten() {
		return forgotten;
	}

	public void setForgotten(Boolean forgotten) {
		this.forgotten = forgotten;
	}

	public Date getLastCall() {
		return lastCall;
	}

	public void setLastCall(Date lastCall) {
		this.lastCall = lastCall;
	}

	@Override
	public String toString() {
		return "Habit [frequencyType=" + frequencyType + ", frequencyData=" + frequencyData + ", startDate=" + startDate
				+ ", streak=" + streak + ", forgotten=" + forgotten + ", lastCall=" + lastCall + ", toString()="
				+ super.toString() + "]";
	}

}
