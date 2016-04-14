package com.example.biezhi.videonew;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.history.SearchHistoryTable;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;

import java.util.ArrayList;
import java.util.List;

public class searchActivity extends AppCompatActivity {

    private SearchView searchView;
    private SearchHistoryTable searchHistoryTable;
    private List<SearchItem> suggestionsList;
    Context context;
    Data appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = searchActivity.this;
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setVoice(false);
        searchHistoryTable = new SearchHistoryTable(this);
        suggestionsList = new ArrayList<>();
        initClass();
    }

    private void initClass() {
        appData = (Data) this.getApplicationContext();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHistoryTable.addItem(new SearchItem(query));
                appData.setSearchVideo(query);
                appData.setSourcePage("Search");
                appData.setFromSearch(true);
                startActivity(new Intent(searchActivity.this,videoList.class));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        searchView.setOnSearchMenuListener(new SearchView.SearchMenuListener() {
            @Override
            public void onMenuClick() {
                finish();
            }
        });
        List<SearchItem> resultsList = new ArrayList<>();
        SearchAdapter searchAdapter = new SearchAdapter(this, resultsList, suggestionsList, SearchCodes.THEME_LIGHT);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                CharSequence text = textView.getText();
                searchHistoryTable.addItem(new SearchItem(text));
                appData.setSearchVideo(text.toString());
                appData.setSourcePage("Search");
                appData.setFromSearch(true);
                startActivity(new Intent(searchActivity.this,videoList.class));
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            }
        });
        searchView.setAdapter(searchAdapter);
        showSearchView();
    }

    private void showSearchView() {
        suggestionsList.clear();
        suggestionsList.addAll(searchHistoryTable.getAllItems());
    }

}
