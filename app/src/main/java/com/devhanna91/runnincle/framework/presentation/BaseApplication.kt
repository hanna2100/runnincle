package com.devhanna91.runnincle.framework.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 컴파일 시 컴포넌트 빌딩에 필요한 클래스들을 초기화 해줌
class BaseApplication: Application() {

}