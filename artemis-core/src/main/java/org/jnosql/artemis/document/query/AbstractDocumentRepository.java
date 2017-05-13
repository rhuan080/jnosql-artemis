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
package org.jnosql.artemis.document.query;

import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.Reflections;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.StreamSupport.stream;
import static org.jnosql.artemis.IdNotFoundException.KEY_NOT_FOUND_EXCEPTION_SUPPLIER;

/**
 * The {@link Repository} template method
 */
public abstract class AbstractDocumentRepository<T, ID> implements Repository<T, ID> {

    protected abstract DocumentTemplate getTemplate();

    protected abstract ClassRepresentation getClassRepresentation();

    protected abstract Reflections getReflections();

    @Override
    public <S extends T> S save(S entity) throws NullPointerException {
        Objects.requireNonNull(entity, "Entity is required");
        Object id = getReflections().getValue(entity, getIdField().getField());
        if (existsById((ID) id)) {
            return getTemplate().update(entity);
        } else {
            return getTemplate().insert(entity);
        }
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) throws NullPointerException {
        return getTemplate().insert(entities);
    }


    @Override
    public void deleteById(ID id) throws NullPointerException {
        requireNonNull(id, "is is required");
        DocumentDeleteQuery query = DocumentDeleteQuery.of(getClassRepresentation().getName());
        String documentName = this.getIdField().getName();
        query.with(DocumentCondition.eq(Document.of(documentName, id)));
        getTemplate().delete(query);
    }

    @Override
    public void deleteById(Iterable<ID> ids) throws NullPointerException {
        requireNonNull(ids, "ids is required");
        ids.forEach(this::deleteById);
    }

    @Override
    public void delete(Iterable<T> entities) throws NullPointerException {
        requireNonNull(entities, "entities is required");
        entities.forEach(this::delete);
    }

    @Override
    public void delete(T entity) throws NullPointerException {
        requireNonNull(entity, "entity is required");
        Object idValue = getReflections().getValue(entity, this.getIdField().getField());
        requireNonNull(idValue, "id value is required");
        deleteById((ID) idValue);
    }

    @Override
    public Optional<T> findById(ID id) throws NullPointerException {
        requireNonNull(id, "id is required");

        DocumentQuery query = DocumentQuery.of(getClassRepresentation().getName());
        String documentName = this.getIdField().getName();
        query.with(DocumentCondition.eq(Document.of(documentName, id)));
        return getTemplate().singleResult(query);
    }

    @Override
    public Iterable<T> findById(Iterable<ID> ids) throws NullPointerException {
        requireNonNull(ids, "ids is required");
        return (Iterable) stream(ids.spliterator(), false)
                .flatMap(optionalToStream()).collect(Collectors.toList());
    }

    private FieldRepresentation getIdField() {
        return getClassRepresentation().getId().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    private Function optionalToStream() {
        return id -> {
            Optional entity = this.findById((ID) id);
            return entity.isPresent() ? Stream.of(entity.get()) : Stream.empty();
        };
    }

    @Override
    public boolean existsById(ID id) throws NullPointerException {
        return findById(id).isPresent();
    }

}
