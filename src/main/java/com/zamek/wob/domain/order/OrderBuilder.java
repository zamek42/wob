package com.zamek.wob.domain.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.zamek.wob.domain.ConvertException;
import com.zamek.wob.util.HasLogger;

/**
 * Builder for Order object. 
 * 
 *  It can build an Order obejct if every properties are valid 
 *  It returns an Optional &lt;Order&gt; which is contains a vlaid Order or empty if the Order is not valid.
 *  
 * @author zamek
 *
 */
public class OrderBuilder implements HasLogger {

	@SuppressWarnings("unused")
	private final static boolean DEBUG=true;
	
	/**
	 * Default date format
	 */
	public final static String DATE_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$
	
	private Order order;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	/**
	 * Constructor for OrderBuilder
	 */
	public OrderBuilder() {
		this.order = new Order();
		this.order.setOrderDate(LocalDate.now());
	}
	
	/**
	 * Setting Id for Order
	 * 
	 * @param id OrderId from CSV
	 * @return reference to OrderBuilder
	 * @throws ConvertException if id is not a valid number
	 */
	public OrderBuilder orderId(String id) throws ConvertException {
		try {
			this.order.setId(Long.valueOf(Long.parseLong(id)));
		}
		catch (NumberFormatException e) {
			throw new ConvertException("OrderId error:"+e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}
	
	/**
	 * Setting Buyer's name
	 * 
	 * @param bn name of the BUyer from CSV
	 * @return reference to OrderBuilder
	 * @throws ConvertException of name is blank
	 */
	public OrderBuilder buyerName(String bn) throws ConvertException {
		if (StringUtils.isBlank(bn))
			throw new ConvertException("Buyer's name is empty"); //$NON-NLS-1$
		this.order.setBuyerName(bn);
		return this;
	}
	
	/**
	 * Setting Buyer's email 
	 *  
	 * @param e Buyer's email from CSV
	 * @return reference to OrderBuilder
	 * @throws ConvertException if email is blank or not valid
	 */
 	public OrderBuilder buyerEmail(String e) throws ConvertException {
 		if (StringUtils.isNotBlank(e) && EmailValidator.getInstance().isValid(e))
 			this.order.setBuyerEmail(e);
 		else
 			throw new ConvertException("email error:"+e); //$NON-NLS-1$
 		return this;
 	}
	
 	/**
 	 * Setting date 
 	 * 
 	 * @param d Date from CSV
 	 * @return reference to OrderBuilder
 	 * @throws ConvertException date is not parseable
 	 */
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
	
	/**
	 * Setting address
	 * @param address address from CSV
	 * @return reference to OrderBuilder
	 */
	public OrderBuilder address(String address) {
		this.order.setAddress(address);
		return this;
	}
	
	/**
	 * Setting PostCode
	 * @param pc PostCOde from CSV
	 * @return reference to OrderBuilder
	 * @throws NumberFormatException if not parseable
	 */
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
	
	/**
	 * Builds an Order or empty if order is not valid
	 * 
	 * @return the nuilt Order or Empty 
	 */
	public Optional<Order> get() {
		return check() ? Optional.of(this.order) : Optional.empty();
	}

}
