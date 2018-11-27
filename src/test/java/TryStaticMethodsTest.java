import io.vavr.control.Try;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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
}