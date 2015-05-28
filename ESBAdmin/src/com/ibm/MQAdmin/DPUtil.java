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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.ibm.datapower.wamt.StringCollection;
import com.ibm.datapower.wamt.amp.ReferencedObjectCollection;
import com.ibm.datapower.wamt.clientAPI.Device;
import com.ibm.datapower.wamt.clientAPI.ManagedSet;
import com.ibm.datapower.wamt.clientAPI.Manager;
import com.ibm.datapower.wamt.clientAPI.ProgressContainer;
import com.ibm.datapower.wamt.clientAPI.RuntimeService;
import com.ibm.mq.constants.MQConstants;

public class DPUtil {

	public void registerDevice(String symbolicName, String hostName,
			String userID, String password, int hlmPort) {
		try {
			// Get an instance of the manager. All subsequent calls to
			// getInstance will return the same
			// instance since the manager is a singleton.
			Manager manager = Manager.getInstance(null);

			// Go ahead and declare the progressContainer var, it will be used a
			// few times.
			ProgressContainer progressContainer = null;

			// Create device object, note the use of a ProgressContainer since
			// the amount of time needed
			// to communicate with the device is indeterminate.
			System.out.println("Create Device: device1");
			progressContainer = null;
			progressContainer = Device.createDevice(symbolicName, hostName,
					userID, password, hlmPort);

			progressContainer.blockAndTrace(Level.FINER);

			Device device1 = (Device) progressContainer.getResult();

			ManagedSet ms = new ManagedSet("mgSet");
			ms.addDevice(device1);

			int deviceCtr = 0;
			StringCollection sc = device1.getAllDomainNames();
			int domainCtr = 0;
			while (domainCtr < sc.size()) {
				// System.out.println(sc.get(domainCtr).toString() +
				// " is to be added now");
				device1.createManagedDomain(sc.get(domainCtr));
				domainCtr++;
			}
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Map> getDeviceDtl(){

		List<Map> deviceListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		try{
			// Get an instance of the manager. All subsequent calls to
			// getInstance will return the same
			// instance since the manager is a singleton.
			Manager manager = Manager.getInstance(null);

			// Go ahead and declare the progressContainer var, it will be used a
			// few times.
			ProgressContainer progressContainer = null;

			// Create device object, note the use of a ProgressContainer since
			// the amount of time needed
			// to communicate with the device is indeterminate.
			
			//Manager manager = Manager.getInstance(null);
			ManagedSet ms = manager.getManagedSet("mgSet");
			Device devices[] = ms.getDeviceMembers();

			for (int deviceCtr = 0; deviceCtr < devices.length; deviceCtr ++) {
			
				iMap = new HashMap();
				
				iMap.put("ActualFirmware", devices[deviceCtr].getActualFirmwareLevel());
				iMap.put("CurrentAMPVersion", devices[deviceCtr].getCurrentAMPVersion());
				iMap.put("GUIPort", devices[deviceCtr].getGUIPort());
				iMap.put("HLMPort", devices[deviceCtr].getHLMPort());
				iMap.put("HostName", devices[deviceCtr].getHostname());
				iMap.put("QuiesceTimeout", devices[deviceCtr].getQuiesceTimeout());
				iMap.put("SerialNumber", devices[deviceCtr].getSerialNumber());
				iMap.put("SymbolicName", devices[deviceCtr].getSymbolicName());
				iMap.put("GetUserID", devices[deviceCtr].getUserId());
				iMap.put("IsDeviceReachable", devices[deviceCtr].isDeviceReachable());
				iMap.put("IsPrimary", devices[deviceCtr].isPrimary());
				iMap.put("IsSourceBackupSupported", devices[deviceCtr].isSecureBackupSupported());
				iMap.put("DeviceType", devices[deviceCtr].getDeviceType());
				iMap.put("ModelType", devices[deviceCtr].getModelType());
				iMap.put("SourceFirmware", devices[deviceCtr].getSourceFirmwareVersion());
				
				deviceListDtl.add(iMap);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return deviceListDtl;
	}
	
	public List<Map> getDomains(String dpHostName){
		List<Map> deviceListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		try{
			// Get an instance of the manager. All subsequent calls to
			// getInstance will return the same
			// instance since the manager is a singleton.
			Manager manager = Manager.getInstance(null);

			// Go ahead and declare the progressContainer var, it will be used a
			// few times.
			ProgressContainer progressContainer = null;

			// Create device object, note the use of a ProgressContainer since
			// the amount of time needed
			// to communicate with the device is indeterminate.
			
			//Manager manager = Manager.getInstance(null);
			ManagedSet ms = manager.getManagedSet("mgSet");
			Device devices[] = ms.getDeviceMembers();
			
			
			for (int deviceCtr = 0; deviceCtr < devices.length; deviceCtr ++) {
				if (devices[deviceCtr].getHostname().equalsIgnoreCase(dpHostName)){
					StringCollection sc = devices[deviceCtr].getAllDomainNames();
					//int domainCtr = 0;
					for (int domainCtr = 0; domainCtr < sc.size(); domainCtr ++) {
						iMap = new HashMap();
						//System.out.println(devices[deviceCtr].getSymbolicName());
						iMap.put("SymbolicName", devices[deviceCtr].getSymbolicName());
						iMap.put("DPHostName", devices[deviceCtr].getHostname());
						iMap.put("DomainName", sc.get(domainCtr));
						deviceListDtl.add(iMap);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return deviceListDtl;
		
	}

	public List<Map> getDomainServices(String dpHostName, String domainName){
		List<Map> deviceListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		try {
			// Get an instance of the manager. All subsequent calls to
			// getInstance will return the same
			// instance since the manager is a singleton.
			Manager manager = Manager.getInstance(null);

			// Go ahead and declare the progressContainer var, it will be used a
			// few times.
			ProgressContainer progressContainer = null;

			// Create device object, note the use of a ProgressContainer since
			// the amount of time needed
			// to communicate with the device is indeterminate.
			
			//Manager manager = Manager.getInstance(null);
			ManagedSet ms = manager.getManagedSet("mgSet");
			Device devices[] = ms.getDeviceMembers();

			for (int deviceCtr = 0; deviceCtr < devices.length; deviceCtr ++) {
				if(devices[deviceCtr].getHostname().equals(dpHostName)){
					RuntimeService rs[] = devices[deviceCtr].getManagedDomain(domainName).getServices();
					for (int serviceCtr = 0; serviceCtr < rs.length; serviceCtr ++) {
						iMap = new HashMap();

						iMap.put("Domain", rs[serviceCtr].getDomain().getAbsoluteDisplayName());
						iMap.put("ClassDisplayName", rs[serviceCtr].getClassDisplayName());
						iMap.put("DomainName", rs[serviceCtr].getName());
						iMap.put("DomainUserComments", rs[serviceCtr].getUserComment());
						iMap.put("OpStatus", rs[serviceCtr].getOpStatus());
						iMap.put("PrimaryKey", rs[serviceCtr].getPrimaryKey());
						iMap.put("QuiesceStatus", rs[serviceCtr].getQuiesceStatus());
						iMap.put("AdminStatus", rs[serviceCtr].getAdminStatus());
						deviceListDtl.add(iMap);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return deviceListDtl;
	}

	// public void getServices(String symbolicName,String hostName,String
	// userID, String password, int hlmPort){
	public List<Map> getServices(String dpHostName, String domainName, String svcName) {
		List<Map> ServiceDtl = new ArrayList<Map>();

		Map iMap = new HashMap();
		try {
			// Get an instance of the manager. All subsequent calls to
			// getInstance will return the same
			// instance since the manager is a singleton.
			Manager manager = Manager.getInstance(null);

			// Go ahead and declare the progressContainer var, it will be used a
			// few times.
			ProgressContainer progressContainer = null;

			// Create device object, note the use of a ProgressContainer since
			// the amount of time needed
			// to communicate with the device is indeterminate.
			
			//Manager manager = Manager.getInstance(null);
			ManagedSet ms = manager.getManagedSet("mgSet");
			Device devices[] = ms.getDeviceMembers();
			
			List<String> fileListDtl = new ArrayList<String>();
			List<String> configListDtl = new ArrayList<String>();

			for (int deviceCtr = 0; deviceCtr < devices.length; deviceCtr ++) {
				if(devices[deviceCtr].getHostname().equals(dpHostName)){
					RuntimeService rs[] = devices[deviceCtr].getManagedDomain(domainName).getServices();
					for (int serviceCtr = 0; serviceCtr < rs.length; serviceCtr ++) {
						if(rs[serviceCtr].getName().equalsIgnoreCase(svcName)){
							iMap = new HashMap();
							ReferencedObjectCollection refObjColl = rs[serviceCtr]
									.getReferencedObjectsAndFiles();
							try {
								StringCollection refFiles = refObjColl
										.getReferencedFiles();
								for (int refFileCtr = 0;refFileCtr < refFiles.size();refFileCtr++){
									fileListDtl.add(refFiles.get(refFileCtr)
											.toString());
								}
								iMap.put("FileList", fileListDtl);
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								com.ibm.datapower.wamt.amp.ConfigObject[] configObj = refObjColl
										.getReferencedObjects();
								for (int configObjCtr = 0;configObjCtr < configObj.length;configObjCtr++){
									configListDtl.add(configObj[configObjCtr]
											.getName());
								}
								iMap.put("configListDtl", configListDtl);
							} catch (Exception e) {
								e.printStackTrace();
							}
							iMap.put("AdminStatus", rs[serviceCtr].getAdminStatus());
							ServiceDtl.add(iMap);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
		return ServiceDtl;
	}

	// public void getServices(String symbolicName,String hostName,String
	// userID, String password, int hlmPort){
	public List<Map> getDPEnvironment() {
		List<Map> deviceListDtl = new ArrayList<Map>();
		List<Map> mpgListDtl = new ArrayList<Map>();
		List<Map> wspListDtl = new ArrayList<Map>();

		Map iMap = new HashMap();
		try {
			// Get an instance of the manager. All subsequent calls to
			// getInstance will return the same
			// instance since the manager is a singleton.
			Manager manager = Manager.getInstance(null);
			ManagedSet ms = manager.getManagedSet("mgSet");
			Device devices[] = ms.getDeviceMembers();
			Map<String, Map<String, String>> deviceDtl = new HashMap<String, Map<String, String>>();
			Map<String, String> domainDtl = new HashMap<String, String>();
			List<String> fileListDtl = new ArrayList<String>();
			List<String> configListDtl = new ArrayList<String>();
			int deviceCtr = 0;
			while (deviceCtr < devices.length) {
				StringCollection sc = devices[deviceCtr].getAllDomainNames();
				int domainCtr = 0;
				while (domainCtr < sc.size()) {
					RuntimeService rs[] = devices[deviceCtr].getManagedDomain(
							sc.get(domainCtr)).getServices();

					int serviceCtr = 0;
					while (serviceCtr < rs.length) {
						iMap = new HashMap();
						iMap.put("Domain", rs[serviceCtr].getDomain()
								.getAbsoluteDisplayName());
						iMap.put("ClassDisplayName",
								rs[serviceCtr].getClassDisplayName());
						iMap.put("ClassName", rs[serviceCtr].getClassName());
						iMap.put("DomainName", rs[serviceCtr].getName());
						iMap.put("DomainUserComments",
								rs[serviceCtr].getUserComment());
						iMap.put("DomainStatus", rs[serviceCtr].getOpStatus());
						iMap.put("PrimaryKey", rs[serviceCtr].getPrimaryKey());
						iMap.put("QuiesceStatus",
								rs[serviceCtr].getQuiesceStatus());

						ReferencedObjectCollection refObjColl = rs[serviceCtr]
								.getReferencedObjectsAndFiles();

						try {
							StringCollection refFiles = refObjColl
									.getReferencedFiles();
							int refFileCtr = 0;
							while (refFileCtr < refFiles.size()) {
								fileListDtl.add(refFiles.get(refFileCtr)
										.toString());
								refFileCtr++;
							}
							iMap.put("FileList", fileListDtl);
						} catch (Exception e) {
							// iMap.put("FileList",null);
						}

						try {
							com.ibm.datapower.wamt.amp.ConfigObject[] configObj = refObjColl
									.getReferencedObjects();
							int configObjCtr = 0;
							while (configObjCtr < configObj.length) {
								configListDtl.add(configObj[configObjCtr]
										.getName());
								// System.out.println(configObj[configObjCtr].getName());
								configObjCtr++;
							}
							iMap.put("configListDtl", configListDtl);
						} catch (Exception e) {
							// iMap.put("configListDtl",null);
						}
						if (rs[serviceCtr].getClassName().equals("WSGateway")) {
							wspListDtl.add(iMap);

						}
						if (rs[serviceCtr].getClassName().equals(
								"MultiProtocolGateway")) {
							mpgListDtl.add(iMap);

						}
						serviceCtr++;

					}

					domainCtr++;
				}

				deviceCtr++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
		deviceListDtl.addAll(wspListDtl);
		deviceListDtl.addAll(mpgListDtl);

		return deviceListDtl;

	}

}
