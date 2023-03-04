package Units;


import Units.Tier.FirstTier;
import Units.Tier.SecondTier;
import arc.struct.ObjectMap;

@SuppressWarnings("unused")
public class Evolution {
    public final static ObjectMap<String, FirstTier> firstTierUnits = ObjectMap.of(
        "dagger", FirstTier.DAGGER,
            "nova", FirstTier.NOVA,
            "merui", FirstTier.MERUI,
            "flare", FirstTier.FLARE,
            "mono", FirstTier.MONO
    );
    public final static ObjectMap<String, SecondTier> secondTierUnits = ObjectMap.of(

    );
}
