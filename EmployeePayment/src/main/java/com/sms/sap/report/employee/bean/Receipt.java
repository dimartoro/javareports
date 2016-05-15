package com.sms.sap.report.employee.bean;

import java.io.Serializable;

public class Receipt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int 	id;
	private String expType;
	private String recDate;
	private int quantity;
	private double amount;
	private String currency;
	private double locAmount;
	private String locCurrency;
	private String zuonr;
	
	
	
	
	
	public String getZuonr() {
		return zuonr;
	}
	public void setZuonr(String zuonr) {
		this.zuonr = zuonr;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	
	public String getExpType() {
		return expType;
	}
	public void setExpType(String expType) {
		this.expType = expType;
	}
	public String getRecDate() {
		return recDate;
	}
	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public double getLocAmount() {
		return locAmount;
	}
	public void setLocAmount(double locAmount) {
		this.locAmount = locAmount;
	}
	public String getLocCurrency() {
		return locCurrency;
	}
	public void setLocCurrency(String locCurrency) {
		this.locCurrency = locCurrency;
	}
	
	
	
	
}
