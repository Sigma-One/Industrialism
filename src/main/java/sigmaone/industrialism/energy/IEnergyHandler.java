package sigmaone.industrialism.energy;

public interface IEnergyHandler {
    /*
     * Inserts energy into the handler
     *
     * @param amount How much energy to insert
     */
    float putEnergy(float amount);
    /*
     * Extracts energy from the handler
     *
     * @param amount How much energy to extract
     *
     * @return Amount of energy extracted
     */
    float takeEnergy(float amount);
}
