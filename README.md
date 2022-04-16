# 러닝클: 운동용 플로팅 인터벌 타이머

## Introduction
다른 앱 위에 띄울 수 있는 플로팅 기능이 있는 타이머 앱 입니다. 인터벌 설정을 쉽게 할 수 있으며 크기, 색상 등 커스터마이징이 가능합니다.

### 검색 화면
![search](https://user-images.githubusercontent.com/53829792/163685994-bafff055-0f1d-4fde-bda8-fee3a8793999.gif)
1. 서치바에 검색어를 입력하면 검색어를 포함하는 모든 타이머(운동 프로그램)가 검색됩니다.
2. 검색어는 서치바 하단에 chip으로 저장됩니다.
3. 더보기를 누르면 상세 운동 목록이 보여지며, 수정/삭제 버튼이 활성화 됩니다.
4. 설정버튼을 누르면 쿨다운 타이머 색상, 전체 진행도 타이머 색상, 언어를 변경 할 수 있습니다.
5. 설정옵션에서 동영상 광고를 보면 3일동안 전면광고가 제거됩니다.

### 타이머 추가/수정 화면
![create](https://user-images.githubusercontent.com/53829792/163685996-6af79241-0c03-4e7e-9353-36e499dd3f33.gif)
1. 하단의 + 버튼을 눌러 운동을 추가할 수 있습니다.
2. 운동 시간, 쿨다운 시간, 세트 횟수 설정은 숫자를 누르고 상/하로 드래그해서 조절 할 수 있습니다.
3. `마지막 쿨다운 생략`을 체크하면 세트 마지막 쿨다운은 skip 됩니다.
4. 운동별로 타이머 색상을 설정 할 수 있습니다.
5. 운동을 추가하면 상단 서클바에 해당 운동의 비중이 보여집니다.
6. 리스트에 추가된 운동을 클릭하면 수정할 수 있습니다.
7. 오른쪽 상단 `v`를 클릭하면 타이머가 저장됩니다.

### 타이머 화면
![run](https://user-images.githubusercontent.com/53829792/163685998-1d43de20-3fe6-499c-a92a-4619948180ee.gif)
1. 검색화면에서 카드를 누르면 타이머가 실행됩니다.
2. `다른 앱 위에 그리기` 권한이 없으면 권한설정으로 넘어갑니다.
3. 전면광고 후, 타이머가 실행됩니다.
4. 바깥 원은 현재 운동의 남은 시간, 안쪽 원은 전체 운동의 진행시간을 뜻 합니다.
5. 남은 세트 수, 남은 시간, 운동 이름이 중앙에 표시됩니다.
6. 가장자리를 드래그하면 타이머를 이동시킬 수 있습니다.
7. 중앙을 클릭하면 정지/재생 됩니다.


## Architecture
```bash
runnincle
├── business
│   ├── data
│   ├── domain
│   ├── interactors
│   └── util
├── framework
│   ├── datasource
│   └── presentation
├── common
└── di
``` 
- business: 비지니스 로직의 집합
    - data: 비지니스 로직에 필요한 데이터를 관리.
    - domain: 도메인 모델이 정의됨
    - interactors: framework계층과 business계층을 연결하는 중간역할. usecase가 정의됨.
    - util: 비지니스 계층에서 사용되는 유틸모음.

- framework: 뷰, 프레임워크의 집합
    - datasource: 데이터베이스(Room)
    - presentation: UI 계층을 다루는 패키지. activity, fragment, theme 등이 정의됨.

- common: extension, util, constant 등 유틸성 패키지.
- di: Hilt를 이용한 Dependency Injection.


## Development Environment
- Android Studio Bumblebee | 2021.1.1 Beta 5
- JAVA 8
- Kotlin 1.5.31


## Application Version
- minSdkVersion: 23
- targetSdkVersion: 32


## APIs
- Jetpack Compose 1.1.0-beta03
- Jetpack Navigation 2.3.5
- Hilt 2.38.1
- Accompanist 0.24.1-alpha
- Material Color 0.0.7
- Palette 1.0.0
- Room 2.4.0
- Gson 2.8.6
- Localization 1.2.11
