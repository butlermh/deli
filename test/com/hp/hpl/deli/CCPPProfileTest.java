package com.hp.hpl.deli;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import com.hp.hpl.deli.Profile;
import com.hp.hpl.deli.Workspace;

abstract public class CCPPProfileTest extends TestCase {

	protected Profile profile;

	private String correctNamespace;

	private String profileFile;

	private static boolean vocabInit = false;

	/**
	 * @return Returns the profileFile.
	 */
	public String getProfileFile() {
		return profileFile;
	}

	public CCPPProfileTest(String name) {
		super(name);
	}

	public void create(String correctNamespace, String profileFile) {
		this.correctNamespace = correctNamespace;
		this.profileFile = profileFile;
	}

	protected void testAttribute(String correctAttributeName, String correctComponent, String correctResolution,
			String correctCollection, String correctType) {
		if (profile.getAttribute(correctAttributeName) != null) {
			if (correctComponent != null) {
				String component = profile.getAttribute(correctAttributeName).getComponent();
				assertTrue("Attribute: " + correctAttributeName + ", Component name was " + component + " should be "
						+ correctComponent, component.equals(correctComponent));
			}

			if (correctResolution != null) {
				String resolution = profile.getAttribute(correctAttributeName).getResolution();
				assertTrue("Attribute: " + correctAttributeName + ", Resolution rule was " + resolution + " should be "
						+ correctResolution, resolution.equals(correctResolution));
			}

			if (correctCollection != null) {
				String collection = profile.getAttribute(correctAttributeName).getCollectionType();
				assertTrue("Attribute: " + correctAttributeName + ", Collection type was " + collection + " should be "
						+ correctCollection, collection.equals(correctCollection));
			}

			if (correctType != null) {
				String type = profile.getAttribute(correctAttributeName).getType();
				assertTrue("Attribute: " + correctAttributeName + ", attribute type was " + type + " should be "
						+ correctType, type.equals(correctType));
			}

			if (correctNamespace != null) {
				String namespace = profile.getAttribute(correctAttributeName).getUri();
				assertTrue("Attribute: " + correctAttributeName + ", namespace was " + namespace + " should be "
						+ correctNamespace, namespace.equals(correctNamespace));
			}
		}
	}

	public void setUp() throws Exception {
		if (!vocabInit) {
			Workspace.getInstance().configure((ServletContext) null, Constants.CONFIG_FILE);
			vocabInit = true;
		}
		profile = new Profile(profileFile);
	}

	public String toString() {
		return profile.toString();
	}
}
