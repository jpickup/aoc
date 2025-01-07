package com.johnpickup.aoc2019;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum OpCode {
    ADD(1, 3),
    MULTIPLY(1, 3),
    READ(3, 1),
    WRITE(4,1),
    JIT(5, 2),
    JIF(6, 2),
    LT(7, 3),
    EQ(8, 3),
    RBO(9, 1),
    END(99, 0);

    final int id;
    final int paramCount;

    static OpCode parse(int value) {
        switch (value) {
            case 1:
                return ADD;
            case 2:
                return MULTIPLY;
            case 3:
                return READ;
            case 4:
                return WRITE;
            case 5:
                return JIT;
            case 6:
                return JIF;
            case 7:
                return LT;
            case 8:
                return EQ;
            case 9:
                return RBO;
            case 99:
                return END;
            default:
                throw new RuntimeException("Unknown OpCode " + value);
        }
    }
}
