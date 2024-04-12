package di

import org.koin.dsl.module
import ui.disney.viewmodel.DisneyDetailViewModel
import ui.disney.viewmodel.DisneyViewModel

fun ViewModelModule() = module {
    factory {
        DisneyViewModel(get(), get(), get())
    }
    factory { (characterId: Long) ->
        DisneyDetailViewModel(get(), get(), characterId)
    }
}