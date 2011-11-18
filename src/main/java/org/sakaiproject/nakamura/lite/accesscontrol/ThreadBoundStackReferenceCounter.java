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
package org.sakaiproject.nakamura.lite.accesscontrol;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Maintains a thread bound reference counter that can be suspended and resumed.
 * When suspended the current counter us pushed to a stack, and the new counter
 * is started. When resumed, the current counter is replaced with the counter on
 * the stack. The operations are all bound to the current thread. inc() dec()
 * and suspend() resume() should be used in matching pairs protected by try {
 * ... } finally { ... } constructs. The class makes no attempt to guess what
 * the code is doing.
 * 
 * @author ieb
 * 
 */
public class ThreadBoundStackReferenceCounter {

    // dont use initial value to avoid JVM bugs.
    private ThreadLocal<Integer> counter = new ThreadLocal<Integer>();
    private ThreadLocal<List<Integer>> suspended = new ThreadLocal<List<Integer>>();

    public void inc() {
        set(get() + 1);
    }

    public void dec() {
        set(get() - 1);
    }

    public void suspend() {
        push(get());
        set(0);
    }

    public void resume() {
        set(pop());
    }

    public boolean isSet() {
        return get() > 0;
    }

    private int get() {
        Integer c = counter.get();
        if (c == null) {
            return 0;
        }
        return c.intValue();
    }

    private void set(int i) {
        if (i < 0) {
            i = 0;
        }
        counter.set(i);
    }

    private void push(int i) {
        List<Integer> s = suspended.get();
        if (s == null) {
            s = Lists.newArrayList();
            suspended.set(s);
        }
        s.add(i);
    }

    private int pop() {
        List<Integer> s = suspended.get();
        if (s == null) {
            s = Lists.newArrayList();
            suspended.set(s);
        }
        if (s.size() == 0) {
            return 0;
        }
        return s.remove(s.size() - 1);
    }

}
