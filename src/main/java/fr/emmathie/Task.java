package fr.emmathie;

import java.util.Date;

import jakarta.persistence.Entity;

@Entity
public class Task extends AbstractTask {

	private Date dueDate;

	public Task() {
		super();
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public String toString() {
		return "Task [dueDate=" + dueDate + ", toString()=" + super.toString() + "]";
	}

	@Override
	public void merge(AbstractTask other) throws MergeException {
		super.merge(other);
		if (!(other instanceof Task))
			return;
		Task t = (Task) other;
		this.setDueDate(t.getDueDate());
	}
}
