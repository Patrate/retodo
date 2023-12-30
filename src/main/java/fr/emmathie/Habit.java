package fr.emmathie;

import java.util.Date;

import jakarta.persistence.Entity;

@Entity
public class Habit extends AbstractTask {
	private static final String FREQTYPE = "DAILY", FREQDATA = "1,3,0"; //Every 1 day, at 03:00
	private String frequencyType;
	private String frequencyData;
	private Date startDate;
	private Integer streak;

	public Habit() {
		super();
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
		this.setFrequencyType(h.getFrequencyType());
		this.setFrequencyData(h.getFrequencyData());
		this.setStreak(h.getStreak());
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getFrequencyData() {
		if(frequencyData == null || frequencyData == "")
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
}
