package example.createfix.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

/**
 * Create port's ContainerExtension.setMaxSize() writes an Optional<Integer> straight into
 * the item's component patch map. In 26.3 the codec expects a raw Integer there, so saving or
 * syncing a Mechanical Crafter throws ClassCastException. This goes through the public
 * ItemStack API instead. Target is given by name so Create is not needed at compile time.
 */
@Mixin(targets = "com.zurrtum.create.infrastructure.items.ContainerExtension")
public interface ContainerExtensionMixin {

    @Overwrite
    default void setMaxSize(ItemStack stack, Optional<Integer> limit) {
        limit.ifPresent(value -> stack.set(DataComponents.MAX_STACK_SIZE, value));
    }

    @Overwrite
    default ItemStack removeMaxSize(ItemStack stack, Optional<Integer> limit) {
        Integer current = stack.get(DataComponents.MAX_STACK_SIZE);
        if (limit.isPresent() && limit.get().equals(current)) {
            Integer original = stack.getPrototype().get(DataComponents.MAX_STACK_SIZE);
            if (original != null) {
                stack.set(DataComponents.MAX_STACK_SIZE, original);
            } else {
                stack.remove(DataComponents.MAX_STACK_SIZE);
            }
        }
        return stack;
    }
}
