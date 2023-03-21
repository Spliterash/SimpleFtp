package ru.spliterash.simpleftp.filesystem

import org.apache.ftpserver.ftplet.FtpFile
import ru.spliterash.simpleftp.common.name
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class VirtualDirectory(
    private val path: String,
    private val filesystem: VirtualFileSystem
) : FtpFile {
    override fun getAbsolutePath() = path

    override fun getName(): String = path.name()

    override fun isHidden() = false

    override fun isDirectory() = true

    override fun isFile() = false

    override fun doesExist() = true

    override fun isReadable() = true

    override fun isWritable() = false

    override fun isRemovable() = false

    override fun getOwnerName() = "virtual"

    override fun getGroupName() = "virtual"

    // FTP magic number, do not have clue what is this
    override fun getLinkCount() = 3

    override fun getLastModified() = 0L

    override fun setLastModified(time: Long) = false

    override fun getSize() = 0L

    override fun getPhysicalFile() = null

    override fun mkdir() = false

    override fun delete() = false

    override fun move(destination: FtpFile?) = false

    override fun listFiles(): MutableList<out FtpFile> {
        val result = arrayListOf<FtpFile>()

        // Сначала найдём виртуальные херни
        for (virtualFolder in filesystem.virtualFolders) {
            if (!virtualFolder.startsWith(path))
                continue

            if (!oneDepth(path, virtualFolder))
                continue

            result += VirtualDirectory(virtualFolder, filesystem)
        }
        // Отлично, теперь найдём реальные смонтированные папки
        for (mount in filesystem.mounts) {
            if (!mount.path.startsWith(path))
                continue
            if (!oneDepth(path, mount.path))
                continue
            result += filesystem.wrapRelative(mount.path, mount)
        }

        return result
    }

    private fun oneDepth(parent: String, child: String): Boolean {
        if (parent == child)
            return false
        val lastSlashIndex = child.lastIndexOf("/")
        var childParent = child.substring(0, lastSlashIndex)
        if (childParent == "")
            childParent = "/"
        return parent == childParent
    }

    override fun createOutputStream(offset: Long): OutputStream {
        throw IOException("Not supported")
    }

    override fun createInputStream(offset: Long): InputStream {
        throw IOException("Not supported")
    }
}