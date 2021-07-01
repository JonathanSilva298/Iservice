package com.iservice.iservice.classes

open class Message(
    var text: String? = null,
    var timestamp: Long? = null,
    var fromId: String? = null,
    var toId: String? = null,
    var userPhoto: String? = null
)