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
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.io.Resources;
import java.io.IOException;
import org.apache.provisionr.core.BaseJaxbTest;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import org.junit.Test;

public class XmlTemplateTest extends BaseJaxbTest {

    @Test
    public void testSerializeBasicTemplateAsXml() throws Exception {
        XmlTemplate template = new XmlTemplate();
        template.setId("jenkins");

        template.setDescription("Just testing");
        template.setOsVersion("10.04 LTS");

        template.setPackages(newArrayList("jenkins", "git-core", "subversion"));
        template.setPorts(newArrayList(8080, 22));

        template.setFiles(newArrayList(
            new FileEntry("http://google.com", "/opt/google.html")
        ));

        template.setRepositories(newArrayList(
            new RepositoryEntry("jenkins", newArrayList("deb ..."), "-----BEGIN PGP PUBLIC KEY BLOCK----- ...")
        ));

        String actual = asXml(template);
        assertXMLEqual(actual, readResource("templates/test.xml"), actual);
    }

    private String readResource(String resource) throws IOException {
        return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
    }

    @Override
    public Class[] getContextClasses() {
        return new Class[]{XmlTemplate.class};
    }
}
