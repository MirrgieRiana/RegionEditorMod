package mirrg.minecraft.regioneditor;

import java.util.Optional;

import org.apache.logging.log4j.Logger;

import mirrg.minecraft.regioneditor.gui.GuiRegionEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModRegionEditor.MODID, name = ModRegionEditor.NAME, version = ModRegionEditor.VERSION)
public class ModRegionEditor
{

	public static final String MODID = "mirrg.minecraft.regioneditor";
	public static final String NAME = "RegionEditor";
	public static final String VERSION = "0.0.1";

	@SuppressWarnings("unused")
	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new Object() {
				@SubscribeEvent
				public void handle(PlayerInteractEvent.RightClickItem event)
				{
					if (event.getWorld().isRemote) {
						ItemStack itemStack = event.getItemStack();
						if (itemStack.getItem() == Items.STICK) {
							if (itemStack.getDisplayName().equals("RegionEditor.show")) {
								new GuiRegionEditor(null, Optional.of(ss -> {

									for (String s : ss) {
										Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(s);
										if (ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, s) != 0) {
											return;
										}
										Minecraft.getMinecraft().player.sendChatMessage(s);
									}

								})).show();
							}
						}
					}
				}
			});
		}
	}

}
