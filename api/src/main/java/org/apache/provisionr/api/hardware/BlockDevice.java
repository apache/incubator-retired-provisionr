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

package org.apache.provisionr.api.hardware;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;

import java.util.Map;

public class BlockDevice extends WithOptions {

    private int size;
    private String name;

    public static BlockDeviceBuilder builder() {
        return new BlockDeviceBuilder();
    }

    BlockDevice(int size, String name, Map<String, String> options) {
        super(options);
        checkArgument(size > 0, "the block device size should be a positive integer");
        this.size = size;
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(size, name, getOptions());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockDevice other = (BlockDevice) obj;
        return Objects.equal(this.size, other.size)
            && Objects.equal(this.name, this.name)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "BlockDevice {" +
            "size=" + size +
            "name=" + name +
            ", options=" + getOptions() + "}";
    }

}
