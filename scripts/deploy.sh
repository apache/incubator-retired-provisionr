#!/bin/bash
#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

# utility script to automatically unzip and setup AWS credentails from Mavem for snapshot builds
if [ $# -ne 2 ]; then
	echo "The script must take two parameters - the destination folder (must exist) and the snapshot version you are trying to deploy."
	exit 0
fi
rm -rf $1/provisionr
archive=$(basename "../karaf/assembly/target/org.apache.provisionr-$2-SNAPSHOT.tar.gz") 
cp karaf/assembly/target/$archive $1 && cd $1
tar xvzf $archive && rm $archive
mv "${archive%.*.*}" provisionr
cd provisionr/
for w in "secretKey" "accessKey"
do
        key=$(grep -E -m 1 -o "<.*amazon.*$w>(.*)</.*amazon.*$w>" ~/.m2/settings.xml | sed -e 's,.*<*.>\([^<]*\)</.*>.*,\1,g')
        sed -i -e "s/$w = .*$/$w = $key/g" ~/provisionr/system/org/apache/provisionr/provisionr-amazon/0.4.0-incubating-SNAPSHOT/provisionr-amazon-0.4.0-incubating-SNAPSHOT-defaults.cfg
done
