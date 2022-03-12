package net.dwarfs;

import net.dwarfs.init.BlockInit;
import net.dwarfs.init.EntityInit;
import net.dwarfs.init.ItemInit;
import net.dwarfs.init.PointOfInterestsInit;
import net.fabricmc.api.ModInitializer;

public class DwarfsMain implements ModInitializer {

    @Override
    public void onInitialize() {
        BlockInit.init();
        PointOfInterestsInit.init();
        EntityInit.init();
        ItemInit.init();
    }

}
