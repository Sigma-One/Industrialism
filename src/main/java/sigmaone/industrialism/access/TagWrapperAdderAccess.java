package sigmaone.industrialism.access;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public interface TagWrapperAdderAccess<T> {
    Tag.Identified<T> addWithNamespace(Identifier id);
}
