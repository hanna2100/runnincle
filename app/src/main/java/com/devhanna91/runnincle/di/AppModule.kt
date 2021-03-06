package com.devhanna91.runnincle.di

import android.content.Context
import com.devhanna91.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.devhanna91.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.devhanna91.runnincle.business.data.cache.implementation.ProgramCacheDataSourceImpl
import com.devhanna91.runnincle.business.data.cache.implementation.WorkoutCacheDataSourceImpl
import com.devhanna91.runnincle.business.util.DateUtil
import com.devhanna91.runnincle.business.domain.util.ResourcesProvider
import com.devhanna91.runnincle.business.domain.util.ResourcesProviderImpl
import com.devhanna91.runnincle.business.interactors.activity.MainActivityInteractors
import com.devhanna91.runnincle.business.interactors.create_program.CreateProgramInteractors
import com.devhanna91.runnincle.business.interactors.program_list.ProgramListInteractors
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.GsonSharedPreferenceService
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.SharedPreferencesService
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.WorkoutDaoService
import com.devhanna91.runnincle.framework.datasource.cache.database.ProgramDao
import com.devhanna91.runnincle.framework.datasource.cache.database.ProgramDatabase
import com.devhanna91.runnincle.framework.datasource.cache.database.WorkoutDao
import com.devhanna91.runnincle.framework.datasource.cache.database.WorkoutDatabase
import com.devhanna91.runnincle.framework.datasource.cache.implementation.GsonSharedPreferencesServiceImpl
import com.devhanna91.runnincle.framework.datasource.cache.implementation.ProgramDaoServiceImpl
import com.devhanna91.runnincle.framework.datasource.cache.implementation.SharedPreferencesServiceImpl
import com.devhanna91.runnincle.framework.datasource.cache.implementation.WorkoutDaoServiceImpl
import com.devhanna91.runnincle.framework.datasource.cache.mappers.ProgramCacheMapper
import com.devhanna91.runnincle.framework.datasource.cache.mappers.WorkoutCacheMapper
import com.devhanna91.runnincle.framework.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) // Application ???????????? ??????????????? ?????? ?????? ???
@Module // ???????????????, ?????? ???????????????, ??????????????? ??? constructor-injection ??? ??? ?????? ????????? ??????
object AppModule {

    @Singleton
    @Provides // ????????? object ??? ??????????????? ??????. n??? ???????????? ????????????. function body ??????.
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
        sharedPreferencesService: SharedPreferencesService
    ): ProgramListInteractors {
        return ProgramListInteractors (programCacheDataSource, workoutCacheDataSource, sharedPreferencesService)
    }

    @Singleton
    @Provides
    fun provideMainActivityInteractors(
        sharedPreferencesService: SharedPreferencesService
    ): MainActivityInteractors {
        return MainActivityInteractors (sharedPreferencesService)
    }

    @Singleton
    @Provides
    fun provideProgramCacheDataSource(
        programDaoService: ProgramDaoService,
        dateUtil: DateUtil
    ): ProgramCacheDataSource {
        return ProgramCacheDataSourceImpl(programDaoService,dateUtil)
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

    @Singleton
    @Provides
    fun provideGsonSharedPreferenceService(@ApplicationContext app: Context)
    : GsonSharedPreferenceService {
        return GsonSharedPreferencesServiceImpl(app)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesService(gsonSharedPreferenceService: GsonSharedPreferenceService)
    : SharedPreferencesService {
        return SharedPreferencesServiceImpl(gsonSharedPreferenceService)
    }

}