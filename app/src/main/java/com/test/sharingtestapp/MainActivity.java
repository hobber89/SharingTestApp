package com.test.sharingtestapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.StrictMode;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.test.sharingtestapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final int PermissionsRequestId = 0;

    private static final int SelectFileId = 1;

    private String testFileContent = "{\"testContent\": 12345}";
    private File testFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        requestFilePermissions();

        createTestFile();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void createTestFile() {
        try {
            File storageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/SharingTestApp");
            testFile = new File(storageDirectory, "testFile.json");
            if (testFile.exists()) {

                if(readFile(testFile))
                    return;

                testFile.delete();
            }

            storageDirectory.mkdirs();
            testFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
            writer.write(testFileContent);
            writer.close();
            if(!testFile.exists())
                Toast.makeText(this, "Failed to write Testfile", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create Testfile", Toast.LENGTH_LONG).show();
        }
    }

    private boolean readFile(File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            if(text.toString().startsWith(testFileContent)) {
                Toast.makeText(this, "Testfile already exists", Toast.LENGTH_LONG).show();
                return true;
            }
            else {
                Toast.makeText(this, "Read: " + text.toString(), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch (IOException e) {
            Toast.makeText(this, "Unable to read the file " + file.getName(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.shareFileButton:
                shareFile(testFile);
                break;
            case R.id.shareUrlButton:
                selectFile();
                break;
        }
    }

    private void selectFile() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), SelectFileId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SelectFileId && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            shareUri(selectedFileUri);
        }
    }
    private void shareFile(File selectedFile) {
        try {
            if(!selectedFile.exists())
            {
                Toast.makeText(this, "File does not exist!", Toast.LENGTH_LONG).show();
                return;
            }

            if(!selectedFile.canRead())
            {
                Toast.makeText(this, "File can not be read!", Toast.LENGTH_LONG).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this, "com.test.sharingTestApp", selectedFile);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent .putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent , "Share..."));
        } catch(Exception error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareUri(Uri selectedFileUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, selectedFileUri);
            startActivity(Intent.createChooser(intent, "Share..."));
        } catch(Exception error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestFilePermissions() {
        ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PermissionsRequestId);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
