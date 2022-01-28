package com.example.runnincle.di

import android.content.Context
import com.example.runnincle.framework.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) // Application 컴포넌트 생명주기에 맞춰 사용 함
@Module // 추상클래스, 외부 라이브러리, 인터페이스 등 constructor-injection 할 수 없는 경우에 사용
object AppModule {

    @Singleton
    @Provides // 모듈이 object 인 메서드에서 사용. n개 파라미터 사용가능. function body 있음.
    fun provideApplication(@ApplicationContext app: Context) : BaseApplication {
        return app as BaseApplication
    }

    @Singleton
    @Provides
    fun randomString(): String {
        return "randomString"
    }


}