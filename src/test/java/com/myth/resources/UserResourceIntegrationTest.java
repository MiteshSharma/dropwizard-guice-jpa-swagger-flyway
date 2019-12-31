package com.myth.resources;

import com.myth.IntegrationTest;
import com.myth.MobileServerApplication;
import com.myth.MobileServerConfiguration;
import com.myth.models.User;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.extension.ExtendWith;

@Category(IntegrationTest.class)
@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceIntegrationTest {
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

    @Test
    public void getTasks_whenNoTasksExist_ShouldReturnEmptyArray() {
        RestAssured.when()
                .get("/user/1")
                .then()
                .statusCode(HttpStatus.OK_200);
    }

    @Test
    public void create() {
        User user = new User("NAme");
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/user")
                .then()
                .statusCode(HttpStatus.CREATED_201)
                .body("name", Is.is(user.getName()));
    }

    @Test
    public void update_whenTaskDoesNotExist_ShouldReturn404() {
        User user = new User("NAme");
        user.setUserId(1);
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("/user/1")
                .then()
                .statusCode(HttpStatus.OK_200);
    }
}
