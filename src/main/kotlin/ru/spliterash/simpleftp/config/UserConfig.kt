package ru.spliterash.simpleftp.config

data class UserConfig(
    val name: String,
    val password: String,
    val mounts: List<String>
)
