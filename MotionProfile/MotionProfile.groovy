public class GreetingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.task("hello")
          .doLast(task -> System.out.println("Hello Gradle!"));
    }
}