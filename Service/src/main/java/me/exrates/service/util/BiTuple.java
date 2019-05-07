package me.exrates.service.util;

public class BiTuple<L,R> {

    public final L left;
    public final R right;

    public BiTuple(L left, R right) {
        this.left = left;
        this.right = right;
    }
}