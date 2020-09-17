package sigmaone.industrialism.energy

interface IEnergyHandler {
    /*
     * Inserts energy into the handler
     *
     * @param amount How much energy to insert
     */
    fun putEnergy(amount: Float): Float

    /*
     * Extracts energy from the handler
     *
     * @param amount How much energy to extract
     *
     * @return Amount of energy extracted
     */
    fun takeEnergy(amount: Float): Float
}