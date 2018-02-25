package com.zamek.wob.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zamek.wob.Consts;
import com.zamek.wob.TestConsts;
import com.zamek.wob.csv.CSVImporter;
import com.zamek.wob.domain.order.Order;
import com.zamek.wob.domain.order.OrderBuilder;
import com.zamek.wob.domain.orderitem.OrderItem;
import com.zamek.wob.domain.orderitem.OrderItemStatus;

public class CSVTest {

	private final static String TEST_PATH = "/tmp/";//$NON-NLS-1$
	private final static String TEST_INPUT = TEST_PATH + "test.csv"; //$NON-NLS-1$
	private final static String TEST_RESPONSE = TEST_PATH + "test.response"; //$NON-NLS-1$
	
	
	private final static String BUYERS_NAME = "Zaphod Beeblebrox";  //$NON-NLS-1$
	private final static String BUYERS_EMAIL ="zaphod.beeblebrox@magrathea.ma";  //$NON-NLS-1$
	private final static String ADDRESS = "Planet Magrathea";  //$NON-NLS-1$
	private final static String ZIP = "4242";  //$NON-NLS-1$
	private final static String SKU = "SKU";  //$NON-NLS-1$
	private final static LocalDate DATE = LocalDate.now().minus(Period.of(5, 1, 20));
	private final static int NUMBER_OF_ORDERS = 10;
	private final static int NUMBER_OF_ITEMS = 10;
	private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(OrderBuilder.DATE_FORMAT);
	private static int lineNumber = 0;
	
	private static EntityManagerFactory emFactoryObj=Persistence.createEntityManagerFactory(TestConsts.PERSISTENCE_NAME);

	private static EntityManager em;
	private static Map<Order, List<OrderItem>> orders = new HashMap<>();
	
	@BeforeClass
	public static void init() {				
		em=emFactoryObj.createEntityManager();
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(Consts.FIELD_DELIMITER).withFirstRecordAsHeader()
				.withIgnoreEmptyLines().withRecordSeparator(Consts.RECORD_SEPARATOR);
		
		try (FileWriter writer = new FileWriter(TEST_INPUT)){
			try(CSVPrinter printer = new CSVPrinter(writer, csvFileFormat)) {
				printer.printRecord(CSVImporter.HEADER);
				for (int i=1;i<=NUMBER_OF_ORDERS;++i)
					createLine(printer);
			}
		}
		catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	private static void createLine(CSVPrinter printer) throws IOException {
		String lns = Integer.toString(++lineNumber);
		LocalDate ld = DATE.plus(Period.ofDays(lineNumber));
		Order order = new Order();
		order.setId(Long.valueOf(lineNumber));
		order.setAddress(ADDRESS+lns);
		order.setBuyerName(BUYERS_NAME+lns);
		order.setBuyerEmail(BUYERS_EMAIL);
		order.setId(Long.valueOf(lineNumber));
		order.setOrderDate(ld);
		order.setPostCode(Integer.parseInt(ZIP+lns));
		List<OrderItem> itms = new ArrayList<>(NUMBER_OF_ITEMS);
		float total = 0;
		for(int i=0; i<NUMBER_OF_ITEMS; ++i) {
			OrderItem item = new OrderItem();
			item.setOrder(order);
			item.setId(Long.valueOf(lineNumber*NUMBER_OF_ITEMS+i));
			item.setSalePrice(42+lineNumber);
			item.setShippingPrice(lineNumber);
			item.setTotalItemPrice(item.getSalePrice()+item.getShippingPrice());
			item.setSKU(SKU+lns);
			item.setStatus(i%2==0?OrderItemStatus.IN_STOCK:OrderItemStatus.OUT_OF_STOCK);
			total+=item.getTotalItemPrice();
			itms.add(item);
			printer.printRecord(Integer.valueOf(lineNumber), 
								Integer.valueOf(item.getId().intValue()), 
								order.getId(), 
								order.getBuyerName(), 
								order.getBuyerEmail(), 
								order.getAddress(), 
								order.getPostCode(),
								Float.valueOf(item.getSalePrice()), 
								Float.valueOf(item.getShippingPrice()), 
								item.getSKU(), 
								item.getStatus(), 
								FORMATTER.format(order.getOrderDate()));
		}
		order.setOrderTotalValue(total);
		orders.put(order, itms);
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void testCSVProcessing() {
		CSVImporter csvImporter= new CSVImporter(TEST_INPUT, TEST_RESPONSE, em);
		assertNotNull(csvImporter);
		csvImporter.process();
		
		for(Map.Entry<Order, List<OrderItem>> e:orders.entrySet()) {
			Order to = e.getKey();
			Order dbo = em.find(Order.class, to.getId());
			assertNotNull(dbo);
			if (!to.equals(dbo)) {
				int a=0;
			}
			assertEquals(to, dbo);
			
			for(OrderItem ti:e.getValue()) {
				OrderItem dbi = em.find(OrderItem.class, ti.getId());
				assertNotNull(dbi);
				assertEquals(ti, dbi);
			}
		}
	}
}
