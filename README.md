# SQL checker for Spring Boot Mybatis Mappers

## Why
To verify that no SQL syntax errors in Mapper.xml

## How
By invoking all Mapper methods with dummy args which hits DB and catching syntax exceptions

## Usage

```java
package app.share;

import io.github.uqix.sqlchecker.SqlChecker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
class SqlCheckerConfig {

    @Bean
    @DependsOn("liquibase")
    @Profile("sql_check")
    public SqlChecker sqlChecker(ApplicationContext context, PlatformTransactionManager txManager) {
        return new SqlChecker(context, txManager);
    }
}
```
