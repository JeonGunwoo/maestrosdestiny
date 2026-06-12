package com.maestrosdestiny.combat.resource;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * {@code /md resource ...} 운영자 디버그 커맨드 (CLAUDE.md §3-2: 각 시스템은 디버그 커맨드 산출).
 *
 * <p>각 시스템이 {@code Commands.literal("md")} 하위에 독립적으로 서브트리를 등록하면
 * Brigadier 가 동일 루트를 병합한다. 권한 레벨 2(OP) 요구.</p>
 */
public final class MdResourceCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TYPES = (ctx, builder) ->
            SharedSuggestionProvider.suggest(ModResources.ids().stream().map(Identifier::toString), builder);

    private MdResourceCommand() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 26.1 권한 개편: 레벨 2(gamemasters) 요구. Commands.hasPermission(PermissionCheck) 패턴.
        dispatcher.register(Commands.literal("md")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("resource")
                        .then(Commands.literal("get")
                                .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_TYPES)
                                        .executes(MdResourceCommand::get)))
                        .then(Commands.literal("set")
                                .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_TYPES)
                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                                .executes(MdResourceCommand::set))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_TYPES)
                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg())
                                                .executes(MdResourceCommand::add))))
                        .then(Commands.literal("fill")
                                .executes(MdResourceCommand::fill))));
    }

    private static ResourceType resolve(CommandContext<CommandSourceStack> ctx) {
        String raw = StringArgumentType.getString(ctx, "type");
        Identifier id = Identifier.tryParse(raw);
        return id == null ? null : ModResources.get(id);
    }

    private static int get(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceType type = resolve(ctx);
        if (type == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown resource: " + StringArgumentType.getString(ctx, "type")));
            return 0;
        }
        double value = ResourceManager.get(player, type);
        ctx.getSource().sendSuccess(() -> Component.literal(
                type.id() + " = " + value + " / " + type.max()), false);
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceType type = resolve(ctx);
        if (type == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown resource: " + StringArgumentType.getString(ctx, "type")));
            return 0;
        }
        double amount = DoubleArgumentType.getDouble(ctx, "amount");
        ResourceManager.set(player, type, amount);
        double value = ResourceManager.get(player, type);
        ctx.getSource().sendSuccess(() -> Component.literal(type.id() + " -> " + value), false);
        return 1;
    }

    private static int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceType type = resolve(ctx);
        if (type == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown resource: " + StringArgumentType.getString(ctx, "type")));
            return 0;
        }
        double delta = DoubleArgumentType.getDouble(ctx, "amount");
        ResourceManager.add(player, type, delta);
        double value = ResourceManager.get(player, type);
        ctx.getSource().sendSuccess(() -> Component.literal(type.id() + " -> " + value), false);
        return 1;
    }

    private static int fill(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        for (ResourceType type : ModResources.all()) {
            ResourceManager.set(player, type, type.max());
        }
        ctx.getSource().sendSuccess(() -> Component.literal("All resources filled."), false);
        return 1;
    }
}
