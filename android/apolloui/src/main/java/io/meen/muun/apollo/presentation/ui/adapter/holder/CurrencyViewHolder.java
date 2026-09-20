package io.meen.apollo.presentation.ui.adapter.holder;

import io.meen.apollo.R;
import io.meen.apollo.databinding.VItemCurrencyBinding;
import io.meen.apollo.presentation.ui.adapter.viewmodel.CurrencyViewModel;
import io.meen.apollo.presentation.ui.helper.MoneyHelper;
import io.meen.apollo.presentation.ui.utils.UiUtils;
import io.meen.common.model.Currency;

import android.view.View;

public class CurrencyViewHolder extends BaseViewHolder<CurrencyViewModel> {

    private final VItemCurrencyBinding binding;

    public CurrencyViewHolder(View view) {
        super(view);
        binding = VItemCurrencyBinding.bind(view);
    }

    @Override
    public void bind(CurrencyViewModel viewModel) {
        final Currency currency = viewModel.model.currencyInfo;

        final String code = MoneyHelper.formatCurrency(currency.getCode(), viewModel.bitcoinUnit);
        final String name = currency.getName();

        if (currency.getFlag() != null) {
            binding.label.setText(String.format("%s %s (%s)", currency.getFlag(), name, code));
            binding.logo.setVisibility(View.GONE);

        } else {

            if (currency.getCode().equals(Currency.BTC.getCode())
                    || currency.getCode().equals("SAT")) {
                binding.logo.setImageResource(R.drawable.btc_logo);

            } else {
                binding.logo.setImageResource(R.drawable.default_flag);
            }

            binding.label.setText(String.format(" %s (%s)", name, code));
            binding.logo.setVisibility(View.VISIBLE);
        }

        binding.selectedIcon.setVisibility(viewModel.isSelected ? View.VISIBLE : View.GONE);
        UiUtils.setTint(binding.selectedIcon, R.color.blue);
    }
}
