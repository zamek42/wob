package com.zamek.wob.csv;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVPrinter;

import com.zamek.wob.domain.ConvertException;

public class ResponseFile implements AutoCloseable {

	public final static String LINE_NUMBER = "LineNumber";	//$NON-NLS-1$
	public final static String STATUS = "Status";	//$NON-NLS-1$
	public final static String MESSAGE = "Message"; //$NON-NLS-1$
	
	public final static Object[] HEADER = new Object[] {LINE_NUMBER, STATUS, MESSAGE };
	
	public final static String ST_STATUS_OK = "OK";	//$NON-NLS-1$
	public final static String ST_STATUS_ERROR = "ERROR";  //$NON-NLS-1$
	
	public enum Status {
		OK, ERROR;
		
		public static Status byString(String s) throws ConvertException {
			switch(s.toUpperCase()) {
			case ST_STATUS_OK : return OK;
			case ST_STATUS_ERROR : return ERROR;
			default:
				throw new ConvertException("Unknown response status"); //$NON-NLS-1$
			}
		}
	}
	
	private String fileName;
	private FileWriter writer;
	private CSVPrinter printer;
	
	public ResponseFile(String name) {
		this.fileName=name;
	}
	
	public void open() throws IOException {		 		
		this.writer = new FileWriter(this.fileName);
		this.printer = new CSVPrinter(this.writer, CSVImporter.CSV_FILE_FORMAT);
		this.printer.printRecord(HEADER);
	}

	@Override
	public void close() throws Exception {
		if (this.printer != null)
			this.printer.close();
		if (this.writer!=null)
			this.writer.close();
	}
	
	public void message(int lineNumber, Status status, String message) throws IOException {
		if (this.printer == null)
			throw new IOException("responseFile is not opened"); //$NON-NLS-1$
		this.printer.printRecord(Integer.valueOf(lineNumber), status, message);
	}
}
