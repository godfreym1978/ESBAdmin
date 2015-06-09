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

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DownloadMsgsFromQueue")
public class DownloadMsgsFromQueue extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadMsgsFromQueue() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// ServletContext ctx = getServletContext();
		java.util.Date date = new java.util.Date();
		Timestamp currentTimestamp = new Timestamp(date.getTime());

		String qName = request.getParameter("QName");

		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ qName + "-" + currentTimestamp);

		MQQueueManager qMgr = null;
		MQQueue queue = null;
		MQPoolToken token = MQEnvironment.addConnectionPoolToken();
		StringBuffer qMsgs = null;

		try {
			qMgr = new MQQueueManager(request.getParameter("QMgr").toString());
			// Set up the options on the queue we wish to open
			int openOptions = MQConstants.MQOO_BROWSE
					| MQConstants.MQOO_INPUT_SHARED;

			// Now specify the queue that we wish to open and the open options
			queue = qMgr.accessQueue(qName, openOptions, null, null, null);
			MQGetMessageOptions getMessageOptions = new MQGetMessageOptions();
			getMessageOptions.options = MQConstants.MQGMO_BROWSE_FIRST
					| MQConstants.MQGMO_WAIT;

			getMessageOptions.waitInterval = 1000;
			// Get the message off the queue.
			// int iCount = 0;
			byte[] b = null;
			qMsgs = new StringBuffer();
			while (true) {
				// iCount++;
				MQMessage rcvMessage = new MQMessage();

				queue.get(rcvMessage, getMessageOptions);
				b = new byte[rcvMessage.getMessageLength()];
				rcvMessage.readFully(b);

				String msgText = new String(b);
				if (msgText.trim().equals(""))
					break;
				qMsgs.append(new String(b));
				qMsgs.append("<EOFMessage>");
				getMessageOptions.options = MQConstants.MQGMO_BROWSE_NEXT;
				b = null;

			}
			System.out.println("Downloading Msgs for Queue - " + qName);
			System.out.println(currentTimestamp);

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
		OutputStream outStream = response.getOutputStream();
		// os.write(data.getBytes());
		outStream.write(qMsgs.toString().getBytes());
		outStream.flush();
		outStream.close();
		System.gc();
	}

}
