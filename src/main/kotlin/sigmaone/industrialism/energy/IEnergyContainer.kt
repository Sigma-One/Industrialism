package sigmaone.industrialism.energy

interface IEnergyContainer {
    /*
     * Returns the currently stored energy
     *
     * @return Energy amount
     */
    val storedEnergy: Float

    /*
     * Returns the maximum energy capacity
     *
     * @return Maximum energy amount
     */
    val maxEnergy: Float

    /*
     * Returns the amount of free space in the energy buffer
     *
     * @return Free space
     */
    val availableEnergyCapacity: Float
}