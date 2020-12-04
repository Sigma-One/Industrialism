package sigmaone.industrialism.util

import net.minecraft.util.StringIdentifiable

enum class IO: StringIdentifiable{
    NONE {
        override fun next(): IO { return INPUT }
        override fun asString(): String { return toString() }
    },
    INPUT {
        override fun next(): IO { return OUTPUT }
        override fun asString(): String { return toString() }
    },
    OUTPUT {
        override fun next(): IO { return NONE }
        override fun asString(): String { return toString() }
    };

    abstract fun next(): IO
}