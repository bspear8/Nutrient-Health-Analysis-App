package edu.gatech.ihi.nhaa.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Goals Stuff:
 * Goal Entity - The object relational mapping to the goal repository
 * Goal Service - Provides interaction methods for the goal controller; also performs all
 * 		calculations needed to update goal statuses.
 * Goal Controller - Provides UI integration methods via HTTP get/post requests
 * 
 * 
 * @author Blaine Spear
 *
 */
@Entity(name = "Goal")
public class Goal {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "goalId", nullable = false, updatable = false)
    private long goalId;
	
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "value", nullable = false)
    private Double value;
    
    @Column(name = "date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date date;
   
    @Enumerated(EnumType.STRING)
    @Column(name = "goalType", nullable = false)
    private GoalRangeType goalType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entryType", nullable = false)
    private GoalEntryType entryType;
    
    @Column(name = "units", nullable = false)
    private String units;

	@Enumerated(EnumType.STRING)
	@Column(name = "timePeriod", nullable = false)
	private GoalTimePeriod timePeriod;

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public GoalEntryType getEntryType() {
		return entryType;
	}

	public void setEntryType(GoalEntryType entryType) {
		this.entryType = entryType;
	}

	public GoalRangeType getGoalType() {
		return goalType;
	}

	public void setGoalType(GoalRangeType goalType) {
		this.goalType = goalType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getGoalId() {
		return goalId;
	}

	public void setGoalId(long goalId) {
		this.goalId = goalId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}


	public GoalTimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(GoalTimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}
}
