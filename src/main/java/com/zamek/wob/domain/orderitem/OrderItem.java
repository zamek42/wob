package com.zamek.wob.domain.orderitem;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zamek.wob.domain.order.Order;
import com.zamek.wob.util.HasLogger;

@Entity
@Table(name=OrderItem.TABLE_NAME)
public class OrderItem implements HasLogger {
	
	@SuppressWarnings("unused")
	private final static boolean DEBUG=true;
	
	public final static String TABLE_NAME = "order_item"; //$NON-NLS-1$
	
	public final static String COL_ORDER_ITEM_ID = "OrderItemId"; //$NON-NLS-1$
	
	public final static String COL_ORDER_ID = "OrderId"; //$NON-NLS-1$
	
	public final static String COL_SALE_PRICE = "SalePrice"; //$NON-NLS-1$
	
	public final static String COL_SHIPPING_PRICE = "ShippingPrice"; //$NON-NLS-1$
	
	public final static String COL_TOTAL_ITEM_PRICE = "TotalItemPrice"; //$NON-NLS-1$
	
	public final static String COL_SKU = "SKU"; //$NON-NLS-1$
	
	public final static String COL_STATUS = "Status";	 //$NON-NLS-1$

	@Id
	@Column(name=COL_ORDER_ITEM_ID, unique=true, nullable=false, updatable=false)
	private Long id;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name=COL_ORDER_ID)
	private Order order;

	@Column(name=COL_SALE_PRICE, nullable=false)
	private float salePrice;
	
	@Column(name=COL_SHIPPING_PRICE, nullable=false)
	private float shippingPrice;
	
	@Column(name=COL_TOTAL_ITEM_PRICE, nullable=false)
	private float totalItemPrice;
	
	@Column(name=COL_SKU, length=50)
	private String SKU;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name=COL_STATUS)
	private OrderItemStatus status;
		
	public OrderItem() {
		//NC
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}


	/**
	 * @return the salePrice
	 */
	public float getSalePrice() {
		return this.salePrice;
	}


	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(float salePrice) {
		this.salePrice = salePrice;
	}


	/**
	 * @return the shippingPrice
	 */
	public float getShippingPrice() {
		return this.shippingPrice;
	}


	/**
	 * @param shippingPrice the shippingPrice to set
	 */
	public void setShippingPrice(float shippingPrice) {
		this.shippingPrice = shippingPrice;
	}


	/**
	 * @return the sKU
	 */
	public String getSKU() {
		return this.SKU;
	}


	/**
	 * @param sKU the sKU to set
	 */
	public void setSKU(String sKU) {
		this.SKU = sKU;
	}


	/**
	 * @return the status
	 */
	public OrderItemStatus getStatus() {
		return this.status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(OrderItemStatus status) {
		this.status = status;
	}


	/**
	 * @return the totalItemPrice
	 */
	public float getTotalItemPrice() {
		return this.totalItemPrice;
	}

	/**
	 * @param totalItemPrice the totalItemPrice to set
	 */
	public void setTotalItemPrice(float totalItemPrice) {
		this.totalItemPrice = totalItemPrice;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.SKU == null) ? 0 : this.SKU.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.order == null) ? 0 : this.order.hashCode());
		result = prime * result + Float.floatToIntBits(this.salePrice);
		result = prime * result + Float.floatToIntBits(this.shippingPrice);
		result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
		result = prime * result + Float.floatToIntBits(this.totalItemPrice);
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderItem other = (OrderItem) obj;
		if (this.SKU == null) {
			if (other.SKU != null)
				return false;
		} else if (!this.SKU.equals(other.SKU))
			return false;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.order == null) {
			if (other.order != null)
				return false;
		} else if (!this.order.equals(other.order))
			return false;
		if (Float.floatToIntBits(this.salePrice) != Float.floatToIntBits(other.salePrice))
			return false;
		if (Float.floatToIntBits(this.shippingPrice) != Float.floatToIntBits(other.shippingPrice))
			return false;
		if (this.status != other.status)
			return false;
		if (Float.floatToIntBits(this.totalItemPrice) != Float.floatToIntBits(other.totalItemPrice))
			return false;
		return true;
	}
	
}
