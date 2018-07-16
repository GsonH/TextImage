package com.hyn.textimage.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hyn.textimage.R;
import com.hyn.textimage.adapter.ColorPickerAdapter;

public class PropertiesBSFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private boolean justColor;

    private int currentColor;
    public PropertiesBSFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public PropertiesBSFragment(boolean justColor) {
        this.justColor = justColor;
    }

    public void setCurrentColor(int color) {
        currentColor = color;
    }

    private Properties mProperties;

    public interface Properties {
        void onColorChanged(int colorCode);

        void onOpacityChanged(int opacity);

        void onBrushSizeChanged(int brushSize);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvColor = view.findViewById(R.id.rvColors);

        SeekBar sbOpacity = view.findViewById(R.id.sbOpacity);
        SeekBar sbBrushSize = view.findViewById(R.id.sbSize);

        sbOpacity.setOnSeekBarChangeListener(this);
        sbBrushSize.setOnSeekBarChangeListener(this);
        TextView bruchText = view.findViewById(R.id.brushColor);
        final ImageView brushImage = view.findViewById(R.id.brushImage);
        if(justColor) {
            sbOpacity.setVisibility(View.GONE);
            sbBrushSize.setVisibility(View.GONE);
            view.findViewById(R.id.txtOpacity).setVisibility(View.GONE);
            view.findViewById(R.id.txtBrushSize).setVisibility(View.GONE);
            bruchText.setVisibility(View.GONE);
            brushImage.setVisibility(View.GONE);
        } else {
            bruchText.setVisibility(View.VISIBLE);
            brushImage.setVisibility(View.VISIBLE);
            brushImage.setBackgroundColor(currentColor);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(layoutManager);
        rvColor.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                if (mProperties != null) {
                    dismiss();
                    mProperties.onColorChanged(colorCode);
                    brushImage.setBackgroundColor(colorCode);
                }
            }
        });
        rvColor.setAdapter(colorPickerAdapter);
    }

    public void setPropertiesChangeListener(Properties properties) {
        mProperties = properties;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sbOpacity:
                if (mProperties != null) {
                    mProperties.onOpacityChanged(i);
                }
                break;
            case R.id.sbSize:
                if (mProperties != null) {
                    mProperties.onBrushSizeChanged(i);
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}