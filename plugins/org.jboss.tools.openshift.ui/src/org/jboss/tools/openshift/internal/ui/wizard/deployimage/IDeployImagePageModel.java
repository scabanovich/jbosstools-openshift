/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.openshift.internal.ui.wizard.deployimage;

import java.util.Collection;

import org.jboss.tools.openshift.core.connection.Connection;
import org.jboss.tools.openshift.internal.common.ui.wizard.IConnectionAware;

import com.openshift.restclient.model.IProject;

/**
 * Page model for the deploy image page
 * @author jeff.cantrill
 *
 */
public interface IDeployImagePageModel extends IConnectionAware<Connection>{

	String PROPERTY_CONNECTIONS = "connections";
	String PROPERTY_PROJECTS = "projects";
	String PROPERTY_PROJECT = "project";
	String PROPERTY_NAME = "name";
	String PROPERTY_IMAGE = "image";
	
	/**
	 * The set of known OpenShift connections
	 * @return
	 */
	Collection<Connection> getConnections();
	
	Collection<IProject> getProjects();
	
	IProject getProject();
	void setProject(IProject project);
	
	String getName();
	void setName(String name);
	
	String getImage();
	void setImage(String image);
}
