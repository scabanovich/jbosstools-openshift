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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.tools.common.databinding.ObservablePojo;
import org.jboss.tools.openshift.common.core.connection.ConnectionsRegistrySingleton;
import org.jboss.tools.openshift.core.connection.Connection;
import org.jboss.tools.openshift.internal.common.ui.wizard.IConnectionAware;
import org.jboss.tools.openshift.internal.ui.wizard.common.IResourceLabelsPageModel;
import org.jboss.tools.openshift.internal.ui.wizard.common.ResourceLabelsPageModel;
import org.jboss.tools.openshift.internal.ui.wizard.common.IResourceLabelsPageModel.Label;

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

	private List<Label> environmentVariables = new ArrayList<Label>();
	private Label selectedEnvironmentVariable = null;

	private Set<String> volumes = new HashSet<String>();
	private String selectedVolume;

	private Set<String> portSpecs = new HashSet<String>();

	private int replicas;

	private boolean addRoute = false;

	List<IServicePort> servicePorts = new ArrayList<IServicePort>();
	IServicePort selectedServicePort = null;
	
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
	public List<Label> getEnvironmentVariables() {
		return environmentVariables;
	}

	@Override
	public void setEnvironmentVariables(List<Label> envVars) {
		firePropertyChange(PROPERTY_ENVIRONMENT_VARIABLES, 
				this.environmentVariables, 
				this.environmentVariables = envVars);
	}

	@Override
	public void setVolumes(Set<String> volumes) {
		firePropertyChange(PROPERTY_VOLUMES, 
				this.volumes, 
				this.volumes = volumes);
	}

	@Override
	public Set<String> getVolumes() {
		return volumes;
	}

	@Override
	public void setPortSpecs(Set<String> portSpecs) {
		firePropertyChange(PROPERTY_PORT_SPECS, 
				this.portSpecs, 
				this.portSpecs = portSpecs);
	}

	@Override
	public Set<String> getPortSpecs() {
		return portSpecs;
	}

	@Override
	public int getReplicas() {
		return replicas;
	}

	@Override
	public void setReplicas(int replicas) {
		firePropertyChange(PROPERTY_REPLICAS, 
				this.replicas, 
				this.replicas = replicas);
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
		return addRoute;
	}

	@Override
	public void setAddRoute(boolean addRoute) {
		firePropertyChange(PROPERTY_ADD_ROUTE, 
				this.addRoute, 
				this.addRoute = addRoute);
	}

	@Override
	public List<IServicePort> getServicePorts() {
		return servicePorts;
	}

	@Override
	public void setSelectedEnvironmentVariable(Label envVar) {
		firePropertyChange(PROPERTY_SELECTED_ENVIRONMENT_VARIABLE, 
				this.selectedEnvironmentVariable, 
				this.selectedEnvironmentVariable = envVar);
	}

	@Override
	public Label getSelectedEnvironmentVariable() {
		return selectedEnvironmentVariable;
	}

	@Override
	public void removeEnvironmentVariable(Label envVar) {
		List<Label> old = new ArrayList<Label>(environmentVariables);
		int index = environmentVariables.indexOf(envVar);
		if(index > -1) {
			this.environmentVariables.remove(envVar);
			fireIndexedPropertyChange(PROPERTY_ENVIRONMENT_VARIABLES, index, old, Collections.unmodifiableList(environmentVariables));
		}
	}

	@Override
	public void updateEnvironmentVariable(Label envVar, String key, String value) {
		List<Label> old = new ArrayList<Label>(environmentVariables);
		int index = environmentVariables.indexOf(envVar);
		if(index > -1) {
			Label changed = environmentVariables.get(index);
			changed.setName(key);
			changed.setValue(value);
			fireIndexedPropertyChange(PROPERTY_ENVIRONMENT_VARIABLES, index, old, Collections.unmodifiableList(environmentVariables));
		}
	}	
	
	@Override
	public void addEnvironmentVariable(String key, String value) {
		List<Label> old = new ArrayList<Label>(environmentVariables);
		this.environmentVariables.add(new Label(key, value));
		fireIndexedPropertyChange(PROPERTY_ENVIRONMENT_VARIABLES, this.environmentVariables.size(), old, Collections.unmodifiableList(environmentVariables));
	}

	@Override
	public void setSelectedVolume(String volume) {
		firePropertyChange(PROPERTY_SELECTED_VOLUME, 
				this.selectedVolume, 
				this.selectedVolume = volume);
	}

	@Override
	public String getSelectedVolume() {
		return selectedVolume;
	}

	@Override
	public void updateVolume(String volume, String value) {
		Set<String> old = new LinkedHashSet<String>(volumes);
		this.volumes.remove(volume);
		this.volumes.add(value);
		firePropertyChange(PROPERTY_VOLUMES, old, Collections.unmodifiableSet(volumes));
	}

	@Override
	public void setSelectedServicePort(IServicePort servicePort) {
		firePropertyChange(PROPERTY_SELECTED_SERVICE_PORT, 
				this.selectedServicePort, 
				this.selectedServicePort = servicePort);
	}

	@Override
	public IServicePort getSelectedServicePort() {
		return selectedServicePort;
	}

	@Override
	public void removeServicePort(IServicePort port) {
		List<IServicePort> old = new ArrayList<IServicePort>(servicePorts);
		int index = servicePorts.indexOf(port);
		this.servicePorts.remove(port);
		fireIndexedPropertyChange(PROPERTY_SERVICE_PORTS, index, old, Collections.unmodifiableList(servicePorts));
	}

}
