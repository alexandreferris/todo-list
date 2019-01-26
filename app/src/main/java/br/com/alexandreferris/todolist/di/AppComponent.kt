package br.com.alexandreferris.todolist.di

import br.com.alexandreferris.todolist.view.EditItem
import br.com.alexandreferris.todolist.view.Main
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun injectMain(app: Main)
    fun injectEditItem(app: EditItem)
}