package ru.spliterash.simpleftp.filesystem

import org.apache.ftpserver.ftplet.FtpFile
import ru.spliterash.simpleftp.common.name
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ExcludedFile(private val path: String) : FtpFile {
    override fun getAbsolutePath() = path

    override fun getName(): String = path.name()

    override fun isHidden() = false

    override fun isDirectory() = false

    override fun isFile() = false

    override fun doesExist() = false

    override fun isReadable() = false

    override fun isWritable() = false

    override fun isRemovable() = false

    override fun getOwnerName() = "user"

    override fun getGroupName() = "group"

    override fun getLinkCount() = 1

    override fun getLastModified() = 0L

    override fun setLastModified(time: Long) = false

    override fun getSize() = 0L

    override fun getPhysicalFile() = null

    override fun mkdir() = false

    override fun delete() = false

    override fun move(destination: FtpFile?) = false

    override fun listFiles() = null

    override fun createOutputStream(offset: Long): OutputStream {
        throw IOException("Not supported")
    }

    override fun createInputStream(offset: Long): InputStream {
        throw IOException("Not supported")
    }

}
