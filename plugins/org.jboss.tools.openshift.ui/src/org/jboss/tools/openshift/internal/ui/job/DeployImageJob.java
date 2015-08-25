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
import org.jboss.tools.openshift.internal.ui.wizard.common.IResourceLabelsPageModel.Label;
import org.jboss.tools.openshift.internal.ui.wizard.deployimage.IDeployImagePageModel;

import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;

/**
 * Job to deploy docker images to OpenShift with a minimal
 * set of OpenShift resources
 * 
 * @author jeff.cantrill
 *
 */
public class DeployImageJob extends AbstractDelegatingMonitorJob {

	private IProject project;
	private Connection connection;
	private IDeployImagePageModel deployImagePageModel;

	public DeployImageJob(Connection connection, 
			IProject project, 
			IDeployImagePageModel deployImagePageModel) {
		super("Deploy Image Job");
		this.project = project;
		this.connection = connection;
		this.deployImagePageModel = deployImagePageModel;
	}

	@Override
	protected IStatus doRun(IProgressMonitor monitor) {
		IResourceFactory factory = connection.getResourceFactory();
		final String name = deployImagePageModel.getName();
		List<IResource> resources = new ArrayList<IResource>(4);
		//image stream
		//route - optional
		//service - optional
		resources.add(stubService(factory, name));
		//deploymentconfig
		
		return Status.OK_STATUS;
	}
	
	private IService stubService(IResourceFactory factory, String name) {
		IService service = factory.stub(ResourceKind.SERVICE, name);
//		service.set
		return service;
	}

}
