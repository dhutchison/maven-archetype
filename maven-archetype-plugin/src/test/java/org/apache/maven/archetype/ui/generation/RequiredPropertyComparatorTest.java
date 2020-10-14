package org.apache.maven.archetype.ui.generation;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.archetype.ui.ArchetypeConfiguration;

public class RequiredPropertyComparatorTest
    extends TestCase
{
    private DefaultArchetypeGenerationConfigurator.RequiredPropertyComparator requiredPropertyComparator;

    private ArchetypeConfiguration archetypeConfiguration;

    @Override
    public void setUp()
        throws Exception
    {
        archetypeConfiguration = new ArchetypeConfiguration();
        requiredPropertyComparator =
            new DefaultArchetypeGenerationConfigurator.RequiredPropertyComparator( archetypeConfiguration );
    }

    public void testShouldOrderPropertiesWhenReferringEachOther()
    {
        archetypeConfiguration.addRequiredProperty( "prop1" );
        archetypeConfiguration.setDefaultProperty( "prop1", "${prop2}" );
        archetypeConfiguration.addRequiredProperty( "prop2" );
        archetypeConfiguration.setDefaultProperty( "prop2", "prop2 default value" );

        List<String> requiredProperties = archetypeConfiguration.getRequiredProperties();
        assertEquals( Arrays.asList( "prop1", "prop2" ), requiredProperties );
        Collections.sort( requiredProperties, requiredPropertyComparator );
        assertEquals( Arrays.asList( "prop2", "prop1" ), requiredProperties );
    }

    public void testShouldOrderPropertiesWhenReferringEachOther2()
    {
        archetypeConfiguration.addRequiredProperty( "prop1" );
        archetypeConfiguration.setDefaultProperty( "prop1", "${prop2}" );
        archetypeConfiguration.addRequiredProperty( "prop2" );

        List<String> requiredProperties = archetypeConfiguration.getRequiredProperties();
        assertEquals( Arrays.asList( "prop1", "prop2" ), requiredProperties );
        Collections.sort( requiredProperties, requiredPropertyComparator );
        assertEquals( Arrays.asList( "prop2", "prop1" ), requiredProperties );
    }

    /**
     * Test that checks that properties which do not depend on other items, or are at least
     * defined in the correct order, do not move when the collection is sorted.
     */
    public void testShouldNotSortPropertiesWithoutDependencies()
    {

        archetypeConfiguration.addRequiredProperty( "a-prop" );
        archetypeConfiguration.addRequiredProperty( "groupId" );
        archetypeConfiguration.setDefaultProperty( "groupId", "com.example" );
        archetypeConfiguration.addRequiredProperty( "artifactId" );
        archetypeConfiguration.setDefaultProperty( "artifactId", "test-${a-prop}" );
        /* The following two properties are in the wrong order, so should sort */
        archetypeConfiguration.addRequiredProperty( "package" );
        archetypeConfiguration.setDefaultProperty( "package", "${groupId}.${artifactId}.${z-prop}" );
        archetypeConfiguration.addRequiredProperty( "z-prop" );

        List<String> requiredProperties = archetypeConfiguration.getRequiredProperties();
        assertEquals( Arrays.asList( "a-prop", "groupId", "artifactId", "package", "z-prop" ), requiredProperties );
        /* Sort then check the results */
        Collections.sort( requiredProperties, requiredPropertyComparator );
        assertEquals( Arrays.asList( "a-prop", "groupId",  "artifactId", "z-prop", "package" ), requiredProperties );
    }
}
