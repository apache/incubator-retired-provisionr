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

package org.apache.provisionr.core;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

/**
 * Utility functions for rendering mustache templates as strings
 */
public final class Mustache {

    private Mustache() {
    }

    public static String toString(Class<?> contextClass, String resource, Map<String, ?> scopes)
        throws IOException {
        return toString(Resources.getResource(contextClass, resource), scopes);
    }

    /**
     * Render a Mustache template as a String
     *
     * @param resource url to resource
     * @param scopes
     * @return
     * @throws IOException
     */
    public static String toString(URL resource, Map<String, ?> scopes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, Charsets.UTF_8);

        String content = Resources.toString(resource, Charsets.UTF_8);

        MustacheFactory factory = new DefaultMustacheFactory();
        factory.compile(new StringReader(content), resource.toString())
            .execute(writer, scopes);

        writer.close();
        return outputStream.toString("UTF-8");
    }

}
