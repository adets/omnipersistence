/*
 * Copyright 2021 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.persistence.criteria;

import static org.omnifaces.persistence.JPA.castAsString;

import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * Creates <code>path LIKE value</code>.
 *
 * @author Bauke Scholtz
 */
public final class Like extends Criteria<String> {

	private enum Type {
		STARTS_WITH,
		ENDS_WITH,
		CONTAINS;
	}

	private Type type;

	private Like(Type type, String value) {
		super(value);
		this.type = type;
	}

	public static Like startsWith(String value) {
		return new Like(Type.STARTS_WITH, value);
	}

	public static Like endsWith(String value) {
		return new Like(Type.ENDS_WITH, value);
	}

	public static Like contains(String value) {
		return new Like(Type.CONTAINS, value);
	}

	public boolean startsWith() {
		return type == Type.STARTS_WITH;
	}

	public boolean endsWith() {
		return type == Type.ENDS_WITH;
	}

	public boolean contains() {
		return type == Type.CONTAINS;
	}

	@Override
	public Predicate build(Expression<?> path, CriteriaBuilder criteriaBuilder, ParameterBuilder parameterBuilder) {
		Class<?> type = path.getJavaType();

		boolean lowercaseable = !Numeric.is(type);
		String searchValue = (startsWith() ? "" : "%") + (lowercaseable ? getValue().toLowerCase() : getValue()) + (endsWith() ? "" : "%");
		Expression<String> pathAsString = castAsString(criteriaBuilder, path);
		return criteriaBuilder.like(lowercaseable ? criteriaBuilder.lower(pathAsString) : pathAsString, parameterBuilder.create(searchValue));
	}

	@Override
	public boolean applies(Object modelValue) {
		if (modelValue == null) {
			return false;
		}

		String lowerCasedValue = getValue().toLowerCase();
		String lowerCasedModelValue = modelValue.toString().toLowerCase();

		if (startsWith()) {
			return lowerCasedModelValue.startsWith(lowerCasedValue);
		}
		else if (endsWith()) {
			return lowerCasedModelValue.endsWith(lowerCasedValue);
		}
		else {
			return lowerCasedModelValue.contains(lowerCasedValue);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), type);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) && Objects.equals(type, ((Like) object).type);
	}

	@Override
	public String toString() {
		return "LIKE " + (startsWith() ? "" : "%") + getValue() + (endsWith() ? "" : "%");
	}

}
