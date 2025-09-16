package com.fernan2529.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.lifecycle.ViewModelStoreOwner;

import com.fernan2529.R;
import com.fernan2529.vm.MainViewModel;

public class CategorySpinnerView extends FrameLayout {

    private Spinner spinner;
    private MainViewModel vm;
    private boolean firstSelectionPass = true;

    public CategorySpinnerView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CategorySpinnerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CategorySpinnerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context ctx, @Nullable AttributeSet attrs) {
        LayoutInflater.from(ctx).inflate(R.layout.view_category_spinner, this, true);
        spinner = findViewById(R.id.spinner_inner);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Obtener owners del árbol de vistas de manera segura
        LifecycleOwner lifecycleOwner = ViewTreeLifecycleOwner.get(this);
        ViewModelStoreOwner storeOwner = ViewTreeViewModelStoreOwner.get(this);

        if (lifecycleOwner == null || storeOwner == null) {
            // Fallback: intentar con el contexto si es Activity
            if (getContext() instanceof LifecycleOwner && getContext() instanceof ViewModelStoreOwner) {
                lifecycleOwner = (LifecycleOwner) getContext();
                storeOwner = (ViewModelStoreOwner) getContext();
            } else {
                throw new IllegalStateException("CategorySpinnerView requiere un LifecycleOwner y ViewModelStoreOwner en el árbol de vistas.");
            }
        }

        vm = new ViewModelProvider(storeOwner).get(MainViewModel.class);

        // Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                vm.getCategories()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Observar índice guardado y reflejarlo visualmente
        vm.getSelectedIndex().observe(lifecycleOwner, idx -> {
            if (idx == null) return;
            if (spinner.getSelectedItemPosition() != idx) {
                spinner.setSelection(idx, false);
            }
        });

        // Cargar la selección persistida
        vm.loadSavedSelection(getContext());

        // Guardar cambios cuando el usuario seleccione (evitar el disparo inicial del setSelection)
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSelectionPass) {
                    firstSelectionPass = false;
                    return; // ignorar el callback inicial
                }
                vm.saveSelection(getContext(), position);
                // (Opcional) Aquí podrías disparar un callback a la Activity con una interfaz si quieres navegar
                // onCategoryChosenListener?.onChosen(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
