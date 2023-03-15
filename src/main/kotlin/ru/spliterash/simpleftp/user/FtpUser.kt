package ru.spliterash.simpleftp.user

import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission
import org.apache.ftpserver.usermanager.impl.TransferRatePermission
import org.apache.ftpserver.usermanager.impl.WritePermission
import ru.spliterash.simpleftp.filesystem.VirtualFileSystemMount

class FtpUser(
    val mounts: List<VirtualFileSystemMount>
) : BaseUser() {
    init {
        authorities = listOf(
            WritePermission(),
            ConcurrentLoginPermission(0, 0),
            TransferRatePermission(0, 0)
        )
    }
}