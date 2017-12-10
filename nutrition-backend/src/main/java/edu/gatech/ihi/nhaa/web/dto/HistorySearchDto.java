package edu.gatech.ihi.nhaa.web.dto;

import java.util.Date;

public class HistorySearchDto {

	private Date startDate;
	private Date endDate;

	public HistorySearchDto() {
	}

	public HistorySearchDto(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
