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
package org.jnosql.artemis.reflection;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The default implementation of {@link ClassRepresentation}.
 * It's storage the class information in a {@link ConcurrentHashMap}
 */
@ApplicationScoped
class DefaultClassRepresentations implements ClassRepresentations {

    private Map<String, ClassRepresentation> representations;

    private Map<Class, ClassRepresentation> classes;


    @Inject
    private ClassConverter classConverter;

    @Inject
    private ClassRepresentationsExtension extension;

    @PostConstruct
    public void init() {
        representations = new ConcurrentHashMap<>();
        classes = new ConcurrentHashMap<>();
        classes.putAll(extension.getClasses());
        representations.putAll(extension.getRepresentations());
    }

    void load(Class classEntity) {
        ClassRepresentation classRepresentation = classConverter.create(classEntity);
        representations.put(classEntity.getName(), classRepresentation);
    }

    @Override
    public ClassRepresentation get(Class classEntity) {
        ClassRepresentation classRepresentation = classes.get(classEntity);
        if (classRepresentation == null) {
            classRepresentation = classConverter.create(classEntity);
            classes.put(classEntity, classRepresentation);
            return this.get(classEntity);
        }
        return classRepresentation;
    }

    @Override
    public ClassRepresentation findByName(String name) throws ClassInformationNotFoundException {
        return representations.keySet().stream()
                .map(k -> representations.get(k))
                .filter(r -> r.getName().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new ClassInformationNotFoundException("There is not entity found with the name: " + name));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("representations", representations.size())
                .append("classes", classes.size())
                .toString();
    }

}
