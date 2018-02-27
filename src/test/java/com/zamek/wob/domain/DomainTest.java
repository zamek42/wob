package com.zamek.wob.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.zamek.wob.domain.order.Order;
import com.zamek.wob.domain.order.OrderBuilder;
import com.zamek.wob.domain.orderitem.OrderItem;
import com.zamek.wob.domain.orderitem.OrderItemBuilder;
import com.zamek.wob.domain.orderitem.OrderItemStatus;

public class DomainTest {

	private final static long ITEM_ID = 42;
	private final static long ORDER_ID = 4242;
	
	private final static float SALE_PRICE = 100.0f;
	private final static float SHIPPING_PRICE = 20.0f;
	private final static float TOTAL_PRICE = 120.0f;
	private final static String SKU = "SKU"; //$NON-NLS-1$
	private final static String ADDRESS = "Planet Magrathea"; //$NON-NLS-1$
	private final static int ZIP=1810;
	private final static String EMAIL="zaphod.beeblebrox@magrathea.ma";	 //$NON-NLS-1$
	private final static String NAME="Zaphod Beeblebrox"; //$NON-NLS-1$
	private final static String DATE ="2000-10-20";  //$NON-NLS-1$
	private final static float FLOAT_LIMIT = 0.1f;
	private static SimpleDateFormat formatter = new SimpleDateFormat(OrderBuilder.DATE_FORMAT);
	
	private OrderItemBuilder correctItemBuilder;
	private OrderBuilder correctOrderBuilder;
	
	@Before
	public void  createCorrectBuilders() {
		try {
			this.correctOrderBuilder = new OrderBuilder();
			this.correctOrderBuilder.orderId(Long.toString(ORDER_ID))
						.buyerName(NAME)
						.address(ADDRESS)
						.postCode(Integer.toString(ZIP))
						.buyerEmail(EMAIL)
						.orderDate(DATE);
			
			this.correctItemBuilder = new OrderItemBuilder();
			this.correctItemBuilder.itemId(Long.toString(ITEM_ID))
					   .salePrice(Float.toString(SALE_PRICE))
					   .shippingPrice(Float.toString(SHIPPING_PRICE))
					   .totalItemPrice(Float.toString(TOTAL_PRICE))
					   .sku(SKU)
					   .status(OrderItemStatus.ST_IN_STOCK)
					   .order(this.correctOrderBuilder.get().get());
		}
		catch (ConvertException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void builderTests() {
		try {
			Optional<Order> o = this.correctOrderBuilder.get();
			assertTrue(o.isPresent());
			Order order = o.get();
			assertEquals(order.getId(), Long.valueOf(ORDER_ID));
			assertEquals(order.getBuyerName(), NAME);
			assertEquals(order.getAddress(), ADDRESS);
			assertEquals(order.getBuyerEmail(), EMAIL);
			assertEquals(order.getPostCode(), Integer.valueOf(ZIP));
			assertEquals(order.getOrderDate(), formatter.parse(DATE));
			
			Optional<OrderItem> oi = this.correctItemBuilder.get();
			assertTrue(oi.isPresent());
			OrderItem item = oi.get();
			assertEquals(item.getId(), Long.valueOf(ITEM_ID));
			assertEquals(item.getSalePrice(), SALE_PRICE, FLOAT_LIMIT);
			assertTrue(item.getSalePrice()>=1.0f);
			assertTrue(item.getShippingPrice() >= 0.0f);
			assertEquals(item.getShippingPrice(), SHIPPING_PRICE, FLOAT_LIMIT);
			assertEquals(item.getTotalItemPrice(), TOTAL_PRICE, FLOAT_LIMIT);
			assertEquals(item.getSalePrice() + item.getShippingPrice(), item.getTotalItemPrice(), FLOAT_LIMIT);
			assertEquals(item.getOrder(), order);
			assertEquals(item.getSKU(), SKU);
			assertEquals(item.getStatus(), OrderItemStatus.IN_STOCK);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void numberTests() {
		try {
			this.correctItemBuilder.salePrice("-1"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}
		try {
			this.correctItemBuilder.salePrice("asd"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctItemBuilder.shippingPrice("-1"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctItemBuilder.shippingPrice("asd"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctItemBuilder.totalItemPrice("-1"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctItemBuilder.totalItemPrice("asd"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctItemBuilder.itemId("asd"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctOrderBuilder.orderId("asd"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}
	}
	
	/**
	 * You can test a set of wrong email, but I use commons-validator because I trust it. :) 
	 */
	@Test
	public void emailChecking() {
		try {
			this.correctOrderBuilder.buyerEmail(null);
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctOrderBuilder.buyerEmail(""); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctOrderBuilder.buyerEmail("asd@@"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC			
		}

		try {
			this.correctOrderBuilder.buyerEmail(EMAIL);
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			fail();
		}

	}
	
	@Test
	public void dateChecking() {
		try {
			this.correctOrderBuilder.orderDate(null);
			assertTrue(this.correctOrderBuilder.get().isPresent());
			assertEquals(this.correctOrderBuilder.get().get().getOrderDate(), new Date());
			this.correctOrderBuilder.orderDate(""); //$NON-NLS-1$
			assertTrue(this.correctOrderBuilder.get().isPresent());
			assertEquals(this.correctOrderBuilder.get().get().getOrderDate(), new Date());
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			fail();			
		}
			
		try {
			this.correctOrderBuilder.orderDate("2000.10.20"); //$NON-NLS-1$
			fail();
		}
		catch (@SuppressWarnings("unused") ConvertException e) {
			//NC
		}
	}
}
