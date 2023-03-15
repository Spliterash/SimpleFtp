package ru.spliterash.simpleftp.config

data class FtpConfig(
    val server:ServerConfig,
    val users: List<UserConfig>
)