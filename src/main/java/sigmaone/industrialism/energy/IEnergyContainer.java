package sigmaone.industrialism.energy;

public interface IEnergyContainer {
    /*
     * Returns the currently stored energy
     *
     * @return Energy amount
     */
    float getStoredEnergy();
    /*
     * Returns the maximum energy capacity
     *
     * @return Maximum energy amount
     */
    float getMaxEnergy();
    /*
     * Returns the amount of free space in the energy buffer
     *
     * @return Free space
     */
    float getAvailableEnergyCapacity();
}
