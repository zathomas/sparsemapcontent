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
package org.sakaiproject.nakamura.lite;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.sakaiproject.nakamura.api.lite.ClientPoolException;
import org.sakaiproject.nakamura.api.lite.Configuration;
import org.sakaiproject.nakamura.api.lite.Repository;
import org.sakaiproject.nakamura.api.lite.Session;
import org.sakaiproject.nakamura.api.lite.StorageClientException;
import org.sakaiproject.nakamura.api.lite.StoreListener;
import org.sakaiproject.nakamura.api.lite.accesscontrol.AccessDeniedException;
import org.sakaiproject.nakamura.api.lite.accesscontrol.PrincipalValidatorResolver;
import org.sakaiproject.nakamura.api.lite.authorizable.User;
import org.sakaiproject.nakamura.lite.accesscontrol.AuthenticatorImpl;
import org.sakaiproject.nakamura.lite.authorizable.AuthorizableActivator;
import org.sakaiproject.nakamura.lite.storage.StorageClient;
import org.sakaiproject.nakamura.lite.storage.StorageClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component(immediate = true, metatype = true)
@Service(value = Repository.class)
public class RepositoryImpl implements Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryImpl.class);

    @Reference
    protected Configuration configuration;

    @Reference
    protected StorageClientPool clientPool;
    
    @Reference 
    protected StoreListener storeListener;

    @Reference
    protected PrincipalValidatorResolver principalValidatorResolver;


    public RepositoryImpl() {
    }

    public RepositoryImpl(Configuration configuration, StorageClientPool clientPool,
            LoggingStorageListener listener) {
        this.configuration = configuration;
        this.clientPool = clientPool;
        this.storeListener = listener;
    }

    @Activate
    public void activate(Map<String, Object> properties) throws ClientPoolException,
            StorageClientException, AccessDeniedException {
        StorageClient client = null;
        try {
            client = clientPool.getClient();
            AuthorizableActivator authorizableActivator = new AuthorizableActivator(client,
                    configuration);
            authorizableActivator.setup();
        } finally {
            if ( client != null ) {
              client.close();
            } else {
                LOGGER.error("Failed to actvate repository, probably failed to create default users");
            }
        }
    }

    @Deactivate
    public void deactivate(Map<String, Object> properties) throws ClientPoolException {
    }

    public Session login(String username, String password) throws ClientPoolException,
            StorageClientException, AccessDeniedException {
        return openSession(username, password);
    }

    public Session login() throws ClientPoolException, StorageClientException,
            AccessDeniedException {
        return openSession(User.ANON_USER);
    }

    public Session loginAdministrative() throws ClientPoolException, StorageClientException,
            AccessDeniedException {
        return openSession(User.ADMIN_USER);
    }

    public Session loginAdministrative(String username) throws StorageClientException,
            ClientPoolException, AccessDeniedException {
        return openSession(username);
    }

    private Session openSession(String username, String password) throws StorageClientException,
            AccessDeniedException {
        StorageClient client = null;
        try {
            client = clientPool.getClient();
            AuthenticatorImpl authenticatorImpl = new AuthenticatorImpl(client, configuration);
            User currentUser = authenticatorImpl.authenticate(username, password);
            if (currentUser == null) {
                throw new StorageClientException("User " + username + " cant login with password");
            }
            return new SessionImpl(this, currentUser, client, configuration, clientPool.getStorageCacheManager(), storeListener, principalValidatorResolver);
        } catch (ClientPoolException e) {
            clientPool.getClient();
            throw e;
        } catch (StorageClientException e) {
            clientPool.getClient();
            throw e;
        } catch (AccessDeniedException e) {
            clientPool.getClient();
            throw e;
        } catch (Throwable e) {
            clientPool.getClient();
            throw new StorageClientException(e.getMessage(), e);
        }
    }

    private Session openSession(String username) throws StorageClientException,
            AccessDeniedException {
        StorageClient client = null;
        try {
            client = clientPool.getClient();
            AuthenticatorImpl authenticatorImpl = new AuthenticatorImpl(client, configuration);
            User currentUser = authenticatorImpl.systemAuthenticate(username);
            if (currentUser == null) {
                throw new StorageClientException("User " + username
                        + " does not exist, cant login administratively as this user");
            }
            return new SessionImpl(this, currentUser, client, configuration,  clientPool.getStorageCacheManager(), storeListener, principalValidatorResolver);
        } catch (ClientPoolException e) {
            clientPool.getClient();
            throw e;
        } catch (StorageClientException e) {
            clientPool.getClient();
            throw e;
        } catch (AccessDeniedException e) {
            clientPool.getClient();
            throw e;
        } catch (Throwable e) {
            clientPool.getClient();
            throw new StorageClientException(e.getMessage(), e);
        }
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setConnectionPool(StorageClientPool connectionPool) {
        this.clientPool = connectionPool;
    }

    public void setStorageListener(StoreListener storeListener) {
        this.storeListener = storeListener;
        
    }

}
