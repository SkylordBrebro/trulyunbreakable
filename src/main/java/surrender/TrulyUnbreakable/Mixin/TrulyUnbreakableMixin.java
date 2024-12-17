package surrender.TrulyUnbreakable.Mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.Set;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class TrulyUnbreakableMixin {
    @Inject(method = "onDurabilityChange*", at = @At("HEAD"))
    public void onDurabilityChange(int damage, ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this; // Get the ItemStack instance
        // First, check if the item already has the unbreakable component
        if (stack.get(DataComponentTypes.UNBREAKABLE) != null && stack.get(DataComponentTypes.UNBREAKABLE).showInTooltip()) {
            return;
        }
        if (stack.hasEnchantments()) {
            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> enchantments = EnchantmentHelper.getEnchantments(stack).getEnchantmentEntries();
            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments) {
                if (entry.getKey().matchesId(Enchantments.UNBREAKING.getValue()) && entry.getIntValue() >= 3) {
                    // Set the item to be unbreakable
                    stack.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
                    stack.setDamage(0);
                    return;
                }
            }
        }
    }
}