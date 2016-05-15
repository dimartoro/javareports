package com.sms.sap.report.employee.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClearingDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5964619350480001484L;
	private String augbl;
	private String bukrs;
	private String lifnr;
	private long personalNumber;
	private String ename;
	
	public List<Trip> listTrips = new ArrayList<Trip>();
	
	
	public String getAugbl() {
		return augbl;
	}
	public void setAugbl(String augbl) {
		this.augbl = augbl;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public long getPersonalNumber() {
		return personalNumber;
	}
	public void setPersonalNumber(long personalNumber) {
		this.personalNumber = personalNumber;
	}
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}

	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Trip)) {
            return false;
        }
        
        ClearingDocument other = (ClearingDocument) obj;
        
        boolean status = false;
        if (this.augbl.equals(other.augbl)){
        	status = true;
        }
        
        //return this.customerId.equals(other.customerId);
        return status;
    }
	
}
