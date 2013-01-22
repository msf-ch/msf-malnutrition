/**
 * 
 */
package org.msf.mobilemrs.forms;

/**
 * @author Nicholas Wilkie
 *
 */
public enum Stage {
	VALIDATE_INPUT(0), MODIFY_ATTRIBUTES(1), REPLACE_NODES(2), VALIDATE_OUTPUT(3);
	
	private int index;
	private Stage(int index) {
	}
	
	public int getIndex() {
		return index;
	}
}
