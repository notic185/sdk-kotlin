package io.github.notic185.sdk_kotlin.utilities

class Simplifier {
    companion object {
        fun transform(target: Any?): Map<String, Any?>? {
            return when (target) {
                is Map<*, *> -> target as Map<String, Any?>
                is List<*> -> {
                    // -
                    val result = mutableMapOf<String, Any?>()
                    // -
                    target.forEachIndexed { itemIndex, item ->
                        result[itemIndex.toString()] = item
                    }
                    // -
                    result
                }

                else -> null
            }
        }

        fun pileDown(target: Any?, keyPrefix: String? = null): Map<String, Any?> {
            // -
            val result = mutableMapOf<String, Any?>()
            // -
            this.transform(target)?.forEach { (key, value) ->
                // -
                val prefixedKey = if (keyPrefix == null) key else "$keyPrefix.$key"
                // -
                this.transform(value).let {
                    if (it == null) {
                        result[prefixedKey] = value
                    } else {
                        result.putAll(pileDown(it, prefixedKey))
                    }
                }
            }
            // -
            return result
        }
    }
}
