package ru.spliterash.simpleftp.config

import com.fasterxml.jackson.annotation.JsonProperty

data class ServerConfig(
    val port: Int,
    val address: String,
    @JsonProperty("passive-ports")
    val passivePorts: String
) {
}
