/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.column;

import org.jnosql.artemis.AttributeConverter;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Embeddable;
import org.jnosql.artemis.reflection.FieldRepresentation;
import org.jnosql.artemis.reflection.FieldType;
import org.jnosql.artemis.reflection.FieldValue;
import org.jnosql.artemis.reflection.GenericFieldRepresentation;
import org.jnosql.diana.api.column.Column;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

class ColumnFieldValue implements FieldValue {

    private final FieldValue fieldValue;

    private ColumnFieldValue(FieldValue fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getValue() {
        return fieldValue.getValue();
    }

    @Override
    public FieldRepresentation getField() {
        return fieldValue.getField();
    }

    @Override
    public boolean isNotEmpty() {
        return fieldValue.isNotEmpty();
    }

    public Column toColumn(ColumnEntityConverter converter, Converters converters) {

        if (FieldType.EMBEDDED.equals(getNativeField())) {
            return Column.of(getField().getName(), converter.toColumn(getValue()).getColumns());
        } else if(FieldType.COLLECTION.equals(getNativeField()) && isEmbeddableElement()) {
            
        }
        Optional<Class<? extends AttributeConverter>> optionalConverter = getField().getConverter();
        if (optionalConverter.isPresent()) {
            AttributeConverter attributeConverter = converters.get(optionalConverter.get());
            return Column.of(getField().getName(), attributeConverter.convertToDatabaseColumn(getValue()));
        }

        return Column.of(getField().getName(), getValue());
    }

    private boolean isEmbeddableElement() {
        return Class.class.cast(ParameterizedType.class.cast(getNativeField()
                 .getGenericType())
                 .getActualTypeArguments()[0])
                 .getAnnotation(Embeddable.class) != null;
    }

    private Field getNativeField() {
        return getField().getField();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnFieldValue{");
        sb.append("fieldValue=").append(fieldValue);
        sb.append('}');
        return sb.toString();
    }

    static ColumnFieldValue of(Object value, FieldRepresentation field) {
        return new ColumnFieldValue(FieldValue.of(value, field));
    }

}
