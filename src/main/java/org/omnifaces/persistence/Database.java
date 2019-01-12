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
package org.omnifaces.persistence;

import static java.util.Arrays.stream;
import static java.util.logging.Level.WARNING;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.omnifaces.utils.Lang.startsWithOneOf;

import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.omnifaces.persistence.service.BaseEntityService;

/**
 * Enumeration of all supported databases.
 */
public enum Database {

	H2,

	MYSQL("MARIA"),

	POSTGRESQL("POSTGRES"),

	UNKNOWN;

	private static final Logger logger = Logger.getLogger(Database.class.getName());

	private String[] names;

	private Database(String... aliases) {
		this.names = concat(Stream.of(name()), stream(aliases)).collect(toList()).toArray(new String[0]);
	}

	public static Database of(EntityManager entityManager) {
		Provider provider = Provider.of(entityManager);
		EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();

		try {
			String uppercasedDialectName = provider.getDialectName(entityManagerFactory).toUpperCase();

			for (Database database : values()) {
				if (startsWithOneOf(uppercasedDialectName, database.names)) {
					return database;
				}
			}
		}
		catch (Exception e) {
			logger.log(WARNING, "Cannot to determine configured Database for " + provider + " by " + entityManagerFactory, e);
		}

		return UNKNOWN;
	}

	public static boolean is(Database database) {
		return BaseEntityService.getCurrentInstance().getDatabase() == database;
	}

}
