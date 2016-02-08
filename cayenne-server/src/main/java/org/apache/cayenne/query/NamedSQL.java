/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.query;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.QueryResult;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.util.QueryResultBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent API for calling named queries.
 *
 * @since 4.0
 */
public class NamedSQL<T> extends IndirectQuery {

	/**
	 * Creates NamedSQL query instance using name of named query stored in the mapping file.
	 */
    public static NamedSQL query(String queryName) {
        return new NamedSQL(queryName);
    }

	/**
	 * Creates type parameterized NamedSQL query instance using name of named query stored in the mapping file.
	 */
	public static <T> NamedSQL<T> query(String queryName, Class<T> resultClass) {
		return new NamedSQL<>(queryName, resultClass);
	}

    protected String queryName;
	protected Class<T> resultClass;
	protected Map<String, Object> params;
    protected boolean forceNoCache;

    public NamedSQL(String queryName) {
        this.queryName = queryName;
    }
	
	public NamedSQL(String queryName, Class<T> resultClass) {
		this(queryName);
		this.resultClass = resultClass;
	}

    public NamedSQL<T> params(Map<String, ?> parameters) {
        if (this.params == null) {
            this.params = new HashMap<>(parameters);
        } else {
            Map bareMap = parameters;
            this.params.putAll(bareMap);
        }

        this.replacementQuery = null;

        return this;
    }

    public NamedSQL<T> param(String name, Object value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }

        this.params.put(name, value);

        this.replacementQuery = null;

        return this;
    }

    public NamedSQL<T> noCache() {
        this.forceNoCache = true;
        return this;
    }

    public List<QueryResult> execute(ObjectContext context) {
        QueryResponse response = context.performGenericQuery(this);

        QueryResultBuilder builder = QueryResultBuilder.builder(response.size());

        for (response.reset(); response.next(); ) {

            if (response.isList()) {
                builder.addSelectResult(response.currentList());
            } else {
                builder.addBatchUpdateResult(response.currentUpdateCount());
            }
        }

        return builder.build();
    }

    public List<T> select(ObjectContext context) {
        List<QueryResult> results = execute(context);
		
		for (QueryResult result : results) {
			if (result.isSelectResult()) {
				return (List<T>) result.getSelectResult();
			}
		}
		
		throw new UnsupportedOperationException("Query result is not a select result.");
    }

	public int[] update(ObjectContext context) {
		List<QueryResult> results = execute(context);
		
		for (QueryResult result : results) {
			if (!result.isSelectResult()) {
				return result.getBatchUpdateResult();
			}
		}
		
		throw new UnsupportedOperationException("Query result is not an update result.");
	}

    @Override
    protected Query createReplacementQuery(EntityResolver resolver) {
        NamedQuery query = new NamedQuery(queryName);
		
        query.parameters = params;
        query.setForceNoCache(forceNoCache);

        return query;
    }
}
