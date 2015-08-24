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
import java.util.Collections;
import java.util.List;

import org.jboss.tools.common.databinding.ObservablePojo;
import org.jboss.tools.openshift.common.core.connection.ConnectionsRegistrySingleton;
import org.jboss.tools.openshift.core.connection.Connection;

import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;

/**
 * The Wizard model to support deploying an image to OpenShift
 * @author jeff.cantrill
 *
 */
public class DeployImageWizardModel extends ObservablePojo implements IDeployImagePageModel{

	private Connection connection;
	private IProject project;
	private String name;
	private String image;
	private Collection<IProject> projects = Collections.emptyList();
	
	public DeployImageWizardModel() {
		setConnection(ConnectionsRegistrySingleton.getInstance().getRecentConnection(Connection.class));
	}

	@Override
	public Collection<Connection> getConnections() {
		return ConnectionsRegistrySingleton.getInstance().getAll(Connection.class);
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void setConnection(Connection connection) {
		firePropertyChange(PROPERTY_CONNECTION, this.connection, this.connection = connection);
		if(this.connection != null) {
			List<IProject> projects = connection.getResources(ResourceKind.PROJECT);
			setProjects(projects);
		}
	}

	private void setProjects(List<IProject> projects) {
		if(projects == null) projects = Collections.emptyList();
		firePropertyChange(PROPERTY_PROJECTS, this.projects, this.projects = projects);
		if(!projects.isEmpty()) {
			setProject(null);
		}
	}

	@Override
	public Collection<IProject> getProjects() {
		return projects;
	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		firePropertyChange(PROPERTY_PROJECT, this.project, this.project = project);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		firePropertyChange(PROPERTY_NAME, this.name, this.name = name);
	}

	@Override
	public String getImage() {
		return this.image;
	}

	@Override
	public void setImage(String image) {
		firePropertyChange(PROPERTY_IMAGE, this.image, this.image = image);
	}

}
