package com.ibm.MQAdmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;

import com.ibm.broker.config.proxy.ApplicationProxy;
import com.ibm.broker.config.proxy.AttributeConstants;
import com.ibm.broker.config.proxy.BrokerConnectionParameters;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyException;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.DeployResult;
import com.ibm.broker.config.proxy.DeployedObject;
import com.ibm.broker.config.proxy.ExecutionGroupProxy;
import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;
import com.ibm.broker.config.proxy.MessageFlowProxy;

public class MBCommons {

	public BrokerProxy getBrokerProxy(String hostName, int portNum)
			throws ConfigManagerProxyException {
		BrokerProxy brkProxy = null;

		BrokerConnectionParameters brkConnParam = new MQBrokerConnectionParameters(
				hostName, portNum, "");

		brkProxy = BrokerProxy.getInstance(brkConnParam);

		return brkProxy;
	}

	public String displayMessageApplnRunState(BrokerProxy brkProxy, String egName,
			String appName) {
		String returnStat = new String();
		try {
			ExecutionGroupProxy egProxy = brkProxy.getExecutionGroupByName(egName);

			if (egProxy != null) {
				ApplicationProxy applnProxy = egProxy.getApplicationByName(appName);

				if (applnProxy != null) {
					boolean isRunning = applnProxy.isRunning();
					returnStat = "Application:" + appName
							+ " on Execution Group:" + egName + " on Broker:"
							+ brkProxy.getName() + " is ";

					if (isRunning) {
						returnStat = returnStat + "running";
					} else {
						returnStat = returnStat + "stopped";
					}
				} else {
					returnStat = "No such flow " + appName;
				}
			} else {
				returnStat = "No such exegrp " + egName + "!";
			}

		} catch (ConfigManagerProxyPropertyNotInitializedException ex) {
			System.err.println("Comms problem! " + ex);
		}

		return returnStat;
	}

	public String displayMessageFlowRunState(BrokerProxy brkProxy, String egName,
			String flowName) {
		String returnStat = new String();
		try {
			ExecutionGroupProxy egProxy = brkProxy.getExecutionGroupByName(egName);

			if (egProxy != null) {
				MessageFlowProxy mfProxy = egProxy.getMessageFlowByName(flowName);

				if (mfProxy != null) {
					boolean isRunning = mfProxy.isRunning();
					returnStat = "Flow " + flowName + " on " + egName + " on "
							+ brkProxy.getName() + " is ";

					if (isRunning) {
						returnStat = "running";
					} else {
						returnStat = "stopped";
					}
				} else {
					returnStat = "No such flow " + flowName;
				}
			} else {
				returnStat = "No such exegrp " + egName + "!";
			}

		} catch (ConfigManagerProxyPropertyNotInitializedException ex) {
			System.err.println("Comms problem! " + ex);
		}

		return returnStat;
	}

	public ArrayList getExecutionGroupDetails(BrokerProxy brkProxy, String egName) {
		ArrayList<ApplicationProxy> egListDetails = null;
		try {
			ExecutionGroupProxy egProxy = brkProxy.getExecutionGroupByName(egName);
			egListDetails = Collections.list(egProxy.getApplications(null));

		} catch (ConfigManagerProxyPropertyNotInitializedException ex) {
			System.err.println("Comms problem! " + ex);
		}

		return egListDetails;
	}

	public String getApplicationDetails(MessageFlowProxy mfProxy) {
		String mfName = null;
		try {
			mfName = mfProxy.getName();
		} catch (ConfigManagerProxyPropertyNotInitializedException ex) {
			System.err.println("Comms problem! " + ex);
		}
		return mfName;
	}

	public ArrayList getMFDetails(BrokerProxy brkProxy, String egName) {
		ArrayList<MessageFlowProxy> mfListDetails = null;
		try {
			ExecutionGroupProxy egProxy = brkProxy.getExecutionGroupByName(egName);
			mfListDetails = Collections.list(egProxy.getMessageFlows(null));

		} catch (ConfigManagerProxyPropertyNotInitializedException ex) {
			System.err.println("Comms problem! " + ex);
		}

		return mfListDetails;
	}

	public String deployBARFileToEG(BrokerProxy brkProxy, String egName,
			String barFileName) {
		DeployResult dr = null;

		try {
			ExecutionGroupProxy egProxy = brkProxy.getExecutionGroupByName(egName);
			dr = egProxy.deploy(barFileName, true, 30000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(dr);

		return dr.toString();
	}

	public String getBrkParameters(String brkName)
			throws ConfigManagerProxyLoggedException, NumberFormatException,
			IOException {

		File brkFile = new File(System.getProperty("catalina.base")
				+ File.separator + "ESBAdmin" + File.separator + "MBEnv.txt");

		String brkParameters = null;
		for (String line : FileUtils.readLines(brkFile)) {
			if (line.indexOf(brkName) > -1) {
				brkParameters = line.substring(line.indexOf("|") + 1,
						line.length());
				break;
			}
		}
		return brkParameters;
	}

	/**
	 * Asks the broker to start the execution group's process.
	 * 
	 * @param eg
	 *            Selected ExecutionGroupProxy object
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws ConfigManagerProxyPropertyNotInitializedException
	 */
	public String StartEG(String brkName, String egName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");

			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);

			egProxy.start();
			brkProxy.disconnect();
			return "EG Started successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error while starting the eg";
		}

	}

	/**
	 * Asks the broker to stop the execution group's process.
	 * 
	 * @param eg
	 *            Selected ExecutionGroupProxy object
	 */
	public String StopEG(String brkName, String egName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");

			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);

			egProxy.stop();
			brkProxy.disconnect();
			return "EG Started successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error while starting the eg";
		}

	}

	public String DeleteEG(String brkName, String egName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");

			brkProxy = BrokerProxy.getInstance(bcp);
			brkProxy.deleteExecutionGroup(egName);
			brkProxy.disconnect();
			return "EG Started successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error while starting the eg";
		}

	}

	/**
	 * Asks the broker to start the application.
	 * 
	 * @param appl
	 *            Selected ApplicationProxy object
	 */
	public String StartApplication(String brkName, String egName,
			String applName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");

			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			ApplicationProxy applnProxy = egProxy.getApplicationByName(applName);
			applnProxy.start();
			brkProxy.disconnect();
			return "Application Started successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error occured while starting";
		}
	}

	/**
	 * Asks the broker to stop the application.
	 * 
	 * @param appl
	 *            Selected ApplicationProxy object
	 */
	public String StopApplication(String brkName, String egName, String applName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");

			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			ApplicationProxy applnProxy = egProxy.getApplicationByName(applName);
			applnProxy.stop();
			brkProxy.disconnect();
			return "Application Stopped successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error occured while stopping";
		}

	}

	/**
	 * Gives a quick test of the Administration API's start message flows
	 * command.
	 * 
	 * @param object
	 *            The Broker, Execution Group or flow whose flow(s) are to be
	 *            started.
	 */
	public String StartMsgFlow(String brkName, String egName, String mfName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");
			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			MessageFlowProxy mfProxy = egProxy.getMessageFlowByName(mfName);
			mfProxy.start();
			brkProxy.disconnect();
			return "MessageFlow Started successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error occured while starting";
		}

	}

	/**
	 * Gives a quick test of the Administration API's stop message flows
	 * methods.
	 * 
	 * @param object
	 *            The broker, execution group or message flow whose flows are to
	 *            be stopped.
	 */
	public String StopMsgFlow(String brkName, String egName, String mfName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");
			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			MessageFlowProxy mfProxy = egProxy.getMessageFlowByName(mfName);
			mfProxy.stop();
			brkProxy.disconnect();
			return "MessageFlow Stopped successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error occured while stopping";
		}

	}

	public String DeleteEGObject(String brkName, String egName, String objName) {
		BrokerProxy brkProxy = null;

		try {

			String brkParameters = getBrkParameters(brkName);
			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");
			brkProxy = BrokerProxy.getInstance(bcp);

			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			System.out.println("eggroup" + egProxy);

			egProxy.deleteDeployedObjectsByName(new String[] { objName }, 0);
			brkProxy.disconnect();
			return "MessageFlow Stopped successfully";
		} catch (Exception e) {
			e.printStackTrace();
			brkProxy.disconnect();
			return "Error occured while stopping";
		}

	}

	/**
	 * Gives a quick test of the Administration API's start message flows
	 * command.
	 * 
	 * @param object
	 *            The Broker, Execution Group or flow whose flow(s) are to be
	 *            started.
	 */
	public String StartEGAll(String brkName, String egName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");
			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			egProxy.startApplications();
			egProxy.startMessageFlows();

			brkProxy.disconnect();
			return "Application and Message Flows started Successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error occured while starting Application and Message Flows ";
		}

	}

	/**
	 * Gives a quick test of the Administration API's stop message flows
	 * methods.
	 * 
	 * @param object
	 *            The broker, execution group or message flow whose flows are to
	 *            be stopped.
	 */
	public String StopEGAll(String brkName, String egName) {
		BrokerProxy brkProxy = null;
		try {
			String brkParameters = getBrkParameters(brkName);

			String hostName = brkParameters.substring(
					brkParameters.indexOf("|") + 1, brkParameters.indexOf(":"));
			int portNum = Integer.parseInt(brkParameters.substring(
					brkParameters.indexOf(":") + 1, brkParameters.length()));

			BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
					hostName, portNum, "");
			brkProxy = BrokerProxy.getInstance(bcp);
			ExecutionGroupProxy egProxy = brkProxy
					.getExecutionGroupByName(egName);
			egProxy.stopApplications();
			egProxy.stopMessageFlows();

			brkProxy.disconnect();
			return "Application and Message Flows stopped Successfully";
		} catch (Exception e) {
			brkProxy.disconnect();
			return "Error occured while stopping Application and Message Flows ";
		}

	}

}
