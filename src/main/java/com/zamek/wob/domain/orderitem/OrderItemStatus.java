package com.zamek.wob.domain.orderitem;

import com.zamek.wob.domain.ConvertException;

public enum OrderItemStatus {
	
	IN_STOCK() {

		@Override
		public String toString() {
			return ST_IN_STOCK;
		}
		
	}, 
	
	OUT_OF_STOCK() {

		@Override
		public String toString() {
			return ST_OUT_OF_STOCK;
		}
		
	};

	public final static String ST_IN_STOCK = "INSTOCK"; //$NON-NLS-1$
	
	public final static String ST_OUT_OF_STOCK = "OUTOFSTOCK"; //$NON-NLS-1$

	public static OrderItemStatus byString(String st) throws ConvertException {
		switch (st.toUpperCase()) {
		case ST_IN_STOCK : return OrderItemStatus.IN_STOCK;
		case ST_OUT_OF_STOCK : return OrderItemStatus.OUT_OF_STOCK;
		default : throw new ConvertException("Unknown orderitem status:"+st); //$NON-NLS-1$
		}
	}
	
	@Override
	public abstract String toString();
	
	
}
