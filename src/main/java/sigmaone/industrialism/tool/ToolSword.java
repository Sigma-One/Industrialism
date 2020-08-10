package sigmaone.industrialism.tool;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ToolSword extends SwordItem {
    public ToolSword(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
}
