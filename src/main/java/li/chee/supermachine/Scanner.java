package li.chee.supermachine;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Scanner<T> {

    private Stream<T> stream;

    protected <X> Scanner<X> create(Stream<X> source) {
        return new Scanner<>(source);
    }

    protected Scanner(Stream<T> stream) {
        this.stream = stream;
    }

    public <X> Scanner<X> walk(Class<X> clazz) {
        @SuppressWarnings("unchecked")
        Stream<X> filteredStream = (Stream<X>) stream.filter(x -> clazz.isAssignableFrom(x.getClass()));
        return create(filteredStream);
    }

    public <X> Scanner<X> find(Class<X> clazz) {
        return walk(clazz);
    }

    public <X> Scanner<X> extract(Function<T, X> extractor) {
        return create(stream.flatMap(x -> {
            X result = extractor.apply(x);
            return result != null ? Stream.of(result) : Stream.empty();
        }));
    }

    public Scanner<T> filter(Predicate<T> predicate) {
        return create(stream.filter(predicate));
    }

    @SafeVarargs
    final public <X> Scanner<X> then(Function<Scanner<T>, Scanner<X>>... fns) {
        return new Scanner<>(stream.flatMap(x ->
                Stream.of(fns).map(fn ->
                        fn.apply(create(Stream.of(x))).stream()).reduce(Stream.empty(), Stream::concat)));
    }

    public Stream<T> stream() {
        return stream;
    }
}
