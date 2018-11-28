# java11-vavr-try

_Reference_: https://www.baeldung.com/vavr-try  
_Reference_: https://www.vavr.io/vavr-docs/#_try  
_Reference_: https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/control/Try.html  
_Reference_: https://dzone.com/articles/why-try-better-exception-handling-in-java-with-try

# preface
`Try` is a monadic container type which represents a computation 
that may either result in an exception, or return a successfully 
computed value. Instances of `Try`, are either an instance of 
`Success` or `Failure`.

# project description
We provide description and tests for `Try`'s methods.

Please refer my other github project: 
https://github.com/mtumilowicz/java11-vavr-option
because we won't provide examples to the similar methods 
described in the project about `Option`.

## static methods
* `Try<T>	failure(Throwable exception)` - 
Creates a `Try.Failure` that contains the given exception.
    ```
    IllegalArgumentException exception = new IllegalArgumentException();
    Try<IllegalArgumentException> failure = Try.failure(exception);
    
    assertTrue(failure.isFailure());
    assertTrue(failure instanceof Try.Failure);
    assertThat(failure.getCause(), is(exception));
    ```
* `Try<T>	success(T value)` -
Creates a `Try.Success` that contains the given value.
    ```
    Try<Integer> success = Try.success(1);
    
    assertTrue(success.isSuccess());
    assertTrue(success instanceof Try.Success);
    assertThat(success.get(), is(1));
    ```
* `Try<T>	narrow(Try<? extends T> t)`
* `Try<T>	of(CheckedFunction0<? extends T> supplier)`
    * success
        ```
        Try<Integer> tryWithoutException = Try.of(() -> 1);
        
        assertTrue(tryWithoutException.isSuccess());
        assertThat(tryWithoutException.get(), is(1));
        ```
    * failure
        ```
        RuntimeException exception = new RuntimeException();
        Try<RuntimeException> exceptionTry = Try.of(() -> {
            throw exception;
        });
        
        assertTrue(exceptionTry.isFailure());
        assertThat(exceptionTry.getCause(), is(exception));
        ```
* `Try<T>	ofCallable(Callable<? extends T> callable)` - 
identical to `of`
* `Try<T>	ofSupplier(Supplier<? extends T> supplier)` - of
without checked exceptions
* `Try<Void>	run(CheckedRunnable runnable)` - 
returns `Success(null)` if no exception occurs, 
otherwise `Failure(throwable)` if an exception occurs
* `Try<Void>	runRunnable(Runnable runnable)` - same as `run`
but without checked exceptions
* `Try<Seq<T>>	sequence(Iterable<? extends Try<? extends T>> values)` - 
Reduces many `Trys` into a single `Try` by transforming an 
`Iterable<Try<? extends T>>` into a `Try<Seq<T>>`.
* `<T1 extends AutoCloseable> Try.WithResources1<T1> withResources(CheckedFunction0<? extends T1> t1Supplier)`
    * java `try-with-resources`
        ```
        String fileName = "NonExistingFile.txt";
        
        String fileLines;
        try (var stream = Files.lines(Paths.get(fileName))) {
        
            fileLines = stream.collect(joining(","));
        }
        ```
    * vavr `try-with-resources`
        ```
        String fileName = "src/test/resources/lines.txt";
        Try<String> fileLines = Try.withResources(() -> Files.lines(Paths.get(fileName)))
                        .of(stream -> stream.collect(joining(",")));
        ```
        * success
            ```
            String fileName = "src/test/resources/lines.txt";
            
            Try<String> fileLines = Try.withResources(() -> Files.lines(Paths.get(fileName)))
                    .of(stream -> stream.collect(joining(",")));
            
            assertTrue(fileLines.isSuccess());
            assertThat(fileLines.get(), is("1,2,3"));
            ```
        * failure
            ```
            String fileName = "NonExistingFile.txt";
            
            Try<String> fileLines = Try.withResources(() -> Files.lines(Paths.get(fileName)))
                    .of(stream -> stream.collect(joining(",")));
            
            assertTrue(fileLines.isFailure());
            ```