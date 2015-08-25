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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.openshift.internal.common.ui.utils.TableViewerBuilder;
import org.jboss.tools.openshift.internal.common.ui.utils.TableViewerBuilder.IColumnLabelProvider;
import org.jboss.tools.openshift.internal.common.ui.wizard.AbstractOpenShiftWizardPage;

/**
 * Page to configure OpenShift services and routes
 * 
 * @author jeff.cantrill
 *
 */
public class ServicesAndRoutingPage extends AbstractOpenShiftWizardPage  {
	private static final String PAGE_NAME = "Services & Routing Settings Page";
	private static final String PAGE_TITLE = "Services & Routing Settings";
	private static final String PAGE_DESCRIPTION = "";
	private IServiceAndRoutingPageModel model;
	
	protected ServicesAndRoutingPage(IWizard wizard, IServiceAndRoutingPageModel model) {
		super(PAGE_TITLE, PAGE_DESCRIPTION, PAGE_NAME, wizard);
		this.model = model;
	}
	
	@Override
	protected void doCreateControls(Composite parent, DataBindingContext dbc) {
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(parent);
		createExposedPortsControl(parent, dbc);
		
		//routing
		Composite routingContainer = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(routingContainer);
		GridLayoutFactory.fillDefaults()
			.margins(6, 6)
			.numColumns(2)
			.applyTo(routingContainer);
		
		Button btnAddRoute = new Button(routingContainer, SWT.CHECK);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL).grab(false, false).applyTo(btnAddRoute);
		
		Label lblRoute = new Label(routingContainer, SWT.NONE);
		lblRoute.setText("Add Route");
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.grab(true, false)
			.applyTo(lblRoute);
	}

	private void createExposedPortsControl(Composite parent, DataBindingContext dbc) {
		Composite envContainer = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(envContainer);
		GridLayoutFactory.fillDefaults()
			.numColumns(2).margins(6, 6).applyTo(envContainer);
		
		Label lblEnvVars = new Label(envContainer, SWT.NONE);
		lblEnvVars.setText("Environment variables:");
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL)
			.span(2,1)
			.applyTo(lblEnvVars);
		Composite tableContainer = new Composite(envContainer, SWT.NONE);
		
		TableViewer envViewer = createTable(tableContainer);
		GridDataFactory.fillDefaults()
			.span(1, 5).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableContainer);
//		ValueBindingBuilder.bind(ViewerProperties.singleSelection().observe(envViewer))
//				.to(BeanProperties.value(IResourceLabelsPageModel.PROPERTY_SELECTED_LABEL).observe(model))
//				.in(dbc);
//		envViewer.setContentProvider(new ObservableListContentProvider());
//		envViewer.setInput(BeanProperties.list(
//				IResourceLabelsPageModel.PROPERTY_LABELS).observe(model));
		
		Button addButton = new Button(envContainer, SWT.PUSH);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL).applyTo(addButton);
		addButton.setText("Add...");
//		addButton.addSelectionListener(onAdd());
		
		
		Button removeButton = new Button(envContainer, SWT.PUSH);
		GridDataFactory.fillDefaults()
			.align(SWT.FILL, SWT.FILL).applyTo(removeButton);
		removeButton.setText("Remove");
//		removeButton.addSelectionListener(onRemove());
//		ValueBindingBuilder
//				.bind(WidgetProperties.enabled().observe(removeButton))
//				.notUpdatingParticipant()
//				.to(BeanProperties.value(IResourceLabelsPageModel.PROPERTY_SELECTED_LABEL).observe(model))
//				.converting(new IsNotNullOrReadOnlyBooleanConverter())
//				.in(dbc);
		
	}
	
	protected TableViewer createTable(Composite tableContainer) {
		Table table =
				new Table(tableContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableViewer envViewer = new TableViewerBuilder(table, tableContainer)
				.contentProvider(new ArrayContentProvider())
				.column(new IColumnLabelProvider<Integer>() {
					@Override
					public String getValue(Integer port) {
						return port.toString();
					}
				})
				.name("Container Port").align(SWT.LEFT).weight(2).minWidth(100).buildColumn()
				.buildViewer();

		return envViewer;
	}
}
