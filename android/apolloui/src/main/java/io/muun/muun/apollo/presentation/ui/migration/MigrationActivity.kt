package io.meen.apollo.presentation.ui.migration

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.ActivityMigrationBinding
import io.meen.apollo.presentation.ui.base.BaseActivity
import io.meen.apollo.presentation.ui.view.LoadingView

class MigrationActivity : BaseActivity<MigrationPresenter>(), MigrationView {

    companion object {
        fun getStartActivityIntent(context: Context) =
            Intent(context, MigrationActivity::class.java)
    }

    private val binding: ActivityMigrationBinding
        get() = getBinding() as ActivityMigrationBinding

    private val loadingView: LoadingView
        get() = binding.migrationLoading

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_migration
    }

    override fun bindingInflater(): (LayoutInflater) -> ViewBinding {
        return ActivityMigrationBinding::inflate
    }

    override fun initializeUi() {
        super.initializeUi()

        loadingView.setTitle(R.string.migration_title)
    }

}