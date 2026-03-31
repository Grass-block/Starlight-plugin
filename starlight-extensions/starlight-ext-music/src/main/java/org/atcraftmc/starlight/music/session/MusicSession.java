package org.atcraftmc.starlight.music.session;

import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.core.view.SchedulerProvider;
import org.atcraftmc.starlight.music.MusicService;
import org.atcraftmc.starlight.music.PlayerUIRenderer;
import org.atcraftmc.starlight.music.resolve.MusicData;
import org.atcraftmc.starlight.music.resolve.MusicNode;
import org.atcraftmc.starlight.util.PlayerList;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicReference;

public abstract class MusicSession implements Runnable, MusicTask.TaskCallback {
    private final PlayerUIRenderer renderer;
    private final PlayerList players = new PlayerList();
    private final AtomicReference<MusicTask> current = new AtomicReference<>(null);
    private final boolean mount;

    public MusicSession(PlayerUIRenderer renderer, boolean mount) {
        this.renderer = renderer;
        this.mount = mount;
    }

    private String rendererID() {
        return "starlight:music-player";
    }

    @Override
    public void start(MusicTask task, MusicData music) {
        for (var p : this.players.getPlayerObjects()) {
            startRender(p);
        }
    }

    @Override
    public void end(MusicTask task, MusicData music) {
        for (var player : this.players.getPlayerObjects()) {
            PlayerUIService.getInstance(player).getActionbar_v2().removeProcess(this.rendererID());
        }
    }


    public void startRender(Player player) {
        PlayerUIService.getInstance(player).getActionbar_v2().registerIntervalProcess(
                this.rendererID(),
                5,
                3,
                SchedulerProvider.ASYNC,
                (a, t) -> {
                    var tt = this.current.get().getTick();
                    this.renderer.renderUI(
                            player,
                            this.current.get().getMusic(),
                            tt,
                            this.current.get().paused()
                    );
                }
        );
    }


    @Override
    public void renderNode(MusicTask task, MusicNode node, MusicData music) {
        float power = node.getPower();
        MusicService.playNode(this.players.getPlayerObjects(), node.getNode(), music.getOffset(), node.getInstrument(), power, this.mount);
    }

    public void playSelected(MusicData current) {
        if (this.active()) {
            this.cancel();
        }
        this.current.set(new MusicTask(current, this));
        this.current.get().run();
        this.current.set(null);
    }

    public Thread startSession() {
        var thread = new Thread(this, this.getThreadId());
        thread.start();
        return thread;
    }

    public String getThreadId() {
        return "Starlight-MusicPlayer-" + this.hashCode();
    }

    public void destroySession() {
    }

    //control
    public abstract void play(MusicData data);

    public boolean active() {
        return this.current.get() != null;
    }

    public final void pause() {
        if (this.active()) {
            this.current.get().pause();
        }
    }

    public final void resume() {
        if (this.active()) {
            this.current.get().resume();
        }
    }

    public final void cancel() {
        if (this.active()) {
            this.current.get().stop();
        }
    }

    public final void addPlayer(Player player) {
        this.players.add(player);
        if (!this.active()) {
            return;
        }
        this.startRender(player);
    }

    public final void removePlayer(Player player) {
        this.players.remove(player);
        PlayerUIService.getInstance(player).getActionbar_v2().removeProcess(this.rendererID());
    }

}
