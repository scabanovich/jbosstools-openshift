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
package org.jboss.tools.openshift.internal.ui.wizard.importapp.operation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.openshift.egit.core.EGitUtils;
import org.jboss.tools.openshift.egit.ui.util.EGitUIUtils;
import org.jboss.tools.openshift.internal.common.ui.application.importoperation.GeneralProjectImportOperation;
import org.jboss.tools.openshift.internal.common.ui.application.importoperation.MavenProjectImportOperation;
import org.jboss.tools.openshift.internal.common.ui.application.importoperation.WontOverwriteException;

import com.openshift.restclient.OpenShiftException;

/**
 * @author André Dietisheim <adietish@redhat.com>
 */
public class ImportNewProject {

	private static final String PLATFORM_SEPARATOR = Matcher.quoteReplacement(File.separator);
	
	private File cloneDestination;
	private String gitUrl;
	private String gitRef;
	private Collection<String> filters;
	private boolean skipClone;

	/**
	 * Constructor to both clone the repository and import the filtered projects
	 * @param gitUrl
	 * @param gitRef
	 * @param cloneDestination
	 * @param filters
	 */
	public ImportNewProject(String gitUrl, String gitRef, File cloneDestination, Collection<String> filters) {
		this.gitUrl = gitUrl;
		this.gitRef = gitRef;
		this.cloneDestination = cloneDestination;
		this.filters = sanitize(filters);
		this.skipClone = false;
	}

	
	/**
	 * Constructor to skip the clone and simply import the filtered projects from the destination
	 * @param cloneDestination
	 * @param filters
	 */
	public ImportNewProject(File cloneDestination, Collection<String> filters) {
		this.cloneDestination = cloneDestination;
		this.filters = sanitize(filters);
		this.skipClone = true;
	}

	
	
	
	/**
	 * Imports the (new) project that the user has chosen into the workspace.
	 * 
	 * @param monitor
	 *            the monitor to report progress to
	 * @throws OpenShiftException
	 * @throws CoreException
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 * @throws InvocationTargetException
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws NoWorkTreeException 
	 */
	public void execute(IProgressMonitor monitor)
			throws OpenShiftException, CoreException, InterruptedException, URISyntaxException,
			InvocationTargetException, IOException, NoWorkTreeException, GitAPIException {
		File repositoryFolder = null;
		if( !skipClone ) {
			repositoryFolder = executeClone(monitor);
		} else {
			repositoryFolder = cloneDestination;
		}
		
		List<IProject> importedProjects = importProjectsFrom(repositoryFolder, filters, monitor);
		connectToGitRepo(importedProjects, repositoryFolder, monitor);
	}
	
	
	private File executeClone(IProgressMonitor monitor) throws OpenShiftException, InvocationTargetException, InterruptedException, URISyntaxException {
		if (cloneDestinationExists()) {
			throw new WontOverwriteException(
					NLS.bind("There's already a folder at {0}. The new OpenShift project would overwrite it. " +
							"Please choose another destination to clone to.",
							getCloneDestination().getAbsolutePath()));
		}
		return cloneRepository(gitUrl, cloneDestination, gitRef, monitor);
	}
	
	/**
	 * Imports the projects that are within the given folder. Supports maven and
	 * general projects
	 * 
	 * @param folder
	 *            the folder the projects are located in
	 * @param filters 
	 * @param monitor
	 *            the monitor to report progress to
	 * @return
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private List<IProject> importProjectsFrom(final File folder, Collection<String> filters, IProgressMonitor monitor)
			throws CoreException, InterruptedException {
		MavenProjectImportOperation mavenImport = new MavenProjectImportOperation(folder);
		mavenImport.setFilters(filters);
		List<IProject> importedProjects;
		if (mavenImport.isMavenProject()) {
			importedProjects = mavenImport.importToWorkspace(monitor);
		} else {
			importedProjects = new GeneralProjectImportOperation(folder).importToWorkspace(monitor);
		}
		return importedProjects;
	}

	private void connectToGitRepo(List<IProject> projects, File projectFolder, IProgressMonitor monitor)
			throws CoreException {
		for (IProject project : projects) {
			if (project != null) {
				EGitUtils.connect(project, monitor);
			}
		}
	}

	/**
	 * Clones the repository of the selected OpenShift application to the user
	 * provided path.
	 * 
	 * @param application
	 *            the application to clone
	 * @param remoteName
	 *            the name of the remote repo to clone
	 * @param destination
	 *            the destination to clone to
	 * @param gitRef
	 *            the git reference to check-out
	 * @param addToRepoView
	 *            if true, the clone repo will get added to the (egit)
	 *            repositories view
	 * @param monitor
	 *            the monitor to report progress to
	 * 
	 * @return the location of the cloned repository
	 * @throws OpenShiftException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 * 
	 * @see AbstractImportApplicationOperation#getApplication()
	 * @see #getRepositoryPath()
	 */
	protected File cloneRepository(String gitUrl, File destination, String gitRef, IProgressMonitor monitor)
			throws OpenShiftException, InvocationTargetException, InterruptedException, URISyntaxException {
		monitor.subTask(NLS.bind("Cloning  {0}...", gitUrl));
		EGitUIUtils.ensureEgitUIIsStarted();
		EGitUtils.cloneRepository(
					gitUrl, Constants.DEFAULT_REMOTE_NAME, gitRef, destination, EGitUIUtils.ADD_TO_REPOVIEW_TASK, monitor);
		return destination;
	}

	
	protected File getCloneDestination() {
		return cloneDestination;
	}

	protected boolean cloneDestinationExists() {
		return cloneDestination != null
				&& cloneDestination.exists();
	}

	private static Collection<String> sanitize(Collection<String> filters) {
		Collection<String> sanitized = null;
		if (filters != null) {
			sanitized = new LinkedHashSet<>(filters.size());
			for (String path : filters) {
				if (StringUtils.isNotBlank(path)) {
					sanitized.add(makePlatformDependent(path));
				}
			}
		}
		return sanitized;
	}

	private static String makePlatformDependent(String path) {
		return path.replaceAll("/", PLATFORM_SEPARATOR)
				   .replaceAll("\\\\", PLATFORM_SEPARATOR);
	}
}
