package dev.parkcommerce.core;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
final class CoreScenariosTest {
    @TestFactory Stream<DynamicTest> coreScenarios() {
        return CoreScenarios.cases().stream().map(c -> DynamicTest.dynamicTest(c.name(), () -> c.body().run()));
    }
}
