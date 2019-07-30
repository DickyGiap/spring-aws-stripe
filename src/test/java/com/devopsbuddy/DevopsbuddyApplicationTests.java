package com.devopsbuddy;

import com.devopsbuddy.backend.service.I18NService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DevopsbuddyApplicationTests {

    @Autowired
    private I18NService i18NService;

    @Test
    public void textMessageByLocaleService() throws Exception{
        String expectedResult = "Bootstrap starter template";
        String messageId = "index.main.callout";
        String actual = i18NService.getMessage(messageId);
        Assert.assertEquals("The actual and expected Strings don't match", expectedResult, actual);
    }

    @Test
    public void textMessageByLocaleWithLocaleService() throws Exception{
        String expectedResult = "Bootstrap starter template GB";
        String messageId = "index.main.callout";
        Locale locale = Locale.UK;
        String actual = i18NService.getMessage(messageId, locale);
        Assert.assertEquals("The actual and expected Strings don't match", expectedResult, actual);
    }
}
