/*
 * Copyright 2019 OmniFaces
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * <p>
 * Mapped superclass for versioned entity.
 * It extends from {@link TimestampedEntity} which in turn extends from {@link GeneratedIdEntity}.
 * In addition to the "id", "created" and "lastModified" columns, it specifies a {@link Version} column, named "version".
 * On pre persist, JPA will automatically set version to 0.
 * On pre update, JPA will automatically increment version with 1.
 *
 * @param <I> The generic ID type.
 * @author Bauke Scholtz
 */
@MappedSuperclass
public abstract class VersionedEntity<I extends Comparable<I> & Serializable> extends TimestampedEntity<I> implements Versioned {

	private static final long serialVersionUID = 1L;

	@Version
	@Column(nullable = false)
	private Long version;

	@Override
	public Long getVersion() {
		return version;
	}

	// No setter! JPA takes care of this.
}