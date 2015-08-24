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

import java.util.Map;
import java.util.Set;

/**
 * Page model for the deployment config page
 * @author jeff.cantrill
 *
 */
public interface IDeploymentConfigPageModel {

	Map<String, String> getEnvironmentVariables();
	
	void setEnvironmentVariables(Map<String, String> envVars);
	
	void setVolumes(Map<String, String> volumes);
	
	Map<String, String> getVolumes();
	
	void setPortSpecs(Set<String> portSpecs);
	
	Set<String> getPortSpecs();
	
}
