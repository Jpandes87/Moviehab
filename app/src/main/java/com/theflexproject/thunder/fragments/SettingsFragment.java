package com.theflexproject.thunder.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.theflexproject.thunder.R;
import com.theflexproject.thunder.model.GitHubResponse;
import com.theflexproject.thunder.utils.CheckForUpdates;
import com.theflexproject.thunder.utils.SettingsManager;

import java.io.IOException;
import java.util.concurrent.Executors;

public class SettingsFragment extends BaseFragment {

    Button addIndex;
    Button viewIndexes;
    Button importIndex;
    Button export;
    Button checkForUpdate;

    ImageButton discord;
    ImageButton github;
    ImageButton telegram;

   SwitchMaterial externalPlayerToggle;
   SwitchMaterial castButtonToggle;
   SwitchMaterial refreshPeriodicallyToggle;

   SettingsManager settingsManager;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings , container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view , @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view , savedInstanceState);
        initWidgets();
        setStatesOfToggleSwitches();
        setMyOnClickListeners();
    }

    private void initWidgets() {
        addIndex = mActivity.findViewById(R.id.addIndexButton);
        viewIndexes = mActivity.findViewById(R.id.viewIndexes);
//        importIndex = mActivity.findViewById(R.id.importIndex);
//        export = mActivity.findViewById(R.id.export);
        checkForUpdate = mActivity.findViewById(R.id.checkforUpdates);

        discord = mActivity.findViewById(R.id.discordImageButton);
        github = mActivity.findViewById(R.id.githubImageButton);
        telegram = mActivity.findViewById(R.id.telegramImageButton);


        externalPlayerToggle = mActivity.findViewById(R.id.externalPlayerToggle);
//        castButtonToggle = mActivity.findViewById(R.id.castButtonVisibility);
//        refreshPeriodicallyToggle =mActivity.findViewById(R.id.refreshPeriodicallyToggle);

        settingsManager = new SettingsManager(mActivity);
    }

    private void setMyOnClickListeners() {

        addIndex.setOnClickListener(v -> {
            AddNewIndexFragment nextFrag= new AddNewIndexFragment();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                    .replace(R.id.newsettingscontainer, nextFrag).addToBackStack(null).commit();
        });

        viewIndexes.setOnClickListener(v -> {
            ManageIndexesFragment manageIndexesFragment = new ManageIndexesFragment();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.from_right,R.anim.to_left,R.anim.from_left,R.anim.to_right)
                    .replace(R.id.newsettingscontainer, manageIndexesFragment).addToBackStack(null).commit();
        });

        externalPlayerToggle.setOnCheckedChangeListener(
                (buttonView , isChecked) -> {
                    if(isChecked){settingsManager.saveExternal(true);}else{settingsManager.saveExternal(false);}
                });

//        castButtonToggle.setOnCheckedChangeListener(
//                (buttonView , isChecked) -> {
//                    if(isChecked){settingsManager.saveCast(true);}else{settingsManager.saveCast(false);}
//                });
//
//        refreshPeriodicallyToggle.setOnCheckedChangeListener(
//                (buttonView , isChecked) -> {
//                    if(isChecked){settingsManager.saveRefresh(true);}else{settingsManager.saveRefresh(false);}
//                });

        checkForUpdate.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        GitHubResponse[] gitHubResponses = new CheckForUpdates().checkForUpdates();
                        if(gitHubResponses!=null){
                            UpdateAppFragment updateAppFragment = new UpdateAppFragment(gitHubResponses);
                            mActivity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.from_right,R.anim.to_left,R.anim.from_left,R.anim.to_right)
                                    .add(R.id.container,updateAppFragment).addToBackStack(null).commit();
                        }else {
                            mActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(mActivity,"Latest Version",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        });

        discord.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/NWrz5euMJs"))));
        github.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/anujd64/Thunder"))));
        telegram.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+qbLDmvEgC65lMWI1"))));
        //        export.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//             new ExportCSV().exportJSON(mActivity.getApplicationContext());
//            }
//        });

    }

    private void setStatesOfToggleSwitches() {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean savedREF = sharedPreferences.getBoolean("REFRESH_SETTING", false);
        boolean savedCAST = sharedPreferences.getBoolean("CAST_SETTING", false);
        boolean savedEXT = sharedPreferences.getBoolean("EXTERNAL_SETTING", false);
        externalPlayerToggle.setChecked(savedEXT);
//        castButtonToggle.setChecked(savedCAST);
//        refreshPeriodicallyToggle.setChecked(savedREF);
    }

}