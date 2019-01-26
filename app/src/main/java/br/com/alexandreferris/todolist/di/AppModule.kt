package br.com.alexandreferris.todolist.di

import android.support.v7.app.AppCompatActivity
import br.com.alexandreferris.todolist.repository.remote.ItemHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: AppCompatActivity) {

    @Provides
    @Singleton
    fun provideItemHelper(): ItemHelper {
        return ItemHelper(app.applicationContext)
    }

}