package com.yungnickyoung.minecraft.betterportals.capability;

public class EntityPortalInfo implements IEntityPortalInfo {
    private boolean isInPortal;

    @Override
    public boolean getInPortal() {
        return isInPortal;
    }

    @Override
    public void setInPortal(boolean isInPortal) {
        this.isInPortal = isInPortal;
    }
}
