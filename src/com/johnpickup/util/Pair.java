package com.johnpickup.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Pair<S, T> {
    final S value1;
    final T value2;
    public static <S, T> Pair<S, T> of(S s, T t) {
        return new Pair<>(s, t);
    }

    @Override
    public String toString() {
        return String.format("(%s + %s)", value1, value2);
    }
}
