DELI Delivery Context Library

Welcome! The zip file you have downloaded contains code for the DELI
library. 

IMPORTANT: Send all support requests to
mark-h.butler@hp.com

For additonal information see 
http://www.hpl.hp.com/people/marbut

=============================

PRE-REQUISITES

To use DELI, you need a Java JDK - I suggest you get JDK 1.4.2_06 from here 
http://java.sun.com/j2se/1.4.2/download.html

You no longer require a separate install of Apache Ant to use DELI, but if
you have Ant installed it is not a problem. 

=============================

INSTALLING DELI ON WINDOWS

1. First you need to build DELI type

ant

Then there are various things you can do with DELI:

2. To validate all UAProf profiles in profiles.xml with a direct 
connection to the internet type

ant UAProfValidator

3. To validate all UAProf profiles in profiles.xml behind a firewall type

ant runUAProfValidatorProxy -Dhttp.proxyHost=your proxy host -Dhttp.proxyPort=your proxy port

4. To validate a UAProf profile type

ant validate -Dprofile=your profile

5. To run the DELI hello world application build DELI, copy ccpp.war
to the webapps directory of your app server, and see your app server
documentation about how to deploy a web app. On Tomcat, the web app
will deploy automatically. Assuming your app server works on port 8088,
to see the hello world page point your web browser at 

http://localhost:8088/ccpp/index.html

=============================

INSTALLING DELI ON LINUX

1. First you need to build DELI type

. ant.sh

Then there are various things you can do with DELI:

2. To validate all UAProf profiles in profiles.xml with a direct 
connection to the internet type

. ant.sh runUAProfValidator

3. To validate all UAProf profiles in profiles.xml behind a firewall type

. ant.sh runUAProfValidatorProxy -Dhttp.proxyHost=your proxy host -Dhttp.proxyPort=your proxy port

4. To validate a UAProf profile type

. ant.sh validate -Dprofile=your profile

5. To run the DELI hello world application build DELI, copy ccpp.war
to the webapps directory of your app server, and see your app server
documentation about how to deploy a web app. On Tomcat, the web app
will deploy automatically. Assuming your app server works on port 8088,
to see the hello world page point your web browser at 

http://localhost:8088/ccpp/index.html

=============================

IMPORTANT

To get DELI to run on Tomcat 4.1 or Tomcat 5, you may need to copy 
deli/lib/xercesImpl.jar, xml-apis.jar and xmlParserAPIs.jar to 
tomcat/common/endorsed to avoid mismatching versions of Xerces. 

==========================================================================

LICENSE INFORMATION

Note: this is the 'BSD License' as endorsed by OpenSource.org.

(c) Copyright Hewlett-Packard Company 2001
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


This distribution includes Jena, RDFFilter, Sax2, Xerces and JUnit.

DELI is released under the Hewlett Packard License above.
For more details on DELI see
http://www-uk.hpl.hp.com/people/marbut/

Jena is released under the Hewlett Packard License above.
For more information on Jena see 
http://www.hpl.hp.com/semweb

For details of the Xerces license see xerces_license.txt in the distribution.
Further information on Xerces is available at 
http://xml.apache.org/xerces-j/index.html

RDF-Filter is Public Domain.
Further information on RDF Filter is available at
http://rdf-filter.sourceforge.net/

Sax2 is Public Domain. 
Further information on Sax2 is available at
http://sax.sourceforge.net/

Junit is IBM Public License Version 1.0
For more details of Junit, See lib/licenses/junit_license.txt
http://www.junit.org/

==========================================================================

***** Using the UAProf validator *****

The UAProf validator is part of the DELI distribution; the class providing this
functionality is com.hp.hpl.deli.UAProfValidator. The easiest way to use it is
to use the command line interface which can be executed as:

java com.hp.hpl.deli.UAProfValidator [list of profiles to validate]

If you need to use the API, it is documented in the DELI documentation, however 
a short example is given here:

Workspace.getInstance().configure(null, "config/deliConfig.xml");
UAProfValidator validator = new UAProfValidator(System.out);
validator.setDefaultDatatypes();

String profileName = ...

if(validator.validate(profileName)) {
System.out.println("Profile is valid\n");
} else {
System.out.println("Profile is not valid\n");
}

The first line is required to instruct DELI to load the required vocabulary
information. The validator object is created, specifying the System.out stream as the
stream to write status messages to. The validator is then instructed to use the default 
UAProf datatypes; if required an alternative set of datatypes can be loaded
from a configuration file. The format of this file is as follows:

<?xml version="1.0"?>
<validator>
  <datatype>
    <name>Literal</name>
    <expression>[A-Za-z0-9/.\\-_ ]+</expression>
  </datatype>
  <datatype>
    <name>Dimension</name>
    <expression>[0-9]+x[0-9]+</expression>
  </datatype>
  ...
  ...
</validator>

Further datatypes can be added by including more <datatype> elements containing a name 
and regular expression for the new type.
