package com.zamek.wob.domain.orderitem;

public enum OrderItemStatus {
	
	IN_STOCK, OUT_OF_STOCK, UNKNOWN;

	public final static String ST_IN_STOCK = "INSTOCK"; //$NON-NLS-1$
	
	public final static String ST_OUT_OF_STOCK = "OUTOFSTOCK"; //$NON-NLS-1$

	public static OrderItemStatus byString(String st) {
		switch (st.toUpperCase()) {
		case ST_IN_STOCK : return OrderItemStatus.IN_STOCK;
		case ST_OUT_OF_STOCK : return OrderItemStatus.OUT_OF_STOCK;
		default : return UNKNOWN;
		}
	}
	
	
}
