package com.zamek.wob.domain.order;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.zamek.wob.util.HasLogger;


public class OrderBuilder implements HasLogger {

	private Order order;
	
	public OrderBuilder() {
		this.order = new Order();
		this.order.setOrderDate(LocalDate.now());
	}
	
	public OrderBuilder orderId(String id) {
		try {
			this.order.setId(Long.valueOf(Long.parseLong(id)));
		}
		catch (NumberFormatException e) {
			getLogger().warn(String.format("OrderBuilder.orderId format exception : %s at: %s", e.getMessage(), id)); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderBuilder buyerName(String bn) {
		this.order.setBuyerName(bn);
		return this;
	}
	
 	public OrderBuilder buyerEmail(String e) {
 		this.order.setBuyerEmail(e);
 		return this;
 	}
	
	public OrderBuilder orderDate(LocalDate d) {
		this.order.setOrderDate(d);
		return this;
	}
	
	public OrderBuilder orderTotalValue (int value) {
		this.order.setOrderTotalValue(value);
		return this;
	}
	
	public OrderBuilder address(String address) {
		this.order.setAddress(address);
		return this;
	}
	
	public OrderBuilder postCode (String pc) {
		try {
			this.order.setPostCode(Integer.parseInt(pc));
		}
		catch (NumberFormatException e) {
			getLogger().warn(String.format("OrderBuilder.postCode format exception : %s at: %s", e.getMessage(), pc)); //$NON-NLS-1$
		}
		return this;
	}
	
	private boolean check() {
		String email = this.order.getBuyerEmail();
		LocalDate date = this.order.getOrderDate();
		LocalDate now = LocalDate.now();
		return this.order.getId() != null 
				&& StringUtils.isNotBlank(this.order.getBuyerName())
				&& StringUtils.isNotBlank(email) && EmailValidator.getInstance().isValid(email)
				&& ( date.isBefore(now) || date.isEqual(now) )
				&& StringUtils.isNotBlank(this.order.getAddress())
				&& this.order.getPostCode() != null;
	}
	
	public Optional<Order> get() {
		return check() ? Optional.of(this.order) : Optional.empty();
	}

}
