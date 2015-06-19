package com.ibm.esbadmin;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.ByteBuffer;

import com.ibm.mq.exits.MQCD;
import com.ibm.mq.exits.MQCXP;
import com.ibm.mq.exits.WMQSecurityExit;
import com.ibm.mq.exits.WMQSendExit;

//public class MyMQExits implements WMQSendExit, WMQReceiveExit, WMQSecurityExit {
public class MyMQExits implements WMQSendExit, WMQSecurityExit {
	// Default constructor
	public MyMQExits() {
	}

	// This method implements the send exit interface
	public ByteBuffer channelSendExit(MQCXP channelExitParms,
			MQCD channelDefinition, ByteBuffer agentBuffer) {
		// Complete the body of the send exit here
		try{
			File exitData = new File("c:\\test.txt");
			if (!exitData.exists()) {
				exitData.createNewFile();
			}
			
			FileWriter fw = new FileWriter(exitData.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			bw.write(channelExitParms.getExitData());
			bw.write(channelExitParms.getExitReason());
			bw.write(channelExitParms.getExitResponse());
			bw.write(new String(channelExitParms.getExitUserArea()));
			bw.write(channelExitParms.getSecurityParms().toString());
			
			bw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return agentBuffer;
	}
	/*

	// This method implements the receive exit interface
	public ByteBuffer channelReceiveExit(MQCXP channelExitParms,
			MQCD channelDefinition, ByteBuffer agentBuffer) {
		// Complete the body of the receive exit here
	}
*/
	// This method implements the security exit interface
	public ByteBuffer channelSecurityExit(MQCXP channelExitParms,
			MQCD channelDefinition, ByteBuffer agentBuffer) {
		// Complete the body of the security exit here
		
		try{
			File exitData = new File("c:\\test.txt");
			if (!exitData.exists()) {
				exitData.createNewFile();
			}
			
			FileWriter fw = new FileWriter(exitData.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			bw.write(channelExitParms.getExitData());
			bw.write(channelExitParms.getExitReason());
			bw.write(channelExitParms.getExitResponse());
			bw.write(new String(channelExitParms.getExitUserArea()));
			bw.write(channelExitParms.getSecurityParms().toString());
			
			bw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return agentBuffer;

		
	}
}