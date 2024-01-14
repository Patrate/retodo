package fr.emmathie;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@JsonIgnoreProperties({"checkTime"})
public abstract class AbstractTask {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(columnDefinition="TEXT")
	private String title, notes;
	private List<Object> checkList;
	private Date reminder;
	private boolean validated;
	private Integer sortOrder;
	private Timestamp checkTime;

	public AbstractTask() {
		checkList = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<Object> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<Object> checkList) {
		this.checkList = checkList;
	}

	public Date getReminder() {
		return reminder;
	}

	public void setReminder(Date reminder) {
		this.reminder = reminder;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		if(!this.validated && validated) {
			checkTime = new Timestamp(System.currentTimeMillis());
		}
		this.validated = validated;
	}
	
	public void clean() {
		// TODO stop the notification scheduler
	}
	
	public void merge(AbstractTask other) throws MergeException{
		this.setTitle(other.title);
		this.setNotes(other.notes);
		this.setCheckList(other.checkList);
		this.setReminder(other.reminder);
		this.setValidated(other.validated);
		this.setSortOrder(other.sortOrder);
	}

	@Override
	public String toString() {
		return "AbstractTask [id=" + id + ", title=" + title + ", notes=" + notes + ", checkList=" + checkList
				+ ", reminder=" + reminder + ", validated=" + validated + "]";
	}

	public Integer getSortOrder() {
		if(sortOrder == null)
			return 0;
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		if(sortOrder == null)
			this.sortOrder = 0;
		this.sortOrder = sortOrder;
	}

	public Timestamp getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}
}
