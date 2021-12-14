package net.dwarfs;

import net.dwarfs.init.RenderInit;
import net.fabricmc.api.ClientModInitializer;

public class DwarfsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RenderInit.init();
    }

}
