package dev.dejvokep.boostedyaml.utils.format;

import org.snakeyaml.engine.v2.nodes.Tag;

public interface Formatter<S, V> {

    S format(Tag tag, V value, S previous);

    static <S, V> Formatter<S, V> identity() {
        return (tag, value, previous) -> previous;
    }

}