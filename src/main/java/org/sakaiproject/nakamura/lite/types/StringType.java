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
package org.sakaiproject.nakamura.lite.types;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringType implements Type<String> {


    private static int lengthLimit;

    public int getTypeId() {
        return 0;
    }

    public void save(DataOutputStream dos, Object object) throws IOException {
        dos.writeUTF((String) object);
    }

    public String load(DataInputStream in) throws IOException {
        return in.readUTF();
    }

    public Class<String> getTypeClass() {
        return String.class;
    }

    public boolean accepts(Object object) {
        if ( object instanceof String) {
            if (StringType.lengthLimit > 0 &&  ((String) object).length() > StringType.lengthLimit ) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public static int getLengthLimit() {
        return lengthLimit;
    }
    
    public static void setLengthLimit(int stringLengthLimit) {
        StringType.lengthLimit = stringLengthLimit;
    }

    
    

}
