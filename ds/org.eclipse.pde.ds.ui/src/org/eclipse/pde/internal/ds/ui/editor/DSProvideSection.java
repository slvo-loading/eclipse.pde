/*******************************************************************************
 * Copyright (c) 2008 Code 9 Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Code 9 Corporation - initial API and implementation
 *     Chris Aniszczyk <caniszczyk@gmail.com>
 *     Rafael Oliveira Nobrega <rafael.oliveira@gmail.com> - bug 242028
 *******************************************************************************/
package org.eclipse.pde.internal.ds.ui.editor;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.internal.ds.core.IDSComponent;
import org.eclipse.pde.internal.ds.core.IDSDocumentFactory;
import org.eclipse.pde.internal.ds.core.IDSModel;
import org.eclipse.pde.internal.ds.core.IDSProvide;
import org.eclipse.pde.internal.ds.core.IDSService;
import org.eclipse.pde.internal.ds.ui.Activator;
import org.eclipse.pde.internal.ds.ui.Messages;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.TableSection;
import org.eclipse.pde.internal.ui.editor.context.InputContextManager;
import org.eclipse.pde.internal.ui.elements.DefaultTableProvider;
import org.eclipse.pde.internal.ui.parts.EditableTablePart;
import org.eclipse.pde.internal.ui.parts.TablePart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class DSProvideSection extends TableSection {

	private TableViewer fProvidesTable;
	private Action fRemoveAction;
	private Action fAddAction;

	class ContentProvider extends DefaultTableProvider {
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IDSModel) {
				IDSModel model = (IDSModel) inputElement;
				IDSComponent component = model.getDSComponent();
				if (component != null) {
					IDSService service = component.getService();
					if (service != null) {
						return service.getProvidedServices();
					}
				} 
				

			}
			return new Object[0];
		}
	}

	public DSProvideSection(PDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[] {
				Messages.DSProvideSection_add,
				Messages.DSProvideSection_remove });
		createClient(getSection(), page.getEditor().getToolkit());
	}

	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText(Messages.DSProvideSection_title);
		section.setDescription(Messages.DSProvideSection_description);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(data);

		Composite container = createClientContainer(section, 2, toolkit);
		EditableTablePart tablePart = getTablePart();
		tablePart.setEditable(isEditable());

		createViewerPartControl(container, SWT.FULL_SELECTION | SWT.MULTI, 2,
				toolkit);
		fProvidesTable = tablePart.getTableViewer();
		fProvidesTable.setContentProvider(new ContentProvider());
		fProvidesTable.setLabelProvider(new DSLabelProvider());

		makeActions();

		IDSModel model = getDSModel();
		if (model != null) {
			fProvidesTable.setInput(model);
			model.addModelChangedListener(this);
		}
		toolkit.paintBordersFor(container);
		section.setClient(container);
	}

	public void dispose() {
		IDSModel model = getDSModel();
		if (model != null)
			model.removeModelChangedListener(this);
	}

	public void refresh() {
		fProvidesTable.refresh();
		updateButtons();
	}

	protected void buttonSelected(int index) {
		switch (index) {
		case 0:
			handleAdd();
			break;
		case 1:
			handleRemove();
			break;
		}
	}

	private void handleEdit() {

		ISelection selection = fProvidesTable.getSelection();
		if (selection != null) {

			int selectionIndex = fProvidesTable.getTable().getSelectionIndex();
			if (selectionIndex != -1) {
				DSEditProvideDialog dialog = new DSEditProvideDialog(Activator
						.getActiveWorkbenchShell(), (IDSProvide) fProvidesTable
						.getElementAt(selectionIndex), this);
				dialog.setTitle(Messages.DSEditProvideDialog_dialog_title);
				dialog.open();
			}

		}

	}

	private void makeActions() {
		fAddAction = new Action(Messages.DSProvideSection_add) {
			public void run() {
				handleAdd();
			}
		};
		fAddAction.setEnabled(isEditable());

		fRemoveAction = new Action(Messages.DSProvideSection_remove) {
			public void run() {
				handleRemove();
			}
		};
		fRemoveAction.setEnabled(isEditable());
	}

	private void updateButtons() {
		Table table = fProvidesTable.getTable();
		TablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(0, isEditable());
		tablePart.setButtonEnabled(1, isEditable()
				&& table.getSelection().length > 0);
	}

	private void handleRemove() {
		IStructuredSelection ssel = (IStructuredSelection) fProvidesTable
				.getSelection();
		if (ssel.size() > 0) {
			Iterator iter = ssel.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				if (object instanceof IDSProvide) {
					getDSModel().getDSComponent().getService()
							.removeProvidedService(
							(IDSProvide) object);
				}
			}
		}
	}

	private void handleAdd() {
		doOpenSelectionDialog(IJavaElementSearchConstants.CONSIDER_INTERFACES);
	}

	private void doOpenSelectionDialog(int scopeType) {
		try {
			String filter = ""; //$NON-NLS-1$
			filter = filter.substring(filter.lastIndexOf(".") + 1); //$NON-NLS-1$
			SelectionDialog dialog = JavaUI.createTypeDialog(Activator
					.getActiveWorkbenchShell(), PlatformUI.getWorkbench()
					.getProgressService(), SearchEngine.createWorkspaceScope(),
					scopeType, false, filter);
			dialog.setTitle(Messages.DSProvideDetails_selectType);
			if (dialog.open() == Window.OK) {
				IType type = (IType) dialog.getResult()[0];
				String fullyQualifiedName = type.getFullyQualifiedName('$');
				addProvide(fullyQualifiedName);
			}
		} catch (CoreException e) {
		}
	}

	private void addProvide(String fullyQualifiedName) {

		IDSDocumentFactory factory = getDSModel().getFactory();
		IDSComponent component = getDSModel().getDSComponent();
		
		IDSService service = component.getService();
		if (service == null) {
			service = factory.createService();
			component.setService(service);
		}

		IDSProvide provide = factory.createProvide();
		// set interface attribute
		provide.setInterface(fullyQualifiedName);

		// add provide
		service.addProvidedService(provide);
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
		} else if (e.getChangeType() == IModelChangedEvent.REMOVE) {
			Object[] objects = e.getChangedObjects();
			for (int i = 0; i < objects.length; i++) {
				Table table = fProvidesTable.getTable();
				if (objects[i] instanceof IDSProvide) {
					int index = table.getSelectionIndex();
					fProvidesTable.remove(objects[i]);
					if (canSelect()) {
						table.setSelection(index < table.getItemCount() ? index
								: table.getItemCount() - 1);
					}
				}
			}
			updateButtons();
		} else if (e.getChangeType() == IModelChangedEvent.INSERT) {
			Object[] objects = e.getChangedObjects();
			if (objects.length > 0) {
				fProvidesTable.refresh();
				fProvidesTable.setSelection(new StructuredSelection(
						objects[objects.length - 1]));
			}
			updateButtons();
		} else {
			fProvidesTable.refresh();
			updateButtons();
		}
	}

	private DSInputContext getDSContext() {
		InputContextManager manager = getPage().getPDEEditor()
				.getContextManager();
		return (DSInputContext) manager.findContext(DSInputContext.CONTEXT_ID);
	}

	private IDSModel getDSModel() {
		DSInputContext context = getDSContext();
		return context == null ? null : (IDSModel) context.getModel();
	}

	public boolean doGlobalAction(String actionId) {
		if (!isEditable()) {
			return false;
		}

		if (actionId.equals(ActionFactory.DELETE.getId())) {
			handleRemove();
			return true;
		}

		return false;
	}

	protected void selectionChanged(IStructuredSelection selection) {
		getPage().getPDEEditor().setSelection(selection);
		updateButtons();
	}

}
