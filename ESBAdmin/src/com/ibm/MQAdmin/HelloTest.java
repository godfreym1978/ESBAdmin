/********************************************************************************/
/* */
/* Project: ESBAdmin */
/* Author: Godfrey Peter Menezes */
/* 
Copyright © 2015 Godfrey P Menezes
All rights reserved. This code or any portion thereof
may not be reproduced or used in any manner whatsoever
without the express written permission of Godfrey P Menezes(godfreym@gmail.com).

*/
/********************************************************************************/

package com.ibm.MQAdmin;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class HelloTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String test = "hello,new,line,wait";
		CSVParser parser = CSVParser.parse(test, CSVFormat.RFC4180);
		
		 for (CSVRecord csvRecord : parser) {
			 System.out.println(csvRecord.size());
			 
		 }

	}

}
