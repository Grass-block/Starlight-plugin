package org.atcgroup.starlight.bundle.worldguard;

import org.atcgroup.starlight.bundle.api.RegionKey;
import org.atcgroup.starlight.bundle.mission.commission.Commission;

import java.util.UUID;

public class WGBuildCommission {

    public static class BuildCommission extends Commission {
        private RegionKey region;

        protected BuildCommission(UUID uuid, UUID creator) {
            super(uuid, creator);
        }

        public void setRegion(RegionKey region) {
            this.region = region;
        }

        @Override
        public void onParticipantAdded(UUID participant) {
            super.onParticipantAdded(participant);
        }

        @Override
        public void onParticipantRemoved(UUID participant) {
            super.onParticipantRemoved(participant);
        }
    }
}