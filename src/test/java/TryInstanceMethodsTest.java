import io.vavr.control.Try;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by mtumilowicz on 2018-11-28.
 */
public class TryInstanceMethodsTest {

    @Test
    public void andFinally_success() {
        var invoked = new AtomicBoolean();

        Try.of(() -> 1).andFinally(() -> invoked.set(true));

        assertTrue(invoked.get());
    }

    @Test
    public void andFinally_failure() {
        var invoked = new AtomicBoolean();

        Try.of(() -> {
            throw new RuntimeException();
        }).andFinally(() -> invoked.set(true));

        assertTrue(invoked.get());
    }

    @Test
    public void andThen_success() {
        var invoked = new AtomicBoolean();

        Try.of(() -> 1).andThen(() -> invoked.set(true));

        assertTrue(invoked.get());
    }

    @Test
    public void andThen_failure() {
        var invoked = new AtomicBoolean();

        Try.of(() -> {
            throw new RuntimeException();
        }).andThen(() -> invoked.set(true));

        assertFalse(invoked.get());
    }

    @Test
    public void failed_success() {
        Try<Throwable> failed = Try.of(() -> 1).failed();

        assertTrue(failed.isFailure());
        assertTrue(failed.getCause() instanceof NoSuchElementException);
    }

    @Test
    public void failed_failure() {
        var runtimeException = new RuntimeException();

        Try<Throwable> failed = Try.of(() -> {
            throw runtimeException;
        }).failed();

        assertTrue(failed.isSuccess());
        assertThat(failed.get(), is(runtimeException));
    }

    @Test
    public void filterTry_success_filter_passes() {
        Try<Integer> vavrTry = Try.of(() -> 1).filterTry(value -> value > 0);

        assertTrue(vavrTry.isSuccess());
        assertThat(vavrTry, is(Try.of(() -> 1)));
    }

    @Test
    public void filterTry_success_filter_not_passes() {
        Try<Integer> vavrTry = Try.of(() -> 1).filterTry(value -> value > 10);

        assertTrue(vavrTry.isFailure());
        assertTrue(vavrTry.getCause() instanceof NoSuchElementException); // Predicate does not hold for 1
    }

    @Test
    public void flatMap_success() {
        Try<Integer> vavrTry = Try.of(() -> Try.of(() -> 1)).flatMap(Function.identity());

        assertThat(vavrTry, is(Try.of(() -> 1)));
    }

    @Test
    public void flatMap_failure() {
        var runtimeException = new RuntimeException();

        Try<Object> vavrTry = Try.of(() -> Try.of(() -> {
            throw runtimeException;
        })).flatMap(Function.identity());

        assertTrue(vavrTry.isFailure());
        assertThat(vavrTry.getCause(), is(runtimeException));
    }

    @Test
    public void recover_class_function_success() {
        Try<Integer> recovered = Try.of(() -> 1).recover(IllegalArgumentException.class, exception -> -1);

        assertThat(recovered, is(Try.of(() -> 1)));
    }

    @Test
    public void recover_class_function_failure_assignable() {
        Try<Integer> recovered = Try.<Integer>of(() -> {
            throw new RuntimeException();
        })
                .recover(RuntimeException.class, exception -> -1);

        assertThat(recovered, is(Try.of(() -> -1)));
    }

    @Test
    public void recover_class_function_failure_notAssignable() {
        Try<Integer> recovered = Try.<Integer>of(() -> {
            throw new RuntimeException();
        })
                .recover(IllegalArgumentException.class, exception -> -1);

        assertTrue(recovered.isFailure());
        assertTrue(recovered.getCause() instanceof RuntimeException);
    }

    @Test
    public void recover_class_value_failure() {
        Try<Integer> recovered = Try.<Integer>of(() -> {
            throw new RuntimeException();
        })
                .recover(RuntimeException.class, -1);

        assertThat(recovered, is(Try.of(() -> -1)));
    }

    @Test
    public void recover_function_failure() {
        Try<Integer> recovered = Try.<Integer>of(() -> {
            throw new RuntimeException();
        })
                .recover(exception -> -1);

        assertThat(recovered, is(Try.of(() -> -1)));
    }

    @Test
    public void recoverWith_function_failure() {
        Try<Integer> recovered = Try.<Integer>of(() -> {
            throw new RuntimeException();
        }).recoverWith(exception -> Try.of(() -> -1));

        assertThat(recovered, is(Try.of(() -> -1)));
    }
    
    @Test
    public void filterTry_success_predicate_passes_function() {
        Try<Integer> vavrTry = Try.of(() -> 1).filterTry(x -> x > 0, value -> new RuntimeException());
        
        assertThat(vavrTry, is(Try.of(() -> 1)));
    }

    @Test
    public void filterTry_success_predicate_notPasses_function() {
        var runtimeException = new RuntimeException();
        
        Try<Integer> vavrTry = Try.of(() -> 1).filterTry(x -> x > 10, value -> runtimeException);

        assertThat(vavrTry.getCause(), is(runtimeException));
    }

    @Test
    public void filterTry_failure_predicate_function() {
        var illegalArgumentException = new IllegalArgumentException();

        Try<Integer> vavrTry = Try.<Integer>of(() -> {throw illegalArgumentException;})
                .filterTry(x -> x > 0, value -> new RuntimeException());

        assertThat(vavrTry.getCause(), is(illegalArgumentException));
    }

    @Test
    public void filterTry_success_predicate_exception_function() {
        var nullPointerException = new NullPointerException();

        Try<Integer> vavrTry = Try.of(() -> 1)
                .filterTry(x -> {throw nullPointerException;}, value -> new RuntimeException());

        assertThat(vavrTry.getCause(), is(nullPointerException));
    }

    @Test
    public void filterTry_failure_predicate_exception_function() {
        var illegalArgumentException = new IllegalArgumentException();

        Try<Integer> vavrTry = Try.<Integer>of(() -> {throw illegalArgumentException;})
                .filterTry(x -> {throw new NullPointerException();}, value -> new RuntimeException());

        assertThat(vavrTry.getCause(), is(illegalArgumentException));
    }
}
