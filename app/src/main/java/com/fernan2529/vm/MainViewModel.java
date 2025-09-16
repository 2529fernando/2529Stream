package com.fernan2529.vm;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fernan2529.data.CategoriesRepository;

public class MainViewModel extends ViewModel {
    private static final String PREFS = "prefs";
    private static final String KEY_SELECTION = "category_index";

    private final MutableLiveData<Integer> selectedIndex = new MutableLiveData<>(0);
    private final CategoriesRepository repo = new CategoriesRepository();

    public LiveData<Integer> getSelectedIndex() { return selectedIndex; }
    public String[] getCategories() { return repo.getCategories(); }

    public void loadSavedSelection(Context ctx) {
        int idx = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_SELECTION, 0);
        selectedIndex.setValue(idx);
    }

    public void saveSelection(Context ctx, int idx) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putInt(KEY_SELECTION, idx).apply();
        selectedIndex.setValue(idx);
    }
}
