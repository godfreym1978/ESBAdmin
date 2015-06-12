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

package com.ibm.ESBAdmin;

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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class MQAdminUtil {

	static final char CARRIAGE_RETURN = 13;
	static final String fileSep = File.separator;
	
	Util newUtil = new Util();
	public int getDepth(String queueName, int port, String hostName,
			String queueMgr, String qChannel) throws MQException {
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

		return queueDepth;// queueDepth;
	}

	public List<Map> getDepthAll(ArrayList qList, int port, String hostName,
			String queueMgr, String qChannel) throws MQException {
		// Build quemanager (this should be done in another method)
		// and not every time in a real life application

		MQEnvironment.channel = qChannel;
		MQEnvironment.port = port;
		MQEnvironment.hostname = hostName;
		MQQueueManager qmgr = new MQQueueManager(queueMgr);
		List<Map> qDepthList = new ArrayList<Map>();
		Map iMap = new HashMap();

		try {
			// access the queue to query its depth
			String qName = new String();
			ArrayList qDepth = new ArrayList();
			for (int i = 0; i < qList.size(); i++) {
				qName = qList.get(i).toString();
				iMap = new HashMap<>();
				com.ibm.mq.MQQueue queue = qmgr.accessQueue(qList.get(i)
						.toString(), MQConstants.MQOO_INQUIRE
						| MQConstants.MQOO_INPUT_AS_Q_DEF, null, null, null);
				iMap.put(qList.get(i).toString(), queue.getCurrentDepth());

				// queue.close();
				qDepthList.add(iMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qmgr.disconnect();
			return qDepthList;// queueDepth;

		}
	}

	public String DisplayRFH2(MQMessage gotMessage) throws IOException {
		MQHeaderIterator it = new MQHeaderIterator(gotMessage);
		while (it.hasNext()) {
			MQHeader header = it.nextHeader();

			System.out.println("Header type " + header.type() + ": " + header);
		}

		String headers = new String();
		return headers;
	};

	public ArrayList browseQueue(String queueMgr, String queueName) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"MM.dd.yyyy HH:mm:ss");
		ArrayList<String> qMessages = new ArrayList<String>();
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		MQPoolToken token = MQEnvironment.addConnectionPoolToken();
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
				if (rcvMessage.getMessageLength() > 200) {
					b = new byte[200];
					rcvMessage.readFully(b, 0, 199);
				} else {
					b = new byte[rcvMessage.getMessageLength()];
					rcvMessage.readFully(b);
				}

				String msgText = new String(b);
				if (msgText.trim().equals(""))
					break;
				qMessages.add(new String(b));
				qMessages.add(newUtil.byteArrayToHexString(rcvMessage.messageId));
				// qMessages.add(new String(rcvMessage.messageId));
				qMessages.add(dateFormat.format(rcvMessage.putDateTime
						.getTime()));
				getMessageOptions.options = MQConstants.MQGMO_BROWSE_NEXT;
				DisplayRFH2(rcvMessage);
				b = null;
			}
		} catch (MQException ex) {
			if (ex.completionCode == 2
					&& ex.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE)
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
		} finally {
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
					null, // no dynamic q name
					null);
			return true;
		} catch (MQException e) {
			e.printStackTrace();
			return false;
		}
	}

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
			msg.messageId = newUtil.hexStringToByteArray(message);
			queue.get(msg, gmo);
			msg.clearMessage();
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

	public String displayMessage(String qManager, String qResponse,
			String message) throws MQException {
		String msgText = new String();
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		try {
			qMgr = new MQQueueManager(qManager);
			int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING
					| MQConstants.MQOO_INPUT_SHARED | MQConstants.MQOO_BROWSE;
			queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQMO_MATCH_MSG_ID
					| MQConstants.MQGMO_BROWSE_HANDLE;
			// msg.characterSet = 1208;//added new godfrey
			msg.messageId = newUtil.hexStringToByteArray(message);
			queue.get(msg, gmo);
			msgText = msg.readString(msg.getMessageLength());
		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		} finally {
			queue.close();
			qMgr.disconnect();
			return msgText;
		}
	}

	public String displayMessageHeaders(String qManager, String qResponse,
			String message) throws MQException {
		String msgText = new String();
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		try {
			qMgr = new MQQueueManager(qManager);
			int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING
					| MQConstants.MQOO_INPUT_SHARED | MQConstants.MQOO_BROWSE;
			queue = qMgr.accessQueue(qResponse, openOptions);
			MQMessage msg = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQMO_MATCH_MSG_ID
					| MQConstants.MQGMO_BROWSE_HANDLE;
			msg.messageId = newUtil.hexStringToByteArray(message);
			queue.get(msg, gmo);
			MQMD md = new MQMD();
			md.copyFrom(msg);
			MQHeaderList mqHdrList = new MQHeaderList(msg, true);

			int intMqHdrList = mqHdrList.size();
			int i = 1;
			while (i < intMqHdrList) {
				mqHdrList.get(i);
				i++;
			}

		} catch (MQException ex) {
			System.out
					.println("A WebSphere MQ Error occured : Completion Code "
							+ ex.completionCode + " Reason Code "
							+ ex.reasonCode);
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		} finally {
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
			while (true) {
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
		} catch (IOException ex) {
			System.out
					.println("An IOException occured whilst writing to the message buffer: "
							+ ex);
		} finally {
			queue.close();
			qMgr.disconnect();
			return msgCounter;
		}
	}

	public ArrayList<String> messageMove(String qMgr, String qPortStr,
			String qHost, String srcQueueName, String tarQueueName,
			String msgCnt) throws MQException, IOException {
		int qPort = Integer.parseInt(qPortStr);
		int msgCount = 0;
		if (!msgCnt.equals("all")) {
			msgCount = Integer.parseInt(msgCnt);
		}
		int openTarOptions = 17;
		MQQueueManager queueMgr = null;
		MQQueue srcQName = null;
		MQQueue tarQName = null;
		ArrayList<String> messagesMoved = new ArrayList();
		try {
			// Create a connection to the QueueManager
			queueMgr = new MQQueueManager(qMgr);

			int openSrcOptions = MQConstants.MQOO_INQUIRE
					| MQConstants.MQOO_INPUT_AS_Q_DEF;

			srcQName = queueMgr.accessQueue(srcQueueName, openSrcOptions, null,
					null, null);
			System.out.println(srcQName.getCurrentDepth());
			if (msgCount == 0) {
				msgCount = srcQName.getCurrentDepth();
			}
			if (srcQName.getCurrentDepth() < msgCount) {
				// messagesMoved.add("Queue depth "+Integer.toString(srcQName.getCurrentDepth())+" - is less than messages requested to be moved -"+Integer.toString(msgCount));
				messagesMoved.add("Queue depth -" + srcQName.getCurrentDepth()
						+ " - is less than messages requested to be moved -"
						+ msgCount);

			} else {
				try {
					openSrcOptions = MQConstants.MQOO_FAIL_IF_QUIESCING
							| MQConstants.MQOO_BROWSE
							| MQConstants.MQOO_INPUT_SHARED;

					// Now specify the queue that we wish to open and the open
					// options
					srcQName = queueMgr.accessQueue(srcQueueName,
							openSrcOptions, null, null, null);
					tarQName = queueMgr.accessQueue(tarQueueName,
							openTarOptions);

					MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
					getMessageOptions.options = MQConstants.MQGMO_BROWSE_FIRST
							| MQConstants.MQGMO_WAIT;
					getMessageOptions.matchOptions = MQConstants.MQMO_NONE;
					getMessageOptions.waitInterval = 1000;

					MQPutMessageOptions putMessageOptions = new MQPutMessageOptions();
					int iCount = 0;
					String srcMessageStr = new String();
					// read message

					while (msgCount > iCount) {

						MQMessage srcMessage = new MQMessage();
						MQMessage tarMessage = new MQMessage();

						srcQName.get(srcMessage, getMessageOptions);
						srcMessageStr = srcMessage.readString(srcMessage
								.getMessageLength());

						messagesMoved.add(srcMessageStr);
						tarMessage.writeString(srcMessageStr);
						tarQName.put(tarMessage, putMessageOptions);

						getMessageOptions.options = MQConstants.MQGMO_WAIT
								| MQConstants.MQGMO_MSG_UNDER_CURSOR;
						srcQName.get(srcMessage, getMessageOptions);

						getMessageOptions.options = MQConstants.MQGMO_WAIT
								| MQConstants.MQGMO_BROWSE_NEXT;
						iCount++;
					}
				} catch (Exception e) {
					tarQName.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			srcQName.close();
			queueMgr.disconnect();
		}
		return messagesMoved;
	}

	public String WriteMsgsToQueue(String QMgr, String qName, String month,
			String day, String hour, boolean realTime) throws IOException {

		MQQueueManager qMgr = null;
		MQQueue queue = null;
		MQMessage msg = new MQMessage();
		MQPoolToken token = MQEnvironment.addConnectionPoolToken();
		StringBuffer qMsgs = null;
		String qmessage = new String();
		String interimMsg = new String();

		File outfile = new File(System.getProperty("catalina.base")
				+ File.separator + "upload.txt");
		try {
			String returnMsg = FileUtils.readFileToString(outfile);
			qMgr = new MQQueueManager(QMgr);
			int openOptions = 17;
			queue = qMgr.accessQueue(qName, openOptions);

			MQPutMessageOptions pmo = new MQPutMessageOptions();

			while (returnMsg.length() > 0) {
				qmessage = returnMsg.substring(0,
						returnMsg.indexOf("<EOFMessage>"));
				System.out.println(qmessage);
				msg.writeString(qmessage);
				queue.put(msg, pmo);
				interimMsg = returnMsg.substring(qmessage.length() + 12,
						returnMsg.length());
				returnMsg = interimMsg;
			}

		} catch (MQException ex) {
			if (ex.completionCode == 2
					&& ex.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE)
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
	
	public List<Map> getQMEnv(String userID) throws XMLStreamException, IOException {
		List<Map> QMListDtl = new ArrayList<Map>();
		try{
			Map iMap = new HashMap();
			String tagContent = null;
			File xmlFile = new File(System.getProperty("catalina.base")
					+ File.separator + "ESBAdmin" + File.separator + userID + File.separator +   
					"MQEnvironment.xml");

			InputStream in = new FileInputStream(xmlFile);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(in);

			while (reader.hasNext()) {
				int event = reader.next();
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					if ("QueueManager".equals(reader.getLocalName())) {
						iMap = new HashMap();
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					tagContent = reader.getText().trim();
					break;
				case XMLStreamConstants.END_ELEMENT:
					switch (reader.getLocalName()) {
					case "QueueManager":
						QMListDtl.add(iMap);
						break;
					case "QMChannel":
						iMap.put("QMChannel",tagContent);
						break;
					case "QMName":
						iMap.put("QMName",tagContent);
						break;
					case "QMHost":
						iMap.put("QMHost",tagContent);
						break;
					case "QMPort":
						iMap.put("QMPort",tagContent);
						break;
					}
					break;
				case XMLStreamConstants.START_DOCUMENT:
					QMListDtl = new ArrayList<Map>();
					break;
				}
			}
			in.close();
		}catch(XMLStreamException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return QMListDtl;
	}
}
