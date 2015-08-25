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
import java.util.Map;
import java.util.Set;

import org.jboss.tools.common.databinding.ObservablePojo;
import org.jboss.tools.openshift.common.core.connection.ConnectionsRegistrySingleton;
import org.jboss.tools.openshift.core.connection.Connection;
import org.jboss.tools.openshift.internal.common.ui.wizard.IConnectionAware;
import org.jboss.tools.openshift.internal.ui.wizard.common.IResourceLabelsPageModel;
import org.jboss.tools.openshift.internal.ui.wizard.common.ResourceLabelsPageModel;

import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IServicePort;

/**
 * The Wizard model to support deploying an image to OpenShift
 * @author jeff.cantrill
 *
 */
public class DeployImageWizardModel 
		extends ResourceLabelsPageModel 
		implements IDeployImagePageModel, IDeploymentConfigPageModel, IServiceAndRoutingPageModel{

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
	public Connection setConnection(Connection connection) {
		firePropertyChange(PROPERTY_CONNECTION, this.connection, this.connection = connection);
		if(this.connection != null) {
			List<IProject> projects = connection.getResources(ResourceKind.PROJECT);
			setProjects(projects);
		}
		return this.connection;
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

	@Override
	public Map<String, String> getEnvironmentVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEnvironmentVariables(Map<String, String> envVars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVolumes(Map<String, String> volumes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getVolumes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPortSpecs(Set<String> portSpecs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getPortSpecs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getReplicas() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setReplicas(int replicas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasConnection() {
		return this.connection != null;
	}

	@Override
	public Object getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAddRoute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAddRoute(boolean addRoute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IServicePort> getServicePorts() {
		return null;
	}
	
	

}
