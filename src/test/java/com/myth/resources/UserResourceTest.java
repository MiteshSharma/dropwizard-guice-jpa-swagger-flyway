package com.myth.resources;

import com.myth.MobileServerApplication;
import com.myth.MobileServerConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceTest {

    public static final DropwizardAppExtension<MobileServerConfiguration> DROPWIZARD =
            new DropwizardAppExtension<MobileServerConfiguration>(MobileServerApplication.class, ResourceHelpers.resourceFilePath("test-config.yml"));

    @Before
    public void beforeClass() throws Exception {
        DROPWIZARD.before();
    }

    @After
    public void afterClass() {
        DROPWIZARD.after();
    }

}
