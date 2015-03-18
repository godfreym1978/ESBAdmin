package com.ibm.MQAdmin;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Test {

	public static void main(String[] args) throws  IOException{
		// TODO Auto-generated method stub

		String test = "hello,new,line,wait";

		CSVParser parser = CSVParser.parse(test, CSVFormat.RFC4180);
		
		 for (CSVRecord csvRecord : parser) {
			 System.out.println(csvRecord.size());
			 System.out.println(csvRecord.get(0));
				
			 System.out.println(csvRecord.get(1));
			 System.out.println(csvRecord.get(2));
			 System.out.println(csvRecord.get(3));
		 }

		
	}

}
