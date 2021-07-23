/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.shared.allowlist;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AllowList
        implements Set<String> {

    private Set<String> allowList = new HashSet<String>();

    public AllowList() {

    }

    public AllowList(final Collection<String> packageNames ) {
        allowList.addAll( packageNames );
    }

    @Override
    public int size() {
        return allowList.size();
    }

    @Override
    public boolean isEmpty() {
        return allowList.isEmpty();
    }

    @Override
    public boolean contains( final Object o ) {
        return allowList.contains( o );
    }

    @Override
    public Iterator<String> iterator() {
        return allowList.iterator();
    }

    @Override
    public Object[] toArray() {
        return allowList.toArray();
    }

    @Override
    public <T> T[] toArray( final T[] ts ) {
        return allowList.toArray( ts );
    }

    @Override
    public boolean add( final String s ) {
        return allowList.add( s );
    }

    @Override
    public boolean remove( final Object o ) {
        return allowList.remove( o );
    }

    @Override
    public boolean containsAll( final Collection<?> collection ) {
        return allowList.containsAll( collection );
    }

    @Override
    public boolean addAll( final Collection<? extends String> collection ) {
        return allowList.addAll( collection );
    }

    @Override
    public boolean retainAll( final Collection<?> collection ) {
        return allowList.retainAll( collection );
    }

    @Override
    public boolean removeAll( final Collection<?> collection ) {
        return allowList.removeAll( collection );
    }

    @Override
    public void clear() {
        allowList.clear();
    }

    public boolean containsAny( final Collection<String> packages ) {
        for ( String aPackage : packages ) {
            if ( contains( aPackage ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return allowList.hashCode();
    }
}
