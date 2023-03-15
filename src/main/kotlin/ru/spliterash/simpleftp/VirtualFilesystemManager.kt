package ru.spliterash.simpleftp

import org.apache.ftpserver.ftplet.FileSystemFactory
import org.apache.ftpserver.ftplet.FileSystemView
import org.apache.ftpserver.ftplet.FtpException
import org.apache.ftpserver.ftplet.User
import ru.spliterash.simpleftp.filesystem.VirtualFileSystem
import ru.spliterash.simpleftp.user.FtpUser

class VirtualFilesystemManager : FileSystemFactory {
    override fun createFileSystemView(user: User): FileSystemView {
        if (user !is FtpUser)
            throw FtpException("Invalid user type")

        val mounts = user.mounts
        return VirtualFileSystem(mounts)
    }

}
