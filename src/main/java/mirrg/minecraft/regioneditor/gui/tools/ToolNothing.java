package mirrg.minecraft.regioneditor.gui.tools;

import mirrg.minecraft.regioneditor.gui.tool.ITool;
import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public class ToolNothing implements ITool
{

	protected final IToolContext toolContext;

	public ToolNothing(IToolContext toolContext)
	{
		this.toolContext = toolContext;
	}

	@Override
	public void on()
	{

	}

	@Override
	public void off()
	{

	}

}
