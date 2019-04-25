#! /bin/bash

## define variables

echo 'begin to replace related parameters from customer input.'

if test $# -ne 3
then
  echo 'must be have three input to execute:subscription_key region license'
  exit 0  
fi  
sconfig="/cc/script/application-arm.properties"
vconfig="/cc/run/callcenter/application-arm.properties"
lconfig="/cc/run/callcenter/license.lic"


vkey="$1"
vregion="$2"
vlicense="$3"


## copy source template to target

cp $sconfig $vconfig

sed -i "s|AZURESBKEY|$vkey|g" "$vconfig"
sed -i "s|AZURESBREGION|$vregion|g" "$vconfig"

if test -e $lconfig
then
  echo 'one license file has existed, will be remove.'
  rm $lconfig
fi

echo "write license info into $lconfig "

echo $vlicense >>  $lconfig
