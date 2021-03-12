/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.context.kernel;

import org.apache.shardingsphere.infra.binder.LogicSQL;
import org.apache.shardingsphere.infra.binder.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.config.properties.ConfigurationProperties;
import org.apache.shardingsphere.infra.config.properties.ConfigurationPropertyKey;
import org.apache.shardingsphere.infra.executor.sql.context.ExecutionContext;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.metadata.resource.ShardingSphereResource;
import org.apache.shardingsphere.infra.metadata.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.sql.parser.sql.common.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.sql.dialect.statement.mysql.dml.MySQLSelectStatement;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KernelProcessorTest {

    private KernelProcessor kernelProcessor;

    @Before
    public void setUp() throws Exception {
        kernelProcessor = new KernelProcessor();
    }

    @Test
    public void testGenerateExecutionContext() {
        SQLStatementContext<SQLStatement> sqlStatementContext = mock(SQLStatementContext.class);
        MySQLSelectStatement selectStatement = mock(MySQLSelectStatement.class);
        when(selectStatement.getLock()).thenReturn(Optional.empty());
        when(sqlStatementContext.getSqlStatement()).thenReturn(selectStatement);
        final LogicSQL logicSQL = new LogicSQL(sqlStatementContext, "select * from t_order", Collections.emptyList());
        final ShardingSphereMetaData metaData = new ShardingSphereMetaData("logic_schema",
                mock(ShardingSphereResource.class, RETURNS_DEEP_STUBS),
                new ShardingSphereRuleMetaData(Collections.emptyList(),
                        Collections.emptyList()),
                mock(ShardingSphereSchema.class));
        Properties properties = new Properties();
        properties.setProperty(ConfigurationPropertyKey.SQL_SHOW.getKey(), Boolean.TRUE.toString());
        final ConfigurationProperties props = new ConfigurationProperties(properties);
        final ExecutionContext result = kernelProcessor.generateExecutionContext(logicSQL, metaData, props);
        assertThat(1, Matchers.is(result.getExecutionUnits().size()));
    }
}
