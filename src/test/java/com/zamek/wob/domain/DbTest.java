package com.zamek.wob.domain;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

import com.zamek.wob.TestConsts;
import com.zamek.wob.domain.order.Order;
import com.zamek.wob.domain.orderitem.OrderItem;
import com.zamek.wob.domain.orderitem.OrderItemStatus;
import com.zamek.wob.util.DateUtils;


public class DbTest {
	private final static String BUYERS_NAME = "Zaphod Beeblebrox";  //$NON-NLS-1$
	private final static String BUYERS_EMAIL ="zaphod.beeblebrox@magrathea.ma";  //$NON-NLS-1$
	private final static String ADDRESS = "Planet Magrathea";  //$NON-NLS-1$
	private final static int ZIP = 4242;
	private final static String SKU = "What is SKU?";  //$NON-NLS-1$
	
	private final static LocalDate DATE = LocalDate.now().minus(Period.of(5, 1, 20));

	private static EntityManagerFactory emFactoryObj=Persistence.createEntityManagerFactory(TestConsts.PERSISTENCE_NAME);

	private static EntityManager em;
	
	@BeforeClass
	public static void initEntitManager() {
		em=emFactoryObj.createEntityManager();
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void dbTest() {
		try {
			Order order = new Order();
			order.setId(Long.valueOf(1));
			order.setAddress(ADDRESS);
			order.setBuyerName(BUYERS_NAME);
			order.setBuyerEmail(BUYERS_EMAIL);
			order.setId(Long.valueOf(1));
			order.setOrderDate(DateUtils.asDate(DATE));
			order.setPostCode(ZIP);
			order.setOrderTotalValue(42.0f);
			em.persist(order);
			
			OrderItem item = new OrderItem();
			item.setId(Long.valueOf(1));
			item.setOrder(order);
			item.setSKU(SKU);
			item.setSalePrice(42.0f);
			item.setShippingPrice(1.0f);
			item.setTotalItemPrice(43.0f);
			item.setStatus(OrderItemStatus.IN_STOCK);
			em.persist(item);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
