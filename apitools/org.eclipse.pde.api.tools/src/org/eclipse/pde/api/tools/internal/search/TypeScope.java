/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.api.tools.internal.model.ApiElement;
import org.eclipse.pde.api.tools.internal.provisional.ApiTypeContainerVisitor;
import org.eclipse.pde.api.tools.internal.provisional.Factory;
import org.eclipse.pde.api.tools.internal.provisional.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.IApiTypeRoot;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IMemberDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IReferenceTypeDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiElement;
import org.eclipse.pde.api.tools.internal.provisional.search.IApiSearchScope;

/**
 * A search scope containing only types from one component. More efficient than a general purpose
 * scope.
 * 
 * @since 1.0
 */
public class TypeScope extends ApiElement implements IApiSearchScope {

	/**
	 * Associated component
	 */
	private IApiComponent fComponent;
	
	/**
	 * Map of package names to associated type descriptors
	 */
	private Map fPackageToTypes;
		
	/**
	 * Constructs a new class file container/search scope on the given types.
	 * 
	 * @param component API component
	 * @param types types within the component
	 */
	public TypeScope(IApiComponent component, IReferenceTypeDescriptor[] types) {
		super(component, IApiElement.API_TYPE_CONTAINER, null);
		fComponent = component;
		fPackageToTypes = new HashMap();
		for (int i = 0; i < types.length; i++) {
			IReferenceTypeDescriptor type = types[i];
			String name = type.getPackage().getName();
			Set set = (Set) fPackageToTypes.get(name);
			if (set == null) {
				set = new HashSet();
				fPackageToTypes.put(name, set);
			}
			set.add(type);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.internal.provisional.search.IApiSearchScope#encloses(java.lang.String, org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor)
	 */
	public boolean encloses(IApiComponent component, IElementDescriptor element) {
		IApiComponent comp = (IApiComponent) getAncestor(IApiElement.COMPONENT);
		if (comp != null && comp.getId().equals(component.getId())) {
			if (element.getElementType() == IElementDescriptor.T_FIELD || element.getElementType() == IElementDescriptor.T_METHOD) {
				element = ((IMemberDescriptor)element).getEnclosingType();
			}
			if (element.getElementType() == IElementDescriptor.T_REFERENCE_TYPE) {
				IReferenceTypeDescriptor type = (IReferenceTypeDescriptor) element;
				String pkg = type.getPackage().getName();
				Set types = (Set) fPackageToTypes.get(pkg);
				if (types != null) {
					return types.contains(type);
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.internal.provisional.IApiTypeContainer#getPackageNames()
	 */
	public String[] getPackageNames() throws CoreException {
		Set pkgs = fPackageToTypes.keySet();
		return (String[]) pkgs.toArray(new String[pkgs.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.internal.provisional.IApiTypeContainer#accept(org.eclipse.pde.api.tools.internal.provisional.ApiTypeContainerVisitor)
	 */
	public void accept(ApiTypeContainerVisitor visitor) throws CoreException {
		if (visitor.visit(fComponent)) {
			Set entrySet = fPackageToTypes.entrySet();
			Iterator iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				String pkg = (String)entry.getKey();
				if (visitor.visitPackage(pkg)) {
					Set types = (Set) entry.getValue();
					Iterator typeIter = types.iterator();
					while (typeIter.hasNext()) {
						IReferenceTypeDescriptor type = (IReferenceTypeDescriptor) typeIter.next();
						IApiTypeRoot classFile = fComponent.findTypeRoot(type.getQualifiedName());
						if (classFile != null) {
							visitor.visit(pkg, classFile);
							visitor.end(pkg, classFile);
						}
					}
				}
				visitor.endVisitPackage(pkg);
			}
		}
		visitor.end(fComponent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.internal.provisional.IApiTypeContainer#close()
	 */
	public void close() throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.internal.provisional.IApiTypeContainer#findClassFile(java.lang.String)
	 */
	public IApiTypeRoot findTypeRoot(String qualifiedName) throws CoreException {
		IReferenceTypeDescriptor descriptor = Factory.typeDescriptor(qualifiedName);
		IApiComponent comp = (IApiComponent) getAncestor(IApiElement.COMPONENT);
		if (comp != null && encloses(comp, descriptor)) {
			return fComponent.findTypeRoot(qualifiedName);
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.internal.provisional.IApiTypeContainer#findClassFile(java.lang.String, java.lang.String)
	 */
	public IApiTypeRoot findTypeRoot(String qualifiedName, String id) throws CoreException {
		IApiComponent comp = (IApiComponent) getAncestor(IApiElement.COMPONENT);
		if (comp != null && comp.getId().equals(id)) {
			return findTypeRoot(qualifiedName);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("*** Type Search Scope ***\n"); //$NON-NLS-1$
		buffer.append("Component: ").append(fComponent); //$NON-NLS-1$
		if(fPackageToTypes != null) {
			String pack = null;
			for(Iterator iter = fPackageToTypes.keySet().iterator(); iter.hasNext();) {
				pack = (String) iter.next();
				buffer.append("Package: ").append(pack).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
				buffer.append("Types: ").append(fPackageToTypes.get(pack).toString()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return buffer.toString();
	}
	
}