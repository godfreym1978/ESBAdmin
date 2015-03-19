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

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;


/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/LoadMsgsToQueue")
public class LoadMsgsToQueue extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadMsgsToQueue() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
		// TODO Auto-generated method stub
		//ServletContext ctx = getServletContext();
		
		/*
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition",
	                     "attachment;filename="+qName+"-"+currentTimestamp);
		*/
		int msgCounter = 0;
		Util newUtil = new Util();

		/*New addition*/
		// Create a new file upload handler 
		DiskFileUpload upload = new DiskFileUpload();

		/*end*/
		
		MQQueueManager qMgr = null;
		MQQueue queue = null;
		MQMessage msg = new MQMessage();
		MQPoolToken token=MQEnvironment.addConnectionPoolToken();
		StringBuffer qMsgs = null;
		String qmessage = new String();
		String interimMsg = new String();

		//get qName
		String qName = new String();
		String QMgr = new String();
		
		try{
			// parse request
			List items = upload.parseRequest(request);

			qName = request.getParameter("QName").toString();
			QMgr = request.getParameter("QMgr").toString();
			
			//get uploaded file 
			FileItem file = (FileItem) items.get(0);
			String source = file.getName();

			File outfile = new File(System.getProperty("catalina.base")+File.separator+"upload.txt");
			try{
				file.write(outfile);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			String returnMsg = FileUtils.readFileToString(outfile);
			qMgr = new MQQueueManager(QMgr);
			int openOptions = 17;
			queue = qMgr.accessQueue(qName, openOptions);
			
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			
			while(returnMsg.length()>0){
				qmessage = returnMsg.substring(0, returnMsg.indexOf("<EOFMessage>")); 
				msg.writeString(qmessage);
				queue.put(msg, pmo);
				msgCounter++;
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
				
			
		} catch (java.io.IOException ex) {
			System.out
					.println("An IOException occured while reading to the message buffer: "
							+ ex);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		request.setAttribute("msgCounter", Integer.toString(msgCounter));
        request.setAttribute("qName", qName);
        request.setAttribute("qMgr", QMgr);
        
        request.getRequestDispatcher("MQAdm/MQWrites.jsp").forward(request, response);

	}

}
