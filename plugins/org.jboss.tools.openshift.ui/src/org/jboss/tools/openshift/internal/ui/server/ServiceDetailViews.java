/*******************************************************************************
 * Copyright (c) 2015 Red Hat Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Incorporated - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.openshift.internal.ui.server;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.openshift.common.core.utils.StringUtils;
import org.jboss.tools.openshift.internal.common.ui.detailviews.AbstractStackedDetailViews;

import com.openshift.restclient.model.IService;

/**
 * @author Andre Dietisheim
 */
public class ServiceDetailViews extends AbstractStackedDetailViews {

	private final IDetailView templateView = new TemplateDetailView();

	public ServiceDetailViews(IObservableValue serviceObservable, Composite parent, DataBindingContext dbc) {
		super(serviceObservable, null, parent, dbc);
	}

	@Override
	protected void createViewControls(Composite parent, Object context, DataBindingContext dbc) {
		templateView.createControls(parent, context, dbc);
		emptyView.createControls(parent, context, dbc);
	}

	@Override
	protected IDetailView[] getDetailViews() {
		return new IDetailView[] { templateView, emptyView };
	}

	private class TemplateDetailView extends EmptyView {

		private StyledText nameText;
		private StyledText namespaceText;
		private StyledText labelsText;
		private StyledText selectorsText;
		private StyledText ipText;
		private StyledText portText;

		@Override
		public Composite createControls(Composite parent, Object context, DataBindingContext dbc) {
			Composite container = setControl(new Composite(parent, SWT.None));
			GridLayoutFactory.fillDefaults()
					.numColumns(2).margins(8, 2).spacing(6, 2).applyTo(container);
			this.nameText = createLabeledValue("Name:", container);
			this.namespaceText = createLabeledValue("Namespace:", container);
			this.labelsText = createLabeledValue("Labels:", container);
			this.selectorsText = createLabeledValue("Selectors:", container);
			this.ipText = createLabeledValue("IP:", container);
			this.portText = createLabeledValue("Port:", container);

			return container;
		}

		@Override
		public void onVisible(IObservableValue serviceObservable, DataBindingContext dbc) {
			Object value = serviceObservable.getValue();
			if (!(value instanceof IService)) {
				return;
			}
			IService service = (IService) value;

			nameText.setText(service.getName());
			namespaceText.setText(service.getNamespace());
			String labels = org.jboss.tools.openshift.common.core.utils.StringUtils.toString(service.getLabels());
			labels = org.apache.commons.lang.StringUtils.defaultString(labels); //replaces null by empty string
			labelsText.setText(labels);
			String selectors = org.jboss.tools.openshift.common.core.utils.StringUtils.toString(service.getSelector());
			selectors = org.apache.commons.lang.StringUtils.defaultString(selectors); //replaces null by empty string
			selectorsText.setText(selectors);
			ipText.setText(
					org.apache.commons.lang.StringUtils.join(new String[] {
							service.getPortalIP(), StringUtils.toStringOrNull(service.getTargetPort())
					}, ':'));
			portText.setText(StringUtils.toStringOrNull(service.getPort()));
		}

		@Override
		public boolean isViewFor(Object object) {
			return object instanceof IService;
		}
	}
}
