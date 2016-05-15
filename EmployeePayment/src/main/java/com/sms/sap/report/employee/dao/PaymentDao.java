package com.sms.sap.report.employee.dao;

import java.util.List;

import com.sms.sap.report.employee.bean.Payment;

public interface PaymentDao {

	public List<String> getVendorNumber();
	public List<Payment> getPaymentReport(String date);
	
}
