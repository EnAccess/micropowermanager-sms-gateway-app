package com.gateway.android.network.model

import java.io.Serializable

data class Employee(
    var name: String,
    var salary: String,
    var age: String
) : Serializable