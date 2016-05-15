package com.sms.sap.report.employee.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Payment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String clearingDate;
	private String vendorNumber;
	private int personalNumber;
	private String bukrs;
	private String employeeName;
	private String clearingDocument;
	private String tripDescription;
	private String tripNumber;
	private double paymentAmount;
	private String paymentDocument;
	private String paymentCurrency;
	private String orderCostCenter;
	private String recipientDate;
	private String expenseType;
	private int quantity;
	private double recipientAmount;
	private String recipientCurrency;
	private double recAmountLoc;
	private String recLC;
	private String configType;
	private String blart;
	
	
	
	
	public String getTripNumber() {
		return tripNumber;
	}

	public void setTripNumber(String tripNumber) {
		this.tripNumber = tripNumber;
	}

	public String getBlart() {
		return blart;
	}

	public void setBlart(String blart) {
		this.blart = blart;
	}

	


	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getTripDescription() {
		return tripDescription;
	}

	public void setTripDescription(String tripDescription) {
		this.tripDescription = tripDescription;
	}

	public Payment(){}
	
	public String getClearingDate() {
		//clearingDate = clearingDate.substring(0,11);
		
		return clearingDate;
	}
	public void setClearingDate(String clearingDate) {
		this.clearingDate = clearingDate;
	}
	public String getVendorNumber() {
		return vendorNumber;
	}
	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}
	public int getPersonalNumber() {
		return personalNumber;
	}
	public void setPersonalNumber(int personalNumber) {
		this.personalNumber = personalNumber;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getClearingDocument() {
		return clearingDocument;
	}
	public void setClearingDocument(String clearingDocument) {
		this.clearingDocument = clearingDocument;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public String getPaymentDocument() {
		return paymentDocument;
	}
	public void setPaymentDocument(String paymentDocument) {
		this.paymentDocument = paymentDocument;
	}
	public String getPaymentCurrency() {
		return paymentCurrency;
	}
	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}
	public String getOrderCostCenter() {
		return orderCostCenter;
	}
	public void setOrderCostCenter(String orderCostCenter) {
		this.orderCostCenter = orderCostCenter;
	}
	public String getRecipientDate() {
		
		recipientDate = recipientDate.substring(0,11);
		return recipientDate;
	}
	public void setRecipientDate(String recipientDate) {
		this.recipientDate = recipientDate;
	}
	public String getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getRecipientAmount() {
		return recipientAmount;
	}
	public void setRecipientAmount(double recipientAmount) {
		this.recipientAmount = recipientAmount;
	}
	public String getRecipientCurrency() {
		return recipientCurrency;
	}
	public void setRecipientCurrency(String recipientCurrency) {
		this.recipientCurrency = recipientCurrency;
	}
	public double getRecAmountLoc() {
		return recAmountLoc;
	}
	public void setRecAmountLoc(double recAmountLoc) {
		this.recAmountLoc = recAmountLoc;
	}
	public String getRecLC() {
		return recLC;
	}
	public void setRecLC(String recLC) {
		this.recLC = recLC;
	}
	
	
	
	
	
}
