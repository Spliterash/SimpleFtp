package ru.spliterash.simpleftp.filesystem

import org.apache.ftpserver.ftplet.FileSystemView
import org.apache.ftpserver.ftplet.FtpFile
import org.slf4j.LoggerFactory
import ru.spliterash.simpleftp.common.normalize
import java.io.File
import java.util.*
import java.util.regex.Pattern

private val log = LoggerFactory.getLogger(VirtualFileSystem::class.java)

class VirtualFileSystem(
    val mounts: List<VirtualFileSystemMount>,
    private val exclude: List<Pattern>
) : FileSystemView {
    val virtualFolders = hashSetOf<String>()

    init {
        mounts.forEach {
            val normalized = it.path.normalize()
            var path = normalized.substringBeforeLast("/")
            while (path != "") {
                virtualFolders.add(path)

                path = path.substringBeforeLast("/")
            }
        }
        virtualFolders += "/"
    }

    private var currentPath = "/"

    override fun getHomeDirectory(): FtpFile {
        return wrap("/")
    }

    override fun getWorkingDirectory(): FtpFile {
        return wrap(currentPath)
    }

    override fun changeWorkingDirectory(dir: String): Boolean {
        val file = getFile(dir)
        val result = file.isDirectory
        if (result)
            currentPath = file.absolutePath

        return result
    }

    override fun getFile(file: String): FtpFile {
        val finalPath = getPhysicalName(currentPath, file)

        return wrap(finalPath)
    }

    override fun isRandomAccessible() = true

    override fun dispose() {
        // Похерам
    }

    fun wrap(virtualPath: String): FtpFile {
        if (virtualFolders.contains(virtualPath))
            return VirtualDirectory(virtualPath, this)
        val mount = mounts
            .first { mount ->
                virtualPath.startsWith(mount.path)
            }

        return wrapRelative(virtualPath, mount)
    }

    fun wrapRelative(virtualPath: String, mount: VirtualFileSystemMount): FtpFile {
        if (isExcluded(virtualPath))
            return ExcludedFile(virtualPath)

        val relativePath = virtualPath.substring(mount.path.length)
        val realFile = File(mount.file, relativePath)

        return RelativeFile(virtualPath, this, mount, realFile)
    }

    fun isExcluded(virtualPath: String) =
        exclude.any { it.matcher(virtualPath).matches() }

    protected fun getPhysicalName(
        currDir: String,
        fileName: String,
    ): String {

        // normalize root dir
        val root = "/"

        // normalize file name
        val normalizedFileName: String = fileName
        var result: String

        // if file name is relative, set resArg to root dir + curr dir
        // if file name is absolute, set resArg to root dir
        result = if (normalizedFileName[0] != '/') {
            // file name is relative
            val normalizedCurrDir: String = currDir.normalize()
            root + normalizedCurrDir.substring(1)
        } else {
            root
        }

        // strip last '/'
        result = trimTrailingSlash(result)

        // replace ., ~ and ..
        // in this loop resArg will never end with '/'
        val st = StringTokenizer(normalizedFileName, "/")
        while (st.hasMoreTokens()) {
            val tok = st.nextToken()

            // . => current directory
            if (tok == ".") {
                // ignore and move on
            } else if (tok == "..") {
                // .. => parent directory (if not root)
                if (result.startsWith(root)) {
                    val slashIndex = result.lastIndexOf('/')
                    if (slashIndex != -1) {
                        result = result.substring(0, slashIndex)
                    }
                }
            } else if (tok == "~") {
                // ~ => home directory (in this case the root directory)
                result = trimTrailingSlash(root)
                continue
            } else {
                result = "$result/$tok"
            }
        }

        // add last slash if necessary
        if (result.length + 1 == root.length) {
            result += '/'
        }

        // make sure we did not end up above root dir
        if (!result.startsWith(root)) {
            result = root
        }
        return result
    }

    private fun trimTrailingSlash(path: String): String {
        return if (path[path.length - 1] == '/') {
            path.substring(0, path.length - 1)
        } else {
            path
        }
    }
}