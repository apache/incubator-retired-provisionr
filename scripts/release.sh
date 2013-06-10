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

RELEASE_VERSION=$1
DEVELOPMENT_VERSION=$2

mvn -Pwith-assembly release:clean release:prepare -DreleaseVersion=$RELEASE_VERSION \
    -Dtag=provisionr-$RELEASE_VERSION -DdevelopmentVersion=$DEVELOPMENT_VERSION -DpushChanges=false

mvn -Pwith-assembly clean release:perform -DconnectionUrl=scm:git:file://`pwd`/.git \
    -Dtag=provisionr-$RELEASE_VERSION -Dgoals="package deploy -DskipTests"

git checkout provisionr-$RELEASE_VERSION

mvn -Pwith-assembly -DskipTests -DskipKarafTests clean package

git checkout master

echo "Done. Please upload the artifacts from karaf/assembly/target and call a vote"
echo "If the vote is succesfull: git push && git push --tags"

