package caveworld.plugin;

public interface ICavePlugin
{
	public String getModId();

	public boolean getPluginState();

	public boolean setPluginState(boolean state);

	public void invoke();
}