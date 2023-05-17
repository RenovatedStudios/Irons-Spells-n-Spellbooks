package io.redspace.ironsspellbooks.spells.poison;

import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.network.spell.ClientboundSyncTargetingData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class RootSpell extends AbstractSpell {
    public RootSpell() {
        this(1);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(caster), 1))
        );
    }

    public RootSpell(int level) {
        super(SpellType.ROOT_SPELL);
        this.level = level;
        this.manaCostPerLevel = 3;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 40;
        this.baseManaCost = 45;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.ROOT.get());
    }

    @Override
    public boolean checkPreCastConditions(Level level, LivingEntity entity, PlayerMagicData playerMagicData) {
        var target = findTarget(entity);
        if (target == null)
            return false;
        else {
            playerMagicData.setAdditionalCastData(new CastTargetingData(target));
            if (entity instanceof ServerPlayer serverPlayer)
                Messages.sendToPlayer(new ClientboundSyncTargetingData(target, getSpellType()), serverPlayer);
            if (target instanceof ServerPlayer serverPlayer)
                Utils.sendTargetedNotification(serverPlayer, entity, this.getSpellType());
            return true;
        }
    }


    @Override
    public void onCast(Level level, LivingEntity entity, PlayerMagicData playerMagicData) {
//        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData targetData) {
//            var targetEntity = targetData.getTarget((ServerLevel) level);
//            if (targetEntity != null) {
//                //targetEntity.addEffect(new MobEffectInstance(MobEffectRegistry.ROOT.get(), getDuration(entity), getAmplifier(entity)));
//                IronsSpellbooks.LOGGER.debug("RootSpell.onCast targetEntity:{}", targetEntity);
//                RootEntity rootEntity = new RootEntity(level, entity, getDuration(entity));
//                rootEntity.setTarget(targetEntity);
//                rootEntity.moveTo(targetEntity.getPosition(2));
//                level.addFreshEntity(rootEntity);
//                targetEntity.stopRiding();
//                targetEntity.startRiding(rootEntity, true);
//            }
//
//        }

        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData castTargetingData) {
            LivingEntity target = castTargetingData.getTarget((ServerLevel) level);
            if (target != null && !target.getType().is(ModTags.CANT_ROOT)){
                Vec3 spawn = target.position();
                RootEntity rootEntity = new RootEntity(level, entity);
                rootEntity.setDuration(getDuration(entity));
                rootEntity.setTarget(target);
                rootEntity.moveTo(spawn);
                level.addFreshEntity(rootEntity);
                target.stopRiding();
                target.startRiding(rootEntity, true);
            }
        }

        super.onCast(level, entity, playerMagicData);
    }

    @Nullable
    private LivingEntity findTarget(LivingEntity caster) {
        var target = Utils.raycastForEntity(caster.level, caster, 32, true, 0.35f);
        if (target instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity livingTarget) {
            return livingTarget;
        } else {
            return null;
        }
    }

    public int getAmplifier(LivingEntity caster) {
        return (int) (getSpellPower(caster) * this.level - 1);
    }

    public int getDuration(LivingEntity caster) {
        return (int) (getSpellPower(caster) * 20);
    }

}
