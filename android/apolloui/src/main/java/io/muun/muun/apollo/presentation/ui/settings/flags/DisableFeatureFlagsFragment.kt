package io.meen.apollo.presentation.ui.settings.flags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.databinding.FragmentDisableFeatureFlagsBinding
import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.presentation.ui.adapter.ItemAdapter
import io.meen.apollo.presentation.ui.adapter.holder.ViewHolderFactory
import io.meen.apollo.presentation.ui.adapter.viewmodel.FeatureFlagViewModel
import io.meen.apollo.presentation.ui.adapter.viewmodel.ItemViewModel
import io.meen.apollo.presentation.ui.base.SingleFragment
import io.meen.apollo.presentation.ui.view.MeenHeader

class DisableFeatureFlagsFragment : SingleFragment<DisableFeatureFlagsPresenter>(),
    DisableFeatureFlagsView {

    private val binding: FragmentDisableFeatureFlagsBinding
        get() = getBinding() as FragmentDisableFeatureFlagsBinding

    private lateinit var adapter: ItemAdapter

    override fun inject() {
        component.inject(this)
    }

    override fun getLayoutResource(): Int =
        R.layout.fragment_disable_feature_flags

    override fun bindingInflater(): (LayoutInflater, ViewGroup, Boolean) -> ViewBinding {
        return FragmentDisableFeatureFlagsBinding::inflate
    }

    override fun setUpHeader() {
        parentActivity.header.apply {
            showTitle(R.string.settings_disable_feature_flags)
            setNavigation(MeenHeader.Navigation.BACK)
        }
    }

    override fun initializeUi(view: View?) {
        super.initializeUi(view)

        adapter = ItemAdapter(ViewHolderFactory())
        adapter.setOnItemClickListener { viewModel: ItemViewModel? ->
            this.onItemClick(viewModel)
        }

        binding.featureFlagsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DisableFeatureFlagsFragment.adapter
        }
    }

    override fun setState(
        features: List<MeenFeature.OverridableFeature.Overridable>,
        featureOverrides: List<MeenFeature.OverridableFeature.Overridable>
    ) {

        val featureFlagViewModels = features
            .map { feature ->
                val state = if (featureOverrides.contains(feature)) {
                    FeatureFlagViewModel.State.DISABLED
                } else {
                    FeatureFlagViewModel.State.ENABLED
                }
                FeatureFlagViewModel(feature, state)
            }

        adapter.setItems(featureFlagViewModels)
    }

    private fun onItemClick(viewModel: ItemViewModel?) {
        if (viewModel is FeatureFlagViewModel) {
            presenter.toggleFeatureFlag(viewModel.overridableFeature, viewModel.state)
        }
    }
}