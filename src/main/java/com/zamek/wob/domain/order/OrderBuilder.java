package com.zamek.wob.domain.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.zamek.wob.domain.ConvertException;
import com.zamek.wob.util.HasLogger;


public class OrderBuilder implements HasLogger {

	@SuppressWarnings("unused")
	private final static boolean DEBUG=true;
	
	public final static String DATE_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$
	
	private Order order;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	public OrderBuilder() {
		this.order = new Order();
		this.order.setOrderDate(LocalDate.now());
	}
	
	public OrderBuilder orderId(String id) throws ConvertException {
		try {
			this.order.setId(Long.valueOf(Long.parseLong(id)));
		}
		catch (NumberFormatException e) {
			throw new ConvertException("OrderId error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderBuilder buyerName(String bn) throws ConvertException {
		if (StringUtils.isBlank(bn))
			throw new ConvertException("Buyer's name is empty"); //$NON-NLS-1$
		this.order.setBuyerName(bn);
		return this;
	}
	
 	public OrderBuilder buyerEmail(String e) throws ConvertException {
 		if (StringUtils.isNotBlank(e) && EmailValidator.getInstance().isValid(e))
 			this.order.setBuyerEmail(e);
 		else
 			throw new ConvertException("email error:"+e); //$NON-NLS-1$
 		return this;
 	}
	
	public OrderBuilder orderDate(String d) throws ConvertException {
		try {
		this.order.setOrderDate(StringUtils.isBlank(d) 
				? LocalDate.now()
				: LocalDate.parse(d, this.formatter));
		}
		catch (DateTimeParseException e) {
			throw new ConvertException("order date error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderBuilder orderTotalValue (String value) throws ConvertException {
		try {
			this.order.setOrderTotalValue(Float.parseFloat(value));
		}
		catch (NumberFormatException e) {
			throw new ConvertException("OrderTotalValue error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	public OrderBuilder address(String address) {
		this.order.setAddress(address);
		return this;
	}
	
	public OrderBuilder postCode (String pc) throws NumberFormatException {
		this.order.setPostCode(Integer.parseInt(pc));
		return this;
	}
	
	/**
	 * You can make some restrictions for the item for example min length of name or etc. 
	 * 
	 * @return true if item is correct or false if something went wrong
	 */

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
