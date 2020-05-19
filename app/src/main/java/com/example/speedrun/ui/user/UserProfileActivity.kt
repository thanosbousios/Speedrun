package com.example.speedrun.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network.model.dto.UserDto
import com.example.network.utils.NetworkConstants
import com.example.speedrun.R
import com.example.speedrun.model.UserGameModel
import com.example.speedrun.ui.base.BaseActivity
import com.example.speedrun.ui.game.GameDetailsActivity
import com.example.speedrun.ui.run.RunDetailsActivity
import com.example.speedrun.utils.Constants
import com.example.speedrun.utils.ItemDivideDecorator
import com.example.speedrun.utils.UserColorUtils
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivity() {

    var viewModel: UserProfileViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        activityComponent?.inject(this)

        initUI()

        viewModel?.getUserDetails(intent.getStringExtra(Constants.EXTRA_USER_ID))
    }

    override fun initViewModel() {
        viewModel = viewModelFactory.create(UserProfileViewModel::class.java)
    }

    override fun observeViewModel() {
        viewModel?.isLoadingLiveData?.observe(this, Observer {
            if (it == null)
                return@Observer

            if (it) {
                user_profile_loader.visibility = View.VISIBLE
                user_profile_layout.visibility = View.GONE
            } else {
                user_profile_layout.visibility = View.VISIBLE
                user_profile_loader.visibility = View.GONE
            }
        })

        viewModel?.userRunsLiveData?.observe(this, Observer {
            if (it == null)
                return@Observer

            updateUserRuns(it)
        })

        viewModel?.userDetailsLiveData?.observe(this, Observer {
            if (it == null) {
                return@Observer
            }

            updateUserInfo(it)
        })

        viewModel?.gameClickedLiveData?.observe(this, Observer {
            if (it.isNullOrEmpty())
                return@Observer

            val intent = Intent(this@UserProfileActivity, GameDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_GAME_ID, it)
            startActivity(intent)
        })

        viewModel?.runClickedLiveData?.observe(this, Observer {
            if (it.isNullOrEmpty())
                return@Observer

            val intent = Intent(this@UserProfileActivity, RunDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_RUN_ID, it)
            startActivity(intent)
        })
    }
    private fun initUI() {
        rv_user_runs.apply {
            layoutManager = LinearLayoutManager(this@UserProfileActivity)
            val itemDecoration =
                ItemDivideDecorator(80)
            addItemDecoration(itemDecoration)
        }
    }

    private fun updateUserInfo(user: UserDto) {
        user_name.text = user.names?.international

        val style = user.nameStyle?.style
        if (style == NetworkConstants.STYLE_SOLID) {
            user_name.setTextColor(UserColorUtils.setSolidColor(user.nameStyle))
        } else if (style == NetworkConstants.STYLE_GRADIENT) {
            user_name.paint.shader = UserColorUtils.setGradientColor(user.nameStyle)
        }

    }

    private fun updateUserRuns(gameList: List<UserGameModel>) {
        rv_user_runs.adapter = UserGamesAdapter(viewModel, gameList)
    }
}