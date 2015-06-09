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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.mq.MQException;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DownloadMsgFromQueue")
public class DownloadMsgFromQueue extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadMsgFromQueue() {
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
		String qMgr = request.getParameter("qMgr");
		String qName = request.getParameter("qName");
		String message = new String(request.getParameter("message"));

		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ qName + "-" + message);

		Util newUtil = new Util();
		String data = new String();
		/*
		 * System.out.println(message);
		 * System.out.println(message.getBytes("US-ASCII").length);
		 * System.out.println(newUtil.byteArrayToHexString(message.getBytes()));
		 */
		try {
			data = newUtil.displayMessage(qMgr, qName, message);
		} catch (MQException e) {
			System.out.println("Error in download of data");
		}

		OutputStream outStream = response.getOutputStream();
		outStream.write(data.getBytes());
		outStream.flush();
		outStream.close();
		System.gc();
	}

}
