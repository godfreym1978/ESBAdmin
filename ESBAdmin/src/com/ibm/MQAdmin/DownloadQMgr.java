package com.ibm.MQAdmin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DownloadQMgr")
public class DownloadQMgr extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadQMgr() {
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
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ request.getParameter("qMgr") + ".mqsc");

		OutputStream outStream = response.getOutputStream();
		byte[] qMgrBytes;
		HttpSession httpSession = request.getSession(true);

		String UserID = httpSession.getAttribute("UserID").toString();
		File userQMFile = new File(
						System.getProperty("catalina.base")
								+ File.separator+"ESBAdmin"+File.separator+UserID+File.separator+"QMEnv.txt");
		String qMgr = httpSession.getAttribute(request.getParameter("qMgr"))
				.toString();
		String qPort = null;
		String qHost = null;
		String qChannel = null;

		for (String line : FileUtils.readLines(userQMFile)) {
			if (line.indexOf(qMgr)>0){
				CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
				
				for (CSVRecord csvRecord : parser) {
					qHost = csvRecord.get(0);
					qPort = csvRecord.get(2);
					qChannel = csvRecord.get(3);
					}							
			}
		}

		try {
			PCFCommons pcfCM = new PCFCommons();

			List<Map> ListQueueNames = pcfCM.ListQueueNamesDtl(qHost, Integer.parseInt(qPort), qChannel);

			for (int i = 0; i < ListQueueNames.size(); i++) {
				qMgrBytes = String.valueOf(
						pcfCM.createQScript(qHost, Integer.parseInt(qPort),
								ListQueueNames.get(i).get("MQCA_Q_NAME")
										.toString().trim(), qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			List<Map> listChannels = pcfCM.channelDetails(qHost, Integer.parseInt(qPort),qChannel);

			for (int i = 0; i < listChannels.size(); i++) {
				qMgrBytes = String.valueOf(
						pcfCM.createChlScript(qHost, Integer.parseInt(qPort),
								listChannels.get(i).get("MQCACH_CHANNEL_NAME")
										.toString().trim(),qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			List<Map> listListener = pcfCM.listenerDetails(qHost, Integer.parseInt(qPort),qChannel);

			for (int i = 0; i < listListener.size(); i++) {
				qMgrBytes = String.valueOf(
						pcfCM.createListScript(qHost, Integer.parseInt(qPort), listListener
								.get(i).get("MQCACH_LISTENER_NAME").toString()
								.trim(),qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			List<Map> listTopic = pcfCM.ListTopicNames(qHost, Integer.parseInt(qPort),qChannel);

			for (int i = 0; i < listTopic.size(); i++) {
				qMgrBytes = String.valueOf(
						pcfCM.createTopicScript(qHost, Integer.parseInt(qPort), listTopic
								.get(i).get("MQCA_TOPIC_NAME").toString()
								.trim(),qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			List<Map> listSubs = pcfCM.ListSubNames(qHost, Integer.parseInt(qPort),qChannel);

			for (int i = 0; i < listSubs.size(); i++) {
				qMgrBytes = String.valueOf(
						pcfCM.createSubScript(qHost, Integer.parseInt(qPort), listSubs.get(i)
								.get("MQCACF_SUB_NAME").toString().trim(),qChannel))
						.getBytes();
				outStream.write(qMgrBytes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		outStream.flush();
		outStream.close();
		System.gc();
	}

}
