import io.vavr.control.Try;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by mtumilowicz on 2018-12-02.
 */
public class PrefaceExampleTest {
    
    @Test
    public void parseIntegerFromStream_exception() {
        Try<Integer> parseInteger = Try.of(() -> Integer.valueOf("a"));

        assertTrue(parseInteger.isFailure());
    }

    @Test
    public void parseIntegerFromStream_noException() {
        Try<Integer> parseInteger = Try.of(() -> Integer.valueOf("1"));

        assertTrue(parseInteger.isSuccess());
        assertThat(parseInteger.get(), is(1));
    }
}
