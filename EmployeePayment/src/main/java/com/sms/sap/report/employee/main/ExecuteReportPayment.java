package com.sms.sap.report.employee.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.Line;
import com.sms.sap.report.employee.bean.ClearingDocument;
import com.sms.sap.report.employee.bean.Payment;
import com.sms.sap.report.employee.bean.Receipt;
import com.sms.sap.report.employee.bean.Trip;
import com.sms.sap.report.employee.dao.PaymentDao;
import com.sms.sap.report.employee.dao.config.DAOFactory;
import com.itextpdf.text.pdf.PdfPageEventHelper;






public class ExecuteReportPayment {
	public static int pageNumber =1;
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	  private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 15,Font.BOLD);
	  private static Font f = new Font(FontFamily.HELVETICA, 6, Font.NORMAL, GrayColor.GRAYBLACK);
	  private static Font fTitle = new Font(FontFamily.HELVETICA, 6, Font.BOLD, GrayColor.BLUE);
	  
	  
	  
	public static void main(String[] args) {
		
		final String dateClearingDateParameter = "2016-11-18";
		
		
		 DAOFactory javabase = DAOFactory.getInstance("accounting.jdbc");
	     PaymentDao paymentDao = javabase.getPaymentDAO();
	     String currentDate =formatDateTime(new Date());
	     //List<String> vendorNumber = paymentDao.getVendorNumber();
	     
	     List<Payment> paymentList = paymentDao.getPaymentReport(dateClearingDateParameter);
	     List<String> vendorNumber = getVendorNumberFromResult(paymentList);
	     
	     System.out.println("vebdir bynver " + vendorNumber.size());
	     
	     List<ClearingDocument>  listClearingDocument = getClearingDocumentFromReport(paymentList);
	     
	     
	     
	     
	     String filePDF = null;
	     Document document = null;
	     PdfPTable table = null;
	     Payment paymentH = null;
	     
	     float[] columnWidths = {2, 2, 2, 3, 2, 2, 2,2,3,2,2,2,2,2,2,2};
	     
	     try {
			filePDF = "C:/Reports/Expenses_Report_"+currentDate+".pdf";
			//document = new Document();
			document = new Document(PageSize.A4.rotate());
			//PdfWriter.getInstance(document, new FileOutputStream(filePDF));
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePDF));
			
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));
			 
            HeaderFooter event = new HeaderFooter();
            writer.setPageEvent(event);
		        
			
			document.open();
			
			
			String clearingDate = null;
			
			if (!paymentList.isEmpty()){
			 clearingDate=	 paymentList.get(0).getClearingDate();	
			}
			
			
			double totalPaymentAmount = 0;
			for (String vendor : vendorNumber) {
				
				Paragraph preface = new Paragraph();
				preface.add(new Paragraph("Expense Report Clearing Date : " +clearingDate   , catFont));
				
				document.add(preface);
				
				Paragraph prefaceEmpty = new Paragraph();
				prefaceEmpty.add(new Paragraph("     "  , catFont));
				document.add(prefaceEmpty);
				
				
				table = new PdfPTable(columnWidths);
				table.setWidthPercentage(100);
				
				createTitle(table);
				
				for (ClearingDocument cld : listClearingDocument) {
					
					
				    
					
					totalPaymentAmount = 0;
					
					if (vendor.equals(cld.getLifnr())){
						
						createClearingDocumentRow(cld, table);
						
						
						createTripZPRow(cld,table);
						
						List<Trip> lstTrip = cld.listTrips;
						for (Trip trip : lstTrip) {
							
						
							
							
							
							if (trip.getBlart()==null){
								createTripRow(trip, table);
								totalPaymentAmount =  totalPaymentAmount + trip.getPswbt();
								
							}else{
							
								
								
								if (trip.getBlart().equals("KG")){
									totalPaymentAmount =  totalPaymentAmount - trip.getPswbt();
									createTripRowNegative(trip, table);
								}else{
									//createTripRow(trip, table);
									//totalPaymentAmount =  totalPaymentAmount + trip.getPswbt();
									
									if (!trip.getBlart().equals("ZP")){
										totalPaymentAmount =  totalPaymentAmount + trip.getPswbt();
										createTripRow(trip, table);
									}
									
									
								}
						
								
								
								
							}
							
							
							
							
							List<Receipt> lstRCP = trip.listReceipt;
							for (Receipt receipt : lstRCP) {
								createReceiptRow(receipt,table);
							}
							
						}
						
						createClearingDocTotal(totalPaymentAmount, table);
						
						
					}
					
					
				
					
					
				}
			
				document.add(table);
			      
			      
			      
			    
			      document.newPage();  
			}
			
			
			
		    
			document.close();	
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	//createTripRow
	 public static void createTripZPRow(ClearingDocument cld, PdfPTable table){
		 
		 for (Trip trip : cld.listTrips) {
			if ( trip.getBlart()!= null ){
			 if (trip.getBlart().equals("ZP")){
				 
				 PdfPCell c1 = null;
					
					c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.NO_BORDER);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.NO_BORDER);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.NO_BORDER);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.NO_BORDER);
				    table.addCell(c1);
				        
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.NO_BORDER);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("Payment Posting",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase(getFormatMoney(trip.getPswbt())+"",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase(trip.getPswsl()+"",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase(trip.getOrderCostCenter(),f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				    
				    c1 = new PdfPCell(new Phrase("",f));
				    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				    c1.setBorder(Rectangle.BOTTOM);
				    table.addCell(c1);
				 
			 }
			}
		}
		 
	 }
	
	
	public static List<Receipt> getRCPByTrip(String tripNumber,String cld, List<Payment> lstPayment){
		List<Receipt> lstRcp = new ArrayList<Receipt>();
		
		Receipt rcp = null;
		for (Payment p : lstPayment) {
			
			
			if (p.getTripNumber().equals(tripNumber) && p.getClearingDocument().equals(cld) && p.getExpenseType()!=null){
				
				if (p.getExpenseType().trim().length()!=0){
				rcp = new Receipt();
				rcp.setAmount(p.getRecipientAmount());
				rcp.setCurrency(p.getRecipientCurrency());
				rcp.setExpType(p.getExpenseType());
				rcp.setLocAmount(p.getRecAmountLoc());
				rcp.setQuantity(p.getQuantity());
				rcp.setRecDate(p.getRecipientDate());
				rcp.setZuonr(p.getTripNumber());
				rcp.setLocCurrency(p.getRecLC());
				lstRcp.add(rcp);
				}
			}
		}
		
		
		return lstRcp;
		
	}
	
	
	public static List<String> getVendorNumberFromResult(List<Payment> result){
		List<String> lstVendors = new ArrayList<String>();
		
		for (Payment pmt : result) {
		lstVendors.add(pmt.getVendorNumber());
		}
		
		
		
		Set<String> hs = new HashSet<>();
		hs.addAll(lstVendors);
		
		lstVendors.clear();
		lstVendors.addAll(hs);
		
		return lstVendors;
		
	}
	
	
	public static  List<ClearingDocument> getClearingDocumentFromReport(List<Payment> lstPayments){
		
		List<ClearingDocument> cldList = new ArrayList<ClearingDocument>();
		List<Trip> tripList = new ArrayList<Trip>();
		List<String> tmpCleanCLD = new ArrayList<String>();
		List<Trip> tmpCleanTrip = new ArrayList<Trip>();
		
		
		
		for (Payment p : lstPayments) {
			tmpCleanCLD.add(p.getClearingDocument());
		}
		
		
		
		Set<String> hs = new HashSet<>();
		hs.addAll(tmpCleanCLD);
		
		tmpCleanCLD.clear();
		tmpCleanCLD.addAll(hs);
		
		
		
		Trip tripCompare = null;
		for (Payment p : lstPayments) {
			tripCompare = new Trip();
			tripCompare.setZuonr(p.getTripNumber());
			tripCompare.setAugbl(p.getClearingDocument());
			
			if (!tmpCleanTrip.contains(tripCompare)){
				tmpCleanTrip.add(tripCompare);
			}
			
		}
		
		/*
		Set<String> hsTrip = new HashSet<>();
		hsTrip.addAll(tmpCleanTrip);
		
		tmpCleanTrip.clear();
		tmpCleanTrip.addAll(hsTrip);
		*/
		
		
		Trip trip = null;
		boolean tripExist = false;
		
		for (Trip tr : tmpCleanTrip) {
			tripExist = false;
			
			for (Payment pay : lstPayments) {
				if(pay.getTripNumber().equals(tr.getZuonr()) && pay.getClearingDocument().equals(tr.getAugbl())){
					
					if (!tripExist){
						
						trip = new Trip();
						trip.setZuonr(pay.getTripNumber());
						trip.setOrderCostCenter(pay.getOrderCostCenter());
						trip.setSgtxt(pay.getTripDescription());
						trip.setAugbl(pay.getClearingDocument());
						trip.setPswbt(pay.getPaymentAmount());
						trip.setPswsl(pay.getPaymentCurrency());
						trip.setBlart(pay.getBlart());
						tripList.add(trip);
						tripExist = true;
					}
				}
			}
			
		}
	
		/*
		for (Trip tppp : tripList) {
			System.out.println("execute trip  " + tppp.getAugbl() + "===" + tppp.getZuonr());
		}
		*/
		
		
		List<Trip> tripByCLD = null;
		ClearingDocument clearingDocument = null;
		boolean existCLD = false;
		for (String  document : tmpCleanCLD) {
			
			existCLD = false;
			for (Payment pay : lstPayments) {
				
				if (document.equals(pay.getClearingDocument())){
					clearingDocument = new ClearingDocument();
					clearingDocument.setBukrs(pay.getBukrs());
					clearingDocument.setLifnr(pay.getVendorNumber());
					clearingDocument.setAugbl(pay.getClearingDocument());
					clearingDocument.setPersonalNumber(pay.getPersonalNumber());
					clearingDocument.setEname(pay.getEmployeeName());
			
					if (!existCLD){
							
							
							tripByCLD = new ArrayList<Trip>();
							
							for (Trip trp : tripList) {
								if (trp.getAugbl().equals(clearingDocument.getAugbl())){
									trp.listReceipt = getRCPByTrip(trp.getZuonr(), trp.getAugbl(), lstPayments);
									tripByCLD.add(trp);
								}
							}
							
							
							if (StringUtils.isNumeric(pay.getTripNumber()) && StringUtils.isNotBlank(pay.getEmployeeName()) ){
								clearingDocument.listTrips = tripByCLD;
								cldList.add(clearingDocument);
								existCLD = true;
							}
							
							if (  pay.getTripNumber()!=null) {
								if ( !pay.getTripNumber().equals("")) {
									if(!StringUtils.isNumeric(pay.getTripNumber())) {
										//if (StringUtils.isEmpty(pay.getEmployeeName())){
										   if( !isUUID(pay.getTripNumber())){
											System.out.println("enter===== " + pay.getTripNumber() );
											clearingDocument.listTrips = tripByCLD;
											cldList.add(clearingDocument);
											existCLD = true;
										   }
										//}
									}
								}
														 
							}
							
							
							
							
							
						}	
						
					
					}
			}
			
		}
		
		
		
		
		return cldList;
	}
	

	public static boolean isUUID(String string) {
	    try {
	        UUID.fromString(string);
	        return true;
	    } catch (Exception ex) {
	        return false;
	    }
	}
	
	
	public static void createTitle(PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("Company",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("Vendor Number",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Personnel Number",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Employee Name",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Clearing Doc.",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Trip Number",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Payment Amount",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Payment Currency",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Order",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Receipt Date",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("Expense Type",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Quantity",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("Receipt Amount",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    c1 = new PdfPCell(new Phrase("Receipt Currency",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("Rec Amnt LC",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("Receipt LC",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	        
	    }
	
	
	public static void createClearingDocTotal(double total, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	        
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(getFormatMoney(total) + "",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	
	
	
	
	
	
	public static void createReceiptRow(Receipt rcp, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	        
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(rcp.getRecDate().trim(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(rcp.getExpType(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(rcp.getQuantity()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    
	    String mount = "0.0";
	    if (rcp.getAmount()>0)
	    	mount = getFormatMoney(rcp.getAmount());
	    
	    c1 = new PdfPCell(new Phrase(mount,f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(rcp.getCurrency(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    mount = "0.0";
	    if (rcp.getLocAmount()>0)
	    	mount = getFormatMoney(rcp.getLocAmount());
	    
	    c1 = new PdfPCell(new Phrase(mount,f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(rcp.getLocCurrency(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	
	
	public static void createTripRowNegative(Trip trip, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	        
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(trip.getZuonr()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase( "(" + getFormatMoney(trip.getPswbt())+")",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(trip.getPswsl()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(trip.getOrderCostCenter(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	

	public static void createTripRow(Trip trip, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	        
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(trip.getZuonr()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(getFormatMoney(trip.getPswbt())+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(trip.getPswsl()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(trip.getOrderCostCenter(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	
	
	public static void createClearingDocumentRow(ClearingDocument cld, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase(cld.getBukrs(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(cld.getLifnr(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(cld.getPersonalNumber()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(cld.getEname(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	        
	    c1 = new PdfPCell(new Phrase(cld.getAugbl(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	
	public static void createDetail(Payment payment, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	        
	    c1 = new PdfPCell(new Phrase(""));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.NO_BORDER);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getTripNumber()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getPaymentAmount()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getPaymentCurrency(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getOrderCostCenter(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecipientDate(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getExpenseType(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getQuantity()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecipientAmount()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecipientCurrency(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecAmountLoc()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecLC(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	
	public static void createHeader(Payment payment, PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase(payment.getBukrs(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getVendorNumber(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getPersonalNumber()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getEmployeeName(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	
	    
	    c1 = new PdfPCell(new Phrase(payment.getClearingDocument(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getTripNumber()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase( getFormatMoney(  payment.getPaymentAmount()),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getPaymentCurrency(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getOrderCostCenter(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecipientDate(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getExpenseType(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getQuantity()+"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase( getFormatMoney( payment.getRecipientAmount()),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecipientCurrency(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(getFormatMoney(payment.getRecAmountLoc()),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(payment.getRecLC(),f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	}
	
	public static void createTitleSumary(PdfPTable table){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("Clearing Document",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("Total",fTitle));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	         
	    }
	
	public static void createSummaryTotal(PdfPTable table,double paymentTotal){
		PdfPCell c1 = null;
		
		c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase(getFormatMoney(paymentTotal) +"",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	    
	    c1 = new PdfPCell(new Phrase("",f));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    c1.setBorder(Rectangle.BOTTOM);
	    c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    table.addCell(c1);
	} 
	
	
	
	public static Payment getHeader(String vendorNumber, List<Payment> list){
			for (Payment payment : list) {
				if(payment.getVendorNumber().equals(vendorNumber)){
					if (payment.getConfigType().equals("1")){
						return payment;
					}
				}
			}
		return null;
	}
	
	
	 public static String formatDateTime(Date date) {
	        if (date == null) {
	            return "";
	        } else {
	            return DATE_TIME_FORMAT.format(date);
	        }
	 }
	 
	 public static String getDateFormat(String dateInString){
		 
		 SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				try {

				Date date = formatter.parse(dateInString);
				//System.out.println(date);
				//System.out.println(formatter.format(date));
				return formatter.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		 return null;
	 }
	 
	 public static String getFormatMoney(double money){
		 
		 //String number = String.valueOf(money);
		 //double amount = Double.parseDouble(number);
		 DecimalFormat formatter = new DecimalFormat("#,###.00");

		
		 return formatter.format(money);
		 
	 }
	  static class HeaderFooter extends PdfPageEventHelper {
		  
	        public void onEndPage (PdfWriter writer, Document document) {
	            Rectangle rect = writer.getBoxSize("art");
	            switch(writer.getPageNumber() % 2) {
	                case 0:
	                    ColumnText.showTextAligned(writer.getDirectContent(),
	                        Element.ALIGN_CENTER, new Phrase("even header"),
	                        rect.getRight(), rect.getTop(), 0);
	                    break;
	                case 1:
	                    ColumnText.showTextAligned(writer.getDirectContent(),
	                        Element.ALIGN_CENTER, new Phrase("odd header"),
	                        rect.getLeft(), rect.getTop(), 0);
	                    break;
	            }
	            ColumnText.showTextAligned(writer.getDirectContent(),
	                    Element.ALIGN_CENTER, new Phrase(String.format("Page %d", writer.getPageNumber())), 40,
	                     rect.getBottom() - 18, 0);
	        }
	    }
}
