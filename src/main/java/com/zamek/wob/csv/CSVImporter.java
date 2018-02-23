package com.zamek.wob.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.zamek.wob.domain.order.Order;
import com.zamek.wob.domain.order.OrderBuilder;
import com.zamek.wob.domain.orderitem.OrderItem;
import com.zamek.wob.domain.orderitem.OrderItemBuilder;
import com.zamek.wob.util.HasLogger;

public class CSVImporter implements HasLogger{

	private final static boolean DEBUG = true;
	private final static String LINE_NUMBER = "LineNumber";//$NON-NLS-1$
	private final static String ORDER_ITEM_ID = "OrderItemId";//$NON-NLS-1$
	private final static String ORDER_ID = "OrderId";//$NON-NLS-1$
	private final static String BUYER_NAME = "BuyerName";//$NON-NLS-1$
	private final static String BUYER_EMAIL = "BuyerEmail";//$NON-NLS-1$
	private final static String ADDRESS =  "Address";//$NON-NLS-1$
	private final static String POST_CODE = "PostCode"; //$NON-NLS-1$ 
	private final static String SALE_PRICE = "SalePrice";//$NON-NLS-1$
	private final static String SHIPPING_PRICE = "ShippingPrice";//$NON-NLS-1$
	private final static String SKU = "SKU";//$NON-NLS-1$
	private final static String STATUS="Status";//$NON-NLS-1$
	private final static String ORDER_DATE = "OrderDate";//$NON-NLS-1$
	
	private String filename;
	private int validRows;
	private int errorRows;
	
	public CSVImporter(String filename) {
		this.filename = filename;
	}
	
	public void process() {
		try (Reader in = new FileReader(this.filename)) {
			@SuppressWarnings("resource")
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(LINE_NUMBER, ORDER_ITEM_ID, ORDER_ID, BUYER_NAME, BUYER_EMAIL, ADDRESS, 
					POST_CODE, SALE_PRICE,SHIPPING_PRICE, SKU, STATUS, ORDER_DATE).parse(in);
			
			for (CSVRecord record : records) {				
				OrderBuilder ob = new OrderBuilder()
						.orderId(record.get(ORDER_ID))
						.buyerName(record.get(BUYER_NAME))
						.buyerEmail(record.get(BUYER_EMAIL))
						.address(record.get(ADDRESS))
						.postCode(record.get(POST_CODE));
						 
				Optional<Order> ordr = ob.get();
				if (!ordr.isPresent()) {
					getLogger().error(String.format("process error at:%s", record.toString())); //$NON-NLS-1$
					++this.errorRows;
					continue;
				}
						
				OrderItemBuilder ib = new OrderItemBuilder();
				ib.order(ordr.get())
				  .itemId(record.get(ORDER_ITEM_ID))
				  .salePrice(record.get(SALE_PRICE))
				  .shippingPrice(record.get(SHIPPING_PRICE))
				  .sku(record.get(SKU))
				  .status(record.get(STATUS));
				Optional<OrderItem> it = ib.get();
				if (!it.isPresent()) {
					getLogger().error(String.format("process error at:%s", record.toString())); //$NON-NLS-1$
					++this.errorRows;
					continue;					
				}
				saveLine(ordr.get(), it.get());
			}
		}
		catch (FileNotFoundException e) {
			getLogger().error("file open error:"+e.getMessage()); //$NON-NLS-1$
		}
		catch (IOException e) {
			getLogger().error("file read error:"+e.getMessage()); //$NON-NLS-1$
		}
	}
	
	private void saveLine(Order order, OrderItem item) {
		
	}
	
}
