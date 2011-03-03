/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.junit.runtime;

import junit.framework.Assert;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * A Workbench that runs a test suite specified in the
 * command line arguments.
 */
public class NonUIThreadTestApplication implements IApplication {

	private static final String DEFAULT_HEADLESSAPP = "org.eclipse.pde.junit.runtime.coretestapplication"; //$NON-NLS-1$

	protected IApplication fApplication;

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		Object app = getApplication(args);

		Assert.assertNotNull(app);

		return runApp(app, context, args);
	}

	protected Object runApp(Object app, IApplicationContext context, String[] args) throws Exception {
		if (app instanceof IApplication) {
			fApplication = (IApplication) app;
			return fApplication.start(context);
		}
		return ((IPlatformRunnable) app).run(args);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (fApplication != null)
			fApplication.stop();
	}

	/*
	 * return the application to run, or null if not even the default application
	 * is found.
	 */
	private Object getApplication(String[] args) throws CoreException {
		// Find the name of the application as specified by the PDE JUnit launcher.
		// If no application is specified, the 3.0 default workbench application
		// is returned.
		IExtension extension = Platform.getExtensionRegistry().getExtension(Platform.PI_RUNTIME, Platform.PT_APPLICATIONS, getApplicationToRun(args));

		Assert.assertNotNull(extension);

		// If the extension does not have the correct grammar, return null.
		// Otherwise, return the application object.
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length > 0) {
			IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
			if (runs.length > 0) {
				Object runnable = runs[0].createExecutableExtension("class"); //$NON-NLS-1$
				if (runnable instanceof IPlatformRunnable || runnable instanceof IApplication)
					return runnable;
			}
		}
		return null;
	}

	/*
	 * The -testApplication argument specifies the application to be run.
	 * If the PDE JUnit launcher did not set this argument, then return
	 * the name of the default application.
	 * In 3.0, the default is the "org.eclipse.ui.ide.worbench" application.
	 *
	 * see bug 228044
	 *
	 */
	private String getApplicationToRun(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-testApplication") && i < args.length - 1) //$NON-NLS-1$
				return args[i + 1];
		}
		IProduct product = Platform.getProduct();
		if (product != null)
			return product.getApplication();
		return getDefaultApplicationId();
	}

	protected String getDefaultApplicationId() {
		return DEFAULT_HEADLESSAPP;
	}

}
