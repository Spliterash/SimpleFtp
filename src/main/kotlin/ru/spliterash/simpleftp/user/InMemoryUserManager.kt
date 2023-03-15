package ru.spliterash.simpleftp.user

import org.apache.ftpserver.ftplet.Authentication
import org.apache.ftpserver.ftplet.AuthenticationFailedException
import org.apache.ftpserver.ftplet.User
import org.apache.ftpserver.ftplet.UserManager
import org.apache.ftpserver.usermanager.AnonymousAuthentication
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication

class InMemoryUserManager : UserManager {
    private val users = hashMapOf<String, FtpUser>()
    override fun getUserByName(username: String): User? = users[username]

    override fun getAllUserNames() = users.keys.toTypedArray()

    override fun delete(username: String) {
        users.remove(username)
    }

    override fun save(user: User) {
        if (user !is FtpUser)
            throw IllegalArgumentException("Only ftpuser support")

        users[user.name] = user
    }

    override fun doesExist(username: String) = users.containsKey(username)
    override fun authenticate(authentication: Authentication): User {
        if (authentication is AnonymousAuthentication)
            return users["anonymous"] ?: throw AuthenticationFailedException("Anonymous not supported")
        else if (authentication !is UsernamePasswordAuthentication)
            throw AuthenticationFailedException("Unknown auth method")

        val user = users[authentication.username] ?: throw AuthenticationFailedException("User not found")

        if (user.password == authentication.password)
            return user
        else
            throw AuthenticationFailedException("Wrong pass")
    }

    override fun getAdminName() = null

    override fun isAdmin(username: String?) = false
}