package amirparsa.shk.omnispace.core.network

import amirparsa.shk.omnispace.core.network.model.AuthSessionResponse
import amirparsa.shk.omnispace.core.network.model.UserProfileResponse
import amirparsa.shk.omnispace.core.utils.NetworkResult

interface AuthRepository {
    suspend fun login(username: String, password: String): NetworkResult<UserProfileResponse>
    suspend fun checkSession(): NetworkResult<AuthSessionResponse>
    suspend fun getProfile(): NetworkResult<UserProfileResponse>
    suspend fun logout(): NetworkResult<Boolean>
}
