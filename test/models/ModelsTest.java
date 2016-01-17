package models;

import com.avaje.ebean.Ebean;
import org.junit.Before;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

import org.junit.Test;
import play.libs.Yaml;
import play.test.WithApplication;

import java.util.List;

public class ModelsTest extends WithApplication {

    @Before
    public void setUp() throws Exception {
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetriveUser() {
        new User("artem.ignatiev1@gmail.com", "Artem", "secret");
        User artem = User.find.where().eq("email", "artem.ignatiev1@gmail.com").findUnique();
        assertNotNull(artem);
        assertEquals("Artem", artem.name);
    }

    @Test
    public void tryAuthenticateUser() {
        new User("artem.ignatiev1@gmail.com", "Artem", "secret").save();

        assertNotNull(User.authenticate("artem.ignatiev1@gmail.com", "secret"));
        assertNull(User.authenticate("artem.ignatiev1@gmail.com", "badpassword"));
        assertNull(User.authenticate("tom@gmail.com", "secret"));
    }

    @Test
    public void findProjectsInvolving() {
        new User("artem.ignatiev1@gmail.com", "Artem", "secret").save();
        new User("jane@gmail.com", "Jane", "secret").save();

        Project.create("Play 2", "play", "artem.ignatiev1@gmail.com");
        Project.create("Play 1", "play", "jane@gmail.com");

        List<Project> results = Project.findInvolving("artem.ignatiev1@gmail.com");
        assertEquals(1, results.size());
        assertEquals("Play 2", results.get(0).name);
    }

    @Test
    public void fullTest() {
        Ebean.save((List) Yaml.load("test-data.yml"));

        // Count things
        assertEquals(3, User.find.findRowCount());
        assertEquals(7, Project.find.findRowCount());
        assertEquals(5, Task.find.findRowCount());

        // Try to authenticate as users
        assertNotNull(User.authenticate("artem.ignatiev1@gmail.com", "secret"));
        assertNotNull(User.authenticate("jane@example.com", "secret"));
        assertNull(User.authenticate("jeff@example.com", "badpassword"));
        assertNull(User.authenticate("tom@example.com", "secret"));

        // Find all Bob's projects
        List<Project> bobsProjects = Project.findInvolving("artem.ignatiev1@gmail.com");
        assertEquals(5, bobsProjects.size());

        // Find all Bob's to do tasks
        List<Task> bobsTasks = Task.findTodoInvolving("artem.ignatiev1@gmail.com");
        assertEquals(4, bobsTasks.size());
    }

}