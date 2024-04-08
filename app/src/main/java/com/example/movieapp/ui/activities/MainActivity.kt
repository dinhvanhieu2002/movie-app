package com.example.movieapp.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.movieapp.R
import com.example.movieapp.api.RetrofitInstance
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.utils.AuthTokenHelper
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var authTokenHelper: AuthTokenHelper
    lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    val FRAGMENT_TO_LOAD = "FRAGMENT_TO_LOAD"
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authTokenHelper = AuthTokenHelper(this)

        subscribeMedias()

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvMain) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.mediaListFragment, R.id.searchFragment, R.id.handleRoomFragment, R.id.favoriteFragment, R.id.reviewFragment),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.linearProgress.isIndeterminate = true

        printKeyHash()

        intent.extras?.let {
            Log.e("Main Activity", "Data from notify")
            Log.e("Main Activity", it.getString("mediaId").toString())
            Log.e("Main Activity", it.getString("mediaType").toString())

            val bundle = Bundle().apply {
                putString("mediaId", it.getString("mediaId"))
                putString("mediaType", it.getString("mediaType"))
                putBoolean("isScroll", it.getBoolean("isScroll"))
            }
            if(!it.getString("mediaId").isNullOrBlank()) {
                navController.navigate(R.id.mediaDetailFragment, bundle)
            }
            
        }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> navController.navigate(R.id.homeFragment)
                R.id.nav_movie -> {
                    val bundle = Bundle().apply {
                        putString("mediaType", "movie")
                    }
                    navController.navigate(R.id.mediaListFragment, bundle)
                }
                R.id.nav_tv -> {
                    val bundle = Bundle().apply {
                        putString("mediaType", "tv")
                    }
                    navController.navigate(R.id.mediaListFragment, bundle)
                }
                R.id.nav_search -> navController.navigate(R.id.searchFragment)
                R.id.nav_join_room -> navController.navigate(R.id.handleRoomFragment)
                R.id.nav_favorite -> navController.navigate(R.id.favoriteFragment)
                R.id.nav_review -> navController.navigate(R.id.reviewFragment)
                R.id.logout -> {
                    authTokenHelper.clearToken()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo("com.example.movieapp", PackageManager.GET_SIGNING_CERTIFICATES)

            val md = MessageDigest.getInstance("SHA")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                md.update(info.signingInfo.toString().toByteArray())
                Log.e("Main Activity", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }

        } catch (e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()

        if (authTokenHelper.getToken().isNullOrBlank()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun showGlobalLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_bar_black))
    }

    fun hideGlobalLoading() {
        binding.loadingLayout.visibility = View.GONE
        binding.toolbar.setBackgroundColor(Color.TRANSPARENT)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    val TAG = "MainActivity"
    fun subscribeMedias() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val authorization = "Bearer ${authTokenHelper.getToken()}"
            val response = RetrofitInstance.api.getListReview(authorization)
            if (response.isSuccessful) {
                response.body()?.let {reviews ->
                    reviews.groupBy { it.mediaId }.keys.forEach { mediaId ->
                        FirebaseMessaging.getInstance().subscribeToTopic(mediaId).addOnCompleteListener {
                            var message = "DONE"
                            if(!it.isSuccessful) {
                                message = "FAILED"
                            }
                        }
                        Log.e(TAG, "Subscribe $mediaId")

                    }
                }
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {

            Log.e(TAG, e.toString())
        }
    }

}