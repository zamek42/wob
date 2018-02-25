package com.zamek.wob.csv;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class ResponseFile implements AutoCloseable {

	private final static String LINE_NUMBER = "LineNumber";	//$NON-NLS-1$
	private final static String STATUS = "Status";	//$NON-NLS-1$
	private final static String MESSAGE = "Message"; //$NON-NLS-1$
	private final static String RECORD_SEPARATOR = "\n"; //$NON-NLS-1$
	
	public enum Status {
		OK, ERROR
	}
	
	private String fileName;
	private FileWriter writer;
	private CSVPrinter printer;
	
	public ResponseFile(String name) {
		this.fileName=name;
	}
	
	public void open() throws IOException {
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(RECORD_SEPARATOR); 		
		this.writer = new FileWriter(this.fileName);
		this.printer = new CSVPrinter(this.writer, csvFileFormat);
		this.printer.printRecord(new Object[] {LINE_NUMBER, STATUS, MESSAGE});
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
