package ru.spliterash.simpleftp.filesystem

import java.io.File

data class VirtualFileSystemMount(
    /**
     * Виртуальный путь
     */
    val path: String,
    /**
     * Реальный файл/папка
     */
    val file: File
)