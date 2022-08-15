# Architecture description

The application is made of 3 modules following the clean architecture principles.

- domain: the domain module, the lowest layer in the clean archicture
  diagram (https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)
  , contains the definition of the models and the repositories, as well as the interactors. The
  domain layer has no dependency on any component defined in the data or ui modules.
- data: the data module contains the implementation of the repository defined in the domain and both
  local and remote data sources with their daos and dtos. The mapping between the data model and the
  domain model is done in the implementation of the repository.
- app: the app module contains all the ui of the application, the Hilt modules necessary for the
  injection of dependencies, as well as utility classes such as the Application class necessary for
  the dependency injection, or the ApplicationGlideModule necessary to inject an okhttp client in
  the Glide instance. The UI is a single activity with a single fragment, and the presentation layer
  follows the MVVM design pattern.

# Main technical choices and libraries

## Album caching for offline usage:

- For a performance and memory usage issue, the album list is paginated to limit the number of
  albums kept in memory. The pagination is made with the Jetpack paging
  library (https://developer.android.com/topic/libraries/architecture/paging/v3-overview). The
  paging library also supports the pagination of both remote and local data sources.
- The album caching is made with Room, partly because it supports pagination with the Jetpack paging
  library, but also because it verifies at build-time the validity of the sqlite queries written in
  the DAOs (https://developer.android.com/training/data-storage/room).
- I configured the Room database schema export as json to be able to use the automatic migration
  feature of
  room (https://developer.android.com/training/data-storage/room/migrating-db-versions#automigrationspec)
  . The schemas are available [here](data/schemas/com.goupnorth.data.db.AppDatabase/1.json)
- Both Paging and Room libraries are clean architecture compatible as they are composed of two
  modules, *-runtime and *-common, the latters being 100% kotlin modules with no dependency on any
  other Android library or apis.

## Image loading

The image loading and caching is done with Glide for its good performances and its compatibility
with okhttp.

## Handling configuration changes and data restoration

The album list restoration after a configuration change or a process death is handled by letting the
RecyclerView saving and restoring its own state in `onSaveInstanceState`, and simply reloading the
albums from the database. The sort restoration is handled with a ViewModel
SavedStateHandle (https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate#savedstatehandle)
. There is no need to save and restore the albums in an `onSaveInstanceState` bundle as they are
already written in DB.

## Presentation layer

The presentation layer follows the MVVM design pattern, as it makes it easy for the View to consume
flows of data. It also reduces the possibilities for memory leaks compared to MVP.

## Repository pattern

The album data can be accessed through the AlbumRepository defined in the domain module, and its
implementation takes care of handling both local and remote sources.

## Async with coroutines and flows

The asynchronous operations are performed with kotlin coroutines, which are light-weight thread that
suspend instead of blocking a thread. Similarly to Rx Observables or Flowables, Kotlin Flows are
used to handle streams of data, and rely on coroutines to operate.

## Network response deserialization with Moshi

Moshi is more lightweight than Gson, and also more performant because it is able to generate
adapters at build-time and doesn't need to rely on reflection.

## Dependency injection

Hilt is an android dependency injection library based on Dagger2 that simplifies the Dagger setup in
an android project and provides out-of-the-box android scoped Dagger Components such as
ActivityComponent, FragmentComponent, SingletonComponent (formerly ApplicationComponent),
ViewModelComponent etc. Hilt can also be used to inject dependencies in non-android objects by
declaring EntryPoints.

## Memory leak detection

I configured LeakCanary for debug builds to detect memory leaks during the development (more useful
in a real world project).

# Testing

I wrote tests in the data and app modules but none in the domain one, as the two interactors are
simple pass-throughs.

Here are the libraries that I used for unit testing:

- Robolectric: Robolectric allows to execute unit tests on Android dependent classes or components
  without the need of an Android emulator. Robolectric can also be used to execute ui tests written
  with espresso.
- turbine: small utility library to unit-test kotlin Flows
- coroutines-test: necessary to unit-test functions that launch coroutines
- mockk: can be used to generate mocks and spies for writing unit-tests
- junit: for the test assertions