package com.androidacestudio.security.hashing

interface HashingService {
    fun generateSaltHash(value: String, saltLength: Int = 32): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}