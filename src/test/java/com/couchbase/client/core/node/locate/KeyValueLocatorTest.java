/**
 * Copyright (C) 2014 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */
package com.couchbase.client.core.node.locate;

import com.couchbase.client.core.config.ClusterConfig;
import com.couchbase.client.core.config.CouchbaseBucketConfig;
import com.couchbase.client.core.config.DefaultNodeInfo;
import com.couchbase.client.core.config.DefaultPartition;
import com.couchbase.client.core.config.NodeInfo;
import com.couchbase.client.core.config.Partition;
import com.couchbase.client.core.message.kv.GetRequest;
import com.couchbase.client.core.node.Node;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies the functionality of the {@link KeyValueLocator}.
 *
 * @author Michael Nitschinger
 * @since 1.0.0
 */
public class KeyValueLocatorTest {

    @Test
    public void shouldLocateGetRequestForCouchbaseBucket() throws Exception {
        Locator locator = new KeyValueLocator();

        GetRequest getRequestMock = mock(GetRequest.class);
        ClusterConfig configMock = mock(ClusterConfig.class);
        Set<Node> nodes = new HashSet<Node>();
        Node node1Mock = mock(Node.class);
        when(node1Mock.hostname()).thenReturn(InetAddress.getByName("192.168.56.101"));
        Node node2Mock = mock(Node.class);
        when(node2Mock.hostname()).thenReturn(InetAddress.getByName("192.168.56.102"));
        nodes.addAll(Arrays.asList(node1Mock, node2Mock));
        CouchbaseBucketConfig bucketMock = mock(CouchbaseBucketConfig.class);
        when(getRequestMock.bucket()).thenReturn("bucket");
        when(getRequestMock.key()).thenReturn("key");
        when(configMock.bucketConfig("bucket")).thenReturn(bucketMock);
        when(bucketMock.partitions()).thenReturn(Arrays.asList(
            new DefaultPartition((short) 0, new short[] {1}),
            new DefaultPartition((short) 0, new short[] {1}),
            new DefaultPartition((short) 1, new short[] {0}),
            (Partition) new DefaultPartition((short) 1, new short[] {0})
        ));
        when(bucketMock.partitionHosts()).thenReturn(Arrays.asList(
            (NodeInfo) new DefaultNodeInfo("foo", "192.168.56.101:11210", Collections.EMPTY_MAP),
            new DefaultNodeInfo("foo", "192.168.56.102:11210", Collections.EMPTY_MAP)
        ));

        Node[] foundNodes = locator.locate(getRequestMock, nodes, configMock);
        assertEquals(node1Mock, foundNodes[0]);
    }

}
