/*
 *
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.albedo.java.config;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by songjiawei on 2016/11/9.
 */
@Configuration
@EnableJpaRepositories(
        value = {"com.albedo.java.modules.*.repository"}
)
@EnableTransactionManagement
@ComponentScan(basePackages = "com.albedo.java.*")
public class TestConfig implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

//    @Bean
//    public DataSource dataSource() throws SQLException {
//        EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript("classpath:/test-init.sql").build();
//
//        ReplicationRoutingDataSource proxy = new ReplicationRoutingDataSource(embeddedDatabase, null);
//        proxy.addSlave(embeddedDatabase);
//        proxy.addSlave(embeddedDatabase);
//        proxy.addSlave(embeddedDatabase);
//        return new LazyConnectionDataSourceProxy(proxy);
//    }
//
//
//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
//        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setTransactionFactory(new ReadWriteManagedTransactionFactory());
//        return factoryBean;
//    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAware<String>() {
            @Override
            public String getCurrentAuditor() {
                return "1";
            }
        };
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

        this.resourceLoader = resourceLoader;
    }
}
