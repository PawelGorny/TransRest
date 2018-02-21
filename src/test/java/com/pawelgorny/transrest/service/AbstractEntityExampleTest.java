package com.pawelgorny.transrest.service;

import com.pawelgorny.transrest.model.EntityExample;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(locations = "classpath:spring/applicationContext-test-datasource.xml")
public abstract class AbstractEntityExampleTest {

    @Autowired
    protected EntityExampleService service;

    final String USER = "junit";

    static List participantProviders = new ArrayList() {{
        add(new JacksonJsonProvider());
    }};

    @Before
    public void clear() {
        List<EntityExample> list = service.findAll();
        if (!list.isEmpty()) {
            for (EntityExample entityExample : list) {
                service.delete(entityExample);
            }
        }
        list = service.findAll();
        Assert.assertTrue(list.isEmpty());
    }
}
