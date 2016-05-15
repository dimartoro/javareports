package com.sms.sap.report.employee.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.sms.sap.report.employee.bean.Payment;
import com.sms.sap.report.employee.dao.PaymentDao;
import com.sms.sap.report.employee.dao.config.DAOException;
import com.sms.sap.report.employee.dao.config.DAOFactory;
import com.sms.sap.report.employee.dao.config.DAOUtil;

public class PaymentDaoImpl implements PaymentDao{
	
	private DAOFactory daoFactory;

	public PaymentDaoImpl(DAOFactory daoFactory){
		this.daoFactory = daoFactory;
	}
 
	
	public List<String> getVendorNumber() {
		
		List<String> vendorList = new ArrayList<String>();

		
		
		
		/*
		
        try{ 
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_VENDOR_NUMBER);
            ResultSet resultSet = statement.executeQuery();
       
            while (resultSet.next()) {
                vendorList.add(resultSet.getString("LIFNR_H"));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        
        */
        
        return vendorList;
	}

	public List<Payment> getPaymentReport(String date) {
		List<Payment> payments = new ArrayList<>();
		
		/*
		Connection connection = null;
	     PreparedStatement preparedStatement = null;
	     ResultSet resultSet = null;
	        
	     
	     
	        try {
	           
	            connection = daoFactory.getConnection();
				CallableStatement cb = connection.prepareCall("exec clsp_accounts_payable_expenses_v5 ?");
				cb.setString(1, date);
				
				cb.execute();
	            resultSet=cb.getResultSet();
	            
	            System.out.println("resulset " + resultSet);
	            
	            while (resultSet.next()) {
	            	payments.add(map(resultSet));
	            }
	            
	            
	        } catch (SQLException e) {
	            throw new DAOException(e);
	        } finally {
	        	DAOUtil.close(connection, preparedStatement, resultSet);
	        }
		*/
		
		String SPsql = "EXEC clsp_accounts_payable_expenses_v5 ?";   // for stored proc taking 2 parameters
		Connection con;
		try {
			con = daoFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SPsql);
			ps.setEscapeProcessing(true);
			ps.setQueryTimeout(30000);
			ps.setString(1,date);
			
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
            	payments.add(map(resultSet));
            }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   // java.sql.Connection
		
		
		
		
		
		
        return payments;
	}
	
	
	private static Payment map(ResultSet resultSet) throws SQLException {
        Payment payment = new Payment();
        
        payment.setBukrs(resultSet.getString("BUKRS"));
        payment.setClearingDate(resultSet.getString("CLEARING_DATE"));
        payment.setVendorNumber(resultSet.getString("VENDOR_NUMBER"));
        payment.setPersonalNumber(resultSet.getInt("PERSONNEL_NUMBER"));
        payment.setEmployeeName(resultSet.getString("EMPLOYEE_NAME"));
        payment.setClearingDocument(resultSet.getString("CLEARING_DOCUMENT"));
        payment.setTripNumber(resultSet.getString("TRIP_NUMBER_"));
        payment.setPaymentAmount(resultSet.getDouble("PAYMENT_AMOUNT"));
        payment.setPaymentCurrency(resultSet.getString("PAYMENT_CURRENCY"));
        payment.setTripDescription(resultSet.getString("TRIP_DESCRIPTION"));
        payment.setOrderCostCenter(resultSet.getString("ORDER_CENTER"));
        payment.setRecipientDate(resultSet.getString("RECEIPT_DATE"));
        payment.setExpenseType(resultSet.getString("EXPENSE_TYPE"));
        payment.setQuantity(resultSet.getInt("QUANTITY"));
        payment.setRecipientAmount(resultSet.getDouble("RECEIPT_AMOUNT"));
        payment.setRecipientCurrency(resultSet.getString("RECEIPT_CURRENCY"));
        payment.setRecAmountLoc(resultSet.getDouble("REC_AMNT_LC"));
        payment.setRecLC(resultSet.getString("RECEIPT_LC"));
        payment.setConfigType(resultSet.getString("TYPE_COLUMN_HD"));
        payment.setBlart(resultSet.getString("BLART"));
        
        
        return payment;
    }

}
