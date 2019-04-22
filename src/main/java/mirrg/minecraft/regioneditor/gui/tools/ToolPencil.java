package mirrg.minecraft.regioneditor.gui.tools;

import mirrg.minecraft.regioneditor.gui.tool.IToolContext;

public class ToolPencil extends ToolBrush
{

	public ToolPencil(IToolContext toolContext)
	{
		super(toolContext);
	}

	@Override
	protected int getBrushSize()
	{
		return 1;
	}

}
