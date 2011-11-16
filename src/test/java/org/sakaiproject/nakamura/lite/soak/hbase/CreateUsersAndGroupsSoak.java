/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.nakamura.lite.soak.hbase;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.sakaiproject.nakamura.api.lite.ClientPoolException;
import org.sakaiproject.nakamura.api.lite.Configuration;
import org.sakaiproject.nakamura.api.lite.StorageClientException;
import org.sakaiproject.nakamura.api.lite.StorageClientUtils;
import org.sakaiproject.nakamura.api.lite.accesscontrol.AccessDeniedException;
import org.sakaiproject.nakamura.lite.ConfigurationImpl;
import org.sakaiproject.nakamura.lite.soak.AbstractSoakController;
import org.sakaiproject.nakamura.lite.soak.authorizable.CreateUsersAndGroupsClient;
import org.sakaiproject.nakamura.lite.storage.StorageClientPool;
import org.sakaiproject.nakamura.lite.storage.hbase.HBaseStorageClientPool;

import java.io.IOException;
import java.util.Map;

public class CreateUsersAndGroupsSoak extends AbstractSoakController {
    private int totalUsers;
    private StorageClientPool connectionPool;
    private Configuration configuration;

    public CreateUsersAndGroupsSoak(int totalUsers, StorageClientPool connectionPool, Configuration configuration) {
        super(totalUsers);
        this.connectionPool = connectionPool;
        this.configuration = configuration;
        this.totalUsers = totalUsers;
    }

    protected Runnable getRunnable(int nthreads) throws ClientPoolException,
            StorageClientException, AccessDeniedException {
        int usersPerThread = totalUsers / nthreads;
        return new CreateUsersAndGroupsClient(usersPerThread, connectionPool, configuration);
    }

    public static void main(String[] argv) throws ClientPoolException, StorageClientException,
            AccessDeniedException, ClassNotFoundException, IOException {

        int totalUsers = 1000;
        int nthreads = 10;

        if (argv.length > 0) {
            nthreads = StorageClientUtils.getSetting(Integer.valueOf(argv[0]), nthreads);
        }
        if (argv.length > 1) {
            totalUsers = StorageClientUtils.getSetting(Integer.valueOf(argv[1]), totalUsers);
        }
        ConfigurationImpl configuration = new ConfigurationImpl();
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("keyspace", "n");
        properties.put("acl-column-family", "ac");
        properties.put("authorizable-column-family", "au");
        properties.put("content-column-family", "cn");
        configuration.activate(properties);

        CreateUsersAndGroupsSoak createUsersAndGroupsSoak = new CreateUsersAndGroupsSoak(
                totalUsers, getConnectionPool(configuration), configuration);
        createUsersAndGroupsSoak.launchSoak(nthreads);
    }

    protected static StorageClientPool getConnectionPool(Configuration configuration) throws ClassNotFoundException {
        HBaseStorageClientPool hp = new HBaseStorageClientPool();
        hp.activate(ImmutableMap.of("test", (Object) "test",
                Configuration.class.getName(), configuration));
        return hp;
    }

}
