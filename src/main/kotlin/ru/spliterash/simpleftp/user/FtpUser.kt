package ru.spliterash.simpleftp.user

import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission
import org.apache.ftpserver.usermanager.impl.TransferRatePermission
import org.apache.ftpserver.usermanager.impl.WritePermission
import ru.spliterash.simpleftp.filesystem.VirtualFileSystemMount
import java.util.regex.Pattern

class FtpUser(
    val mounts: List<VirtualFileSystemMount>,
    val excludes: List<Pattern>
) : BaseUser() {
    init {
        authorities = listOf(
            WritePermission(),
            ConcurrentLoginPermission(0, 0),
            TransferRatePermission(0, 0)
        )
    }
}