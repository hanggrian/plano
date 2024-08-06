package com.hanggrian.plano

/** Represents a dimension of a paper. */
public interface Size {
    public val width: Float

    public val height: Float

    public val dimension: String get() = "${width.clean()} x ${height.clean()}"
}
