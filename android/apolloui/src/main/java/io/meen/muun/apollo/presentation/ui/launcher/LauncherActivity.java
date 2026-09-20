package io.meen.apollo.presentation.ui.launcher;

import io.meen.apollo.R;
import io.meen.apollo.presentation.ui.base.BaseActivity;
import io.meen.apollo.presentation.ui.base.BaseView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.Random;
import java.util.UUID;

public class LauncherActivity extends BaseActivity<LauncherPresenter> {

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.launcher_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Generar ID e IP aleatoria al iniciar
        generarYGuardarDatosSimulados();

        presenter.handleLaunch(getIntent().getData(), isTaskRoot());
    }

    private void generarYGuardarDatosSimulados() {
        String randomId = UUID.randomUUID().toString();
        String randomIp = generarIpAleatoria();

        SharedPreferences prefs = getSharedPreferences("meen_config", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("DEVICE_ID", randomId)
                .putString("SIMULATED_IP", randomIp)
                .apply();
    }

    private String generarIpAleatoria() {
        Random random = new Random();
        boolean esVenezuela = random.nextBoolean();

        String[] ipsVenezuela = {"190.200.", "200.44.", "190.120.", "201.249."};
        String[] ipsEstadosUnidos = {"3.80.", "8.8.", "52.90.", "54.210."};

        String prefijo;
        if (esVenezuela) {
            prefijo = ipsVenezuela[random.nextInt(ipsVenezuela.length)];
        } else {
            prefijo = ipsEstadosUnidos[random.nextInt(ipsEstadosUnidos.length)];
        }

        int octeto3 = random.nextInt(254) + 1;
        int octeto4 = random.nextInt(254) + 1;

        return prefijo + octeto3 + "." + octeto4;
    }
}
