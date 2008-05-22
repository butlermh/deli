#!/usr/bin/perl -w

## 11th of January 2005, ver 1.0 by Teijo Heimola, Nokia corp.
## perl cgi that uses DELI's UAProfileValidator

## Redistribution and use in source and binary forms, with or without
## modification, are permitted provided that the following conditions
## are met:
## 1. Redistributions of source code must retain the above copyright
## notice, this list of conditions and the following disclaimer.
## 2. Redistributions in binary form must reproduce the above copyright
## notice, this list of conditions and the following disclaimer in the
## documentation and/or other materials provided with the distribution.
## 3. The name of the author may not be used to endorse or promote products
## derived from this software without specific prior written permission.
## THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
## IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
## OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
## IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
## INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
## NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
## DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
## THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
## (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
## THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

use CGI qw(:standard);
use CGI::Carp qw/fatalsToBrowser/;

## customise these for your particular installation

## uaprofile file is uploaded to this directory. If changed, set file/directory access correctly
$savedir = "/tmp"; 

## where java is installed
$javaHome = '/usr/java';

## where java sdk is installed
$javaSDK = '/usr/j2sdk1.4.0';

## where DELI is installed
$deliHome = '/usr/local/deli';

## where tomcat is installed
$catalinaHome = '/var/tomcat4';

## where ant is installed
$antHome = '/usr/local/ant';

## path required
$path = '/bin:/usr/bin:/usr/local/bin:/usr/local/ant/bin';

## end of customisation section

$deliLib = $deliHome = '/lib/';

## classpath
$classpath = $deliLib + 'antlr.jar:' + 
	$deliLib + 'bsf.jar:' + 
	$deliLib + 'deli.jar:' +
	$deliLib + 'deliTest.jar:' + 
	$deliLib + 'deliTestClient.jar:' +
	$deliLib + 'jena.jar:' + 
	$deliLib + 'junit.jar:' + 
	$deliLib + 'servlet.jar:' + 
	$deliLib + 'xercesImpl.jar:' + 
	$deliLib + 'xml-apis.jar:' + 
	$deliLib + 'xmlParserAPIs.jar:' +
	$deliLib + 'commons-logging.jar:' +
	$deliLib + 'concurrent.jar:' + 
	$deliLib + 'log4j-1.2.7.jar:' + 
	$deliLib + 'icu4j.jar:' + 
	$deliLib + 'rdf-api-2001-01-19.jar:' + 
	$deliLib + 'jakarta-oro-2.0.5.jar:' + 
	$deliLib + 'buildTools/httpunit-1.4.jar:' + 
	$deliLib + 'buildTools/junit-3.7.jar:' +
	$deliLib + 'buildTools/Tidy.jar' +
	$javaSDK + '/lib/tools.jar:';

%extensions = map { $_ => 1 } qw(xml XML rdf RDF);
$query = new CGI;
## set environment variables....maybe all of them are not needed...
$ENV{'JAVA_HOME'} = $javaHome;
$ENV{'DELI_HOME'} = $deliHome; 
$ENV{'CATALINA_HOME'} = $catalinaHome;
$ENV{'ANT_HOME'} = $antHome;
$ENV{'PATH'} = $path;
$ENV{'CLASSPATH'} = $classpath;

### main ###
if ($file=param('filename')) {
   ($filename = $file ) =~ s/.*[\/\\](.*)/$1/; ### Windows:take only filename e.g. N6600r100.xml, not like c:\temp\N6600r100.xml
   ($pre,$ext) = split(/\./,$filename);
   check_ext($ext);
   save_file();
   process_profile();

} else {
    show_form();
}## end main ###

### subroutine that prints html file-upload form ###
sub show_form {
    print $query->header();
    print $query->start_html("DELI UAProf validator"),
     h1('UAProf validator'),
     $query->start_multipart_form(),
     p,
     p,
     "Use this form to upload the UAProf file and let <a href=\"http://www-uk.hpl.hp.com/people/marbut/deli/\">DELI's</a> validation tool to check it.",
     br,
     "Pass criteria: The Profile is validated without errors.",
    p,
     "Choose uaprofile file (xml/rdf):",
     p,
     $query->filefield(-name=>'filename',
                       -size=>45
		       -maxsize=>100),
     p,
     $query->submit(-name=>'submit',
                    -value=>'Upload and validate profile'),
     $query->endform,
    $query->end_html;
} ## end show_form

### subroutine that checks that the file is either xml or rdf ###
sub check_ext {
    my($ex)= @_;
    if (!$extensions{$ex}){
	print $query->header(),"\n",
	$query->start_html("DELI UAProf validator"),"\n",
	"UAProf file must have extension xml or rdf",p,"\n",
a({ 
    href => "/cgi-bin/deli_val.pl",
    style => "text-decoration:underline;color:red;"},
    "Back to UAProf validator"),<br>,
	p,"\n",
    $query->end_html;
    exit(0);
}
} ##end check_ext 

### subroutine that saves the uploaded uaprofile file to /tmp.  ###
sub save_file {
    open (SAVEFILE, ">$savedir/$filename");
    while (read($file, $buffer, 1024)) {
	print SAVEFILE $buffer;
    }
    close(SAVEFILE);
}##end save_file

### subroutine that uses DELI's uaprofile validator and echoes it's output back to client  ###
sub process_profile {
    $| = 1;
    print "Expires: Mon, 26 Jul 1997 05:00:00 GMT" . "\n";
    print "Cache-Control: no-store, no-cache, must-revalidate" . "\n";
    print "\"Cache-Control: post-check=0, pre-check=0\", false". "\n";
    print "Pragma: no-cache" . "\n";
    print $query->header() . "\n\n";
    print $query->start_html("DELI UAProf validator");
    print "<pre>\n";
    system "cd /usr/local/deli;java -classpath $ENV{'CLASSPATH'} com.hp.hpl.deli.UAProfValidator $savedir/$filename 2>/dev/null" || die "cannot exec $\n";
    print "</pre>\n";
    print a({href => "/cgi-bin/deli_val.pl",
	 style => "text-decoration:underline;color:red;"}, 
    "Back to UAProf validator"),
    $query->end_html;
    ## delete uploaded profile from /tmp
    unlink("$savedir/$filename") || die "Could not delete temp file $savedir/$filename: $!\n";
    exit(0);
} ## end process_profile
