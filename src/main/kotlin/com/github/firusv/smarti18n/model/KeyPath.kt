package com.github.firusv.smarti18n.model

class KeyPath : ArrayList<String?> {
    constructor()

    constructor(vararg path: String?) {
        super.addAll(listOf(*path))
    }

    constructor(path: List<String?>) : super(path)

    constructor(path: KeyPath, vararg pathToAppend: String?) : super(path) {
        super.addAll(listOf(*pathToAppend))
    }

    override fun toString(): String {
        return super.toString()
    }
}