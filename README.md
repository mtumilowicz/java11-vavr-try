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
## instance methods
* `Try<T>	andFinally(Runnable runnable)` - 
Provides try's finally behavior no matter what the result 
of the operation is.
    ```
    var invoked = new AtomicBoolean();
    
    Try.of(() -> 1).andFinally(() -> invoked.set(true));
    
    assertTrue(invoked.get());
    ```
    ```
    var invoked = new AtomicBoolean();
    
    Try.of(() -> {
        throw new RuntimeException();
    }).andFinally(() -> invoked.set(true));
    
    assertTrue(invoked.get());
    ```
    ```
    Try<Integer> vavrTry = Try.of(() -> 1).andFinally(() -> {
        throw new RuntimeException();
    });
    
    assertTrue(vavrTry.isFailure());
    assertTrue(vavrTry.getCause() instanceof RuntimeException)
    ```
* `Try<T>	andFinallyTry(CheckedRunnable runnable)`
* `Try<T>	andThen(Consumer<? super T> consumer)`
    * if this is a success - pass the value to consumer
    * if this is a failure - do nothing
    * if success and then exception in consumer - 
    failure with new exception
    ```
    Try<Integer> vavrTry = Try.<Integer>of(() -> {
        throw new IllegalArgumentException();
    }).andThen(() -> {
                throw new RuntimeException();
            });
    
    assertTrue(vavrTry.isFailure());
    assertTrue(vavrTry.getCause() instanceof IllegalArgumentException);
    ```
* `Try<T>	andThenTry(CheckedConsumer<? super T> consumer)`
* `Try<T>	andThenTry(CheckedRunnable runnable)`
* `Try<T>	andThen(Runnable runnable)`
* `Try<R>	collect(PartialFunction<? super T,? extends R> partialFunction)`
* `Try<Throwable>	failed()`
    * returns `Success(throwable)` if this is a `Failure(throwable)`
    * returns `Failure(new NoSuchElementException("Success.failed()"))` 
if this is a `Success`.
* `Try<T>	filterTry(CheckedPredicate<? super T> predicate,
           CheckedFunction1<? super T,? extends Throwable> errorProvider)`
    * returns this if this is a `Failure` 
    * returns this if is a `Success` and the value satisfies the predicate.
    * returns a new `Failure`, if this is a `Success` and 
    the value does not satisfy the `Predicate` or an exception
    occurs testing the predicate. The returned `Failure` wraps 
    a `Throwable` instance provided by the given `errorProvider`.
    ```
    var runtimeException = new RuntimeException();
    
    Try<Integer> vavrTry = Try.of(() -> 1).filterTry(x -> x > 10, value -> runtimeException);
    
    assertThat(vavrTry.getCause(), is(runtimeException));
    ```
* `Try<T>	filter(Predicate<? super T> predicate,
      Function<? super T,? extends Throwable> errorProvider)`
* `Try<T>	filter(Predicate<? super T> predicate)`
* `Try<T>	filter(Predicate<? super T> predicate,
      Supplier<? extends Throwable> throwableSupplier)`
* `Try<T>	filterTry(CheckedPredicate<? super T> predicate)` 
* `Try<T>	filterTry(CheckedPredicate<? super T> predicate,
         Supplier<? extends Throwable> throwableSupplier)`
* `Try<U>	flatMap(Function<? super T,? extends Try<? extends U>> mapper)`
* `Try<U>	flatMapTry(CheckedFunction1<? super T,? extends Try<? extends U>> mapper)`
* `T	get()` - 
Gets the result of this `Try` if this is a `Success` or throws 
if this is a `Failure`.
    * If this is a `Failure`, the underlying cause of type 
    `Throwable` is thrown.
* `Throwable	getCause()` - 
Gets the cause if this is a `Failure` or throws 
`UnsupportedOperationException` if this is a `Success`.
* `T	getOrElseGet(Function<? super Throwable,? extends T> other)` 
`<X extends Throwable>
T	getOrElseThrow(Function<? super Throwable,X> exceptionProvider) `
* `int	hashCode()`
* `boolean	isEmpty()` - 
true if this is a `Failure`, returns false if this is a `Success`.
* `boolean	isFailure()`
* `boolean	isSuccess()`
* `Try<U>	map(Function<? super T,? extends U> mapper)`
* `Try<T>	mapFailure(API.Match.Case<? extends Throwable,? extends Throwable>... cases)` - 
Maps the cause to a new exception if this is a `Failure` 
or returns this instance if this is a `Success`.
`Try<U>	mapTry(CheckedFunction1<? super T,? extends U> mapper)` - 
Runs the given checked function if this is a `Success`, 
passing the result of the current expression to it.
* `Try<T>	onFailure(Consumer<? super Throwable> action)` - 
Consumes the throwable if this is a `Failure`.
* `Try<T>	onSuccess(Consumer<? super T> action)` - 
Consumes the value if this is a `Success`.
* `Try<T>	orElse(Supplier<? extends Try<? extends T>> supplier)` 
* `Try<T>	orElse(Try<? extends T> other) `
* `void	orElseRun(Consumer<? super Throwable> action)` 
* `Try<T>	peek(Consumer<? super T> action)` - 
Applies the action to the value of a `Success` or does 
nothing in the case of a `Failure`.
* `<X extends Throwable> Try<T>	recover(Class<X> exception,
       Function<? super X,? extends T> f)` - 
Returns `this`, if `this` is a `Success` or `this` is a 
`Failure` and the cause is not assignable from `cause.getClass()`.
    ```
    Try<Integer> recovered = Try.<Integer>of(() -> {
        throw new RuntimeException();
    })
            .recover(RuntimeException.class, -1);
    
    assertThat(recovered, is(Try.of(() -> -1)));
    ```
* `<X extends Throwable>
Try<T>	recover(Class<X> exception,
       T value)`
* `Try<T>	recover(Function<? super Throwable,? extends T> f)`
* `<X extends Throwable>
Try<T>	recoverWith(Class<X> exception,
           Function<? super X,Try<? extends T>> f)` - same as `recover` but recover function returns Try
    ```
    Try<Integer> recovered = Try.<Integer>of(() -> {
        throw new RuntimeException();
    }).recoverWith(exception -> Try.of(() -> -1));
    
    assertThat(recovered, is(Try.of(() -> -1)));
    ```
* `<X extends Throwable>
Try<T>	recoverWith(Class<X> exception,
           Try<? extends T> recovered) `
`Try<T>	recoverWith(Function<? super Throwable,? extends Try<? extends T>> f)`
* `Either<Throwable,T>	toEither()`
* `U	transform(Function<? super Try<T>,? extends U> f)`