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

/**
 * Validates a principal Token.
 */
public interface PrincipalValidatorPlugin {

    /**
     * Validate the token to see if its current. This should not need to consider
     * the user since if the user is relevant they will have access to the token,
     * if not, the token would not have been resolved for the user.
     *
     * @param proxyPrincipalToken
     * @return true if the principal is valid, and the user who resolved it can
     *         have the principal.
     */
    boolean validate(Content proxyPrincipalToken);

    /**
     * @return a list of fields that must be protected, these are incorporated
     * into the hmac to ensure no tampering.
     */
    String[] getProtectedFields();

}
