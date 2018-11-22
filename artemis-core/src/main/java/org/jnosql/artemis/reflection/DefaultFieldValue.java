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
package org.jnosql.artemis.reflection;


import java.util.Objects;

final class DefaultFieldValue implements FieldValue {

    private final Object value;

    private final FieldMapping field;


    DefaultFieldValue(Object value, FieldMapping field) {
        this.value = value;
        this.field = Objects.requireNonNull(field, "field is required");
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public FieldMapping getField() {
        return field;
    }

    @Override
    public boolean isNotEmpty() {
        return value != null;
    }


    @Override
    public String toString() {
        return "FieldValue{" +
                "value=" + value +
                ", field=" + field +
                '}';
    }
}
