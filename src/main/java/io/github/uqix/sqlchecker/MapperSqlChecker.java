package io.github.uqix.sqlchecker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
class MapperSqlChecker {

    private PlatformTransactionManager txManager;
    private Object mapper;

    public void check() {
        Class<?> mapperInterface = mapper.getClass().getInterfaces()[0];
        log.debug("Check mapper: {}", mapperInterface.getName());
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        Arrays.asList(mapperInterface.getDeclaredMethods()).forEach(m -> {
                txTemplate.execute(txStatus -> {
                        txStatus.setRollbackOnly();
                        try {
                            invokeMapperMethod(mapper, m);
                        } catch (MyBatisSystemException | BadSqlGrammarException ex) {
                            throw ex;
                        } catch (DataIntegrityViolationException ex) {
                            if (ex.getCause() instanceof SQLSyntaxErrorException) {
                                throw ex;
                            }
                        } catch (Exception ex) {
                            log.warn("Mute ex", ex);
                        }
                        return 0;
                    });
            });
    }

    @SneakyThrows
    private void invokeMapperMethod(Object mapper, Method method) {
        log.debug("Invoke mapper method: {}.{}", method.getDeclaringClass().getName(), method.getName());
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            method.invoke(mapper, new MethodArgsBuilder(method).build());
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

}
