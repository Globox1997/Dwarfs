package net.dwarfs;

import net.dwarfs.init.EntityInit;
import net.fabricmc.api.ModInitializer;

public class DwarfsMain implements ModInitializer {

    @Override
    public void onInitialize() {
        EntityInit.init();
    }

}
