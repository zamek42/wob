package com.zamek.wob.domain.orderitem;

import java.util.Optional;

import com.zamek.wob.domain.ConvertException;
import com.zamek.wob.domain.order.Order;
import com.zamek.wob.util.HasLogger;

public class OrderItemBuilder implements HasLogger {

	@SuppressWarnings("unused")
	private final static boolean DEBUG = true;
	
	private OrderItem item;
	
	public OrderItemBuilder() {
		this.item = new OrderItem();
	}

	public OrderItemBuilder itemId(String id) throws ConvertException {
		try {
			long li = Long.parseLong(id);
			this.item.setId(Long.valueOf(li));
			
		}
		catch (NumberFormatException e) {
			throw new ConvertException("OrderItemId error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder order(Order order) {
		this.item.setOrder(order);
		return this;
	}

	public OrderItemBuilder salePrice(String salePrice) throws ConvertException {
		try {
			float sp = Float.parseFloat(salePrice);
			if (sp < 1.0f)
				throw new ConvertException("salePrice is less than 1.0"); //$NON-NLS-1$
			this.item.setSalePrice(sp);
		}
		catch (NumberFormatException e) {
			throw new ConvertException("salePrice number format error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder shippingPrice(String shippingPrice) throws ConvertException {
		try {
			float sp = Float.parseFloat(shippingPrice);
			if (sp<0.0f)
				throw new ConvertException("shippingPrice is less than 0.0"); //$NON-NLS-1$
			this.item.setShippingPrice(sp);
			
		}
		catch (NumberFormatException e) {
			throw new ConvertException("shippingPrice number format error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder totalItemPrice(String totalItemPrice) throws ConvertException {
		try {
			float tp = Float.parseFloat(totalItemPrice);
			if (tp<=1.0f)
				throw new ConvertException("totalItemPrice is less than 1.0"); //$NON-NLS-1$
				
			this.item.setTotalItemPrice(tp);
		}
		catch (NumberFormatException e) {
			throw new ConvertException("totalItemPrice number format error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderItemBuilder sku(String SKU) {
		this.item.setSKU(SKU);
		return this;
	}
	
	public OrderItemBuilder status(String status) throws ConvertException {
		this.item.setStatus(OrderItemStatus.byString(status));
		return this;
	}

	/**
	 * You can make some restrictions for the item for example totalPrice = salePrice+shippingPrice and etc. 
	 * 
	 * @return true if item is correct or false if something went wrong
	 */
	private boolean check() {
		return this.item.getOrder() != null
				&& this.item.getId() != null 
				&& this.item.getSalePrice() >= 1.0f
				&& this.item.getShippingPrice() >= 0.0f
				&& this.item.getTotalItemPrice() >= 0.0f
				&& this.item.getStatus() != null;
	}
	
	private void postCalculation() {
		this.item.setTotalItemPrice(this.item.getSalePrice() + this.item.getShippingPrice());
	}
	
	public Optional<OrderItem> get() {
		if (!check())
			return Optional.empty();
		
		postCalculation();
		return Optional.of(this.item);
	}
}
