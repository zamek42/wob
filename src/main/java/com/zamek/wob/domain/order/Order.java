package com.zamek.wob.domain.order;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zamek.wob.util.HasLogger;

@Entity
@Table(name=Order.TABLE_NAME)
public class Order implements Serializable, HasLogger {

	private final static boolean DEBUG=true;
	
	public final static String TABLE_NAME = "order"; //$NON-NLS-1$
	
	public final static String COL_ID = "OrderId"; //$NON-NLS-1$
	
	public final static String COL_BUYER_NAME = "BuyerName"; //$NON-NLS-1$
	
	public final static String COL_BUYER_EMAIL = "BuyerEmail"; //$NON-NLS-1$
	
	public final static String COL_ORDER_DATE = "OrderDate"; //$NON-NLS-1$
	
	public final static String COL_ORDER_TOTAL_VALUE = "OrderTotalValue"; //$NON-NLS-1$
	
	public final static String COL_ADDRESS = "Address"; //$NON-NLS-1$
	
	public final static String COL_POST_CODE = "PostCode"; //$NON-NLS-1$
	
	@Id
	@Column(name=COL_ID, unique=true, nullable=false, updatable=false)
	private Long id;
	
	@Column(name=COL_BUYER_NAME, nullable=false)  // unique=true?
	private String buyerName;
	
	@Column(name=COL_BUYER_EMAIL, nullable=false) // unique=true?
 	private String buyerEmail;  
	
	@Column(name=COL_ORDER_DATE, nullable=false)
	private LocalDate orderDate;
	
	@Column(name=COL_ORDER_TOTAL_VALUE)
	private int orderTotalValue;
	
	@Column(name=COL_ADDRESS, nullable=false)
	private String address;
	
	@Column(name=COL_POST_CODE)
	private Integer postCode; 
	
	public Order() {
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
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return this.buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the buyerEmail
	 */
	public String getBuyerEmail() {
		return this.buyerEmail;
	}

	/**
	 * @param buyerEmail the buyerEmail to set
	 */
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	/**
	 * @return the orderDate
	 */
	public LocalDate getOrderDate() {
		return this.orderDate;
	}

	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	/**
	 * @return the orderTotalValue
	 */
	public int getOrderTotalValue() {
		return this.orderTotalValue;
	}

	/**
	 * @param orderTotalValue the orderTotalValue to set
	 */
	public void setOrderTotalValue(int orderTotalValue) {
		this.orderTotalValue = orderTotalValue;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the postCode
	 */
	public Integer getPostCode() {
		return this.postCode;
	}

	/**
	 * @param postCode the postCode to set
	 */
	public void setPostCode(int postCode) {
		this.postCode = Integer.valueOf(postCode);
	}
	
	/**
	 * @param postCode the postCode to set
	 */
	public void setPostCode(Integer postCode) {
		this.postCode = postCode;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.address == null) ? 0 : this.address.hashCode());
		result = prime * result + ((this.buyerEmail == null) ? 0 : this.buyerEmail.hashCode());
		result = prime * result + ((this.buyerName == null) ? 0 : this.buyerName.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.orderDate == null) ? 0 : this.orderDate.hashCode());
		result = prime * result + this.orderTotalValue;
		result = prime * result + ((this.postCode == null) ? 0 : this.postCode.hashCode());
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
		Order other = (Order) obj;
		if (this.address == null) {
			if (other.address != null)
				return false;
		} else if (!this.address.equals(other.address))
			return false;
		if (this.buyerEmail == null) {
			if (other.buyerEmail != null)
				return false;
		} else if (!this.buyerEmail.equals(other.buyerEmail))
			return false;
		if (this.buyerName == null) {
			if (other.buyerName != null)
				return false;
		} else if (!this.buyerName.equals(other.buyerName))
			return false;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!this.orderDate.equals(other.orderDate))
			return false;
		if (this.orderTotalValue != other.orderTotalValue)
			return false;
		if (this.postCode == null) {
			if (other.postCode != null)
				return false;
		} else if (!this.postCode.equals(other.postCode))
			return false;
		return true;
	}
	
	
	
}
