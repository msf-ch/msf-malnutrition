/**
 * 
 */
package org.msf.android.modules;

import org.msf.android.managers.malnutrition.MalnutritionWorkflowManager;

import com.google.inject.AbstractModule;

/**
 * @author Nicholas Wilkie
 *
 */
public class WorkflowManagerModule extends AbstractModule {

	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		bind(MalnutritionWorkflowManager.class).to(MalnutritionWorkflowManager.class);
	}

}
