package sigmaone.industrialism.energy;

public interface IEnergyHandler {
    /*
     * Inserts energy into the handler
     *
     * @param amount How much energy to insert
     */
    void putEnergy(int amount);
    /*
     * Extracts energy from the handler
     *
     * @param amount How much energy to extract
     *
     * @return Amount of energy extracted
     */
    int takeEnergy(int amount);
}
