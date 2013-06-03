/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.provisionr.core.templates.xml;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import org.apache.provisionr.api.network.Network;
import org.apache.provisionr.api.network.Rule;
import org.apache.provisionr.api.software.Software;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class ExampleTemplatesTest {

    public static final String DEFAULT_JENKINS_TEMPLATE = "org/apache/provisionr/examples/templates/jenkins.xml";

    public static final String DEFAULT_CDH3_TEMPLATE = "org/apache/provisionr/examples/templates/cdh3.xml";

    @Test
    public void testLoadDefaultCdh3Template() throws Exception {
        XmlTemplate template = XmlTemplate.newXmlTemplate(readResource(DEFAULT_CDH3_TEMPLATE));

        assertThat(template.getId()).isEqualTo("cdh3");
        assertThat(template.getPackages()).contains("hue");
        assertThat(template.getOsVersion()).isEqualTo("10.04 LTS");

        assertThat(template.getRepositories()).hasSize(2);
        assertThat(template.getFiles()).hasSize(1);
    }

    @Test
    public void testApplyCdh3TemplateToNetworkAndSoftware() throws Exception {
        XmlTemplate template = XmlTemplate.newXmlTemplate(readResource(DEFAULT_CDH3_TEMPLATE));

        Network network = template.apply(Network.builder().createNetwork());
        assertThat(network.getIngress()).contains(
            Rule.builder().anySource().tcp().port(8080).createRule());


        Software software = template.apply(Software.builder().createSoftware());
        assertThat(software.getPackages()).contains("hue").contains("hadoop-0.20");

        assertThat(software.getFiles()).hasSize(1);
        assertThat(software.getRepositories()).hasSize(2);
    }

    @Test
    public void testLoadDefaultJenkinsTemplate() throws Exception {
        XmlTemplate template = XmlTemplate.newXmlTemplate(readResource(DEFAULT_JENKINS_TEMPLATE));

        assertThat(template.getPorts()).contains(8080);
        assertThat(template.getRepositories()).hasSize(1);

        assertThat(template.getPackages()).contains("jenkins").contains("git-core");
    }

    private String readResource(String resource) throws IOException {
        return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
    }
}
