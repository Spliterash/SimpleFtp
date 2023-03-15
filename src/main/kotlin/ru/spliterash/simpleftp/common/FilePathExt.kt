package ru.spliterash.simpleftp.common

import org.apache.commons.io.FilenameUtils

fun String.normalize(): String = FilenameUtils.normalizeNoEndSeparator(this, true)

fun String.name(): String = FilenameUtils.getName(this)