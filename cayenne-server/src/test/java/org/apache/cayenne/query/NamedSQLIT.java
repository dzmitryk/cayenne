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

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.testdo.testmap.Artist;
import org.apache.cayenne.testdo.testmap.Painting;
import org.apache.cayenne.unit.di.server.CayenneProjects;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@UseServerRuntime(CayenneProjects.TESTMAP_PROJECT)
public class NamedSQLIT extends ServerCase {

	@Inject
	private DataContext context;
	
	@Test
	public void testNonSelectingQuery() {
		int[] updated = NamedSQL.query("NonSelectingQuery").update(context);
		
		assertEquals(1, updated.length);
		assertEquals(1, updated[0]);
		
		assertEquals(1, ObjectSelect.query(Painting.class).select(context).size());
	}
	
	@Test
	public void testSelectLower() {
		createArtist();
		
		List<DataRow> selected = NamedSQL.query("SelectTestLower", DataRow.class).select(context);
		
		assertEquals(1, selected.size());
		assertEquals("An Artist", selected.get(0).get("artist_name"));
	}
	
	@Test
	public void testSelectUpper() {
		createArtist();

		List<DataRow> selected = NamedSQL.query("SelectTestUpper", DataRow.class).select(context);

		assertEquals(1, selected.size());
		assertEquals("An Artist", selected.get(0).get("ARTIST_NAME"));
	}
	
	@Test
	public void testParameterizedNonSelectingQuery() {
		int[] updated = NamedSQL.query("ParameterizedNonSelectingQuery")
				.param("id", 300)
				.param("title", "Test Painting")
				.param("price", 2000)
				.update(context);

		assertEquals(1, updated.length);
		assertEquals(1, updated[0]);
		
		List<Painting> paintings = ObjectSelect.query(Painting.class).select(context);

		assertEquals(1, paintings.size());
		assertEquals("Test Painting", paintings.get(0).getPaintingTitle());
		assertEquals(2000, paintings.get(0).getEstimatedPrice().intValue());
	}
	
	@Test
	public void testParameterizedQuery() {
		createArtist();
		
		List<Artist> artists = NamedSQL.query("ParameterizedQueryWithLocalCache", Artist.class)
				.param("name", "An Artist")
				.select(context);
		
		assertEquals(1, artists.size());
		assertEquals("An Artist", artists.get(0).getArtistName());
	}

	protected void createArtist() {
		Artist a = context.newObject(Artist.class);
		a.setArtistName("An Artist");

		context.commitChanges();
	}

}