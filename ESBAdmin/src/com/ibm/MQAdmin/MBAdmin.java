package com.ibm.MQAdmin;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/MBAdmin")
public class MBAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MBAdmin() {
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
		try {
			MBCommons newMBComm = new MBCommons();
			String action = request.getParameter("action").toString();
			HttpSession session = request.getSession(true);
			File userFile = new File(System.getProperty("catalina.base")
					+ File.separator + "ESBAdmin" + File.separator
					+ session.getAttribute("UserID").toString()
					+ File.separator + "MBEnv.txt");
			String env = null;

			for (String line : FileUtils.readLines(userFile)) {
				env = line.substring(0, line.indexOf(";"));
				break;
			}

			if (session.getAttribute("UserID").toString().indexOf("admin") > -1
					|| (env.equals("DEV") || env.equals("QA"))
					&& session.getAttribute("UserID").toString().indexOf("dev") > -1) {
				if (action.indexOf("EG") == 0) {
					String brkName = request.getParameter("brkName").toString();
					String egName = request.getParameter("egName").toString();
					if (action.indexOf("start") > 0) {
						System.out.println("Starting Execution Group - "
								+ egName + " /Broker - " + brkName);
						newMBComm.StartEG(brkName, egName);
					} else if (action.indexOf("stop") > 0) {
						System.out.println("Stopping Execution Group - "
								+ egName + " /Broker - " + brkName);
						newMBComm.StopEG(brkName, egName);
					} else {
						System.out.println("Deleting Execution Group - "
								+ egName + " /Broker - " + brkName);
						newMBComm.DeleteEG(brkName, egName);
					}
				} else if (action.indexOf("MF") == 0) {
					String brkName = request.getParameter("brkName").toString();
					String egName = request.getParameter("egName").toString();
					String mfName = request.getParameter("mfName").toString();
					if (action.indexOf("start") > 0) {
						System.out.println("Starting MF - " + mfName
								+ "/ Execution Group - " + egName
								+ " /Broker - " + brkName);
						newMBComm.StartMsgFlow(brkName, egName, mfName);
					} else if (action.indexOf("stop") > 0) {
						System.out.println("Stopping MF - " + mfName
								+ "/ Execution Group - " + egName
								+ " /Broker - " + brkName);
						newMBComm.StopMsgFlow(brkName, egName, mfName);
					} else {
						System.out.println("Deleting MF - " + mfName
								+ "/ Execution Group - " + egName
								+ " /Broker - " + brkName);
						newMBComm.DeleteEGObject(brkName, egName, mfName);
					}
				} else if (action.indexOf("APPL") == 0) {

					String brkName = request.getParameter("brkName").toString();
					String egName = request.getParameter("egName").toString();
					String applName = request.getParameter("applName")
							.toString();
					if (action.indexOf("start") > 0) {
						System.out.println("Starting Application - " + applName
								+ "/ Execution Group - " + egName
								+ " /Broker - " + brkName);
						newMBComm.StartApplication(brkName, egName, applName);
					} else if (action.indexOf("stop") > 0) {
						System.out.println("Stopping Application - " + applName
								+ "/ Execution Group - " + egName
								+ " /Broker - " + brkName);
						newMBComm.StopApplication(brkName, egName, applName);
					} else {
						System.out.println("Deleting Application - " + applName
								+ "/ Execution Group - " + egName
								+ " /Broker - " + brkName);
						newMBComm.DeleteEGObject(brkName, egName, applName);
					}
				} else if (action.indexOf("LIB") == 0) {

					String brkName = request.getParameter("brkName").toString();
					String egName = request.getParameter("egName").toString();
					String libName = request.getParameter("libName").toString();
					System.out.println("Deleting Library - " + libName
							+ "/ Execution Group - " + egName + " /Broker - "
							+ brkName);
					newMBComm.DeleteEGObject(brkName, egName, libName);
				}

			}

			// response.sendRedirect("/ESBAdmin/MBAdm/MBEnvAdm.jsp");
			response.sendRedirect(request.getHeader("referer"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
