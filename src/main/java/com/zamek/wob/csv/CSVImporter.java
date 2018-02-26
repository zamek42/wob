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

/**
 * CSV importer class. 
 * 
 * <p>Try to process the given csv file as input csv.</p>
 * <p>The result database has two tables Order_ and OrderItem</p> 
 * 
 * <p>Until the conversion it creates a response file which contains all linenumber with result. Format of response:</p>
 * <ul>
 * <li>LineNumber: line index from the input csv file.</li>
 * <li>Status : Result status of conversion: OK or ERROR</li>
 * <li>Message: reason of error or empty if the status is OK</li>
 * </ul>
 * 
 *  
 * <p>Fileds in csv:</p>
 * <ul>
	<li>LineNumber : line index, it must be exists in csv</li>
	<li>OrderItemId : primary key for OrderItem, it must be exists in csv  and it cannot be duplicated in the target database</li>  
	<li>OrderId : primary key for Order, it must be exists in csv  and it cannot be duplicated in the target database</li>
	<li>BuyerName : name of the buyer, it must be exists in csv</li>
	<li>BuyerEmail : email of buyer, it must be exists in csv and it must be valid email</li> 
	<li>Address : Address of buyer, it must be exists in csv</li>
	<li>PostCode : Postcode of buyer, it must be exists in csv</li>
	<li>SalePrice : SalePrice, it must be exists in csv and minimum value is 1.0</li>
	<li>ShippingPrice : Shipping price, it must be exists in csv and minimum value is 0.0</li>
	<li>SKU : I don't know what is it</li>
	<li>Status : Status of ordering, it must be exists in csv values IN_STOCK or OUT_OF_STOCK</li>
	<li>OrderDate : Date of ordering, it can be empty. It needs to set today date if it is empty. </li>
	</ul>
 *
 * 
 * @author zamek
 *
 */
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
	
	public static final CSVFormat CSV_FILE_FORMAT = CSVFormat.DEFAULT.withDelimiter(Consts.FIELD_DELIMITER).withFirstRecordAsHeader()
														.withIgnoreEmptyLines().withRecordSeparator(Consts.RECORD_SEPARATOR);
	

	private String inputFileName;
	private String responseFileName;
	private int validRows;
	private int errorRows;
	private int lines;
	private ResponseFile response;
	private Map<Order, List<OrderItem>> orders;
	private EntityManager entityManager;
	
	/**
	 * Constructor of CSVImporter
	 * 
	 * Simple stores the arguments, but not starts the conversion. 
	 * 
	 * @param inputFileName name of the input csv file (Coming from config)
	 * @param responseFileName name of the response csv file (Coming from config)
	 * @param em Entity Manager for the database
	 */
	public CSVImporter(String inputFileName, String responseFileName, EntityManager em) {
		this.inputFileName = inputFileName;
		this.responseFileName = responseFileName;
		this.orders = new HashMap<>();
		this.entityManager = em;
	}
		
	/**
	 * After a conversion it contains the valid (successfully converted) number of rows
	 * 
	 * @return the validRows
	 */
	public int getValidRows() {
		return this.validRows;
	}

	/**
	 * After a conversion it contains the number of error rows
	 *  
	 * @return the errorRows
	 */
	public int getErrorRows() {
		return this.errorRows;
	}

	/**
	 * After a conversion it contains the all number of processed rows  
	 * 
	 * @return the processedRows
	 */
	public int getProcessedRows() {
		return this.lines;
	}

	/**
	 * Getting orders map for tests
	 * 
	 * @return Map of orders
	 */
	public Map<Order, List<OrderItem>> getOrders() {
		return this.orders;
	}
	
	/**
	 * Starting process
	 * 
	 * @return true if the conversion finished successfully or flase if something went wrong
	 */
	public boolean process() {
		try (Reader in = new FileReader(this.inputFileName)) {
			try (ResponseFile r = new ResponseFile(this.responseFileName)) {
				r.open();
				
				this.response = r;
				
				try (CSVParser csvFileParser = new CSVParser(in, CSV_FILE_FORMAT) ) {						
					for (CSVRecord record : csvFileParser.getRecords()) {							
						++this.lines;
						
						int lineNumber=this.lines;
						try {
							lineNumber = Integer.parseInt(record.get(LINE_NUMBER));
						}
						catch (NumberFormatException e) {
							this.response.message(this.lines, ResponseFile.Status.ERROR, "Linenumber format error"+e.getMessage());  //$NON-NLS-1$
						}
						processLine(record, lineNumber);
						System.out.print(String.format("\r%10d.", Integer.valueOf(lineNumber))); //$NON-NLS-1$
					}
				}
				System.out.println();
				saveLines();
				return true;
			}
			catch (Exception e) { // response
				getLogger().error(String.format("Cannot open response file %s : %s ",this.responseFileName, e.getMessage())); //$NON-NLS-1$
				System.out.print("\rSomething went wrong, details in log\n"); //$NON-NLS-1$
				return false;
			}
		}
		catch (IOException e) { //Reader
			getLogger().error(String.format("Cannot open input file %s: %s", this.inputFileName,e.getMessage())); //$NON-NLS-1$
			System.out.print("\rSomething went wrong, details in log\n"); //$NON-NLS-1$
			return false;
		}			
	}

	/**
	 * Checking an Order and OrderItem in the Database.
	 * 
	 * @param order Order object
	 * @param item OrderItem object
	 * @param lineNumber LineNuumber for error messages
	 * @return true if the given order and orderItem is not exist in the database
	 * @throws IOException throws an IOEXception if an error message makes a mistake
	 */
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

	/**
	 * Add an Order/OrderItem pair to the memory map.  
	 * 
	 * if an Order isn't exists in the keyset of the map, it creates a new key with an OrderItem list
	 * If an orderItem exists in the list of given Order, it doesn't append it and creates an error message
	 * 
	 * @param order Order object
	 * @param orderItem OrderItem object
	 * @param lineNumber lineNumber for the error message
	 * @return true if successfully added or false if something went wrong
	 * @throws IOException if the error message makes a mistake
	 */
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
	
	/**
	 * Try to create an Order/OrderItem object from the CSV. 
	 * 
	 * @param record CSV line as a Record
	 * @param lineNumber LineNumber 
	 */
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
	
	/**
	 * Save all processed Order/OrderItem objects to the database
	 */
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
	
	/**
	 * Save an Order/OrderItem+ objects to the database
	 *  
	 * @param entry Map entry to save (Order/OrderItems+)
	 */
	private void processOrder(Map.Entry<Order, List<OrderItem>> entry) {
		float total = 0;
		Order order = entry.getKey();
		
		for(OrderItem i:entry.getValue())
			total += i.getTotalItemPrice();
		
		order.setOrderTotalValue(total);
	
		this.entityManager.persist(order);
		
		for(OrderItem i:entry.getValue()) {
			i.setOrder(order);
			this.entityManager.persist(i);
		}
			
		this.entityManager.flush();		
	}
	
}
