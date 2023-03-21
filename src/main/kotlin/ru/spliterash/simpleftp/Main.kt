package ru.spliterash.simpleftp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.commons.io.IOUtils
import org.apache.ftpserver.DataConnectionConfigurationFactory
import org.apache.ftpserver.impl.DefaultFtpServer
import org.apache.ftpserver.impl.DefaultFtpServerContext
import org.apache.ftpserver.listener.ListenerFactory
import org.slf4j.LoggerFactory
import ru.spliterash.simpleftp.common.normalize
import ru.spliterash.simpleftp.config.FtpConfig
import ru.spliterash.simpleftp.filesystem.VirtualFileSystemMount
import ru.spliterash.simpleftp.user.FtpUser
import ru.spliterash.simpleftp.user.InMemoryUserManager
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

fun main() {
    Main().launch()
}
private val log = LoggerFactory.getLogger(Main::class.java)

class Main {
    fun launch() {
        val configFile = File("config.yml")
        if (!configFile.isFile) {
            val defaultConfigStream = javaClass.classLoader.getResourceAsStream("config.yml")
            val fileStream = FileOutputStream("config.yml")
            IOUtils.copy(defaultConfigStream, fileStream)

            println("Please setup config")
            return
        }

        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val config = mapper.readValue<FtpConfig>(configFile)

        val filesystem = VirtualFilesystemManager()

        val context = DefaultFtpServerContext()

        context.fileSystemManager = filesystem
        context.userManager = InMemoryUserManager()
        // CRINGE
        context.addListener("default", ListenerFactory().apply {
            port = config.server.port
            dataConnectionConfiguration = DataConnectionConfigurationFactory().apply {
                passiveExternalAddress = config.server.address
                passivePorts = config.server.passivePorts
            }
                .createDataConnectionConfiguration()

        }.createListener())

        for (user in config.users) {
            val mounts = parseMounts(user.mounts)
            val excludes = parseExcludes(user.excludes)
            context.userManager.save(
                FtpUser(mounts,excludes).apply {
                    name = user.name
                    password = user.password
                    homeDirectory = "/"
                }
            )
        }

        val server = DefaultFtpServer(context)
        server.start()
    }

    private fun parseExcludes(excludes: List<String>): List<Pattern> = excludes.mapNotNull {
        try {
            Pattern.compile(it)
        } catch (ex: Exception) {
            log.warn("Failed parse exclude '${it}'", it)
            null
        }
    }

    private fun parseMounts(strMounts: List<String>): List<VirtualFileSystemMount> {
        val mounts = arrayListOf<VirtualFileSystemMount>()

        for (mount in strMounts) {
            val split = mount.split(":")
            val realFile = File(split[0])
            val virtualPath = split[1].normalize()

            mounts += VirtualFileSystemMount(virtualPath, realFile)
        }

        return mounts
    }
}