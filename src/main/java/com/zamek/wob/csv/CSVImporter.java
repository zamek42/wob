package com.zamek.wob.csv;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.zamek.wob.Consts;
import com.zamek.wob.domain.ConvertException;
import com.zamek.wob.domain.order.Order;
import com.zamek.wob.domain.order.OrderBuilder;
import com.zamek.wob.domain.orderitem.OrderItem;
import com.zamek.wob.domain.orderitem.OrderItemBuilder;
import com.zamek.wob.util.HasLogger;

public class CSVImporter implements HasLogger{

	@SuppressWarnings("unused")
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
	
	public static final String HEADER[] = new String[] {LINE_NUMBER, ORDER_ITEM_ID, ORDER_ID, BUYER_NAME, 
			 											BUYER_EMAIL, ADDRESS, POST_CODE, SALE_PRICE, SHIPPING_PRICE, 
			 											SKU, STATUS, ORDER_DATE};

	private String inputFileName;
	private String responseFileName;
	private int validRows;
	private int errorRows;
	private int lines;
	private ResponseFile response;
	private Map<Order, List<OrderItem>> orders;
	private EntityManager entityManager;
	
	public CSVImporter(String inputFileName, String responseFileName, EntityManager em) {
		this.inputFileName = inputFileName;
		this.responseFileName = responseFileName;
		this.orders = new HashMap<>();
		this.entityManager = em;
	}
		
	/**
	 * @return the validRows
	 */
	public int getValidRows() {
		return this.validRows;
	}

	/**
	 * @return the errorRows
	 */
	public int getErrorRows() {
		return this.errorRows;
	}

	public Map<Order, List<OrderItem>> getOrders() {
		return this.orders;
	}
	
	public void process() {
		try (Reader in = new FileReader(this.inputFileName)) {
			try (ResponseFile r = new ResponseFile(this.responseFileName)) {
				r.open();
				
				this.response = r;
				
				CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(Consts.FIELD_DELIMITER).withFirstRecordAsHeader()
						.withIgnoreEmptyLines().withRecordSeparator(Consts.RECORD_SEPARATOR);

				
				try (CSVParser csvFileParser = new CSVParser(in, csvFileFormat) ) {						
					for (CSVRecord record : csvFileParser.getRecords()) {							
						++this.lines;
						if (this.lines==1) //Skip header
							continue;
						
						int lineNumber=this.lines;
						try {
							lineNumber = Integer.parseInt(record.get(LINE_NUMBER));
						}
						catch (NumberFormatException e) {
							this.response.message(this.lines, ResponseFile.Status.ERROR, "Linenumber format error"+e.getMessage());  //$NON-NLS-1$
						}
						processLine(record, lineNumber);
					}
				}
				
				saveLines();
			}
			catch (Exception e) { // response
				getLogger().error(String.format("Cannot open response file &s : %s ",this.responseFileName, e.getMessage())); //$NON-NLS-1$
			}
		}
		catch (IOException e) { //Reader
			getLogger().error(String.format("Cannot open input file %s: %s", this.inputFileName,e.getMessage())); //$NON-NLS-1$
		}			
	}

	private boolean checkDb(Order order, OrderItem item, int lineNumber) throws IOException {
		Long fid = order.getId();
		
		if (this.entityManager.find(Order.class, fid)!= null) {
			this.response.message(lineNumber, ResponseFile.Status.ERROR, "OrderId already exists in database: "+fid); //$NON-NLS-1$
			return false;
		}
		fid = item.getId();
		if (this.entityManager.find(OrderItem.class, fid)!=null) {
			this.response.message(lineNumber, ResponseFile.Status.ERROR, "OrderItemId already exists in database: "+fid); //$NON-NLS-1$
			return false;			
		}
		return true;
	}

	private boolean addOrderItemPairs(Order order, OrderItem orderItem, int lineNumber) throws IOException {
		if (this.orders.containsKey(order)) {
			List<OrderItem> items = this.orders.get(order);
			if (items.contains(orderItem)) {
				this.response.message(lineNumber, ResponseFile.Status.ERROR, "Duplicate orderitem:"+orderItem); //$NON-NLS-1$
				return false;
			}
			items.add(orderItem);
			return true;
		}
		
		List<OrderItem> items = new ArrayList<>();
		items.add(orderItem);
		this.orders.put(order, items);
		return true;
	}
	
	private void processLine(CSVRecord record, int lineNumber) {
		try {
			try {
				OrderBuilder ob = new OrderBuilder()
						.orderId(record.get(ORDER_ID))
						.buyerName(record.get(BUYER_NAME))
						.buyerEmail(record.get(BUYER_EMAIL))
						.address(record.get(ADDRESS))
						.postCode(record.get(POST_CODE))
						.orderDate(record.get(ORDER_DATE));
				
				Optional<Order> ordr = ob.get();
				if (!ordr.isPresent()) {
					++this.errorRows;
					this.response.message(lineNumber, ResponseFile.Status.ERROR, "Order final test fail"); //$NON-NLS-1$
					return;
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
					++this.errorRows;
					this.response.message(lineNumber, ResponseFile.Status.ERROR, "item final test fail"); //$NON-NLS-1$
					return;
				}
				
				Order order = ordr.get();
				OrderItem orderItem = it.get();
				if (checkDb(order, orderItem, lineNumber) &&  
					addOrderItemPairs(order, orderItem, lineNumber)) {
					this.response.message(lineNumber, ResponseFile.Status.OK, ""); //$NON-NLS-1$
					++this.validRows;
				}
			}
			catch (ConvertException e) {
				this.response.message(lineNumber, ResponseFile.Status.ERROR, e.getMessage());
			}
		}
		catch (IOException e) {
			getLogger().error("processLine error:"+e.getMessage()); //$NON-NLS-1$
		}
	}
	
	private void saveLines() {
		if (this.orders.isEmpty()) {
			getLogger().warn("Orders list is empty!"); //$NON-NLS-1$
			return;
		}
		
		this.entityManager.setFlushMode(FlushModeType.COMMIT);
		EntityTransaction tr = this.entityManager.getTransaction();
		
		tr.begin();
		try {
			for (Map.Entry<Order, List<OrderItem>> entry : this.orders.entrySet() ) 
				processOrder(entry);
			
			tr.commit();			
		}
		catch (Exception e) {
			tr.rollback();
			getLogger().error("Database error:"+e.getMessage()); //$NON-NLS-1$
		}
	}
	
	private void processOrder(Map.Entry<Order, List<OrderItem>> entry) {
		float total = 0;
		Order order = entry.getKey();
		
		for(OrderItem i:entry.getValue())
			total += i.getTotalItemPrice();
		
		order.setOrderTotalValue(total);
	
		this.entityManager.persist(order);
		this.entityManager.flush();
		
		for(OrderItem i:entry.getValue()) {
			i.setOrder(order);
			this.entityManager.persist(i);
		}
			
		this.entityManager.flush();		
	}
	
}
