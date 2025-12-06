package com.johnpickup.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Pair<S, T> {
    final S s;
    final T t;
    public static <S, T> Pair<S, T> of(S s, T t) {
        return new Pair<>(s, t);
    }
}
