package io.github.uqix.sqlchecker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

public class MethodArgsBuilderTest {

    @Test
    @SneakyThrows
    public void primitiveAndWrapperTypes() {
        Method m1 = method("m1");
        Object[] args = new MethodArgsBuilder(m1).build();
        assertThat(args,
                   is(new Object[]{
                           true, 'c',
                           true, 'c',
                           (byte) 1, (short) 1, 1, 1L,
                           (byte) 1, (short) 1, 1, 1L,
                           1.0F, 1.0,
                           1.0F, 1.0
                       }));
        m1.invoke(this, args);
    }

    private Method method(String name) {
        return Arrays.stream(this.getClass().getDeclaredMethods())
            .filter(m -> m.getName().equals(name))
            .collect(Collectors.toList())
            .get(0);
    }

    void m1(boolean bo, char c,
            Boolean bo2, Character c2,
            byte b, short s, int i, long l,
            Byte b2, Short s2, Integer i2, Long l2,
            float f, double d,
            Float f2, Double d2) {
    }

    @Test
    @SuppressWarnings("unchecked")
    public void coreJdkReferenceTypes() {
        Method m2 = method("m2");
        assertThat(new MethodArgsBuilder(m2).build(),
                   Matchers.<Object>array(is("str"),
                                          notNullValue(),
                                          is(BigDecimal.valueOf(1.0)),
                                          is(BigInteger.valueOf(1L))));
    }

    void m2(String s,
            Date d,
            BigDecimal bd, BigInteger bi) {
    }

    @Test
    public void beanTypes() {
        Method m3 = method("m3");
        assertThat(new MethodArgsBuilder(m3).build(), is(new Object[] {
                    new Bean(1, "str", true, new InnerBean(1, "str"))
                }));
    }

    void m3(Bean b) {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Bean {
        private Integer id;
        private String name;
        private boolean enabled;
        private InnerBean innerBean;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class InnerBean {
        private Integer id;
        private String name;
    }

    @Test
    public void enumTypes() {
        Method m4 = method("m4");
        assertThat(new MethodArgsBuilder(m4).build(), is(new Object[] {Gender.MALE}));
    }

    void m4(Gender g) {
    }

    static enum Gender {
        MALE, FEMALE
    }

    @Test
    public void arrayTypes() {
        Method m5 = method("m5");
        assertThat(new MethodArgsBuilder(m5).build(),is(new Object[] {
                    new String[] {"str"}
                }));
    }

    void m5(String[] sa) {
    }

    @Test
    public void collectionTypes() {
        Method m6 = method("m6");
        ArrayList<InnerBean> list = new ArrayList<>();
        list.add(new InnerBean(1, "str"));
        HashSet<String> set = new HashSet<>();
        set.add("str");
        HashMap<String, Integer> map = new HashMap<>();
        map.put("str", 1);
        ArrayList<Bean2> list2 = new ArrayList<>();
        list2.add(new Bean2(list));

        assertThat(new MethodArgsBuilder(m6).build(), is(new Object[] {
                    list, set, map,
                    list2
                }));
    }

    void m6(List<InnerBean> l, Set<String> s, Map<String, Integer> m,
            List<Bean2> l2) {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Bean2 {
        List<InnerBean> innerBeans;
    }

}
