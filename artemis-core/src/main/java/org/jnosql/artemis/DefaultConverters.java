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
package org.jnosql.artemis;


import org.jnosql.artemis.reflection.Reflections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Default implementation to {@link Converters}
 */
@ApplicationScoped
class DefaultConverters implements Converters {

    @Inject
    private Reflections reflections;

    private Map<Class<? extends AttributeConverter>, AttributeConverter> attributeConverters = new ConcurrentHashMap<>();


    @Override
    public AttributeConverter get(Class<? extends AttributeConverter> converterClass) throws NullPointerException {
        Objects.requireNonNull(converterClass, "The converterClass is required");
        AttributeConverter converter = attributeConverters.get(converterClass);
        if (Objects.isNull(converter)) {
            converter = reflections.newInstance(converterClass);
            attributeConverters.put(converterClass, converter);
        }

        return converter;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultConverters{");
        sb.append("attributeConverters=").append(attributeConverters.size());
        sb.append('}');
        return sb.toString();
    }
}
