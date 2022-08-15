package com.goupnorth.leboncoin

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.InputStream

@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
class ApplicationGlideModule : AppGlideModule() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ApplicationGlideModuleEntryPoint {
        fun okhttpClient(): OkHttpClient
    }


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        
        // Use an entrypoint to inject access the OkHttpClient instance instantiated by hilt
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                ApplicationGlideModuleEntryPoint::class.java
            )

        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(hiltEntryPoint.okhttpClient())
        )
    }

    override fun isManifestParsingEnabled() = false
}