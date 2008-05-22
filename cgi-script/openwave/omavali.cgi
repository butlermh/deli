#!/bin/sh -f

echo "Content-type: text/html"

echo ""

value="`echo $QUERY_STRING | awk -F= '{print $2}' | sed 's/%3A/:/'g | sed 's/%2F/\//g'`"

cd /opt/deli/

timestamp=`date '+%m%d%y%H%M%S'`

uaproffilename=`/opt/deli/getuaprofname $value`

/opt/UAProf_checker/links -source $value > ./$uaproffilename.$timestamp

cat /opt/deli/header.txt > ./$uaproffilename.$timestamp.tmp
echo '<!--  OMA BAC-UAPROF DELI Ver 0.9.7 Validated -' $timestamp '-->' >> ./$uaproffilename.$timestamp.tmp
cat 
cat ./$uaproffilename.$timestamp | grep -v -i "^<?xml" >> ./$uaproffilename.$timestamp.tmp

cat ./$uaproffilename.$timestamp.tmp

mv ./$uaproffilename.$timestamp.tmp /spare/www/VAULT/VALIDATED/$uaproffilename.$timestamp

vendor=`./getvendor /spare/www/VAULT/VALIDATED/$uaproffilename.$timestamp`

year=`date | awk '{ print $6 }'`

vendor=$vendor$year.txt

echo "$value - see $uaproffilename.$timestamp" >> /spare/www/VAULT/$vendor

exit 0
