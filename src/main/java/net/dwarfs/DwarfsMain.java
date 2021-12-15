package net.dwarfs;

import net.dwarfs.init.EntityInit;
import net.dwarfs.init.PointOfInterestsInit;
import net.fabricmc.api.ModInitializer;

public class DwarfsMain implements ModInitializer {

    @Override
    public void onInitialize() {
        PointOfInterestsInit.init();
        EntityInit.init();
    }

}
