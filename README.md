# SQL checker for Spring Boot Mybatis Mappers

This package can check SQL syntax in `Mapper.xml` files by invoking all `Mapper` methods with dummy args.

## Usage

Maven dependency: [io.github.uqix/sql-checker](https://search.maven.org/artifact/io.github.uqix/sql-checker)

```java
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
    // @DependsOn("liquibase")
    // or
    // @DependsOn("flyway")
    @Profile("sql_check")
    public SqlChecker sqlChecker(ApplicationContext context, PlatformTransactionManager txManager) {
        return new SqlChecker(context, txManager);
    }
}
```

### DisableSqlChecker
You can ignore a Mapper method by annotate it with `@DisableSqlChecker`.
