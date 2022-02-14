package com.example.runnincle.di

import android.content.Context
import com.example.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.example.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.example.runnincle.business.data.cache.implementation.ProgramCacheDataSourceImpl
import com.example.runnincle.business.data.cache.implementation.WorkoutCacheDataSourceImpl
import com.example.runnincle.business.domain.util.DateUtil
import com.example.runnincle.business.domain.util.ResourcesProvider
import com.example.runnincle.business.domain.util.ResourcesProviderImpl
import com.example.runnincle.business.interactors.create_program.CreateProgramInteractors
import com.example.runnincle.business.interactors.program_list.ProgramListInteractors
import com.example.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.example.runnincle.framework.datasource.cache.abstraction.WorkoutDaoService
import com.example.runnincle.framework.datasource.cache.database.ProgramDao
import com.example.runnincle.framework.datasource.cache.database.ProgramDatabase
import com.example.runnincle.framework.datasource.cache.database.WorkoutDao
import com.example.runnincle.framework.datasource.cache.database.WorkoutDatabase
import com.example.runnincle.framework.datasource.cache.implementation.ProgramDaoServiceImpl
import com.example.runnincle.framework.datasource.cache.implementation.WorkoutDaoServiceImpl
import com.example.runnincle.framework.datasource.cache.mappers.ProgramCacheMapper
import com.example.runnincle.framework.datasource.cache.mappers.WorkoutCacheMapper
import com.example.runnincle.framework.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
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
    fun provideProgramDao(programDatabase: ProgramDatabase): ProgramDao {
        return programDatabase.programDao()
    }

    @Singleton
    @Provides
    fun provideWorkoutDao(workoutDatabase: WorkoutDatabase): WorkoutDao {
        return workoutDatabase.workoutDao()
    }

    @Singleton
    @Provides
    fun provideDateUtil(dateFormat: SimpleDateFormat): DateUtil {
        return DateUtil(dateFormat)
    }

    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf
    }

    @Singleton
    @Provides
    fun provideCreateProgramInteractors(
        programCacheDataSource: ProgramCacheDataSource,
        workoutCacheDataSource: WorkoutCacheDataSource,
        dateUtil: DateUtil
    ): CreateProgramInteractors {
        return CreateProgramInteractors (programCacheDataSource, workoutCacheDataSource, dateUtil)
    }

    @Singleton
    @Provides
    fun provideProgramListInteractors(
        programCacheDataSource: ProgramCacheDataSource,
        workoutCacheDataSource: WorkoutCacheDataSource,
    ): ProgramListInteractors {
        return ProgramListInteractors (programCacheDataSource, workoutCacheDataSource)
    }

    @Singleton
    @Provides
    fun provideProgramCacheDataSource(
        programDaoService: ProgramDaoService
    ): ProgramCacheDataSource {
        return ProgramCacheDataSourceImpl(programDaoService)
    }

    @Singleton
    @Provides
    fun provideWorkoutCacheDataSource(
        workoutDaoService: WorkoutDaoService
    ): WorkoutCacheDataSource {
        return WorkoutCacheDataSourceImpl(workoutDaoService)
    }

    @Singleton
    @Provides
    fun provideWorkoutDaoService(
        workoutDao: WorkoutDao,
        workoutCacheMapper: WorkoutCacheMapper
    ): WorkoutDaoService {
        return WorkoutDaoServiceImpl(workoutDao, workoutCacheMapper)
    }

    @Singleton
    @Provides
    fun provideProgramDaoService(
        programDao: ProgramDao,
        programCacheMapper: ProgramCacheMapper,
        dateUtil: DateUtil
    ): ProgramDaoService {
        return ProgramDaoServiceImpl(
            programDao, programCacheMapper, dateUtil
        )
    }

    @Singleton
    @Provides
    fun provideProgramCacheMapper(): ProgramCacheMapper {
        return ProgramCacheMapper()
    }

    @Singleton
    @Provides
    fun provideWorkoutCacheMapper(): WorkoutCacheMapper {
        return WorkoutCacheMapper()
    }

    @Singleton
    @Provides
    fun provideResourcesProvider(@ApplicationContext app: Context): ResourcesProvider {
        return ResourcesProviderImpl(app)
    }

}