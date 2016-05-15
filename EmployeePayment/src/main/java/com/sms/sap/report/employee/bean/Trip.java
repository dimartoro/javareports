package com.sms.sap.report.employee.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String zuonr;
	private double pswbt;
	private String pswsl;
	private String sgtxt;
	private String orderCostCenter;
	private String augbl;
	private String blart;
	
	
	public List<Receipt> listReceipt = new ArrayList<Receipt>();
	
	
	
	
	
	public String getBlart() {
		return blart;
	}
	public void setBlart(String blart) {
		this.blart = blart;
	}
	public String getZuonr() {
		return zuonr;
	}
	public void setZuonr(String zuonr) {
		this.zuonr = zuonr;
	}
	public String getAugbl() {
		return augbl;
	}
	public void setAugbl(String augbl) {
		this.augbl = augbl;
	}
	public List<Receipt> getListReceipt() {
		return listReceipt;
	}
	public void setListReceipt(List<Receipt> listReceipt) {
		this.listReceipt = listReceipt;
	}
	
	
	
	
	public double getPswbt() {
		return pswbt;
	}
	public void setPswbt(double pswbt) {
		this.pswbt = pswbt;
	}
	public String getPswsl() {
		return pswsl;
	}
	public void setPswsl(String pswsl) {
		this.pswsl = pswsl;
	}
	public String getSgtxt() {
		return sgtxt;
	}
	public void setSgtxt(String sgtxt) {
		this.sgtxt = sgtxt;
	}
	public String getOrderCostCenter() {
		return orderCostCenter;
	}
	public void setOrderCostCenter(String orderCostCenter) {
		this.orderCostCenter = orderCostCenter;
	}
	
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Trip)) {
            return false;
        }
        
        Trip other = (Trip) obj;
        
        boolean status = false;
        if (this.zuonr.equals(other.zuonr) && this.augbl.equals(other.augbl)){
        	status = true;
        }
        
        //return this.customerId.equals(other.customerId);
        return status;
    }
	
	
}
