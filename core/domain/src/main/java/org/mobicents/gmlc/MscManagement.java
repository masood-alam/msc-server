/**
 * TeleStax, Open Source Cloud Communications  Copyright 2012. 
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.gmlc;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;

/**
 * @author amit bhayani
 * 
 */
public class MscManagement {
	private static final Logger logger = Logger.getLogger(MscManagement.class);

	public static final String JMX_DOMAIN = "org.mobicents.gmlc";

	protected static final String GMLC_PERSIST_DIR_KEY = "gmlc.persist.dir";
	protected static final String USER_DIR_KEY = "user.dir";

	private String persistDir = null;
	private final String name;

	private MscPropertiesManagement mscPropertiesManagement = null;

	private MBeanServer mbeanServer = null;

	public MscManagement(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getPersistDir() {
		return persistDir;
	}

	public void setPersistDir(String persistDir) {
		this.persistDir = persistDir;
	}

	public void start() throws Exception {
		this.mscPropertiesManagement = MscPropertiesManagement.getInstance(this.name);
		this.mscPropertiesManagement.setPersistDir(this.persistDir);
		this.mscPropertiesManagement.start();

		// Register the MBeans
		this.mbeanServer = MBeanServerLocator.locateJBoss();

		ObjectName gmlcPropObjNname = new ObjectName(MscManagement.JMX_DOMAIN + ":name=MscPropertiesManagement");
		StandardMBean gmlcPropMxBean = new StandardMBean(this.mscPropertiesManagement,
				MscPropertiesManagementMBean.class, true);
		this.mbeanServer.registerMBean(gmlcPropMxBean, gmlcPropObjNname);

		logger.info("Started MSC Management");
	}

	public void stop() throws Exception {
		this.mscPropertiesManagement.stop();

		if (this.mbeanServer != null) {

			ObjectName gmlcPropObjNname = new ObjectName(MscManagement.JMX_DOMAIN + ":name=MscPropertiesManagement");
			this.mbeanServer.unregisterMBean(gmlcPropObjNname);
		}
	}
}
