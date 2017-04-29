/*
 * Copyright 2017 Otavio Santana and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jnosql.artemis.column.spi;


import org.jnosql.artemis.DatabaseQualifier;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.column.ColumnTemplateAsync;
import org.jnosql.artemis.column.ColumnRepositoryAsyncProducer;
import org.jnosql.diana.api.column.ColumnFamilyManagerAsync;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

class ColumnRepositoryAsyncBean implements Bean<ColumnTemplateAsync>, PassivationCapable {

    private final BeanManager beanManager;

    private final Set<Type> types;

    private final String provider;

    private final Set<Annotation> qualifiers;

    /**
     * Constructor
     *
     * @param beanManager the beanManager
     * @param provider    the provider name, that must be a
     */
    public ColumnRepositoryAsyncBean(BeanManager beanManager, String provider) {
        this.beanManager = beanManager;
        this.types = Collections.singleton(ColumnTemplateAsync.class);
        this.provider = provider;
        this.qualifiers = Collections.singleton(DatabaseQualifier.ofColumn(provider));
    }

    @Override
    public Class<?> getBeanClass() {
        return ColumnTemplateAsync.class;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public ColumnTemplateAsync create(CreationalContext<ColumnTemplateAsync> creationalContext) {

        ColumnRepositoryAsyncProducer producer = getInstance(ColumnRepositoryAsyncProducer.class);
        ColumnFamilyManagerAsync columnFamilyManager = getColumnFamilyManager();
        return producer.get(columnFamilyManager);
    }

    private ColumnFamilyManagerAsync getColumnFamilyManager() {
        Bean<ColumnFamilyManagerAsync> bean = (Bean<ColumnFamilyManagerAsync>) beanManager.getBeans(ColumnFamilyManagerAsync.class,
                DatabaseQualifier.ofColumn(provider)).iterator().next();
        CreationalContext<ColumnFamilyManagerAsync> ctx = beanManager.createCreationalContext(bean);
        return (ColumnFamilyManagerAsync) beanManager.getReference(bean, ColumnFamilyManagerAsync.class, ctx);
    }


    private <T> T getInstance(Class<T> clazz) {
        Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }

    private <T> T getInstance(Class<T> clazz, String name) {
        Bean bean = beanManager.getBeans(clazz, DatabaseQualifier.ofColumn(name)).iterator().next();
        CreationalContext ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }


    @Override
    public void destroy(ColumnTemplateAsync instance, CreationalContext<ColumnTemplateAsync> creationalContext) {

    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public String getId() {
        return ColumnTemplateAsync.class.getName() + DatabaseType.COLUMN + "-" + provider;
    }

}
