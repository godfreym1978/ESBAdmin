package com.ibm.MQAdmin;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import sun.misc.BASE64Decoder;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.headers.*;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.headers.MQHeaderList;
import com.ibm.mq.constants.MQConstants;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

	static final char CARRIAGE_RETURN = 13;
	static final String fileSep = File.separator;
	
	public String md5Digest(String pwd) throws NoSuchAlgorithmException {
		MessageDigest msgDig = MessageDigest.getInstance("MD5");
		msgDig.reset();
		msgDig.update(pwd.getBytes());
		byte[] digest = msgDig.digest();
		String hashText = new String(digest);
		// Now we need to zero pad it if you actually want the full 32 chars.

		return hashText;
	}

	public void WriteToFilePassword(String UserID, String Password) throws NoSuchAlgorithmException{
		try {
 
			File file = new File(System.getProperty("catalina.base")
					+ fileSep+"ESBAdmin"+fileSep+UserID+fileSep+UserID);

 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(md5Digest(Password));
			bw.close();
  
		} catch (IOException e) {
			e.printStackTrace();
		}
}
	
	public boolean createUser(String UsrID, String UsrPwd) throws NoSuchAlgorithmException {

		Util utilInstance = new Util();

		try{
			utilInstance.WriteToFilePassword(UsrID, UsrPwd);
			return true;
		}catch(Exception e){
			return false;
		}

	}

	

	public boolean changePasswd(String UsrID, String oldPwd,String newPwd) throws NoSuchAlgorithmException {

		Util utilInstance = new Util();

		try{
			utilInstance.WriteToFilePassword(UsrID, newPwd);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	

	public int getDepth(String queueName,int port, String hostName, String queueMgr, String qChannel) throws MQException {
		// Build quemanager (this should be done in another method)
		// and not every time in a real life application

		MQEnvironment.channel = qChannel;
		MQEnvironment.port = port;
		MQEnvironment.hostname = hostName;
		MQQueueManager qmgr = new MQQueueManager(queueMgr);
		// access the queue to query its depth
		com.ibm.mq.MQQueue queue = qmgr.accessQueue(queueName,
				MQConstants.MQOO_INQUIRE | MQConstants.MQOO_INPUT_AS_Q_DEF,
				null, null, null);
		int queueDepth = queue.getCurrentDepth();
		queue.close(); 
		qmgr.disconnect();

		return queueDepth;//queueDepth;
		
	}


	public List<Map> getDepthAll(ArrayList qList,int port, String hostName, String queueMgr, String qChannel) throws MQException {
		// Build quemanager (this should be done in another method)
		// and not every time in a real life application

		MQEnvironment.channel = qChannel;
		MQEnvironment.port = port;
		MQEnvironment.hostname = hostName;
		MQQueueManager qmgr = new MQQueueManager(queueMgr);
		List<Map> qDepthList = new ArrayList<Map>();
		Map iMap = new HashMap();

		try{
			// access the queue to query its depth
			String qName = new String();
			ArrayList qDepth = new ArrayList(); 
			for (int i = 0; i< qList.size();i++){
				qName = qList.get(i).toString();
				iMap = new HashMap<>();
				com.ibm.mq.MQQueue queue = qmgr.accessQueue(qList.get(i).toString(),
						MQConstants.MQOO_INQUIRE | MQConstants.MQOO_INPUT_AS_Q_DEF,
						null, null, null);
				iMap.put(qList.get(i).toString(), queue.getCurrentDepth());
				
				//queue.close(); 
				qDepthList.add(iMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			qmgr.disconnect();
			return qDepthList;//queueDepth;
			
		}
		
	}

	/** 
	    * Convert a byte array to a hex string representation 
	    * @param byteArray 
	    * @return string 
	    */ 
	/*
	   public static String byteArrayToHexString(byte[] byteArray) 
	   { 
	      int len = byteArray.length; 
	      StringBuffer hexStr = new StringBuffer(); 

	      for (int j=0; j < len; j++) 
	         hexStr.append(byteToHex((char)byteArray[j])); 

	      return hexStr.toString(); 
	   } 
*/

	   public static String byteArrayToHexString(byte[] byteArray) 
	   { 
	      final StringBuilder builder = new StringBuilder();
	      for(byte b : byteArray) {
	          builder.append(String.format("%02x", b));
	      }
	      return builder.toString();
	      
	   } 

	   /** 
	    * Convert a byte to its hexadecimal value. 
	    * @param val 
	    * @return 
	    */ 
	   private static String byteToHex(char val) 
	   { 
	     int hi = (val & 0xF0) >> 4; 
	     int lo = (val & 0x0F); 
	     return "" + HEX.charAt(hi) + HEX.charAt(lo); 
	   } 

	   private static final String HEX = "0123456789ABCDEF";
	
	   public String DisplayRFH2(MQMessage gotMessage) throws IOException {
		   
		   
				   MQHeaderIterator it = new MQHeaderIterator (gotMessage);

				   while (it.hasNext ())
				   {
				   	MQHeader header = it.nextHeader ();
				           		
				   	System.out.println ("Header type " + header.type () + ": " + header);
				   }
		   
		   String headers = new String();
		   return headers; 
		  };    
	   
	   
	public ArrayList browseQueue(String queueMgr, String queueName) {
		
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
		ArrayList<String> qMessages = new ArrayList<String>();
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		MQPoolToken token=MQEnvironment.addConnectionPoolToken();
		try {
			
			// Create a connection to the QueueManager
			qMgr = new MQQueueManager(queueMgr);

			// Set up the options on the queue we wish to open
			int openOptions = MQConstants.MQOO_BROWSE
					| MQConstants.MQOO_INPUT_SHARED;

			// Now specify the queue that we wish to open and the open options
			queue = qMgr.accessQueue(queueName, openOptions, null, null, null);

			MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
			getMessageOptions.options = MQConstants.MQGMO_BROWSE_FIRST
					| MQConstants.MQGMO_WAIT; 

			getMessageOptions.waitInterval = 1000;
			// Get the message off the queue.
			int iCount = 0;
			byte[] b = null;
			while (true) {
				
				iCount++;
				MQMessage rcvMessage = new MQMessage();
				
				queue.get(rcvMessage, getMessageOptions);
				if (rcvMessage.getMessageLength()>200){
					b = new byte[200];
					rcvMessage.readFully(b,0,199);
				}else{
					b = new byte[rcvMessage.getMessageLength()];
					rcvMessage.readFully(b);
				}
				
				String msgText = new String(b);
				if (msgText.trim().equals(""))
					break;
				qMessages.add(new String(b));
				qMessages.add(byteArrayToHexString(rcvMessage.messageId));
				//qMessages.add(new String(rcvMessage.messageId));
				qMessages.add(dateFormat.format(rcvMessage.putDateTime.getTime()));
				getMessageOptions.options = MQConstants.MQGMO_BROWSE_NEXT;
				DisplayRFH2(rcvMessage);
				b = null;
				
			}
			
		} catch (MQException ex) {
			if (ex.completionCode == 2 && ex.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) 
				System.out.println("No more messages ");
			 else 
				System.out
						.println("A WebSphere MQ Error occured : Completion Code "
								+ ex.completionCode
								+ " Reason Code "
								+ ex.reasonCode);
				
			
		} catch (java.io.IOException ex) {
			System.out
					.println("An IOException occured while reading to the message buffer: "
							+ ex);
		}  finally {
			try {
				System.out.println("Closing the queue");
				queue.close();
				System.out.println("Disconnecting from the Queue Manager");
				qMgr.disconnect();
				System.out.println("Done!");
			} catch (Exception e) {
				System.out.println("Error in finally!");
			}
		}
		MQEnvironment.removeConnectionPoolToken(token);
		return qMessages;
	}

	public Object accessQMgr(String QmgrNm, String QMgrIPAddr, String QMgrChl,
			int QMgrPort) {

		MQEnvironment.channel = QMgrChl;
		MQEnvironment.port = QMgrPort;
		MQEnvironment.hostname = QMgrIPAddr;
		int openOptions = 17;
		try {
			MQQueueManager QMgr = new MQQueueManager(QmgrNm);

			MQQueue system_default_local_queue = QMgr.accessQueue(
					"SYSTEM.DEFAULT.LOCAL.QUEUE", openOptions, null, // default
																		// q
					null, // no dynamic q name
					null);
			return true;
		} catch (MQException e) {
			e.printStackTrace();
			return false;
		}

	}

	/*
	public ArrayList getQueueList(String qMgr,String qPort,String qHost) {

		ArrayList<String> alQueueList = new ArrayList<String>();
		try {
			String hostName = qHost;
			int portNo = Integer.parseInt(qPort);
			MQEnvironment.channel = "SYSTEM.DEF.SVRCONN";
			MQEnvironment.port = portNo;
			MQEnvironment.hostname = hostName;
			MQEnvironment.properties.put(MQConstants.TRANSPORT_PROPERTY,
					MQConstants.TRANSPORT_MQSERIES);

			PCFMessageAgent agent = new PCFMessageAgent(hostName, portNo,
					"SYSTEM.DEF.SVRCONN");

			PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
			request.addParameter(CMQC.MQCA_Q_NAME, "*");
			request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
			request.addParameter(CMQCFC.MQIACF_Q_ATTRS,
					new int[] { CMQC.MQCA_Q_NAME });

			PCFMessage[] responses = agent.send(request);
			
			for (int i = 0; i < responses.length; i++) {
				if((responses[i].getStringParameterValue(CMQC.MQCA_Q_NAME).indexOf("SYSTEM")==-1)&&
				(responses[i].getStringParameterValue(CMQC.MQCA_Q_NAME).indexOf("AMQ.")==-1)){
					alQueueList.add(responses[i].getStringParameterValue(CMQC.MQCA_Q_NAME));
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return alQueueList;
	}
	*/
	
	public String writeMessageToQueue(String queueMgr, String queueName,
			String qMessage) {
		String returnMsg = null;

		try {
			MQQueueManager qMgr = new MQQueueManager(queueMgr);
			int openOptions = 17;
			MQQueue queue = qMgr.accessQueue(queueName, openOptions);

			String qmessage = qMessage;
			MQMessage msg = new MQMessage();
			msg.writeString(qmessage);
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			queue.put(msg, pmo);
			queue.close();
			qMgr.disconnect();

			returnMsg = "Success";
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
			returnMsg = "MQ Exception" + ex.completionCode + " Reason Code "
					+ ex.reasonCode;

		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);

			returnMsg = "IO Exception" + ex;
		}

		return returnMsg;
	}

	/*
	public static String prettyFormat(String input, int indent) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        System.out.println(xmlOutput.getWriter().toString());
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}
	
	public StringBuffer invokeWebService(String webServiceURL, String formData) {
		String responseDB = new String();
		StringBuffer responseXML = null;
		// Checking command line arguments
		String sURL = webServiceURL;
		String outFile = "c:\\personal\\outputrequest.xml";

		try {
			// Creating the HttpURLConnection object
			URL oURL = new URL(sURL);
			HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			con.setDoOutput(true);
			con.setDoInput(true);

			// Posting the SOAP request XML message
			OutputStream reqStream = con.getOutputStream();
			reqStream.write(formData.getBytes());
			reqStream.flush();

			// Reading the SOAP response XML message
			byte[] byteBuf = new byte[1024];
			FileOutputStream outStream = new FileOutputStream(outFile);
			InputStream resStream = con.getInputStream();
			resStream.read(byteBuf);

			outStream.close();

			reqStream.close();
			resStream.close();
			responseDB = new String(byteBuf);
			System.out.println(responseDB);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new StringBuffer(responseDB);
	}
	
	public StringBuffer transformStringToXML(StringBuffer inputStringBuffer) {
		StringBuffer sbXML = new StringBuffer();

		// write the output file
		try {
			System.out.println("In Try");
			// create a transformer
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();

			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			System.out.println("After Transformer set");
			// get a transformer and supporting classes
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			DOMSource source = new DOMSource();
			System.out.println("After DOM Sets");
			// transform the xml document into a string
			transformer.transform(source, result);

			// open the output file
			sbXML = sbXML.append(writer.toString());

		} catch (javax.xml.transform.TransformerException e) {
			// do something with this error
			e.printStackTrace();
		}

		System.out.println(sbXML);
		return sbXML;
	}
	
	public void getMessageFromQueue(String qManager, String qResponse,
			String responseFileLocation) {
		try {
			// Store the response in the disk
			File responseFile = new File(responseFileLocation);

			responseFile.createNewFile();
			final String encoding = "UTF-8";
			OutputStreamWriter oSW = new OutputStreamWriter(
					new FileOutputStream(responseFile), encoding);

			MQQueueManager qMgr = new MQQueueManager(qManager);
			int openOptions = 17;
			MQQueue queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			queue.get(msg);
			byte[] b = new byte[msg.getMessageLength()];
			msg.readFully(b);

			oSW.write(new String(b));
			oSW.flush();
			oSW.close();

			queue.close();
			qMgr.disconnect();
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}
	}
	*/

	final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static byte[] intToBytes(int x) throws IOException {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    DataOutputStream out = new DataOutputStream(bos);
	    out.writeInt(x);
	    out.close();
	    byte[] int_bytes = bos.toByteArray();
	    bos.close();
	    return int_bytes;
	}
	
	public void readMessage(String qManager, String qResponse, String message) {
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		try {

			qMgr = new MQQueueManager(qManager);
			int openOptions = 17;
			queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQMO_MATCH_MSG_ID;

			msg.messageId = hexStringToByteArray(message); 

			queue.get(msg, gmo);
			msg.clearMessage();
			
			queue.close();
			qMgr.disconnect();
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} 
		catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}
		
	}

	public String displayMessage(String qManager, String qResponse, String message) throws MQException{
		String msgText = new String();
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		try {

			qMgr = new MQQueueManager(qManager);
			//int openOptions = 17;
			int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING | MQConstants.MQOO_INPUT_SHARED | MQConstants.MQOO_BROWSE;  

			queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQMO_MATCH_MSG_ID|MQConstants.MQGMO_BROWSE_HANDLE;
			//msg.characterSet = 1208;//added new godfrey
			msg.messageId = hexStringToByteArray(message); 
			
			queue.get(msg, gmo);
						
			msgText = msg.readString(msg.getMessageLength());
			
			
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} 
		catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}finally{
			queue.close();
			qMgr.disconnect();

			return msgText;
		}
		
	}
	
	
	public String displayMessageHeaders(String qManager, String qResponse, String message) throws MQException{
		String msgText = new String();
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		try {

			qMgr = new MQQueueManager(qManager);
			//int openOptions = 17;
			int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING | MQConstants.MQOO_INPUT_SHARED | MQConstants.MQOO_BROWSE;  

			queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQMO_MATCH_MSG_ID|MQConstants.MQGMO_BROWSE_HANDLE;

			msg.messageId = hexStringToByteArray(message); 
			queue.get(msg, gmo);
			MQMD md = new MQMD();

			md.copyFrom (msg);
			MQHeaderList mqHdrList = new MQHeaderList(msg, true);

			
			
			int intMqHdrList = mqHdrList.size();
			int i=1;
					while (i<intMqHdrList)
					{
						mqHdrList.get(i);
						i++;
					}
					
			System.out.println (new MQHeaderList (msg, true));
			
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} 
		catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}finally{
			queue.close();
			qMgr.disconnect();

			return msgText;
		}
		
	}

	public int purgeQueue(String qManager, String qName) throws MQException {
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		int msgCounter = 0;
		try {

			qMgr = new MQQueueManager(qManager);
			int openOptions = 17;
			queue = qMgr.accessQueue(qName, openOptions);
			MQMessage msg = null;
			MQGetMessageOptions gmo = null;
			//gmo.options = MQConstants.t.TRUNCATED_MSG_ACCEPTED;
			while(true){
				msg = new MQMessage();
				gmo = new MQGetMessageOptions();
				queue.get(msg, gmo);
				msg.clearMessage();
				msgCounter++;
			}
			
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
			return msgCounter;
		} 
		catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		}finally{
			queue.close();
			qMgr.disconnect();
			return msgCounter;
		}
		
	}


	public void deleteDirectory(File file)
	    	throws IOException{
	 
	    	if(file.isDirectory()){
	 
	    		//directory is empty, then delete it
	    		if(file.list().length==0){
	 
	    		   file.delete();
	    		   System.out.println("Directory is deleted : " 
	                                                 + file.getAbsolutePath());
	 
	    		}else{
	 
	    		   //list all the directory contents
	        	   String files[] = file.list();
	 
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	 
	        	      //recursive delete
	        	      deleteDirectory(fileDelete);
	        	   }
	 
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
	        	     System.out.println("Directory is deleted : " 
	                                                  + file.getAbsolutePath());
	        	   }
	    		}
	 
	    	}else{
	    		//if file, then delete it
	    		file.delete();
	    		System.out.println("File is deleted : " + file.getAbsolutePath());
	    	}
	    }
	
	public boolean updateUser(String userList, String userID) throws IOException{
		
		File inputFile = new File(userList);
		File tempFile = new File(userList+"temp");
		tempFile.createNewFile();
		for (String line : FileUtils.readLines(inputFile)) {
			if(!line.equals(userID)){
				FileUtils.writeStringToFile(tempFile, line+"\n", true);
				
			}
		}
		inputFile.delete();
		boolean successful = tempFile.renameTo(inputFile);
		return successful;
		
	}
	
			    
	public ArrayList<String> messageMove(String qMgr,String qPortStr, String qHost, String srcQueueName, String tarQueueName, String msgCnt) throws MQException, IOException{
		int qPort = Integer.parseInt(qPortStr);
		int msgCount = 0;
		if (!msgCnt.equals("all")){
			msgCount = Integer.parseInt(msgCnt);
		}
		
		int openTarOptions = 17;
		
		MQQueueManager queueMgr = null;
		MQQueue srcQName = null;
		MQQueue tarQName = null;
		ArrayList<String> messagesMoved = new ArrayList();
		
		try{
			// Create a connection to the QueueManager
			queueMgr = new MQQueueManager(qMgr);

			// Set up the options on the queue we wish to open
			//int openSrcOptions = MQConstants.MQOO_BROWSE
			//		| MQConstants.MQOO_INPUT_SHARED ;


			int openSrcOptions = MQConstants.MQOO_INQUIRE | MQConstants.MQOO_INPUT_AS_Q_DEF; 
			
			srcQName = queueMgr.accessQueue(srcQueueName, openSrcOptions, null, null, null);
			System.out.println(srcQName.getCurrentDepth());
			if (msgCount==0){
				msgCount=srcQName.getCurrentDepth();
			}
			if (srcQName.getCurrentDepth() < msgCount){
				//messagesMoved.add("Queue depth "+Integer.toString(srcQName.getCurrentDepth())+" - is less than messages requested to be moved -"+Integer.toString(msgCount));
				messagesMoved.add("Queue depth -"+srcQName.getCurrentDepth()+" - is less than messages requested to be moved -"+msgCount);
				
			}else{
				try{
					openSrcOptions = MQConstants.MQOO_FAIL_IF_QUIESCING |MQConstants.MQOO_BROWSE
							| MQConstants.MQOO_INPUT_SHARED;

					// Now specify the queue that we wish to open and the open options
					srcQName = queueMgr.accessQueue(srcQueueName, openSrcOptions, null, null, null);
					tarQName = queueMgr.accessQueue(tarQueueName, openTarOptions);

					MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
					getMessageOptions.options = MQConstants.MQGMO_BROWSE_FIRST | MQConstants.MQGMO_WAIT ;
					getMessageOptions.matchOptions=MQConstants.MQMO_NONE;
					getMessageOptions.waitInterval = 1000;

					MQPutMessageOptions putMessageOptions = new MQPutMessageOptions();
					int iCount = 0;
					String srcMessageStr = new String();
					//read message
					
					while (msgCount > iCount ) {
						
						MQMessage srcMessage = new MQMessage();
						MQMessage tarMessage = new MQMessage();
						
						srcQName.get(srcMessage, getMessageOptions);
						srcMessageStr = srcMessage.readString(srcMessage.getMessageLength());
						
						/*
						qMessages.add(new String(b));
						qMessages.add(byteArrayToHexString(rcvMessage.messageId));
						//qMessages.add(new String(rcvMessage.messageId));
						qMessages.add(dateFormat.format(rcvMessage.putDateTime.getTime()));
						*/
						messagesMoved.add(srcMessageStr);
						tarMessage.writeString(srcMessageStr);
						tarQName.put(tarMessage, putMessageOptions);

						getMessageOptions.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_MSG_UNDER_CURSOR;
						srcQName.get(srcMessage, getMessageOptions);

						getMessageOptions.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_BROWSE_NEXT;
						iCount++;
						
					}
					
				}catch(Exception e){
					tarQName.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			srcQName.close();
			queueMgr.disconnect();
		}
		return messagesMoved;
		
	}

		public ArrayList<String> ListSyslog(String hostName, String tailCount,String month ,String day ,String hour, boolean realTime) throws IOException {
			
			BASE64Decoder decoder = new BASE64Decoder();
			
			final String HOST = hostName;
			final String USER = "menezesg";
			final int PORT = 22;
			final String PASS = new String(decoder.decodeBuffer("cGEzM3dvcmQ="));
			System.out.println(USER+" "+HOST+" "+PORT+" "+PASS);										             

			Channel channel = null;
			Session session = null;
			ArrayList<String> logList = new ArrayList(); 
			try {
				JSch jsch = new JSch();
				
				session = jsch.getSession(USER, HOST, PORT);
				session.setPassword(PASS);
				
				
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				config.put("PreferredAuthentications", "password");
				session.setConfig(config);
				session.connect();
				String command = new String();
				if(tailCount.length()!=0){
					command=new String("tail -"+tailCount+" /var/mqsi/syslog/syslog.user");
				}else if(month!=null&&day!=null&&hour!=null){
					command=new String("tail -"+tailCount+" /var/mqsi/syslog/syslog.user |grep '"+month+" "+day+" "+hour+"'");
				}else{
					command=new String("tail -"+100+" /var/mqsi/syslog/syslog.user");
				}
				channel = session.openChannel("exec");
				((ChannelExec)channel).setCommand(command);
				channel.setInputStream(null);
				InputStream in=channel.getInputStream();
				channel.connect();
				StringBuffer sbOld = new StringBuffer("");
				StringBuffer sbNew = new StringBuffer("");
			      byte[] tmp=new byte[1024];
			      while(true){
			        while(in.available()>0){
			          int i=in.read(tmp, 0, 1024);
			          sbNew.append(new String(tmp, 0, i));
			          while(sbNew.indexOf("\n")>0){
			        	  sbOld = new StringBuffer(sbNew.substring(0,sbNew.indexOf("\n")));
			        	  logList.add(sbOld.toString());

			        	  sbNew = new StringBuffer(sbNew.substring(sbNew.indexOf("\n")+1, sbNew.length()));
			          }
			          if(i<0)break;
			          
			        }
			        if(channel.isClosed()){
			        	
			          break;
			        }
			        try{Thread.sleep(1000);}catch(Exception ee){}
			      }
			      
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				channel.disconnect();
				session.disconnect();
				return logList;
			}

		}
		

		public String WriteMsgsToQueue(String QMgr, String qName,String month ,String day ,String hour, boolean realTime) throws IOException {
			
			MQQueueManager qMgr = null;
			MQQueue queue = null;
			MQMessage msg = new MQMessage();
			MQPoolToken token=MQEnvironment.addConnectionPoolToken();
			StringBuffer qMsgs = null;
			String qmessage = new String();
			String interimMsg = new String();

			
			File outfile = new File(System.getProperty("catalina.base")+File.separator+"upload.txt");
			try{
				String returnMsg = FileUtils.readFileToString(outfile);
				qMgr = new MQQueueManager(QMgr);
				int openOptions = 17;
				queue = qMgr.accessQueue(qName, openOptions);
				
				MQPutMessageOptions pmo = new MQPutMessageOptions();
				
				while(returnMsg.length()>0){
					qmessage = returnMsg.substring(0, returnMsg.indexOf("<EOFMessage>")); 
					System.out.println(qmessage);
					msg.writeString(qmessage);
					queue.put(msg, pmo);
					interimMsg = returnMsg.substring(qmessage.length()+12, returnMsg.length());
					returnMsg = interimMsg;
				}
				
			}catch (MQException ex) {
				if (ex.completionCode == 2 && ex.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) 
					System.out.println("No more messages ");
				 else 
					System.out
							.println("A WebSphere MQ Error occured : Completion Code "
									+ ex.completionCode
									+ " Reason Code "
									+ ex.reasonCode);
					
				
			} finally {
				try {
					System.out.println("Closing the queue");
					queue.close();
					System.out.println("Disconnecting from the Queue Manager");
					qMgr.disconnect();
					System.out.println("Done!");
				} catch (Exception e) {
					System.out.println("Error in finally!");
					e.printStackTrace();
				}
			}
			MQEnvironment.removeConnectionPoolToken(token);
			
			return "hello";
			
		}


}
