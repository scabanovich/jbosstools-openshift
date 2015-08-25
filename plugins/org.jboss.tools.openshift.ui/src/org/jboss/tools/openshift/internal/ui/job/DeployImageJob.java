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
package org.jboss.tools.openshift.internal.ui.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.openshift.core.connection.Connection;
import org.jboss.tools.openshift.internal.common.core.job.AbstractDelegatingMonitorJob;
import org.jboss.tools.openshift.internal.ui.wizard.common.IResourceLabelsPageModel;
import org.jboss.tools.openshift.internal.ui.wizard.common.IResourceLabelsPageModel.Label;
import org.jboss.tools.openshift.internal.ui.wizard.deployimage.IDeployImagePageModel;
import org.jboss.tools.openshift.internal.ui.wizard.deployimage.IServiceAndRoutingPageModel;

import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.route.IRoute;

/**
 * Job to deploy docker images to OpenShift with a minimal
 * set of OpenShift resources
 * 
 * @author jeff.cantrill
 *
 */
public class DeployImageJob extends AbstractDelegatingMonitorJob {
	
	private static final String SELECTOR_KEY = "deploymentconfig";
	
	private IProject project;
	private Connection connection;
	private IDeployImagePageModel deployImagePageModel;
	private IServiceAndRoutingPageModel servicePageModel;
	private IResourceLabelsPageModel labelsModel;

	public DeployImageJob(Connection connection, 
			IProject project, 
			IDeployImagePageModel deployImagePageModel,
			IResourceLabelsPageModel labelsModel,
			IServiceAndRoutingPageModel servicePageModel) {
		super("Deploy Image Job");
		this.project = project;
		this.connection = connection;
		this.deployImagePageModel = deployImagePageModel;
		this.labelsModel = labelsModel;
		this.servicePageModel = servicePageModel;
	}

	@Override
	protected IStatus doRun(IProgressMonitor monitor) {
		IResourceFactory factory = connection.getResourceFactory();
		final String name = deployImagePageModel.getName();
		List<IResource> resources = new ArrayList<IResource>(4);
		//image stream
		resources.add(stubImageStream(factory, name, project));
		//service - optional
		IService service = stubService(factory, name, SELECTOR_KEY, name);
		resources.add(service);
		//route - optional
		if(servicePageModel.isAddRoute()) {
			resources.add(stubRoute(factory, name, service.getName()));
		}
		//deploymentconfig
		resources.add(stubDeploymentConfig(factory, name));
		
		for (IResource resource : resources) {
			addLabelsToResource(resource);
		}
		return Status.OK_STATUS;
	}
	
	private IResource stubDeploymentConfig(IResourceFactory factory, String name) {
		IDeploymentConfig dc = factory.stub(ResourceKind.DEPLOYMENT_CONFIG, name);
		dc.addLabel(SELECTOR_KEY, name);
		return dc;
	}

	private IImageStream stubImageStream(IResourceFactory factory, String name, IProject project) {
		IImageStream imageStream = factory.stub(ResourceKind.IMAGE_STREAM, name);
		DockerImageURI sourceImage = new DockerImageURI(deployImagePageModel.getImage());
		DockerImageURI imageUri = new DockerImageURI(null, project.getName(), sourceImage.getName(), sourceImage.getTag());
		imageStream.setDockerImageRepository(imageUri);
		return imageStream;
	}

	private IResource stubRoute(IResourceFactory factory, String name, String serviceName) {
		IRoute route = factory.stub(ResourceKind.ROUTE, name);
		route.setServiceName(serviceName);
		return route;
	}

	private IService stubService(IResourceFactory factory, String name, String selectorKey, String selectorValue) {
		IService service = factory.stub(ResourceKind.SERVICE, name);
		service.setPorts(servicePageModel.getServicePorts());
		service.setSelector(selectorKey, selectorValue);
		return service;
	}
	
	private void addLabelsToResource(IResource resource) {
		for (Label label : labelsModel.getLabels()) {
			resource.addLabel(label.getName(), label.getValue());
		}
	}

}
