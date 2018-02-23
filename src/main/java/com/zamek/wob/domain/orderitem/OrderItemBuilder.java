package com.zamek.wob.domain.orderitem;

import java.util.Optional;

import com.zamek.wob.domain.order.Order;
import com.zamek.wob.util.HasLogger;

public class OrderItemBuilder implements HasLogger {

	private final static boolean DEBUG = true;
	
	private OrderItem item;
	
	public OrderItemBuilder() {
		this.item = new OrderItem();
	}

	public OrderItemBuilder itemId(String id) {
		try {
			this.item.setId(Long.valueOf(Long.parseLong(id)));
		}
		catch (NumberFormatException e) {
			getLogger().warn(String.format("OrderItemBuilder.itemId format exception : %s at: %s", e.getMessage(), id)); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder order(Order order) {
		this.item.setOrder(order);
		return this;
	}

	public OrderItemBuilder salePrice(String salePrice) {
		try {			
			this.item.setSalePrice(Integer.parseInt(salePrice));
		}
		catch (NumberFormatException e) {
			getLogger().warn(String.format("OrderItemBuilder.salePrice format exception : %s at: %s", e.getMessage(), salePrice)); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder shippingPrice(String shippingPrice) {
		try {			
			this.item.setShippingPrice(Integer.parseInt(shippingPrice));
		}
		catch (NumberFormatException e) {
			getLogger().warn(String.format("OrderItemBuilder.shippingPrice format exception : %s at: %s", e.getMessage(), shippingPrice)); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder totalItemPrice(String totalItemPrice) {
		try {
			this.item.setTotalItemPrice(Integer.parseInt(totalItemPrice));
		}
		catch (NumberFormatException e) {
			getLogger().warn(String.format("OrderItemBuilder.totalItemPrice format exception : %s at: %s", e.getMessage(), totalItemPrice)); //$NON-NLS-1$
		}			
		return this;
	}
	
	public OrderItemBuilder sku(String SKU) {
		this.item.setSKU(SKU);
		return this;
	}
	
	public OrderItemBuilder status(String status) {
		this.item.setStatus(OrderItemStatus.byString(status));
		return this;
	}

	private boolean check() {
		return this.item.getOrder() != null
				&& this.item.getSalePrice() >= 0
				&& this.item.getShippingPrice() >= 0
				&& this.item.getTotalItemPrice() >= 0
				&& this.item.getStatus() != OrderItemStatus.UNKNOWN;
	}
	
	public Optional<OrderItem> get() {
		return check() ? Optional.of(this.item) : Optional.empty();
	}
}
