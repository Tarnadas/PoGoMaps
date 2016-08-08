package com.pokemongomap.pokemon;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.pokemongomap.pokemongomap.R;

public abstract class TypeModifier {

    public enum Type {
        BUG, DARK, DRAGON, ELECTRIC, FAIRY, FIGHTING, FIRE, FLYING, GHOST, GRASS, GROUND, ICE, NONE, NORMAL, POISON, PSYCHIC, ROCK, STEEL, WATER
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getBackground(Context context, Type type) {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (type) {
                case BUG:
                    return context.getDrawable(R.drawable.label_bug);
                case DARK:
                    return context.getDrawable(R.drawable.label_dark);
                case DRAGON:
                    return context.getDrawable(R.drawable.label_dragon);
                case ELECTRIC:
                    return context.getDrawable(R.drawable.label_electric);
                case FAIRY:
                    return context.getDrawable(R.drawable.label_fairy);
                case FIGHTING:
                    return context.getDrawable(R.drawable.label_fighting);
                case FIRE:
                    return context.getDrawable(R.drawable.label_fire);
                case FLYING:
                    return context.getDrawable(R.drawable.label_flying);
                case GHOST:
                    return context.getDrawable(R.drawable.label_ghost);
                case GRASS:
                    return context.getDrawable(R.drawable.label_grass);
                case GROUND:
                    return context.getDrawable(R.drawable.label_ground);
                case ICE:
                    return context.getDrawable(R.drawable.label_ice);
                case NORMAL:
                    return context.getDrawable(R.drawable.label_normal);
                case POISON:
                    return context.getDrawable(R.drawable.label_poison);
                case PSYCHIC:
                    return context.getDrawable(R.drawable.label_psychic);
                case ROCK:
                    return context.getDrawable(R.drawable.label_rock);
                case STEEL:
                    return context.getDrawable(R.drawable.label_steel);
                case WATER:
                    return context.getDrawable(R.drawable.label_water);
                default:
                    return context.getDrawable(R.drawable.label_normal);
            }
        } else {
            switch (type) {
                case BUG:
                    return context.getResources().getDrawable(R.drawable.label_bug);
                case DARK:
                    return context.getResources().getDrawable(R.drawable.label_dark);
                case DRAGON:
                    return context.getResources().getDrawable(R.drawable.label_dragon);
                case ELECTRIC:
                    return context.getResources().getDrawable(R.drawable.label_electric);
                case FAIRY:
                    return context.getResources().getDrawable(R.drawable.label_fairy);
                case FIGHTING:
                    return context.getResources().getDrawable(R.drawable.label_fighting);
                case FIRE:
                    return context.getResources().getDrawable(R.drawable.label_fire);
                case FLYING:
                    return context.getResources().getDrawable(R.drawable.label_flying);
                case GHOST:
                    return context.getResources().getDrawable(R.drawable.label_ghost);
                case GRASS:
                    return context.getResources().getDrawable(R.drawable.label_grass);
                case GROUND:
                    return context.getResources().getDrawable(R.drawable.label_ground);
                case ICE:
                    return context.getResources().getDrawable(R.drawable.label_ice);
                case NORMAL:
                    return context.getResources().getDrawable(R.drawable.label_normal);
                case POISON:
                    return context.getResources().getDrawable(R.drawable.label_poison);
                case PSYCHIC:
                    return context.getResources().getDrawable(R.drawable.label_psychic);
                case ROCK:
                    return context.getResources().getDrawable(R.drawable.label_rock);
                case STEEL:
                    return context.getResources().getDrawable(R.drawable.label_steel);
                case WATER:
                    return context.getResources().getDrawable(R.drawable.label_water);
                default:
                    return context.getResources().getDrawable(R.drawable.label_normal);
            }
        }
    }

    public static int getTypeColor(Type type) {
        switch (type) {
            case BUG:
                return R.color.bug;
            case DARK:
                return R.color.dark;
            case DRAGON:
                return R.color.dragon;
            case ELECTRIC:
                return R.color.electric;
            case FAIRY:
                return R.color.fairy;
            case FIGHTING:
                return R.color.fighting;
            case FIRE:
                return R.color.fire;
            case FLYING:
                return R.color.flying;
            case GHOST:
                return R.color.ghost;
            case GRASS:
                return R.color.grass;
            case GROUND:
                return R.color.ground;
            case ICE:
                return R.color.ice;
            case NORMAL:
                return R.color.normal;
            case POISON:
                return R.color.poison;
            case PSYCHIC:
                return R.color.psychic;
            case ROCK:
                return R.color.rock;
            case STEEL:
                return R.color.steel;
            case WATER:
                return R.color.water;
            default:
                return R.color.normal;
        }
    }

}
