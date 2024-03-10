package api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import model.DisneyCharacter
import model.DisneyResponse

interface ApiService {

    /**
     * load characters by page
     * @param page the page number
     * @return DisneyResponse<List<DisneyCharacter>>
     */
    suspend fun loadCharacters(page: Int): DisneyResponse<List<DisneyCharacter>>
}

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    override suspend fun loadCharacters(page: Int): DisneyResponse<List<DisneyCharacter>> {
        return httpClient.get("https://api.disneyapi.dev/character") {
            parameter("page", page)
        }.body()
    }
}