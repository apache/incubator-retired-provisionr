/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.provisionr.core.activities;

import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.api.software.Software;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DownloadFilesTest {

    @Test
    public void testCreatePuppetScript() throws Exception {
        Software software = Software.builder()
            .file("http://provisionr.incubator.apache.org/test.tar.gz", "/opt/test.tar.gz")
            .file("http://google.com", "/opt/google.html")
            .createSoftware();

        Pool pool = mock(Pool.class);
        when(pool.getSoftware()).thenReturn(software);

        PuppetActivity activity = new DownloadFiles();
        String content = activity.createPuppetScript(pool, null);

        assertThat(content)
            .contains("download_file {\"/opt/test.tar.gz\":\n" +
                "  uri => \"http://provisionr.incubator.apache.org/test.tar.gz\"\n" +
                "}")
            .contains("download_file {\"/opt/google.html\":\n" +
                "  uri => \"http://google.com\"\n" +
                "}");

        assertThat(activity.createAdditionalFiles(pool, null)).isEmpty();
    }
}
