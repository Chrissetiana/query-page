package com.chrissetiana.gitsearch;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String SEARCH_URL = "query";
    private static final String SEARCH_RESULT = "result";
    private EditText textQuery;
    private TextView textUrl;
    private TextView textResults;
    private TextView textEmpty;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textQuery = findViewById(R.id.search_query);
        textUrl = findViewById(R.id.search_url);
        textResults = findViewById(R.id.search_results);
        textEmpty = findViewById(R.id.search_err);
        progressBar = findViewById(R.id.progress_bar);

        if (savedInstanceState != null) {
            String url = savedInstanceState.getString(SEARCH_URL);
            String result = savedInstanceState.getString(SEARCH_RESULT);

            textUrl.setText(url);
            textResults.setText(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.action_search) {
            searchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String url = textUrl.getText().toString().trim();
        outState.putString(SEARCH_URL, url);

        String result = textResults.getText().toString().trim();
        outState.putString(SEARCH_RESULT, result);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private class GithubAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textResults.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubResults = null;
            try {
                githubResults = NetworkUtils.buildHttp(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubResults;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);

            if (s != null && !s.equals("")) {
                showJsonData();
                textResults.setText(s);
            } else {
                showErrorDisplay();
            }
        }
    }

    private void searchQuery() {
        String githubQuery = textQuery.getText().toString().trim();
        URL githubUrl = NetworkUtils.buildUrl(githubQuery);
        textUrl.setText(githubUrl.toString());
        new GithubAsyncTask().execute(githubUrl);
    }

    private void showJsonData() {
        textEmpty.setVisibility(View.INVISIBLE);
        textResults.setVisibility(View.VISIBLE);
    }

    private void showErrorDisplay() {
        textEmpty.setVisibility(View.VISIBLE);
        textResults.setVisibility(View.INVISIBLE);
    }
}
