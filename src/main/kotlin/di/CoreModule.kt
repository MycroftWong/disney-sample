package di

import api.ApiService
import api.ApiServiceImpl
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import wang.mycroft.disney.data.Database
import wang.wang.disney.data.DisneyCharacterQueries
import wang.wang.disney.data.FavoriteCharacterQueries

fun CoreModule() = module {
    single<HttpClient> {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = false
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single<ApiService> {
        ApiServiceImpl(get())
    }

    single<Database> {
        val driver = JdbcSqliteDriver("jdbc:sqlite:disney.db")
        Database.Schema.create(driver)
        Database(driver)
    }

    single<DisneyCharacterQueries> {
        get<Database>().disneyCharacterQueries
    }

    single<FavoriteCharacterQueries> {
        get<Database>().favoriteCharacterQueries
    }

}