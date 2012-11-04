#!/bin/sh

base=`pwd`

cd /Applications/eclipse/dropins/eclipse/features/org.infinitest.eclipse.feature_5.1.104 ; jar xf ${base}/infinitest-eclipse/target/update_site/features/org.infinitest.eclipse.feature_5.1.104.jar 

cd /Applications/eclipse/dropins/eclipse/plugins/org.infinitest.eclipse_5.1.104 ; jar -xf ${base}/infinitest-eclipse/target/update_site/plugins/org.infinitest.eclipse_5.1.104.jar 

