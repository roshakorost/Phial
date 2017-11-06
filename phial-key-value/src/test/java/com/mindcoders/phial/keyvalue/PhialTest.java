package com.mindcoders.phial.keyvalue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by rost on 11/2/17.
 */
@RunWith(JUnit4.class)
public class PhialTest {
    //we would like to check the Phial call save and remove forEach saver
    private static final int SAVERS_COUNT = 2;

    private List<Saver> savers;

    @Before
    public void setUp() throws Exception {
        Phial.cleanSavers();
        savers = createSavers();
        savers.forEach(Phial::addSaver);
    }

    @Test
    public void setKey_call_setKey_for_all_savers() throws Exception {
        Phial.setKey("key", "value");
        savers.forEach(saver ->
                verify(saver).save(anyString(), eq("key"), eq("value"))
        );
    }

    @Test
    public void removeKey_call_removeKey_for_all_savers() throws Exception {
        Phial.removeKey("key");
        savers.forEach(saver ->
                verify(saver).remove(anyString(), eq("key"))
        );
    }

    @Test
    public void category_setKey_call_setKey_for_all_savers() {
        Phial.category("category").setKey("key", "value");
        savers.forEach(saver ->
                verify(saver).save(eq("category"), eq("key"), eq("value"))
        );
    }

    @Test
    public void category_removeKey_call_removeKey_for_all_savers() {
        Phial.category("category").removeKey("key");
        savers.forEach(saver ->
                verify(saver).remove(eq("category"), eq("key"))
        );
    }

    @Test
    public void removeSaver() {
        savers.forEach(Phial::removeSaver);
        Phial.setKey("key", "value");
        Phial.removeKey("key");
        Phial.category("category").setKey("key", "value");
        Phial.category("category").removeKey("key");
        savers.forEach(Mockito::verifyZeroInteractions);
    }

    @Test
    public void clearCategory() {
        Phial.category("category").setKey("key", "value").clear();
        savers.forEach(saver ->
                verify(saver).remove(eq("category"))
                      );
    }

    private List<Saver> createSavers() {
        return IntStream.range(0, SAVERS_COUNT)
                .mapToObj(i -> mock(Saver.class))
                .collect(Collectors.toList());
    }
}
