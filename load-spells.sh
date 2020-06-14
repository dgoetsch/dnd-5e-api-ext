#!/bin/bash
URL_BASE='https://www.dnd5eapi.co'
RESOURCE_TYPE='spells'
mkdir $RESOURCE_TYPE
cat "$RESOURCE_TYPE.txt" | while read line || [[ -n $line ]];
do
  RESOURCE_NAME=`echo "$line" | sed 's/ /+/g'`
  RESOURCE_PATH=`curl $URL_BASE/api/$RESOURCE_TYPE?name=$RESOURCE_NAME | jq -r '.results[].url'`
  curl $URL_BASE$RESOURCE_PATH > $RESOURCE_TYPE/$RESOURCE_NAME.json
done