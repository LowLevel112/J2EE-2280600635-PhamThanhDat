package com.example._PhamThanhDat.constants;

public enum Role {
    ADMIN(1L),
    USER(2L);

    public final long value;

    Role(long value) {
        this.value = value;
    }
}