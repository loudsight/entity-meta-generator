package com.loudsight.collection;

public record Pair<P1, P2>(P1 one, P2 two) {

    public static <P1, P2> Pair<P1, P2> of(P1 one, P2 two) {
        return new Pair<>(one, two);
    }
}
