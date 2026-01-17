package dev.anchxt.jod;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


/*Check if Java 21 is installed.*/
public class EnvTest {
    @Test
    void verifyJava21Features() {
        // create sequenced collection
        var list = java.util.List.of("A", "B", "C");

        assertThat(list.getFirst()).isEqualTo("A");
        assertThat(list.getLast()).isEqualTo("C");
    }
}
