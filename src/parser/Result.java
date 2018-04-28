package parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

import utils.Tuple;

public final class Result<A> {
    final Deque<Tuple<A, String>> deck;

    Result(Deque<Tuple<A, String>> deck) {
        this.deck = deck;
    }

    public static <A> Result<A> empty() {
        return new Result<>(new ArrayDeque<>());
    }

    public static <A> Result<A> of(Tuple<A, String> tuple) {
        Result<A> instance = Result.empty();

        instance.deck.addFirst(tuple);

        return instance;
    }

    private Deque<Tuple<A, String>> getDeck() {
        return this.deck;
    }

    public Tuple<A, String> pop() {
        return deck.pop();
    }

    public <B> Result<B> concatMap(Function<A, Parser<B>> f) {
        Deque<Tuple<B, String>> newDeck = this.deck.stream().map(tuple -> f.apply(tuple.fst).apply(tuple.snd))
                .map(Result::getDeck).flatMap(Deque::stream)
                .collect(ArrayDeque::new, ArrayDeque::add, ArrayDeque::addAll);

        return new Result<>(newDeck);
    }

    @Override
    public String toString() {
        return deck.toString();
    }

    public boolean isEmpty() {
        return this.deck.isEmpty();
    }
}
