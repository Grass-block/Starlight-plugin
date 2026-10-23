package org.atcgroup.starlight.bundle.music.session;

import org.atcgroup.starlight.bundle.music.PlayerUIRenderer;
import org.atcgroup.starlight.bundle.music.resolve.MusicData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LegacyMusicSession extends MusicSession {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final BlockingQueue<MusicData> list = new LinkedBlockingQueue<>();

    public LegacyMusicSession(PlayerUIRenderer renderer, boolean mount) {
        super(renderer, mount);
    }

    @Override
    public void destroySession() {
        this.running.set(false);
        this.cancel();
        this.list.offer(MusicData.INT_SESSION);
    }

    @Override
    public void play(MusicData data) {
        if (this.active()) {
            this.cancel();
        }

        this.list.offer(data);
    }

    public void run() {
        while (this.running.get()) {
            MusicData data;
            try {
                data = this.list.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (data == MusicData.INT_SESSION) {
                return;
            }

            this.playSelected(data);
        }
    }
}
