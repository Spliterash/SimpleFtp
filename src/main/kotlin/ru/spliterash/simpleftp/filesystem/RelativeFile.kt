package ru.spliterash.simpleftp.filesystem

import org.apache.ftpserver.ftplet.FtpFile
import ru.spliterash.simpleftp.common.name
import java.io.*

class RelativeFile(
    private val path: String,
    private val filesystem: VirtualFileSystem,
    private val mount: VirtualFileSystemMount,
    val realFile: File
) : FtpFile {

    override fun getAbsolutePath() = path

    override fun getName(): String = path.name()

    override fun isHidden() = realFile.isHidden

    override fun isDirectory() = realFile.isDirectory

    override fun isFile() = realFile.isFile

    override fun doesExist() = realFile.exists()

    override fun isReadable() = realFile.canRead()


    override fun isWritable(): Boolean {
        if (realFile.exists())
            return realFile.canWrite()

        return true
    }

    override fun isRemovable(): Boolean {
        return path != mount.path
    }

    override fun getOwnerName() = "user"

    override fun getGroupName() = "group"

    override fun getLinkCount() = if (realFile.isDirectory) 3 else 1

    override fun getLastModified() = realFile.lastModified()

    override fun setLastModified(time: Long) = realFile.setLastModified(time)

    override fun getSize() = realFile.length()

    override fun getPhysicalFile() = realFile

    override fun mkdir(): Boolean {
        return realFile.mkdirs()
    }

    override fun delete() = realFile.delete()

    override fun move(destination: FtpFile): Boolean {
        if (destination !is RelativeFile)
            return false
        val dest = destination.realFile
        if (dest.exists())
            return false

        return realFile.renameTo(dest)
    }

    override fun listFiles(): List<FtpFile>? {
        if (!realFile.isDirectory)
            return null

        val list = realFile.listFiles() ?: return null

        return list
            .map { file ->
                val relativePath = file.path.substring(mount.file.path.length)
                val virtualPath = mount.path + relativePath

                RelativeFile(virtualPath, filesystem, mount, file)
            }
    }

    override fun createOutputStream(offset: Long): OutputStream {
        val raf = RandomAccessFile(realFile, "rw")

        raf.setLength(offset)
        raf.seek(offset)

        return object : FileOutputStream(raf.fd) {
            override fun close() {
                super.close()
                raf.close()
            }
        }
    }

    override fun createInputStream(offset: Long): InputStream {
        val raf = RandomAccessFile(realFile, "r")
        raf.seek(offset)

        return object : FileInputStream(raf.fd) {
            override fun close() {
                super.close()
                raf.close()
            }
        }
    }
}