package com.hp.hpl.deli;

import javax.servlet.ServletContext;

import com.hp.hpl.deli.Profile;
import com.hp.hpl.deli.Workspace;


public class W3CTestHarness extends Object {
    public static Workspace workspace;

    public static void main(String[] args) {
        System.out.println("IMPORTANT: If you run this behind a firewall, you must configure it");
        System.out.println("To use a HTTP proxy eg.");
        System.out.println(
            "java -Dhttp.proxyHost=garlic.hpl.hp.com -Dhttp.proxyPort=8088 com.hp.hpl.deliTest.W3CTestHarness");

        for (int testNumber = 1; testNumber < 30; testNumber++) {
            try {
                System.out.println("Test Number:");
                System.out.println();

                switch (testNumber) {
                case 1: {
                    System.out.println(
                        "01A: Checking processor can process valid RDF serialized XML");

                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01a.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 2: {
                    System.out.println(
                        "01B: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01b.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 3: {
                    System.out.println(
                        "01C: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01c.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 4: {
                    System.out.println(
                        "01D: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01d.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 5: {
                    System.out.println(
                        "O1E: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01e.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 6: {
                    System.out.println(
                        "O1F: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01f.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 7: {
                    System.out.println(
                        "O1G: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01g.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 8: {
                    System.out.println(
                        "01H: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01h.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 9: {
                    System.out.println(
                        "01I: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01i.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 10: {
                    System.out.println(
                        "01J: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01j.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 11: {
                    System.out.println(
                        "01K: Checking processor can process valid RDF serialized XML");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-01k.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 12: {
                    System.out.println("02A: Checking component name MAY be in a type statement");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-02a.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 13: {
                    System.out.println("02B: Checking component name MAY be in a type statement");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-02b.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 14: {
                    System.out.println(
                        "03: Checking components MUST be associated with the CC/PP namespace or some subclass thereof");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-03.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 15: {
                    System.out.println("06A: Checking default references MUST be valid URLs");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-06a.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 16: {
                    System.out.println("06B: Checking references MUST be valid URLs");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-06b.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 17: {
                    System.out.println(
                        "07: Checking defaults MUST be associted with the CC/PP namespace or some subclass thereof");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-07.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 18: {
                    System.out.println(
                        "08: Checking MAY contain both a default value and a directly applied value, directly applied value takes precedence");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-08.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 19: {
                    System.out.println(
                        "09: Checking MUST use valid syntax for namesapce declarations");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-09.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 20: {
                    System.out.println(
                        "10: Checking MUST use valid syntax for namespace declarations");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-10.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 21: {
                    System.out.println(
                        "11A: Checking MUST use valid syntax for namespace declarations");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-11a.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 22: {
                    System.out.println(
                        "11B: Checking MUST use valid syntax for namespace declarations");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-11b.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 23: {
                    System.out.println(
                        "12: Checking MAY contain attributes that are sets of values (Bags)");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-12.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 24: {
                    System.out.println(
                        "13: Checking MAY contain attributes that are sequences of values (Seq)");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-13.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 25: {
                    System.out.println("15: Checking MAY contain attributes that are Text values");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-15.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 26: {
                    System.out.println(
                        "16: Checking MAY contain attributes that are Integer numbers");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-16.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 27: {
                    System.out.println(
                        "17: Checking MAY contain attributes that are Rational numbers");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-17.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 90: {
                    System.out.println(
                        "18: Invalid: components MUST be associated with the CC/PP namespace or some subclass thereof");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-18.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 91: {
                    System.out.println("19: Invalid: profile MUST contain one or more components ");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-19.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 92: {
                    System.out.println("20: Invalid: profile MUST contain one or more components");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-20.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 93: {
                    System.out.println(
                        "21: Invalid: defaults MUST be associated with the CC/PP namespace or some subclass thereof");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-21.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 94: {
                    System.out.println("22: Invalid: the component graph must not contain loops");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-22.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 28: {
                    System.out.println("23: the component graph must not contain loops");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-23.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 95: {
                    System.out.println("24: Invalid: the component graph must not contain loops");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-24.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 29: {
                    System.out.println("25: MUST NOT contain both inline and referenced defaults");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-25.rdf");
                    System.out.println(myprofile.toString());
                }

                break;

                case 96: {
                    System.out.println(
                        "26: Invalid: MUST NOT contain both inline and referenced defaults");
                    Workspace.getInstance().configure((ServletContext) null,
                        Constants.CONFIG_FILE);

                    Profile myprofile = new Profile("profiles/W3Ctests/test-profile-26.rdf");
                    System.out.println(myprofile.toString());
                }

                break;
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }
}
