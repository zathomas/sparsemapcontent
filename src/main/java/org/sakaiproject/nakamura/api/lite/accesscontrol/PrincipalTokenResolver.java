/**
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
package org.sakaiproject.nakamura.api.lite.accesscontrol;

import org.sakaiproject.nakamura.api.lite.content.Content;

import java.util.List;

/**
 * Resolves proxyPrincipals to tokens. An implementation of this will be
 * provided by the caller if principal tokens are to be resolved. This
 * implementation should bind to the user in question.
 */
public interface PrincipalTokenResolver {

    /**
     * Resolve the principal.
     *
     * @param principal
     * @return the tokens associated with the proxyPrincipal, could be more than
     *         one. 
     */
    List<Content> resolveTokens(String principal);

}
