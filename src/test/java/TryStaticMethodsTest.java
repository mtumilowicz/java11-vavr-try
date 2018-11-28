import io.vavr.control.Try;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mtumilowicz on 2018-11-27.
 */
public class TryStaticMethodsTest {

    @Test
    public void failure() {
        IllegalArgumentException exception = new IllegalArgumentException();
        Try<IllegalArgumentException> failure = Try.failure(exception);

        assertTrue(failure.isFailure());
        assertTrue(failure instanceof Try.Failure);
        assertThat(failure.getCause(), is(exception));
    }

    @Test
    public void success() {
        Try<Integer> success = Try.success(1);

        assertTrue(success.isSuccess());
        assertTrue(success instanceof Try.Success);
        assertThat(success.get(), is(1));
    }

    @Test
    public void of_failure_checkedException() {
        IOException exception = new IOException();
        Try<RuntimeException> exceptionTry = Try.of(() -> {
            throw exception;
        });

        assertTrue(exceptionTry.isFailure());
        assertThat(exceptionTry.getCause(), is(exception));
    }

    @Test
    public void of_failure_uncheckedException() {
        RuntimeException exception = new RuntimeException();
        Try<RuntimeException> exceptionTry = Try.of(() -> {
            throw exception;
        });

        assertTrue(exceptionTry.isFailure());
        assertThat(exceptionTry.getCause(), is(exception));
    }

    @Test
    public void of_success() {
        Try<Integer> tryWithoutException = Try.of(() -> 1);

        assertTrue(tryWithoutException.isSuccess());
        assertThat(tryWithoutException.get(), is(1));
    }

    @Test
    public void run_success() {
        AtomicBoolean invoked = new AtomicBoolean();

        Try<Void> run = Try.run(() -> invoked.set(true));

        assertTrue(run.isSuccess());
        assertNull(run.get());
        assertTrue(invoked.get());
    }

    @Test
    public void run_failure() {
        RuntimeException exception = new RuntimeException();
        Try<Void> runFailure = Try.run(() -> {
            throw exception;
        });

        assertTrue(runFailure.isFailure());
        assertThat(runFailure.getCause(), is(exception));
    }

    @Test
    public void java_try_with_resource_success() throws IOException {
        String fileName = "src/test/resources/lines.txt";
        
        String fileLines;
        try (var stream = Files.lines(Paths.get(fileName))) {

            fileLines = stream.collect(joining(","));
        }
        
        assertThat(fileLines, is("1,2,3"));
    }

    @Test(expected = IOException.class)
    public void java_try_with_resource_failure() throws IOException {
        String fileName = "NonExistingFile.txt";
        
        String fileLines;
        try (var stream = Files.lines(Paths.get(fileName))) {

            fileLines = stream.collect(joining(","));
        }

        assertThat(fileLines, is("1,2,3"));
    }

    @Test
    public void vavr_try_with_resources_success() {
        String fileName = "src/test/resources/lines.txt";

        Try<String> fileLines = Try.withResources(() -> Files.lines(Paths.get(fileName)))
                .of(stream -> stream.collect(joining(",")));

        assertTrue(fileLines.isSuccess());
        assertThat(fileLines.get(), is("1,2,3"));
    }

    @Test
    public void vavr_try_with_resources_failure() {
        String fileName = "NonExistingFile.txt";

        Try<String> fileLines = Try.withResources(() -> Files.lines(Paths.get(fileName)))
                .of(stream -> stream.collect(joining(",")));

        assertTrue(fileLines.isFailure());
    }
}