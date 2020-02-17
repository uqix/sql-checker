package io.github.uqix.sqlchecker;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SqlChecker implements ApplicationRunner {

    private ApplicationContext context;
    private PlatformTransactionManager txManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, Object> mappers = context.getBeansWithAnnotation(Mapper.class);
        mappers.forEach((k, v) -> new MapperSqlChecker(txManager, v).check());
    }

}
