package com.pokemongomap.helpers;

import com.google.android.gms.maps.model.LatLng;
import com.pokemongomap.pokemon.Pokemon;
import com.pokemongomap.pokemon.gen1.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public abstract class PokemonHelper {

    private static Class[] mPokemonClasses;

    public static void init() {
        mPokemonClasses = new Class[151];
        mPokemonClasses[0] = Bulbasaur.class;
        mPokemonClasses[1] = Ivysaur.class;
        mPokemonClasses[2] = Venusaur.class;
        mPokemonClasses[3] = Charmander.class;
        mPokemonClasses[4] = Charmeleon.class;
        mPokemonClasses[5] = Charizard.class;
        mPokemonClasses[6] = Squirtle.class;
        mPokemonClasses[7] = Wartortle.class;
        mPokemonClasses[8] = Blastoise.class;
        mPokemonClasses[9] = Caterpie.class;
        mPokemonClasses[10] = Metapod.class;
        mPokemonClasses[11] = Butterfree.class;
        mPokemonClasses[12] = Weedle.class;
        mPokemonClasses[13] = Kakuna.class;
        mPokemonClasses[14] = Beedrill.class;
        mPokemonClasses[15] = Pidgey.class;
        mPokemonClasses[16] = Pidgeotto.class;
        mPokemonClasses[17] = Pidgeot.class;
        mPokemonClasses[18] = Rattata.class;
        mPokemonClasses[19] = Raticate.class;
        mPokemonClasses[20] = Spearow.class;
        mPokemonClasses[21] = Fearow.class;
        mPokemonClasses[22] = Ekans.class;
        mPokemonClasses[23] = Arbok.class;
        mPokemonClasses[24] = Pikachu.class;
        mPokemonClasses[25] = Raichu.class;
        mPokemonClasses[26] = Sandshrew.class;
        mPokemonClasses[27] = Sandslash.class;
        mPokemonClasses[28] = Nidoranw.class;
        mPokemonClasses[29] = Nidorina.class;
        mPokemonClasses[30] = Nidoqueen.class;
        mPokemonClasses[31] = Nidoranm.class;
        mPokemonClasses[32] = Nidorino.class;
        mPokemonClasses[33] = Nidoking.class;
        mPokemonClasses[34] = Clefairy.class;
        mPokemonClasses[35] = Clefable.class;
        mPokemonClasses[36] = Vulpix.class;
        mPokemonClasses[37] = Ninetales.class;
        mPokemonClasses[38] = Jigglypuff.class;
        mPokemonClasses[39] = Wigglytuff.class;
        mPokemonClasses[40] = Zubat.class;
        mPokemonClasses[41] = Golbat.class;
        mPokemonClasses[42] = Oddish.class;
        mPokemonClasses[43] = Gloom.class;
        mPokemonClasses[44] = Vileplume.class;
        mPokemonClasses[45] = Paras.class;
        mPokemonClasses[46] = Parasect.class;
        mPokemonClasses[47] = Venonat.class;
        mPokemonClasses[48] = Venomoth.class;
        mPokemonClasses[49] = Diglett.class;
        mPokemonClasses[50] = Dugtrio.class;
        mPokemonClasses[51] = Meowth.class;
        mPokemonClasses[52] = Persian.class;
        mPokemonClasses[53] = Psyduck.class;
        mPokemonClasses[54] = Golduck.class;
        mPokemonClasses[55] = Mankey.class;
        mPokemonClasses[56] = Primeape.class;
        mPokemonClasses[57] = Growlithe.class;
        mPokemonClasses[58] = Arcanine.class;
        mPokemonClasses[59] = Poliwag.class;
        mPokemonClasses[60] = Poliwhirl.class;
        mPokemonClasses[61] = Poliwrath.class;
        mPokemonClasses[62] = Abra.class;
        mPokemonClasses[63] = Kadabra.class;
        mPokemonClasses[64] = Alakazam.class;
        mPokemonClasses[65] = Machop.class;
        mPokemonClasses[66] = Machoke.class;
        mPokemonClasses[67] = Machamp.class;
        mPokemonClasses[68] = Bellsprout.class;
        mPokemonClasses[69] = Weepinbell.class;
        mPokemonClasses[70] = Victreebel.class;
        mPokemonClasses[71] = Tentacool.class;
        mPokemonClasses[72] = Tentacruel.class;
        mPokemonClasses[73] = Geodude.class;
        mPokemonClasses[74] = Graveler.class;
        mPokemonClasses[75] = Golem.class;
        mPokemonClasses[76] = Ponyta.class;
        mPokemonClasses[77] = Rapidash.class;
        mPokemonClasses[78] = Slowpoke.class;
        mPokemonClasses[79] = Slowbro.class;
        mPokemonClasses[80] = Magnemite.class;
        mPokemonClasses[81] = Magneton.class;
        mPokemonClasses[82] = Farfetchd.class;
        mPokemonClasses[83] = Doduo.class;
        mPokemonClasses[84] = Dodrio.class;
        mPokemonClasses[85] = Seel.class;
        mPokemonClasses[86] = Dewgong.class;
        mPokemonClasses[87] = Grimer.class;
        mPokemonClasses[88] = Muk.class;
        mPokemonClasses[89] = Shellder.class;
        mPokemonClasses[90] = Cloyster.class;
        mPokemonClasses[91] = Gastly.class;
        mPokemonClasses[92] = Haunter.class;
        mPokemonClasses[93] = Gengar.class;
        mPokemonClasses[94] = Onix.class;
        mPokemonClasses[95] = Drowzee.class;
        mPokemonClasses[96] = Hypno.class;
        mPokemonClasses[97] = Krabby.class;
        mPokemonClasses[98] = Kingler.class;
        mPokemonClasses[99] = Voltorb.class;
        mPokemonClasses[100] = Electrode.class;
        mPokemonClasses[101] = Exeggcute.class;
        mPokemonClasses[102] = Exeggutor.class;
        mPokemonClasses[103] = Cubone.class;
        mPokemonClasses[104] = Marowak.class;
        mPokemonClasses[105] = Hitmonlee.class;
        mPokemonClasses[106] = Hitmonchan.class;
        mPokemonClasses[107] = Lickitung.class;
        mPokemonClasses[108] = Koffing.class;
        mPokemonClasses[109] = Weezing.class;
        mPokemonClasses[110] = Rhyhorn.class;
        mPokemonClasses[111] = Rhydon.class;
        mPokemonClasses[112] = Chansey.class;
        mPokemonClasses[113] = Tangela.class;
        mPokemonClasses[114] = Kangaskhan.class;
        mPokemonClasses[115] = Horsea.class;
        mPokemonClasses[116] = Seadra.class;
        mPokemonClasses[117] = Goldeen.class;
        mPokemonClasses[118] = Seaking.class;
        mPokemonClasses[119] = Staryu.class;
        mPokemonClasses[120] = Starmie.class;
        mPokemonClasses[121] = MrMime.class;
        mPokemonClasses[122] = Scyther.class;
        mPokemonClasses[123] = Jynx.class;
        mPokemonClasses[124] = Electabuzz.class;
        mPokemonClasses[125] = Magmar.class;
        mPokemonClasses[126] = Pinsir.class;
        mPokemonClasses[127] = Tauros.class;
        mPokemonClasses[128] = Magikarp.class;
        mPokemonClasses[129] = Gyarados.class;
        mPokemonClasses[130] = Lapras.class;
        mPokemonClasses[131] = Ditto.class;
        mPokemonClasses[132] = Eevee.class;
        mPokemonClasses[133] = Vaporeon.class;
        mPokemonClasses[134] = Jolteon.class;
        mPokemonClasses[135] = Flareon.class;
        mPokemonClasses[136] = Porygon.class;
        mPokemonClasses[137] = Omanyte.class;
        mPokemonClasses[138] = Omastar.class;
        mPokemonClasses[139] = Kabuto.class;
        mPokemonClasses[140] = Kabutops.class;
        mPokemonClasses[141] = Aerodactyl.class;
        mPokemonClasses[142] = Snorlax.class;
        mPokemonClasses[143] = Articuno.class;
        mPokemonClasses[144] = Zapdos.class;
        mPokemonClasses[145] = Moltres.class;
        mPokemonClasses[146] = Dratini.class;
        mPokemonClasses[147] = Dragonair.class;
        mPokemonClasses[148] = Dragonite.class;
        mPokemonClasses[149] = Mewtwo.class;
        mPokemonClasses[150] = Mew.class;
    }

    public static Pokemon getPokemon(int id, LatLng loc, Date disappearTime) throws NullPointerException {
        Class c = mPokemonClasses[id-1];
        try {
            //c.getDeclaredConstructor()
            return (Pokemon) c.getDeclaredConstructor(int.class, LatLng.class, Date.class).newInstance(id, loc, disappearTime);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new NullPointerException();
    }

}
