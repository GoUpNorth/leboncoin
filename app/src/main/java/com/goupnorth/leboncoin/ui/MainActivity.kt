package com.goupnorth.leboncoin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.goupnorth.leboncoin.R
import com.goupnorth.leboncoin.ui.album.AlbumsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            showAlbums()
        }
    }

    private fun showAlbums() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AlbumsFragment())
            .commitNow()
    }
}