package com.myth.resources;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.myth.MobileServerApplication;
import com.myth.MobileServerConfiguration;
import com.myth.auth.UserAuthenticator;
import com.myth.auth.UserAuthorizer;
import com.myth.models.User;
import com.myth.service.impl.UserService;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.restassured.RestAssured;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceTest {

    private final UserService service = Mockito.mock(UserService.class);

    public final ResourceExtension resources = ResourceExtension.builder()
            .addResource(new UserResource(service))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();

    public static final DropwizardAppExtension<MobileServerConfiguration> DROPWIZARD =
            new DropwizardAppExtension<MobileServerConfiguration>(MobileServerApplication.class, ResourceHelpers.resourceFilePath("test-config.yml"));

    @Before
    public void beforeClass() throws Throwable {
        DROPWIZARD.before();
        resources.before();
        Mockito.when(service.getUser(1)).thenReturn(new User());
    }

    @After
    public void afterClass() throws Throwable {
        DROPWIZARD.after();
        resources.after();
        Mockito.reset(service);
    }

    @Test
    public void canGetExpectedResourceOverHttp() {
        User user = resources.target("/user/{userId}").resolveTemplate("userId", 1).request().header(HttpHeaders.AUTHORIZATION, "Bearer XYZ").get(User.class);
        if (user != null) {
            Assert.assertEquals(user.getUserId(), 0);
            return;
        }
        assert false;
    }
}
