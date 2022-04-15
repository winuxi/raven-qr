package com.ravenioet.ravenqr;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ravenioet.animator.Animate;
import com.ravenioet.ravenqr.databinding.ActivityMainBinding;

public class Main extends AppCompatActivity {
    ActivityMainBinding binding;
    public static NavController navController;
    public static NavOptions navOption(){
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setLaunchSingleTop(true)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.qr_files)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        binding.fabMenu.setOnClickListener(view -> {
            if(!isActive){
                showFavMenu();
            }else {
                hideFavMenu();
            }
        });
    }
    boolean isActive = false;
    public void showFavMenu(){
        isActive = true;
        binding.fabMenu.setAnimation(Animate.fadeOut(1000));
        binding.fabMenu.setImageResource(R.drawable.ic_baseline_close_24);
        binding.fabMenu.setAnimation(Animate.fadeIn(1000));
        binding.favItems.setAnimation(Animate.inFromBottomAnimation(500));
        binding.favItems.setVisibility(View.VISIBLE);
    }
    public void hideFavMenu(){
        isActive = false;
        binding.fabMenu.setAnimation(Animate.fadeOut(1000));
        binding.fabMenu.setImageResource(R.drawable.ic_baseline_menu_24);
        binding.fabMenu.setAnimation(Animate.fadeIn(1000));
        binding.favItems.setAnimation(Animate.outToBottomAnimation(500));
        binding.favItems.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if(navController.getCurrentDestination().getId() != R.id.settings)
                navController.navigate( R.id.settings,null,navOption());
        }
        return false;
    }
}