package com.hp.hpl.deli.utils;

import java.io.IOException;

import com.hp.hpl.deli.Constants;
import com.hp.hpl.deli.ModelUtils;
import com.hp.hpl.deli.ProfileProcessor;
import com.hp.hpl.deli.ValidateProfile;
import com.hp.hpl.jena.shared.JenaException;

/**
 * Check that a UAProf profile is valid.
 */
public class UAProfValidator {
	/**
	 * Provides a command line interface to the validator.
	 * 
	 * @param args a profile to be validated by the processor.
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 1) {
				new UAProfValidator(args[0]);
			} else {
				System.out.println("Must supply one argument: UAProfValidator <uaprofilefile>");
			}
			
		} catch (Exception f) {
			f.printStackTrace();
			outputMsg("DELI error:" + f.toString());
		}
	}

	public UAProfValidator(String s) throws IOException {
		new ProcessProfile().process(s);
	}

	class ProcessProfile extends ValidatorProcessProfile {
		
		private ProfileProcessor configuration;
		
		ProcessProfile() throws IOException {
			configuration = new ProfileProcessor(Constants.VALIDATOR_CONFIG_FILE);
		}

		public boolean process(String profileUrl) {
			try {
				myARPReader.read(model, ModelUtils.getResource(profileUrl), "");
			} catch (JenaException e) {
				outputMsg("Could not parse profile " + profileUrl);
				profileValidFlag = false;
			} catch (Exception e) {
				outputMsg("Could not load profile " + profileUrl);
				unreachable = true;
				profileValidFlag = false;
				outputMsg("PROFILE IS UNREACHABLE");
			}
			if (!unreachable) {
				try {
					profileValidFlag = new ValidateProfile(configuration, model)
							.isProfileValid();
				} catch (Exception e) {
					e.printStackTrace();
					profileValidFlag = false;
					outputMsg(e.toString());
				}

				if (profileValidFlag) {
					outputMsg("PROFILE IS VALID");
				} else {
					outputMsg("PROFILE IS NOT VALID");
				}
			}
			return profileValidFlag;
		}

		void outputMsg(String s) {
			System.out.println(s);
		}

	}

	static void outputMsg(String s) {
		System.out.println(s);
	}
}