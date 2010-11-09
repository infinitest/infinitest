package org.infinitest;

public interface ReloadListener
{
    /**
     * Called when the core is completely reloaded. When this occurs, clients are encouraged to drop
     * all state, as it may have become inaccurate.
     * 
     * @see InfinitestCore#reload()
     */
    void reloading();
}