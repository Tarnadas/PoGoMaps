package com.pokemongomap.pokemongomap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pokemongomap.helpers.PokemonHelper;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.TypeModifier;


public class PokemonFragment extends Fragment {

    private static int HEIGHT = 250;
    private static int IMAGE_SIZE = 220;
    private static int ROW_HEIGHT = 40;

    private static int SCROLL_DIFF = 200;

    private int mPosition = 1;

    public PokemonFragment() {
    }

    public static PokemonFragment newInstance() {
        PokemonFragment fragment = new PokemonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ScrollView rootView = (ScrollView) inflater.inflate(R.layout.fragment_pokemon, container, false);
        final RelativeLayout layout = (RelativeLayout) rootView.getChildAt(0);
        rootView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = rootView.getChildAt(rootView.getChildCount() - 1);
                int diff = (view.getBottom() - (rootView.getHeight() + rootView.getScrollY()));

                if (diff < SCROLL_DIFF) {
                    for (int i = mPosition; mPosition < (i+5); mPosition++) {
                        loadPokemon(layout);
                    }
                }
            }
        });

        for (int i = mPosition; mPosition < (i+9); mPosition++) {
            loadPokemon(layout);
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadPokemon(RelativeLayout layout) {
        Pokemon pokemon;
        try {
            pokemon = PokemonHelper.getBasePokemon(mPosition);
        } catch (NullPointerException e) {
            return;
        }
        View pokeView = View.inflate(getContext(), R.layout.pokemon_view, null);

        // get screen size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // set image
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 0;
        params.topMargin = (mPosition-1) * HEIGHT;
        pokeView.setLayoutParams(params);
        ImageView image = (ImageView) pokeView.findViewById(R.id.pokemon_image);
        params = new RelativeLayout.LayoutParams(IMAGE_SIZE, IMAGE_SIZE);
        image.setLayoutParams(params);
        image.setImageResource(pokemon.getResource());
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // set type
        GridLayout gridLayout = (GridLayout) pokeView.findViewById(R.id.types);
        params = new RelativeLayout.LayoutParams(width - IMAGE_SIZE, ROW_HEIGHT);
        params.leftMargin = IMAGE_SIZE;
        params.topMargin = 0;
        gridLayout.setLayoutParams(params);

        TextView textView = ((TextView) pokeView.findViewById(R.id.type_primary));
        textView.setText(pokemon.getType().toString().toLowerCase());
        textView.setBackground(TypeModifier.getBackground(getContext(), pokemon.getType()));
        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 2;
        textView.setLayoutParams(layoutParams);

        if (pokemon.getTypeSecondary() != TypeModifier.Type.NONE) {
            textView = ((TextView) pokeView.findViewById(R.id.type_secondary));
            textView.setText(pokemon.getTypeSecondary().toString().toLowerCase());
            textView.setBackground(TypeModifier.getBackground(getContext(), pokemon.getTypeSecondary()));
            layoutParams = textView.getLayoutParams();
            layoutParams.width = (width - IMAGE_SIZE) / 2;
            textView.setLayoutParams(layoutParams);
        }

        // set gym meta values
        gridLayout = (GridLayout) pokeView.findViewById(R.id.gym);
        params = new RelativeLayout.LayoutParams(width - IMAGE_SIZE, ROW_HEIGHT);
        params.leftMargin = IMAGE_SIZE;
        params.topMargin = ROW_HEIGHT;
        gridLayout.setLayoutParams(params);

        textView = ((TextView) pokeView.findViewById(R.id.gym_offense));
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.gym_offense_val));
        textView.setText(pokemon.getGymOffense() + "");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.gym_defense));
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.gym_defense_val));
        textView.setText(pokemon.getGymDefense() + "");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        // set cp
        gridLayout = (GridLayout) pokeView.findViewById(R.id.cp);
        params = new RelativeLayout.LayoutParams(width - IMAGE_SIZE, ROW_HEIGHT);
        params.leftMargin = IMAGE_SIZE;
        params.topMargin = ROW_HEIGHT * 2;
        gridLayout.setLayoutParams(params);

        textView = ((TextView) pokeView.findViewById(R.id.min_cp));
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.min_cp_val));
        textView.setText(pokemon.getMinCp() + "");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.max_cp));
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.max_cp_val));
        textView.setText(pokemon.getMaxCp() + "");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 4;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        // set base stats
        gridLayout = (GridLayout) pokeView.findViewById(R.id.stats);
        params = new RelativeLayout.LayoutParams(width - IMAGE_SIZE, ROW_HEIGHT);
        params.leftMargin = IMAGE_SIZE;
        params.topMargin = ROW_HEIGHT * 3;
        gridLayout.setLayoutParams(params);

        textView = ((TextView) pokeView.findViewById(R.id.hitpoints));
        textView.setText(pokemon.getHpRatio() + " HP");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 3;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.attack));
        textView.setText(pokemon.getAttackRatio() + " ATK");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 3;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        textView = ((TextView) pokeView.findViewById(R.id.defense));
        textView.setText(pokemon.getDefenseRatio() + " DEF");
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setBackground(getContext().getDrawable(R.drawable.label_default));
        } else {
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.label_default));
        }
        layoutParams = textView.getLayoutParams();
        layoutParams.width = (width - IMAGE_SIZE) / 3;
        layoutParams.height = ROW_HEIGHT;
        textView.setLayoutParams(layoutParams);

        layout.addView(pokeView);
    }
}
