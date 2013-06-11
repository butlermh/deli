#!/bin/sh -f

echo "Content-type: text/html"

echo ""

echo "<strong>======= DELI Validator Running =======</strong><br>"

value="`echo $QUERY_STRING | awk -F= '{print $2}' | sed 's/%3A/:/'g | sed 's/%2F/\//g'`"

echo "Looking at UAProf <font color=\"#FF0000\">$value</font><br>"

echo " It's "

date

echo "<br><font color=\"#0000FF\">Please wait... this may take up to 30 seconds!</font><br>"

#echo "</h1>"
echo "<HTML><BODY><pre>"

cd /opt/deli/

timestamp=`date '+%m%d%y%H%M%S'`

uaproffilename=`/opt/deli/getuaprofname $value`

/opt/UAProf_checker/links -source $value > ./$uaproffilename.$timestamp


/usr/bin/java -Xms32M -Xmx512M -Djava.endorsed.dirs=lib/endorsed -classpath "tools/lib/ant-launcher.jar" -Dant.home="tools" -Dant.library.dir="tools/lib" org.apache.tools.ant.launch.Launcher -lib "/opt/deli/lib/buildTools/Tidy.jar:/opt/deli/lib/buildTools/junit-3.7.jar:/opt/deli/lib/buildTools/httpunit-1.4.jar:/opt/deli/lib/xml-apis.jar:/opt/deli/lib/servlet.jar:/opt/deli/lib/rdf-api-2001-01-19.jar:/opt/deli/lib/log4j-1.2.7.jar:/opt/deli/lib/junit.jar:/opt/deli/lib/jena.jar:/opt/deli/lib/jakarta-oro-2.0.5.jar:/opt/deli/lib/icu4j.jar:/opt/deli/lib/deliTestClient.jar:/opt/deli/lib/deliTest.jar:/opt/deli/lib/deli.jar:/opt/deli/lib/concurrent.jar:/opt/deli/lib/commons-logging.jar:/opt/deli/lib/antlr.jar:/opt/deli/lib/xmlParserAPIs.jar:/opt/deli/lib/xercesImpl.jar:lib/endorsed/*.jar" "-logger" "org.apache.tools.ant.NoBannerLogger" "-emacs" "validate" "-Dprofile=./$uaproffilename.$timestamp" > ./delivali.tmp.$timestamp

/usr/bin/java -Xms32M -Xmx512M -Djava.endorsed.dirs=lib/endorsed -classpath "tools/lib/ant-launcher.jar" -Dant.home="tools" -Dant.library.dir="tools/lib" org.apache.tools.ant.launch.Launcher -lib "/opt/deli/lib/buildTools/Tidy.jar:/opt/deli/lib/buildTools/junit-3.7.jar:/opt/deli/lib/buildTools/httpunit-1.4.jar:/opt/deli/lib/xml-apis.jar:/opt/deli/lib/servlet.jar:/opt/deli/lib/rdf-api-2001-01-19.jar:/opt/deli/lib/log4j-1.2.7.jar:/opt/deli/lib/junit.jar:/opt/deli/lib/jena.jar:/opt/deli/lib/jakarta-oro-2.0.5.jar:/opt/deli/lib/icu4j.jar:/opt/deli/lib/deliTestClient.jar:/opt/deli/lib/deliTest.jar:/opt/deli/lib/deli.jar:/opt/deli/lib/concurrent.jar:/opt/deli/lib/commons-logging.jar:/opt/deli/lib/antlr.jar:/opt/deli/lib/xmlParserAPIs.jar:/opt/deli/lib/xercesImpl.jar:lib/endorsed/*.jar" "-logger" "org.apache.tools.ant.NoBannerLogger" "-emacs" "validate" "-Dprofile=./$uaproffilename.$timestamp"

grep "PROFILE IS VALID" /opt/deli/delivali.tmp.$timestamp > /dev/null 2>&1

if [ "$?" -eq "PROFILE IS VALID" ]; then
    echo "<strong>"
    echo "Your UAProf is CORRECT!<BR>"
    echo "Do you want to OMA Validate your UAProf?"
    echo "</strong>"
    echo "<form method=get action=\"omavali.cgi\">"
    echo "<input type=string SIZE=\"70\" name=finger MAXLENGTH=\"256\" value=\"$value\" READONLY>"
    echo "<input type=submit value=\"Validate UAProf\">"
    echo "</form>"
    echo ""
else
    echo "<br>"
    echo "<br>"
    echo "<br>"
    echo "<br>"
    echo "<strong>"
    echo "Please fix these errors and try to re-validate again!"
    echo "</strong>"
    echo ""
fi

echo "<table border=\"1\" width=\"425\">"
echo "<tr>"
echo "<td width=\"162\"><pre><a href=\"../\">VAULT Directory</a> </pre>"
echo "</td>"
echo "<td width=\"202\"><pre> <a href=\"../VALIDATED/\">VALIDATED Directory</a></pre>"
echo "</td>"
echo "<td width=\"114\"><pre>  <a href=\"deli-faq.html\">DELI FAQ</a> </pre>"
echo "</td>"
echo "</tr>"
echo "</table>"

echo "</BODY></HTML>"

exit 0

